package com.awab.calculator.viewmodels

import android.app.Application
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.awab.calculator.R
import com.awab.calculator.data.HistoryItem
import com.awab.calculator.data.Repository
import com.awab.calculator.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.math.BigDecimal

// TODO: 1/1/2022 order of operations
// templates text

class CalculatorViewModel(application: Application) : AndroidViewModel(application) {
    private val TAG = "CalculatorViewModel"
    private val repository = Repository(application)

    //  the mutable values for the livedata... any edit must happen on this variables
    private val _equationText: MutableLiveData<String> = MutableLiveData<String>("")
    private val _answerText: MutableLiveData<String> = MutableLiveData<String>("")

    val equationText: LiveData<String>
    get() = _equationText

    val answerText: LiveData<String>
    get() = _answerText

    var historyFragmentActive = false

    val historyItems = repository.getAllHistoryItems()

    /**
     * this function will change the value of the equation text
     */
    fun updateEquation(text: String) {
        _equationText.value = text
    }

    /**
     * this function will change the value of the answer text
     */
    fun updateAnswer(answer: String) {
        _answerText.value = answer
    }

    /**
     * here where all the calculator utils starts
     * it will take the equation text and will try to solve it
     * if the equation is solvable, the answer text will get updated, when any
     * error occur... it will show a toast with the message of the error.
     * @param context the context to show the toast message
     * @return true if the calculation was successful, false if any error occur
     */
    fun makeCalculations(context: Context): Boolean {
        //  answer will come as a number if every thing was good
        //  and it will come as the error message when error occur

        val answer = solve(closeParenthesis(_equationText.value!!))

        return try {
            var newAnswer = BigDecimal.valueOf(answer.toDouble()).toString()
            //  formatting some kotlin math stuff
            if (newAnswer == "-0")
                newAnswer = "0"

            _answerText.value = newAnswer

            //  saving the equation
            insertHistory(HistoryItem(equation = _equationText.value!!, answer = answer))
            true
        } catch (e: Exception) {
            _answerText.value = ""
            //  an error occurred... showing the toast
            Toast.makeText(context, answer, Toast.LENGTH_SHORT).show()
            false
        }

    }

    /**
     * this function will take the equation text and close all the open parenthesis then return
     * the new string.
     * @param text the initial equation text
     * @return the equationText with all the open parenthesis closed.
     */
    private fun closeParenthesis(text:String):String {
        var oldValue = text
        while(filterInput(oldValue, RIGHT_PARENTHESIS) != oldValue){
            oldValue = filterInput(oldValue, RIGHT_PARENTHESIS)
            }
        return oldValue
    }

    /**
     * clearing the equation and answer text
     */
    fun clearAll() {
        _equationText.value = ""
        _answerText.value = ""
    }

    /**
     * do delete something from the the equation
     */
    private fun backSpace() {
        if (_equationText.value.isNullOrEmpty())
            return
        //  deleting the word symbols like sin( cos(  and |(
        if (_equationText.value?.last() == LEFT_PARENTHESIS){
            //  dropping the left parenthesis and the word symbols if any
            _equationText.value = _equationText.value?.dropLast(1)?.dropLastWhile {
                it !in "$ADDITION_SYMBOL$SUBTRACTION_SYMBOL$EXPONENT_SYMBOL$MULTIPLICATION_SYMBOL$DIVISION_SYMBOL$LEFT_PARENTHESIS"
            }
            return
        }

        //  normal backspace
        _equationText.value = _equationText.value?.dropLast(1)
    }

    /**
     * save the history item to the database
     * @param item the that will get saved
     */
    private fun insertHistory(item: HistoryItem) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insert(item)
        }
    }

    /**
     * clearing the history items database
     */
    fun clearHistoryItems() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.clearHistoryItems()
        }
    }

    /**
     * all the keyPad buttons will trigger this function
     * @param id the id of the button
     */
    fun buttonClicked(id:Int){
        when (id) {
            R.id.decimalPoint -> {
                type('.')
            }
            R.id.number_0 -> {
                type('0')
            }
            R.id.number_1 -> {
                type('1')
            }
            R.id.number_2 -> {
                type('2')
            }
            R.id.number_3 -> {
                type('3')
            }
            R.id.number_4 -> {
                type('4')
            }
            R.id.number_5 -> {
                type('5')
            }
            R.id.number_6 -> {
                type('6')
            }
            R.id.number_7 -> {
                type('7')
            }
            R.id.number_8 -> {
                type('8')
            }
            R.id.number_9 -> {
                type('9')
            }
            R.id.pi -> {
                type(PI_SYMBOL)
            }
            R.id.e -> {
                type(e_SYMBOL)
            }
            R.id.exponent -> {
                type(EXPONENT_SYMBOL)
            }
            R.id.addition -> {
                type(ADDITION_SYMBOL)
            }
            R.id.subtraction -> {
                type(SUBTRACTION_SYMBOL)
            }
            R.id.multiply -> {
                type(MULTIPLICATION_SYMBOL)
            }
            R.id.division -> {
                type(DIVISION_SYMBOL)
            }
            R.id.squareRoot -> {
                typeSquareRoot()
            }
            R.id.sin -> {
                typeSin()
            }
            R.id.cos -> {
                typeCos()
            }
            R.id.tan -> {
                typeTan()
            }
            R.id.ln -> {
                typeLn()
            }
            R.id.lParenthesis -> {
                type(LEFT_PARENTHESIS)
            }
            R.id.rParenthesis -> {
                type(RIGHT_PARENTHESIS)
            }
            R.id.negativeP -> {
                typeNegativeParenthesis()
            }
            R.id.t1 -> {
                type10ToThePower()
            }
            R.id.t2 -> {
                typePower2()
            }
            R.id.backSpace -> {
                backSpace()
            }
        }
    }

    /**
     * this will try to add the char to the equation text
     * @param char the char tha will get added
     */
    fun type(char: Char) {
        updateEquation(filterInput(_equationText.value.toString(), char))
    }

    /**
     * this is a template for: 10 to the power of, 10^x
     */
    private fun type10ToThePower() {
        if (_equationText.value == null)
            return

        //  if there is any DIGITS at the end of the equation... place a  multiplication symbol
        if (_equationText.value!!.isNotEmpty() && _equationText.value!!.last() in DIGITS)
            type(MULTIPLICATION_SYMBOL)

        //  the template...
        type('1')
        type('0')
        type(EXPONENT_SYMBOL)
        type(LEFT_PARENTHESIS)
    }

    /**
     * this is a template for: x to the power of 2, x^(2)
     */
    private fun typePower2() {
        //  the power 2 template only after a number or right parenthesis
        if (_equationText.value == null || _equationText.value!!.isEmpty() ||
            _equationText.value!!.last() !in "$DIGITS$RIGHT_PARENTHESIS")
            return

        //  the template...
        type(EXPONENT_SYMBOL)
        type(LEFT_PARENTHESIS)
        type('2')
        type(RIGHT_PARENTHESIS)
    }

    /**
     * open parenthesis with a negative at the start, (-
     */
    private fun typeNegativeParenthesis() {
        type(LEFT_PARENTHESIS)
        type(SUBTRACTION_SYMBOL)
    }

    /**
     * type square root symbol to the equation
     */
    private fun typeSquareRoot() {
        type(SQUARE_ROOT_SYMBOL)
        type(LEFT_PARENTHESIS)
    }

    /**
     * type sin symbol to the equation
     */
    private fun typeSin() {
        type(SIN_SYMBOL)
        type('i')
        type('n')
        type(LEFT_PARENTHESIS)
    }

    /**
     * type cos symbol to the equation
     */
    private fun typeCos() {
        type(COS_SYMBOL)
        type('o')
        type('s')
        type(LEFT_PARENTHESIS)
    }

    /**
     * type tan symbol to the equation
     */
    private fun typeTan() {
        type(TAN_SYMBOL)
        type('a')
        type('n')
        type(LEFT_PARENTHESIS)
    }

    /**
     * type ln symbol to the equation
     */
    private fun typeLn() {
        type(LN_SYMBOL)
        type('n')
        type(LEFT_PARENTHESIS)
    }
}