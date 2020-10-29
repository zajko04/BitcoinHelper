package com.example.bitcoinhelper.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cash_entity")
data class CashEntity(

    @ColumnInfo(name = "name")
    var name: String,

    @ColumnInfo(name = "cashIn")
    var cashIn: Float,

    @ColumnInfo(name = "btcExchange")
    var btcExchange: Float,

    @ColumnInfo(name = "btcQuantity")
    var btcQuantity: Double
) {
    @PrimaryKey(autoGenerate = true)
    var uid: Int = 0
}