package com.awab.calculator.viewmodels

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.awab.calculator.R
import com.awab.calculator.data.data_models.ThemeModel
import com.awab.calculator.data.local.room.entitys.HistoryItem
import com.awab.calculator.data.repository.Repository
import com.awab.calculator.utils.*
import com.awab.calculator.utils.calculator_utils.Calculator
import com.awab.calculator.utils.calculator_utils.Lexer
import com.awab.calculator.utils.calculator_utils.TokenType
import com.awab.calculator.view.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.math.RoundingMode
import java.text.NumberFormat
import java.util.*
import javax.inject.Inject

// TODO: 1/1/2022 order of operations

@HiltViewModel
class CalculatorViewModel
@Inject constructor(private val repository: Repository, private val calculator: Calculator) : ViewModel() {

    //  the mutable values for the livedata... any edit must happen on this variables
    private val _equationText: MutableLiveData<String> = MutableLiveData<String>("")
    private val _answerText: MutableLiveData<String> = MutableLiveData<String>("")
    private val _cursorPosition: MutableLiveData<Int> = MutableLiveData<Int>(0)
    private val _errorMessage: MutableLiveData<String?> = MutableLiveData<String?>(null)

    val equationText: LiveData<String>
        get() = _equationText

    val answerText: LiveData<String>
        get() = _answerText

    val cursorPosition: LiveData<Int>
        get() = _cursorPosition

    val errorMessage: LiveData<String?>
        get() = _errorMessage

    /**
     * the cursor positions when the user has clicked a button
     * -> it only used in the type function
     * -> I make it global so i don't have to pass it to every type() call.
     */
    private var currentCursorPos = 0

    var historyFragmentActive = false

    val historyItems = repository.getAllHistoryItems()

    /**
     * this function will change the value of the equation text
     * @param beforeText the text before the cursor
     * @param afterText the text after the cursor
     */
    fun updateEquation(beforeText: String, afterText: String = "") {
        // the cursor will be after the new text always
        // if any change happened to the before text the cursor will after that change
        val newCursorPos = beforeText.length
        _equationText.value = beforeText + afterText
        _cursorPosition.value = newCursorPos

        // updating the current pos
        // this is for the templates that call type() multiple times the current pos has to be updated
        currentCursorPos = newCursorPos
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
     * @return true if the calculation was successful, false if any error occur
     */
    fun makeCalculations(): Boolean {
        //  answer will come as a number if every thing was good
        //  and it will come as the error message when error occur

        return try {
            /* checking if the answer is a number or an error message buy calling toBigDecimal()
             if the answer is an error message it will catch the error and toast the message
              */
            val answer = calculator.solve(closeParenthesis(_equationText.value!!))

            val answerString = adjustAnswer(answer)
            _answerText.value = answerString
            //  saving the equation
            insertHistory(HistoryItem(equation = _equationText.value!!, answer = _answerText.value!!))
            true
        } catch (e: Exception) {
            //  an error occurred... showing a toast

            val errorMessage = e.message

            _answerText.value = ""

            // when the answer is too big it will come as Infinity
            // showing a better message
            _errorMessage.value = if (errorMessage == Double.POSITIVE_INFINITY.toString()) {
                NUMBER_TOO_BIG_ERROR
            } else // the normal error message
                errorMessage

            _errorMessage.postValue(null)
            false
        }
    }

    private fun showAnswerPreview() {
        _answerText.value = try {
            val answer = calculator.solve(_equationText.value!!)
            adjustAnswer(answer)
        } catch (e: Exception) {
            ""
        }
    }

    /**
     * this function will take a number and beautify it looks, make it more readable and
     * displayed in a nice way
     * @param answer the number that will get beautify
     * @return beautiful number as String
     */
    private fun adjustAnswer(answer: Double): String {
        val nf = NumberFormat.getInstance(Locale.US)
        nf.maximumFractionDigits = 10
        return nf.format(answer)
    }

    /**
     * this function will take the equation text and close all the open parenthesis then return
     * the new string.
     * @param text the initial equation text
     * @return the equationText with all the open parenthesis closed.
     */
    private fun closeParenthesis(text: String): String {
        var oldValue = text
        while (calculator.filterInput(oldValue, RIGHT_PARENTHESIS) != oldValue) {
            oldValue = calculator.filterInput(oldValue, RIGHT_PARENTHESIS)
        }
        return oldValue
    }

    /**
     * clearing the equation and answer text
     */
    fun clearAll() {
        _equationText.value = ""
        _answerText.value = ""
        _cursorPosition.value = 0
    }

    /**
     * do delete something from the the equation
     */
    private fun backSpace() {
        // applying the backspace to the text before the cursor
        var textBeforeCursor = _equationText.value?.removeRange(currentCursorPos, _equationText.value?.length!!)
        val textAfterCursor = _equationText.value?.removeRange(0, currentCursorPos)

        if (textBeforeCursor.isNullOrEmpty() || textAfterCursor == null)
            return

        //  deleting the word symbols like sin( cos(  and |(
        textBeforeCursor = if (textBeforeCursor.last() == LEFT_PARENTHESIS) {
            //  dropping the left parenthesis and the word symbols if any
            textBeforeCursor.dropLast(1).dropLastWhile {
                it !in "$ADDITION_SYMBOL$SUBTRACTION_SYMBOL$EXPONENT_SYMBOL$MULTIPLICATION_SYMBOL$DIVISION_SYMBOL$LEFT_PARENTHESIS"
            }
        } else //  normal backspace
            textBeforeCursor.dropLast(1)

        updateEquation(textBeforeCursor, textAfterCursor)
    }

    /**
     * save the history item to the database
     * @param item the that will get saved
     */
    private fun insertHistory(item: HistoryItem) {
        CoroutineScope(Dispatchers.IO).launch {
            repository.insert(item)
        }
    }

    /**
     * clearing the history items database
     */
    fun clearHistoryItems() {
        CoroutineScope(Dispatchers.IO).launch {
            repository.clearAll()
        }
    }

    /**
     * all the keyPad buttons will trigger this function
     * @param id the id of the button
     */
    fun buttonClicked(id: Int, cursorStartPos: Int, cursorEndPos: Int) {
//        the selection has a range
        if (cursorEndPos != cursorStartPos) {
            _equationText.value = _equationText.value?.removeRange(cursorStartPos, cursorEndPos)
            if (id == R.id.backSpace)
                return
        }
        // so all the type functions calls us it
        currentCursorPos = cursorStartPos
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
                typeEndWithParenthesis(SQUARE_ROOT_SYMBOL.toString())
            }
            R.id.sin -> {
                typeEndWithParenthesis(TokenType.SIN.toString())
            }
            R.id.cos -> {
                typeEndWithParenthesis(TokenType.COS.toString())
            }
            R.id.tan -> {
                typeEndWithParenthesis(TokenType.TAN.toString())
            }
            R.id.ln -> {
                typeEndWithParenthesis(TokenType.LN.toString())
            }
            R.id.parenthesis -> {
                typeParenthesis()
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

        showAnswerPreview()
    }

    /**
     * this will try to add the char to the equation text
     * @param char the char tha will get added
     */
    fun type(char: Char) {
        val textBeforeCursor = _equationText.value?.removeRange(currentCursorPos, _equationText.value?.length!!)
        val textAfterCursor = _equationText.value?.removeRange(0, currentCursorPos)

        // only applying the text filter to the text before the cursor
        val newText = textBeforeCursor?.let { calculator.filterInput(it, char) }

        if (newText != null && textAfterCursor != null) {
            updateEquation(newText, textAfterCursor)
        }
    }

    /**
     * this is a template for: 10 to the power of x, 10^x
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
            _equationText.value!!.last() !in "$DIGITS$RIGHT_PARENTHESIS"
        )
            return
        //  the template...
        type(EXPONENT_SYMBOL)
        type(LEFT_PARENTHESIS)
        type('2')
        type(RIGHT_PARENTHESIS)
    }

    private fun typeParenthesis() {
        if (calculator.filterInput(_equationText.value!!, ')') == _equationText.value + ')')
            type(')')
        else
            type('(')
    }

    /**
     * open parenthesis with a negative at the start, (-
     */
    private fun typeNegativeParenthesis() {
        type(LEFT_PARENTHESIS)
        type(SUBTRACTION_SYMBOL)
    }

    /**
     * type the symbols that ends with an left parentheses e.g Sin(
     * @param text the text before the left parentheses
     */
    private fun typeEndWithParenthesis(text: String) {
        text.forEach {
            type(it)
        }
        type(LEFT_PARENTHESIS)
    }

    /**
     * put the answer number
     */
    fun answerClicked() {
        val readyAnswerText = formatAnswer(_answerText.value!!)
        updateEquation(readyAnswerText)
    }

    /**
     * format the answer text to a solvable equation
     * @param answerString the answer text
     * @return solvable answer text
     */
    private fun formatAnswer(answerString: String): String {
        //  E is a shortcut for 10^(x)
        if (answerString.contains('E')) {
            val s = answerString.replace(
                "E",
                "${MULTIPLICATION_SYMBOL}10$EXPONENT_SYMBOL$LEFT_PARENTHESIS"
            )
            return "$s$RIGHT_PARENTHESIS"
            // removing the decimal point at the end of the text
        } else if (answerString.endsWith(".0"))
            return answerString.removeSuffix(".0")
        return answerString
    }

    /**
     * this make sure the cursor is in a place that doesn't make a trouble
     * like: Si|n(
     */
    fun refactorCursorPosition(position: Int) {
        var newPosition = position
        val lexer = Lexer()
        var currentToken = lexer.generateToken(_equationText.value?.get(newPosition)!!)

        // while the cursor in a wrong position
        while (currentToken == null || currentToken.tokenType in cursorCantBeAfter) {
            newPosition++
            currentToken = lexer.generateToken(_equationText.value?.get(newPosition)!!)
        }
        // because the position of the cursor is the position of the char before it
        newPosition++
        _cursorPosition.value = newPosition
    }


    fun getSavedDarkModeState(context: Context): Boolean {
        val sp = context.getSharedPreferences(SETTINGS_SHARED_PREFERENCES, AppCompatActivity.MODE_PRIVATE)
        return sp.getBoolean(CURRENT_DARK_MODE_STATE, DEFAULT_DARK_MODE_STATE)
    }

    fun getSavedTheme(context: Context): ThemeModel {
        val sp = context.getSharedPreferences(SETTINGS_SHARED_PREFERENCES, AppCompatActivity.MODE_PRIVATE)

        // getting the save theme number or the default option
        val currentThemeNumber = sp.getInt(CURRENT_THEME_NUMBER, DEFAULT_THEME_NUMBER)

        // return the saved theme
        return AVAILABLE_THEME_COLORS.find { it.themeNumber == currentThemeNumber }
            ?: AVAILABLE_THEME_COLORS[DEFAULT_THEME_NUMBER]
    }
}