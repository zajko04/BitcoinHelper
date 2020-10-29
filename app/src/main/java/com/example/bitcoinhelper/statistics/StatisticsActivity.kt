package com.example.bitcoinhelper.statistics

import android.content.Intent
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bitcoinhelper.R
import com.example.bitcoinhelper.mainscreen.CashEntityViewModel
import com.example.bitcoinhelper.mainscreen.MainActivity

class StatisticsActivity : AppCompatActivity() {

    private var x1 = 0F
    private var x2 = 0F
    private lateinit var recyclerView: RecyclerView
    private lateinit var cashEntityViewModel: CashEntityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistics)
        val viewAdapter = StatisticsRecyclerViewAdapter()

        cashEntityViewModel = ViewModelProvider(this).get(CashEntityViewModel::class.java)
        cashEntityViewModel.allCash.observe(this, { cash ->
            cash?.let { viewAdapter.setItems(it) }
        })

        recyclerView = findViewById<RecyclerView>(R.id.recyclerView).apply {
            layoutManager = LinearLayoutManager(this.context)
            adapter = viewAdapter
            addItemDecoration(MainActivity.MainItemDecoration())
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    override fun onTouchEvent(touchEvent: MotionEvent) : Boolean {
        when(touchEvent.action) {
            MotionEvent.ACTION_DOWN -> {
                x1 = touchEvent.x
            }
            MotionEvent.ACTION_UP -> {
                x2 = touchEvent.x
                if(x2 < x1 - Resources.getSystem().displayMetrics.widthPixels/3) {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                }
            }
        }
        return true
    }
}