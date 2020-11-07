package com.example.bitcoinhelper

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import androidx.appcompat.app.AppCompatActivity
import com.example.bitcoinhelper.apis.BitcoinInfoAPIWrapper
import com.example.bitcoinhelper.mainscreen.MainActivity

class OpenActivity : AppCompatActivity() {

    private val context = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_open)
        BitcoinInfoAPIWrapper.initialize(BitcoinInfoAPIWrapper.APIType.COINBASE)
        BitcoinInfoAPIWrapper.getInstance()

        object: CountDownTimer(2000,1000) {
            override fun onTick(millisUntilFinished: Long) {

            }

            override fun onFinish() {
                val intent = Intent(context, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
                finish()
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        }.start()
    }
}