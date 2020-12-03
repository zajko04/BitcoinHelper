package com.example.bitcoinhelper.mainscreen

import android.app.Application
import androidx.lifecycle.*
import com.example.bitcoinhelper.R
import com.example.bitcoinhelper.apis.BitcoinInfoAPIWrapper
import com.example.bitcoinhelper.database.CashEntity
import com.example.bitcoinhelper.database.CashEntityRepository
import com.example.bitcoinhelper.database.CashRoomDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class CashEntityViewModel(application: Application) : AndroidViewModel(application) {

    private val mRepository: CashEntityRepository
    private var mTimer: Timer? = Timer()
    private val loading = (getApplication() as Application).getString(R.string.loading)
    val allCash: LiveData<List<CashEntity>>
    val allProfit = MutableLiveData<List<String>>()

    init {
        val cashDao = CashRoomDatabase.getDatabase(application, viewModelScope).cashDao()
        mRepository = CashEntityRepository(cashDao)
        allCash = mRepository.allCash

        val lifeCycleOwner = MyLifeCycleOwner()
        allCash.observe(lifeCycleOwner, { cash ->
            cash?.let {
                viewModelScope.launch {
                    refreshCurrentPrice(it)
                }
            }
        })
    }

    private fun refreshCurrentPrice(items: List<CashEntity>) =
        viewModelScope.launch(Dispatchers.IO) {
            var helpArg = 0
            mTimer?.scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    val profitList = ArrayList<String>()
                    for (item in items) {
                        val result = BitcoinInfoAPIWrapper.getInstance().getCurrentProfit(
                            item.btcQuantity,
                            item.cashIn,
                            helpArg,
                            getApplication()
                        )

                        if (result.contains(loading)) {
                            helpArg = when (helpArg) {
                                3 -> 0
                                else -> helpArg + 1
                            }
                        }

                        profitList.add(result)
                    }
                    allProfit.postValue(profitList)
                }
            }, 0, 1000)
        }

    fun stopTimer() {
        mTimer?.cancel()
        mTimer?.purge()
        mTimer = null
    }

    fun insert(cash: CashEntity) = viewModelScope.launch(Dispatchers.IO) {
        mRepository.insert(cash)
    }

    fun delete(cash: CashEntity) = viewModelScope.launch(Dispatchers.IO) {
        mRepository.delete(cash)
    }

    private class MyLifeCycleOwner : LifecycleOwner {

        private val lifecycleRegistry: LifecycleRegistry = LifecycleRegistry(this)

        init {
            lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
        }

        override fun getLifecycle(): Lifecycle = lifecycleRegistry
    }
}