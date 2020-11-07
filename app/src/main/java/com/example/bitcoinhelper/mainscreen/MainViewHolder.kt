package com.example.bitcoinhelper.mainscreen

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bitcoinhelper.R
import com.example.bitcoinhelper.database.CashEntity


@SuppressLint("SetTextI18n")
class MainViewHolder(itemView: View, private val mParent: ViewGroup) :
    RecyclerView.ViewHolder(itemView) {

    private val TAG = "MainViewHolder"
    private var mView: View = itemView
    private val loading = mView.context.applicationContext.getString(R.string.loading)
    private val mCurrentPrice = mView.findViewById<TextView>(R.id.currentPrice)

    fun setUp(cash: CashEntity) {
        val who = mView.findViewById<TextView>(R.id.who)
        val cashIn = mView.findViewById<TextView>(R.id.cashIn)
        val btcExchange = mView.findViewById<TextView>(R.id.btcExchange)
        val btcQuantity = mView.findViewById<TextView>(R.id.btcQuantity)

        who.text = "Właściciel: " + cash.name
        cashIn.text = "Ile kasy wpłacił: " + cash.cashIn.toString() + " zł"
        btcExchange.text = "Po ile kupił bitcoin'a: " + cash.btcExchange.toString() + " zł"
        btcQuantity.text = "Ile ma bitcoin'ów: " + cash.btcQuantity.toString() + " BTC"
    }

    fun setProfit(profit: String) {
        if (profit.contains("-")) {
            mCurrentPrice.setTextColor(Color.RED)
        } else if (profit.contains(loading)) {
            mCurrentPrice.setTextColor(Color.WHITE)
        } else {
            mCurrentPrice.setTextColor(Color.GREEN)
        }
        mCurrentPrice.text = profit
    }
}