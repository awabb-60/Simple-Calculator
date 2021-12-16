package com.awab.calculator.models

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [HistoryItem::class], version = 1, exportSchema = false)
abstract class HistoryDateBase : RoomDatabase() {

    abstract fun getDao(): HistoryItemsDao

    companion object {

        @Volatile
        private var INSTANCE: HistoryDateBase? = null

        fun getInstance(context: Context): HistoryDateBase {
            synchronized(this) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        HistoryDateBase::class.java,
                        "history_database"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                }
                return INSTANCE as HistoryDateBase
            }
        }

    }
}