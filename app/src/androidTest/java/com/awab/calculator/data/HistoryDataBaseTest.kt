package com.awab.calculator.data

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.awab.calculator.data.data_models.HistoryItem
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
class HistoryDataBaseTest {

    private lateinit var database: HistoryDataBase
    private lateinit var databaseDAO: HistoryItemsDao

    @Before
    fun setup(){
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            HistoryDataBase::class.java
        )
            .allowMainThreadQueries()
            .build()

        databaseDAO = database.getDao()
    }

    @After
    fun teardown(){
        database.close()
    }

    @Test
    fun insertHistoryItemTest() = runBlocking{
        val item = HistoryItem("1+1", "2")
        databaseDAO.insert(item)
        val list = databaseDAO.getAll().value
        assert(list?.contains(item)?:false)
    }
}














