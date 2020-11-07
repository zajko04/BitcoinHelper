package com.example.bitcoinhelper.apis

import android.content.Context
import android.util.Log
import com.example.bitcoinhelper.R
import com.google.gson.GsonBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicReference

abstract class API {

    private var timer: Timer? = null
    @Volatile
    protected var lastResponse: Any? = null
    protected var URL: String = ""
    protected var TAG = "API"
    private val scope = CoroutineScope(
        Executors
            .newSingleThreadExecutor()
            .asCoroutineDispatcher()
    )

    companion object {
        protected fun stop() {

        }
    }

    @Synchronized
    fun getCurrentProfit(
        btcQuantity: Double,
        cashIn: Float,
        helpArg: Int,
        context: Context
    ): String {
        val result = getCurrentPrice() * btcQuantity

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
        val result = getCurrentPrice()

        if (result == 0.toDouble()) {
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
        val result = getCurrentPrice().toFloat()

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

    protected open fun getCurrentPrice(): Double {
        return synchronized(lastResponse!!) {
            0.toDouble()
        }
    }


    protected fun stopUpdate() {
        timer?.cancel()
        timer?.purge()
        timer = null
    }

    protected fun runUpdate() {
        scope.launch {
            if(timer == null) timer = Timer()
            timer?.scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    loadResponse()
                }
            }, 0, 3000)
        }
    }

    protected open fun loadResponse() {

    }

    protected object APIServiceBuilder {

        private val okHttp = OkHttpClient.Builder()
        private val gson = GsonBuilder().setLenient().create()
        private val interceptor =
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        private val builder = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttp.build())
            .addConverterFactory(GsonConverterFactory.create(gson))

        fun <T> buildService(serviceType: Class<T>, url: String): T {
            val retrofit = builder.baseUrl(url).build()
            interceptor.level = HttpLoggingInterceptor.Level.BODY
            okHttp.interceptors().add(interceptor)
            return retrofit.create(serviceType)
        }
    }
}