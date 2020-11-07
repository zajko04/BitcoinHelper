package com.example.bitcoinhelper.apis

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.*
import java.util.concurrent.atomic.AtomicReference

class CoinbaseAPI : API() {

    companion object {
        @Volatile
        private var INSTANCE: CoinbaseAPI? = null

        fun getInstance(): CoinbaseAPI {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = CoinbaseAPI()
                instance.runUpdate()
                instance.URL = "https://api.coinbase.com/"
                instance.TAG = "CoinbaseAPI"
                instance.lastResponse = AtomicReference(CoinbaseResponse())
                INSTANCE = instance
                return instance
            }
        }

        fun stop() {
            INSTANCE?.stopUpdate()
            INSTANCE = null
        }
    }

    override fun getCurrentPrice(): Double {
        return synchronized(lastResponse!!) {
            (lastResponse as AtomicReference<CoinbaseResponse>).get().data.amount.toDouble()
        }
    }

    override fun loadResponse() {
        val service = APIServiceBuilder.buildService(CoinbaseAPIService::class.java, URL)
        val requestCall = service.getAPIResponse()

        requestCall.enqueue(object : Callback<CoinbaseResponse> {
            override fun onResponse(
                call: Call<CoinbaseResponse>,
                response: Response<CoinbaseResponse>
            ) {
                if (response.isSuccessful) {
                    (lastResponse as AtomicReference<CoinbaseResponse>).set(response.body()!!)
                }
            }

            override fun onFailure(call: Call<CoinbaseResponse>, t: Throwable) {
                Log.e(TAG, t.toString())
            }
        })
    }

    private interface CoinbaseAPIService {
        @Headers("Content-Type:application/json")
        @GET("v2/prices/BTC-PLN/sell")
        fun getAPIResponse(): Call<CoinbaseResponse>
    }


    data class CoinbaseResponse(
        var data: Data = Data()
    ) {
        data class Data(
            val amount: String = "0",
            val base: String = "0",
            val currency: String = "0"
        )
    }
}