package com.awab.calculator.data.local.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.awab.calculator.data.local.room.entitys.HistoryItem

@Dao
interface HistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: HistoryItem)

    @Query("SELECT * FROM history_items")
    fun getAll(): LiveData<List<HistoryItem>>

    @Query("DELETE FROM history_items")
    suspend fun clearHistoryItems()
}