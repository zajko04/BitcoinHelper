package com.example.bitcoinhelper.database

import androidx.lifecycle.LiveData

class CashEntityRepository(
    private val mCashEntityDao: CashEntityDao
) {

    val allCash: LiveData<List<CashEntity>> = mCashEntityDao.getAll()

    suspend fun insert(cashEntity: CashEntity) {
        mCashEntityDao.insert(cashEntity)
    }

    suspend fun delete(cashEntity: CashEntity) {
        mCashEntityDao.delete(cashEntity)
    }
}