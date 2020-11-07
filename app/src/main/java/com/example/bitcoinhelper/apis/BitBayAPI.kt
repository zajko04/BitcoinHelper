package com.example.bitcoinhelper.apis

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.GET
import java.util.concurrent.atomic.AtomicReference

class BitBayAPI : API() {

    companion object {
        @Volatile
        private var INSTANCE: BitBayAPI? = null

        fun getInstance(): BitBayAPI {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = BitBayAPI()
                instance.runUpdate()
                instance.URL = "https://bitbay.net/API/Public/BTCPLN/"
                instance.TAG = "BitBayAPI"
                instance.lastResponse = AtomicReference(BitBayResponse())
                INSTANCE = instance
                return instance
            }
        }

        fun stop() {
            synchronized(this) {
                INSTANCE?.stopUpdate()
                INSTANCE = null
            }
        }
    }

    override fun getCurrentPrice(): Double {
        return synchronized(lastResponse!!) {
            (lastResponse as AtomicReference<BitBayResponse>).get().ask
        }
    }

    override fun loadResponse() {
        val service = APIServiceBuilder.buildService(BitBayAPIRService::class.java, URL)
        val requestCall = service.getAPIResponse()

        requestCall.enqueue(object : Callback<BitBayResponse> {
            override fun onResponse(
                call: Call<BitBayResponse>,
                response: Response<BitBayResponse>
            ) {
                if (response.isSuccessful) {
                    (lastResponse as AtomicReference<BitBayResponse>).set(response.body()!!)
                }
            }

            override fun onFailure(call: Call<BitBayResponse>, t: Throwable) {
                Log.e(TAG, t.toString())
            }
        })
    }

    private interface BitBayAPIRService {
        @GET("ticker.json")
        fun getAPIResponse(): Call<BitBayResponse>
    }

    data class BitBayResponse(
        val ask: Double = 0.toDouble(),
        val average: Double = 0.toDouble(),
        val bid: Double = 0.toDouble(),
        val last: Double = 0.toDouble(),
        val max: Float = 0F,
        val min: Double = 0.toDouble(),
        val volume: Double = 0.toDouble(),
        val vwap: Double = 0.toDouble()
    )
}