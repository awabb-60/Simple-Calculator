package com.awab.calculator.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.awab.calculator.data.local.room.entitys.HistoryItem

class FakeRepository():Repository {

    private val historyItemsDatabase = mutableListOf<HistoryItem>()

    private val _historyItems = MutableLiveData<List<HistoryItem>>(historyItemsDatabase)
    private val historyItems:LiveData<List<HistoryItem>>
    get() = _historyItems

    override fun getAllHistoryItems(): LiveData<List<HistoryItem>> {
        return historyItems
    }

    override suspend fun insert(historyItem: HistoryItem) {
        historyItemsDatabase.add(historyItem)
    }

    override suspend fun clearAll() {
        historyItemsDatabase.clear()
    }
}