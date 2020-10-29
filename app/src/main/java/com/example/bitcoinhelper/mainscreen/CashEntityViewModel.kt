package com.example.bitcoinhelper.mainscreen

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.bitcoinhelper.database.CashEntity
import com.example.bitcoinhelper.database.CashEntityRepository
import com.example.bitcoinhelper.database.CashRoomDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CashEntityViewModel(application: Application) : AndroidViewModel(application){

    private val mRepository: CashEntityRepository
    val allCash: LiveData<List<CashEntity>>

    init {
        val cashDao = CashRoomDatabase.getDatabase(application, viewModelScope).cashDao()
        mRepository = CashEntityRepository(cashDao)
        allCash = mRepository.allCash
    }

    fun insert(cash: CashEntity) = viewModelScope.launch(Dispatchers.IO) {
        mRepository.insert(cash)
    }

    fun delete(cash: CashEntity) = viewModelScope.launch(Dispatchers.IO) {
        mRepository.delete(cash)
    }
}