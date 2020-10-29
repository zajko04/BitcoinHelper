package com.example.bitcoinhelper.statistics

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.bitcoinhelper.BitBayAPI
import com.example.bitcoinhelper.R
import com.example.bitcoinhelper.database.CashEntity
import kotlinx.coroutines.*
import java.util.*
import java.util.concurrent.Executors
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class StatisticsRecyclerViewAdapter :
    RecyclerView.Adapter<StatisticsRecyclerViewAdapter.StatisticsViewHolder>()  {

    private val itemsList = ArrayList<Item>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatisticsViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.statistics_item, parent, false)
        return StatisticsViewHolder(view, itemsList, this)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: StatisticsViewHolder, position: Int) {
        val item = itemsList[position]
        holder.nameTV.text = item.name
        holder.cashCountTV.text = "Ile wpłacił: ${item.cashCount}"
        holder.btcCountTV.text = "Ile posiada BTC: ${item.btcQuantity}"
        val isExpanded = itemsList[position].expanded
        if (isExpanded) holder.expandableLayout.visibility = View.VISIBLE
        else holder.expandableLayout.visibility = View.GONE
    }

    override fun getItemCount() = itemsList.size

    internal fun setItems(items: List<CashEntity>) {
        itemsList.clear()
        val namesMap = HashMap<String, Int>()
        for(item in items) {
            if(!namesMap.contains(item.name)) {
                namesMap[item.name] = itemsList.size
                itemsList.add(Item(item.name, item.cashIn, item.btcQuantity))
            } else {
                namesMap[item.name]?.let {
                    itemsList[it].btcQuantity += item.btcQuantity
                    itemsList[it].cashCount += item.cashIn
                }
            }
        }
        notifyDataSetChanged()
    }

    class StatisticsViewHolder(
        view: View,
        itemsList: ArrayList<Item>,
        adapter: StatisticsRecyclerViewAdapter
    ) : RecyclerView.ViewHolder(view) {
        val expandableLayout: ConstraintLayout = view.findViewById(R.id.expandableLayout)
        val nameTV: TextView = view.findViewById(R.id.titleTextView)
        val cashCountTV: TextView = view.findViewById(R.id.cashCount)
        val btcCountTV: TextView = view.findViewById(R.id.btcCount)
        val currentPriceTV: TextView = view.findViewById(R.id.currentPrice)
        private val scope = CoroutineScope(Executors.newSingleThreadExecutor().asCoroutineDispatcher())
        private var timer: Timer? = Timer()

        init {
            nameTV.setOnClickListener {
                val item = itemsList[adapterPosition]
                if(item.expanded)
                    stopTimer()
                else
                    scope.launch {
                        startRefreshing(item.btcQuantity)
                    }

                item.expanded = !item.expanded
                adapter.notifyDataSetChanged()
            }
        }

        private fun stopTimer() {
            timer?.cancel()
            timer?.purge()
            timer = null
        }

        private suspend fun startRefreshing(btcQuantity: Double)  = withContext(
            Dispatchers.IO
        ) {
            var helpArg = 0
            val loading = itemView.context.getString(R.string.loading)
            timer?.scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    val result = BitBayAPI
                        .getInstance()
                        .getCurrentSellPrice(helpArg, itemView.context, btcQuantity)

                    currentPriceTV.text = result

                    if (result.contains(loading)) {
                        currentPriceTV.setTextColor(Color.WHITE)
                        helpArg = when (helpArg) {
                            3 -> 0
                            else -> helpArg + 1
                        }
                    }
                }
            }, 0, 1000)
        }
    }

    data class Item(
        val name: String,
        var cashCount: Float,
        var btcQuantity: Double,
        var expanded: Boolean = false
    )
}