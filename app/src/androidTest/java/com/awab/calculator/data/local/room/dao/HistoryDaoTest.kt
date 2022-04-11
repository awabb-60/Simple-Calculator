package com.awab.calculator.data.local.room.dao

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.awab.calculator.data.local.room.database.HistoryDataBase
import com.awab.calculator.data.local.room.entitys.HistoryItem
import getOrAwaitValueTest
import junit.framework.TestCase
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class HistoryDaoTest:TestCase() {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private lateinit var database:HistoryDataBase
    private lateinit var dao:HistoryDao


    @Before
    public override fun setUp() {
        database = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(),
            HistoryDataBase::class.java)
            .allowMainThreadQueries()
            .build()

        dao = database.getDao()
    }

    @After
    public override fun tearDown() {
        database.close()
    }

    @Test
    fun insertHistoryItem() = runBlocking{
        val item  = HistoryItem("1+1", "2", id = 1)
        dao.insert(item)

        // livedata run async... not good
        val listOfItem = dao.getAll()

        val result = listOfItem.getOrAwaitValueTest()

        Log.d("TAG", "insertHistoryItem: $result")
        assert(result.contains(item))
    }

    @Test
    fun deleteHistoryItem() = runBlocking{
        val item1 = HistoryItem("1+1", "2")
        val item2  = HistoryItem("2+1", "3")
        val item3 = HistoryItem("3+1", "4")
        dao.insert(item1)
        dao.insert(item2)
        dao.insert(item3)

        dao.clearHistoryItems()

        // livedata run async... not good
        val listOfItem = dao.getAll()
        val result = listOfItem.getOrAwaitValueTest().isEmpty()
        assert(result)
    }
}