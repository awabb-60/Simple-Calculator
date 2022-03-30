package com.awab.calculator.data.local.room.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.awab.calculator.data.local.room.entitys.HistoryItem
import com.awab.calculator.data.local.room.dao.HistoryDao

@Database(entities = [HistoryItem::class], version = 1, exportSchema = false)
abstract class HistoryDataBase : RoomDatabase() {

    /**
     * get the DAO of this room database
     * @return DAO of type HistoryItemsDao
     */
    abstract fun getDao(): HistoryDao

    companion object{const val DATABASE_NAME = "history_db"}

}