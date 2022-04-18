package com.awab.calculator.data.repository

import androidx.lifecycle.LiveData
import com.awab.calculator.data.local.room.dao.HistoryDao
import com.awab.calculator.data.local.room.entitys.HistoryItem

class CalculatorRepository(private val historyDao: HistoryDao):Repository {

    /**
     * get all the history items form the database
     * @return LiveData of type List of HistoryItem
     */
    override fun getAllHistoryItems(): LiveData<List<HistoryItem>> {
        return historyDao.getAll()
    }

    /**
     *  insert this history item to the database
     *  @param historyItem the item that will get inserted
     */

    override suspend fun insert(historyItem: HistoryItem) {
        historyDao.insert(historyItem)
    }

    /**
     * clear all the data
     */
    override suspend fun clearAll() {
        historyDao.clearHistoryItems()
    }
}