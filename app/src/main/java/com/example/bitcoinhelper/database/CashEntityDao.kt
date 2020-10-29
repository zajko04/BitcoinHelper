package com.example.bitcoinhelper.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.bitcoinhelper.database.CashEntity

@Dao
interface CashEntityDao {

    @Query("SELECT * FROM cash_entity")
    fun getAll(): LiveData<List<CashEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(cashEntity: CashEntity)

    @Delete
    fun delete(cashEntity: CashEntity)
}