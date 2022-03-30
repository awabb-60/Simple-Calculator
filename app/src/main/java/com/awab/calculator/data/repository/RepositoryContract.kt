package com.awab.calculator.data.repository

import androidx.lifecycle.LiveData
import com.awab.calculator.data.local.room.entitys.HistoryItem

interface RepositoryContract {

    fun getAllHistoryItems(): LiveData<List<HistoryItem>>

    suspend fun insert(historyItem: HistoryItem)

    suspend fun clearAll()
}