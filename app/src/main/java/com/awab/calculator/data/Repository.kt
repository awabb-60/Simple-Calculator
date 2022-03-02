package com.awab.calculator.data

import android.app.Application
import androidx.lifecycle.LiveData
import com.awab.calculator.data.data_models.HistoryItem

class Repository(application: Application) {
    /**
     * the room database
     */
    private val historyDatabase = HistoryDateBase.getInstance(application)

 /**
 * the room database DAO that will handle the operation on the database
 */
    private val historyItemDao = historyDatabase.getDao()

    /**
    * get all the history items form the database
    * @return LiveData of type List of HistoryItem
    */
    fun getAllHistoryItems(): LiveData<List<HistoryItem>> {
        return historyItemDao.getAll()
    }

    /**
     *  insert this history item to the database
     *  @param historyItem the item that will get inserted
     */

    suspend fun insert(historyItem: HistoryItem) {
        historyItemDao.insert(historyItem)
    }


    /**
     * clear all the data
     */
    suspend fun clearHistoryItems() {
        historyItemDao.clearHistoryItems()
    }
}