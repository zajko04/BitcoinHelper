package com.example.bitcoinhelper.mainscreen

import android.annotation.SuppressLint
import android.graphics.Color
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bitcoinhelper.BitBayAPI
import com.example.bitcoinhelper.R
import com.example.bitcoinhelper.database.CashEntity
import kotlinx.coroutines.*
import java.util.*
import java.util.concurrent.Executors


@SuppressLint("SetTextI18n")
class MainViewHolder(itemView: View, private val mParent: ViewGroup) :
    RecyclerView.ViewHolder(itemView) {

    private val TAG = "MainViewHolder"
    private var mView: View = itemView
    private val loading = mView.context.applicationContext.getString(R.string.loading)
    private val mCurrentPrice = mView.findViewById<TextView>(R.id.currentPrice)
    private var mTimer: Timer? = Timer()

    fun setUp(cash: CashEntity) {

        val scope = CoroutineScope(Executors.newSingleThreadExecutor().asCoroutineDispatcher())
        scope.launch {
            refreshCurrentPrice(cash.btcQuantity, cash.cashIn)
        }

        val who = mView.findViewById<TextView>(R.id.who)
        val cashIn = mView.findViewById<TextView>(R.id.cashIn)
        val btcExchange = mView.findViewById<TextView>(R.id.btcExchange)
        val btcQuantity = mView.findViewById<TextView>(R.id.btcQuantity)

        who.text = "Właściciel: " + cash.name
        cashIn.text = "Ile kasy wpłacił: " + cash.cashIn.toString() + " zł"
        btcExchange.text = "Po ile kupił bitcoin'a: " + cash.btcExchange.toString() + " zł"
        btcQuantity.text = "Ile ma bitcoin'ów: " + cash.btcQuantity.toString() + " BTC"
    }

    private suspend fun refreshCurrentPrice(btcQuantity: Double, cashIn: Float) =
        withContext(Dispatchers.IO) {
            var helpArg = 0
            mTimer?.scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    Log.d(TAG, "$btcQuantity  $cashIn")
                    val result = BitBayAPI.getInstance().getCurrentProfit(
                        btcQuantity,
                        cashIn,
                        helpArg,
                        mView.context
                    )
                    if (result.contains("-")) {
                        mCurrentPrice.setTextColor(Color.RED)
                    } else {
                        mCurrentPrice.setTextColor(Color.GREEN)
                    }

                    if (result.contains(loading)) {
                        mCurrentPrice.setTextColor(Color.WHITE)
                        helpArg = when (helpArg) {
                            3 -> 0
                            else -> helpArg + 1
                        }
                    }
                    mCurrentPrice.text = result
                }
            }, 0, 1000)
        }

    fun stopTimer() {
        mTimer?.cancel()
        mTimer?.purge()
        mTimer = null
    }
}