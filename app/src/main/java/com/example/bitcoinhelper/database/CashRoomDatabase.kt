package com.example.bitcoinhelper.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.CoroutineScope

@Database(entities = [CashEntity::class], version = 1, exportSchema = false)
abstract class CashRoomDatabase : RoomDatabase() {

    abstract fun cashDao(): CashEntityDao

    companion object {

        @Volatile
        private var INSTANCE: CashRoomDatabase? = null

        fun getDatabase(
            context: Context,
            scope: CoroutineScope //need it only to callback but there is no callback so should be deleted
        ): CashRoomDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CashRoomDatabase::class.java,
                    "cash_database"
                ).build()

                INSTANCE = instance
                return instance
            }
        }
    }
}