package com.awab.calculator.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.awab.calculator.data.data_models.HistoryItem

@Database(entities = [HistoryItem::class], version = 1, exportSchema = false)
abstract class HistoryDataBase : RoomDatabase() {

    /**
     * get the DAO of this room database
     * @return DAO of type HistoryItemsDao
     */
    abstract fun getDao(): HistoryItemsDao

    companion object{const val DATABASE_NAME = "history_db"}

}