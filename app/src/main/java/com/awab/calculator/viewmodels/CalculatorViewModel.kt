package com.awab.calculator.viewmodels

import android.app.Application
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.awab.calculator.models.HistoryDateBase
import com.awab.calculator.models.HistoryItem
import com.awab.calculator.utils.solve
import kotlinx.coroutines.launch


class CalculatorViewModel(application: Application) : AndroidViewModel(application) {
    val equationText: MutableLiveData<String> = MutableLiveData("")
    val answerText: MutableLiveData<String> = MutableLiveData("")
    private val historyDatabase = HistoryDateBase.getInstance(application)
    private val historyItemDao = historyDatabase.getDao()
    val historyItems = historyItemDao.getAll()


    fun updateEquation(text: String) {
        equationText.value = text
    }

    fun updateAnswer(answer: String) {
        answerText.value = answer
    }

    fun makeCalculations(context: Context): Boolean {
//        answer will come as a number if every thing was good
//        and it will come as the error message when error occur
        val answer = solve(equationText.value!!)
        return try {
            answer.toDouble()
            answerText.value = answer
            insertHistory(HistoryItem(equation = equationText.value!!,answer = answer))
            true
        } catch (e: Exception) {
            answerText.value = ""
            Toast.makeText(context, answer, Toast.LENGTH_SHORT).show()
            false
        }
    }

    fun clearAll() {
        equationText.value = ""
        answerText.value = ""
    }

    fun backSpace() {
        equationText.value = equationText.value?.dropLast(1)
    }

    private fun insertHistory(item: HistoryItem) {
        viewModelScope.launch {
            historyItemDao.insert(item)
        }
    }

    fun clearHistoryItems(){
        viewModelScope.launch {
            historyItemDao.clearHistoryItems()
        }
    }
}