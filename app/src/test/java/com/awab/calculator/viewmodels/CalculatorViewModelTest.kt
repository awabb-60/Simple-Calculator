package com.awab.calculator.viewmodels

import android.util.Log
import com.awab.calculator.data.repository.FakeRepository
import com.awab.calculator.utils.*
import com.awab.calculator.utils.calculator_utils.Calculator
import getOrAwaitValueTest
import junit.framework.TestCase
import org.junit.After
import org.junit.Before
import org.junit.Test

class CalculatorViewModelTest{

    private lateinit var viewModel:CalculatorViewModel

    @Before
    fun setUp() {
        viewModel = CalculatorViewModel(FakeRepository(), Calculator())
    }

    @Test
    fun `solve an equation, should give the right answer`(){
        try{
        val equation = "1${ADDITION_SYMBOL}2${SUBTRACTION_SYMBOL}3${MULTIPLICATION_SYMBOL}2"
        viewModel.updateEquation(equation)
        viewModel.makeCalculations()
        val answer = viewModel.answerText.getOrAwaitValueTest()
        val result = answer == "3"
        Log.d("TAG", "solve an equation, should give the right answer: $result")
        assert(result)}
        catch (e:Exception){
            Log.d("", "solve an equation, should give the right answer: ${e.message}")
            assert(false)
        }
    }

//    @Test
//    fun `catching an error from an equation`(){
//        val equation = "1${DIVISION_SYMBOL}0"
//        viewModel.updateEquation(equation)
//        viewModel.makeCalculations()
//        val answer = viewModel.answerText.getOrAwaitValueTest()
//        val result = answer == DIVISION_ERROR
//        Log.d("TAG", "error: $result")
//        assert(result)
//    }
}