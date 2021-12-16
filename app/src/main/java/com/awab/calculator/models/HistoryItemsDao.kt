package com.awab.calculator.models

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface HistoryItemsDao {
    @Insert
    suspend fun insert(item:HistoryItem)

    @Query("SELECT * FROM history_items ORDER BY id")
    fun getAll(): LiveData<List<HistoryItem>>

    @Query("DELETE FROM history_items")
    suspend fun clearHistoryItems()
}