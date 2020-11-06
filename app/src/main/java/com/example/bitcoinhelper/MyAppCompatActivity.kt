package com.example.bitcoinhelper

import androidx.appcompat.app.AppCompatActivity

open class MyAppCompatActivity : AppCompatActivity()  {
    override fun onPause() {
        super.onPause()
        BitBayAPI.stop()
    }

    override fun onResume() {
        super.onResume()
        BitBayAPI.getInstance()
    }
}