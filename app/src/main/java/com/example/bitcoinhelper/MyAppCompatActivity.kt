package com.example.bitcoinhelper

import androidx.appcompat.app.AppCompatActivity
import com.example.bitcoinhelper.apis.BitcoinInfoAPIWrapper

open class MyAppCompatActivity : AppCompatActivity()  {
    override fun onPause() {
        super.onPause()
        BitcoinInfoAPIWrapper.stop()
    }

    override fun onResume() {
        super.onResume()
        BitcoinInfoAPIWrapper.getInstance()
    }
}