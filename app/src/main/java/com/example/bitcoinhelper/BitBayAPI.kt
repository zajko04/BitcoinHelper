package com.example.bitcoinhelper

import android.content.Context
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicReference

class BitBayAPI {

    private var timer: Timer? = null
    @Volatile
    private var mLastTickerResponse = AtomicReference(TickerResponse())

    companion object {
        private const val TAG = "BitBayAPI"
        @Volatile
        private var INSTANCE: BitBayAPI? = null

        fun getInstance() : BitBayAPI {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = BitBayAPI()
                instance.runUpdate()
                INSTANCE = instance
                return instance
            }
        }

        fun stop() {
            INSTANCE?.stopUpdate()
            INSTANCE = null
        }
    }

    @Synchronized
    fun getCurrentProfit(
        btcQuantity: Double,
        cashIn: Float,
        helpArg: Int,
        context: Context
    ): String {
        val result = synchronized(mLastTickerResponse) {
            mLastTickerResponse.get().ask * btcQuantity
        }

        val temp = context.getString(R.string.currentPrice)

        if (result == 0.toDouble()) {
            val loading = context.getString(R.string.loading)
            return when (helpArg) {
                0 -> "$temp $loading"
                1 -> "$temp $loading."
                2 -> "$temp $loading.."
                3 -> "$temp $loading..."
                else -> throw IllegalArgumentException()
            }
        }

        return "$temp %.2f".format(result - cashIn) + context.getString(R.string.currency)
    }

    @Synchronized
    fun getCurrentSellPrice(
        helpArg: Int,
        context: Context,
        btcQuantity: Double
    ): String {
        val result = synchronized(mLastTickerResponse) {
            mLastTickerResponse.get().ask.toFloat()
        }

        if (result == 0F) {
            val loading = context.getString(R.string.loading)
            return when (helpArg) {
                0 -> loading
                1 -> "$loading."
                2 -> "$loading.."
                3 -> "$loading..."
                else -> throw IllegalArgumentException()
            }
        }

        return "Ile jest warte obecnie: %.2f".format(result * btcQuantity) + context.getString(R.string.currency)
    }

    @Synchronized
    fun getCurrentSellPrice(
        helpArg: Int,
        context: Context
    ): String {
        val result = synchronized(mLastTickerResponse) {
            mLastTickerResponse.get().ask.toFloat()
        }

        if (result == 0F) {
            val loading = context.getString(R.string.loading)
            return when (helpArg) {
                0 -> loading
                1 -> "$loading."
                2 -> "$loading.."
                3 -> "$loading..."
                else -> throw IllegalArgumentException()
            }
        }

        return "%.2f".format(result) + context.getString(R.string.currency)
    }

    private fun runUpdate() {
        val scope = CoroutineScope(Executors.newSingleThreadExecutor().asCoroutineDispatcher())
        scope.launch {
            if(timer == null) timer = Timer()
            timer?.scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    loadTicker()
                }
            }, 0, 3000)
        }
    }

    private fun stopUpdate() {
        timer?.cancel()
        timer?.purge()
        timer = null
    }

    private fun loadTicker() {
        val service = BitBayAPIServiceBuilder.buildService(BitBayAPIService::class.java)
        val requestCall = service.getTickerResponse()

        requestCall.enqueue(object : Callback<TickerResponse> {
            override fun onResponse(
                call: Call<TickerResponse>,
                response: Response<TickerResponse>
            ) {
                if (response.isSuccessful) {
                    val tickerResponse = response.body()!!
                    mLastTickerResponse.set(tickerResponse)
                }
            }

            override fun onFailure(call: Call<TickerResponse>, t: Throwable) {
                Log.e(TAG, t.toString())
            }
        })
    }

    private interface BitBayAPIService {

        @GET("ticker.json")
        fun getTickerResponse(): Call<TickerResponse>
    }

    private object BitBayAPIServiceBuilder {

        private const val URL = "https://bitbay.net/API/Public/BTCPLN/"
        private val okHttp = OkHttpClient.Builder()
        private val builder = Retrofit.Builder().baseUrl(URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttp.build())
        private val retrofit = builder.build()

        fun <T> buildService(serviceType: Class<T>): T {
            return retrofit.create(serviceType)
        }
    }

    data class TickerResponse(
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