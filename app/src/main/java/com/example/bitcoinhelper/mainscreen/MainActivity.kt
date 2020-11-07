package com.example.bitcoinhelper.mainscreen

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bitcoinhelper.MyAppCompatActivity
import com.example.bitcoinhelper.R
import com.example.bitcoinhelper.apis.BitcoinInfoAPIWrapper
import com.example.bitcoinhelper.statistics.StatisticsActivity
import kotlinx.coroutines.*
import java.util.*
import java.util.concurrent.Executors

class MainActivity : MyAppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: MainRecyclerViewAdapter
    private lateinit var viewManager: GridLayoutManager
    private lateinit var cashEntityViewModel: CashEntityViewModel
    private var x1 = 0F
    private var x2 = 0F
    private var timer: Timer? = Timer()
    private val context = this

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val currentPriceBTC = findViewById<TextView>(R.id.currentBTCPrice)
        val scope = CoroutineScope(Executors.newSingleThreadExecutor().asCoroutineDispatcher())
        scope.launch {
            startRefreshing(currentPriceBTC)
        }

        cashEntityViewModel = ViewModelProvider(this).get(CashEntityViewModel::class.java)
        cashEntityViewModel.allCash.observe(this, { cash ->
            cash?.let { viewAdapter.setItems(it) }
        })

        cashEntityViewModel.allProfit.observe(this, { profit ->
            profit?.let { viewAdapter.setProfits(it) }
        })

        viewManager = GridLayoutManager(this, 1)
        viewAdapter = MainRecyclerViewAdapter(cashEntityViewModel, this)

        recyclerView = findViewById<RecyclerView>(R.id.recyclerView).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
            addItemDecoration(MainItemDecoration())
        }

        val addButton = findViewById<Button>(R.id.addButton)

        addButton.setOnClickListener {
            AddCashDialog(cashEntityViewModel).show(supportFragmentManager, "AddCashDialog")
        }

        val statisticsButton = findViewById<Button>(R.id.statisticButton)

        statisticsButton.setOnClickListener {
            val intent = Intent(this, StatisticsActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }
    }

    override fun onDestroy() {
        stopTimer()
        super.onDestroy()
    }

    @SuppressLint("SetTextI18n")
    private suspend fun startRefreshing(currentPriceBTC: TextView) = withContext(
        Dispatchers.IO
    ) {
        val loading = getString(R.string.loading)
        var helpArg = 0
        timer?.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                val result = BitcoinInfoAPIWrapper.getInstance().getCurrentSellPrice(helpArg, context)
                runOnUiThread {
                    currentPriceBTC.text = result
                }

                if (result.contains(loading)) {
                    currentPriceBTC.setTextColor(Color.WHITE)
                    helpArg = when (helpArg) {
                        3 -> 0
                        else -> helpArg + 1
                    }
                }
            }
        }, 0, 1000)
    }

    private fun stopTimer() {
        timer?.cancel()
        timer?.purge()
        timer = null
    }

    override fun onTouchEvent(event: MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                x1 = event.x
            }
            MotionEvent.ACTION_UP -> {
                x2 = event.x
                if (x1 < x2 - Resources.getSystem().displayMetrics.widthPixels / 3) {
                    val intent = Intent(this, StatisticsActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
                }
            }
        }
        return true
    }

    public class MainItemDecoration : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            outRect.bottom = 20
        }
    }
}