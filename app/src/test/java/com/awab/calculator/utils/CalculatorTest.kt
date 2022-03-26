package com.awab.calculator.utils

import com.awab.calculator.utils.calculator_utils.Calculator
import com.awab.calculator.utils.calculator_utils.Lexer
import com.awab.calculator.utils.calculator_utils.TokenType
import org.junit.Before
import org.junit.Test
import kotlin.math.round

class CalculatorTest {

    private lateinit var calculator: Calculator
    private lateinit var lexer: Lexer

    @Before
    fun setup() {
        calculator = Calculator()
        lexer = Lexer()
    }

    /**
     * theses isWorking functions test if the calculator can solve the mathematical equations correctly
     */

    @Test
    fun `is working 1`() {
        try {
            val result = calculator.solve("1+2+11+22").toDouble() == 36.0
            assert(result)
        } catch (e: Exception) {
            assert(false)
        }
    }

    @Test
    fun `is working 2`() {
        // negative answer
        try {
            val results = calculator.solve("1-33").toDouble() == -32.0
            assert(results)
        } catch (e: Exception) {
            assert(false)
        }
    }

    // order of operations
    @Test
    fun `is working 3`() {
        try {
            val results = calculator.solve("1+2${MULTIPLICATION_SYMBOL}2").toDouble() == 5.0
            assert(results)
        } catch (e: Exception) {
            assert(false)
        }
    }

    @Test
    fun `is working 4`() {
        try {
            val results = calculator.solve("1+2${DIVISION_SYMBOL}2").toDouble() == 2.0
            assert(results)
        } catch (e: Exception) {
            assert(false)
        }
    }

    @Test
    fun `is working 5`() {
        try {
            val results = calculator.solve("-0-3${MULTIPLICATION_SYMBOL}(2+2)").toDouble() == -12.0
            assert(results)
        } catch (e: Exception) {
            assert(false)
        }
    }

    @Test
    fun `is working 6`() {
        try {
            val results =
                calculator.solve("(3${EXPONENT_SYMBOL}2+(-2-1)+(1))").toDouble() == 7.0
            assert(results)
        } catch (e: Exception) {
            assert(false)
        }
    }

    @Test
    fun `is working 7`() {
        try {
            val results =
                calculator.solve("1+2${MULTIPLICATION_SYMBOL}2${EXPONENT_SYMBOL}+Sin(2)")
                    .toDouble() == 3.0489709024508
            assert(results)
        } catch (e: Exception) {
            assert(false)
        }
    }

    @Test
    fun `is working 8`() {
        try {
            val results =
                calculator.solve("9${MULTIPLICATION_SYMBOL}-(9)").toDouble() == -81.0
            assert(results)
        } catch (e: Exception) {
            assert(false)
        }
    }

    @Test
    fun `is working 9`() {
        val answer = calculator.solve("9${MULTIPLICATION_SYMBOL}-()")
        try {
            answer.toDouble()
            assert(false)
        } catch (e: Exception) {
            val result = answer == SYNTAX_ERROR
            assert(result)
        }
    }

    @Test
    fun `is working 10`() {
        val answer =
            calculator.solve("-(+1+3${MULTIPLICATION_SYMBOL}(-(|(90))))-.4").toDouble()
        val result = round(answer) == 27.0
        assert(result)
    }

    @Test
    fun `its working`() {
        try {
            val results = calculator.solve(
                "-2-((-9+2)${MULTIPLICATION_SYMBOL}(-1))+" + //-9
                        "3${DIVISION_SYMBOL}(9${EXPONENT_SYMBOL}3+(2))-" +//0.0041039
                        "Sin(-Cos(Tan(-|(Ln(90)-1)-1.)-1.)-1)+.+" +//-0.03489
                        "2${MULTIPLICATION_SYMBOL}-2${MULTIPLICATION_SYMBOL}-(-4)+20 - .1"//3.9
            ).toDouble()
            assert(round(results) == -5.0)
        } catch (e: Exception) {
            assert(false)
        }
    }

    /**
     * testing isSolvable function
     */

    @Test
    fun `no text test`() {
        // when the list is empty it returns syntax error
        val tokens = lexer.generateTokens("")
        try {
            calculator.isSolvable(tokens)
        } catch (e: Exception) {
            e.message?.let {
                assert(it == SYNTAX_ERROR)
            }
        }
    }

    @Test
    fun `no numbers in the text`() {
        val tokens = lexer.generateTokens("-(+Sin(|(-)))")
        try {
            calculator.isSolvable(tokens)
        } catch (e: Exception) {
            e.message?.let {
                assert(it == SYNTAX_ERROR)
            }
        }
    }

    @Test
    fun `must ends with number or ) 1`() {
        val tokens = lexer.generateTokens("-.-")
        try {
            calculator.isSolvable(tokens)
        } catch (e: Exception) {
            e.message?.let {
                assert(it == SYNTAX_ERROR)
            }
        }
    }

    @Test
    fun `must ends with number or ) 2`() {
        val tokens = lexer.generateTokens("(.-.)")
        try {
            calculator.isSolvable(tokens)
            assert(true)
        } catch (e: Exception) {
            assert(false)
        }
    }

    @Test
    fun `must ends with number or ) 3`() {
        val tokens = lexer.generateTokens("(-.)-.")
        try {
            calculator.isSolvable(tokens)
            assert(true)
        } catch (e: Exception) {
            assert(false)
        }
    }

    @Test
    fun `number in a wrong position 1`() {
        for (token in
        listOf(
            TokenType.L_PARENTHESIS, TokenType.SQUARE_ROOT, TokenType.SIN, TokenType.COS,
            TokenType.TAN, TokenType.LN
        ).map { it.toString() }) {
            val answer = calculator.solve("1$token")
            try {
                answer.toDouble()
                assert(false)
            } catch (e: Exception) {
                e.message?.let {
                    assert(answer == SYNTAX_ERROR)
                }
            }
        }
    }

    @Test
    fun `number in a wrong position 2`() {
        val tokens = lexer.generateTokens("(1)1")
        try {
            calculator.isSolvable(tokens)
            assert(false)
        } catch (e: Exception) {
            e.message?.let {
                assert(it == SYNTAX_ERROR)
            }
        }
    }

    @Test
    fun `messy parentheses 1`() {
        val tokens = lexer.generateTokens("(9)-(1")
        try {
            calculator.isSolvable(tokens)
            assert(false)
        } catch (e: Exception) {
            e.message?.let {
                assert(it == SYNTAX_ERROR)
            }
        }
    }

    @Test
    fun `messy parentheses 2`() {
        val tokens = lexer.generateTokens("-((1))-(1))(")
        try {
            calculator.isSolvable(tokens)
            assert(false)
        } catch (e: Exception) {
            e.message?.let {
                assert(it == SYNTAX_ERROR)
            }
        }
    }

    @Test
    fun `no straight division by zero`() {
        val tokens = lexer.generateTokens("1${DIVISION_SYMBOL}0")
        try {
            calculator.isSolvable(tokens)
            assert(false)
        } catch (e: Exception) {
            e.message?.let {
                assert(it == DIVISION_ERROR)
            }
        }
    }

    /**
     * test for the filter input function
     */
    @Test
    fun `filter input case 1`() {
        // cant place × ÷ ex at the start
        val text = ""
        var result = true
        for (opp in operationsWillAnyBefore) {
            if (calculator.filterInput(text, opp).isNotEmpty())
                result = false
        }
        assert(result)
    }

    @Test
    fun `filter input case 2`() {
        // cant place × ÷ ex after (
        val text = "("
        var result = true
        for (opp in operationsWillAnyBefore) {
            if (calculator.filterInput(text, opp) != text)
                result = false
        }
        assert(result)
    }

    @Test
    fun `filter input case 3`() {
        // + must get replace with the new opp
        val text = "0+"
        var result = true
        for (opp in operationsWillAnyBefore) {
            if (calculator.filterInput(text, opp) != "0$opp")
                result = false
        }
        assert(result)
    }

    @Test
    fun `filter input case 4`() {
        // + must get replace with the new opp
        val text = "0+${DIVISION_SYMBOL}-${EXPONENT_SYMBOL}${DIVISION_SYMBOL}"
        var result = true
        for (opp in operationsWillAnyBefore) {
            if (calculator.filterInput(text, opp) != "0$opp")
                result = false
        }
        assert(result)
    }

    @Test
    fun `filter input case 5`() {
// + must get replace with the new opp
        val text = "0+"
        var result = true
        for (opp in listOf('-', '+')) {
            if (calculator.filterInput(text, opp) != "0$opp")
                result = false
        }
        assert(result)
    }

    @Test
    fun `filter input case 6`() {
        // + must get replace with the new opp
        val text = "0${MULTIPLICATION_SYMBOL}+"
        var result = true
        for (opp in listOf('-', '+')) {
            if (calculator.filterInput(text, opp) != text.replace('+', opp))
                result = false
        }
        assert(result)
    }

    @Test
    fun `filter input case 7`() {
        // auto place ×
        var result = true
        for (char in (DIGITS + PI_SYMBOL + e_SYMBOL + RIGHT_PARENTHESIS)) {
            for (auto in autoPlaceMultiplicationBefore) {
                if (calculator.filterInput("$char", auto) != "${char}${MULTIPLICATION_SYMBOL}$auto")
                    result = false
            }
        }
        assert(result)
    }

    @Test
    fun `filter input case 8`() {
        // auto place × before number
        var result = true
        // testing Pi and e and all the digits but not the decimal point
        for (char in "$PI_SYMBOL${e_SYMBOL}${DIGITS.removeRange(DIGITS.lastIndex, DIGITS.lastIndex + 1)}") {
            if (calculator.filterInput(")", char) != ")${MULTIPLICATION_SYMBOL}$char")
                result = false
        }
        assert(result)
    }

    @Test
    fun `filter input case 9`() {
        // decimal point format at the start
        val result = calculator.filterInput("", '.') == "0."
        assert(result)
    }

    @Test
    fun `filter input case 10`() {
        val result = calculator.filterInput("1${DIVISION_SYMBOL}", '.') == "1${DIVISION_SYMBOL}0."
        assert(result)
    }

    @Test
    fun `filter input case 11`() {
        val result = calculator.filterInput("1", '.') == "1."
        assert(result)
    }

    @Test
    fun `filter input case 12`() {
        val result = calculator.filterInput("0.1", '.') == "0.1"
        assert(result)
    }

    @Test
    fun `filter input case 13`() {
        // cant place ) if all the open parenthesis ( are closed
        val result = calculator.filterInput("(1+(1))", ')') == "(1+(1))"
        assert(result)
    }

    @Test
    fun `filter input case 14`() {
        // can only place ) after a number or )
        val result = calculator.filterInput("(1+(1)", ')') == "(1+(1))"
        assert(result)
    }

    @Test
    fun `filter input case 15`() {
        // can only place ) after a number or )
        val result = calculator.filterInput("1+(1", ')') == "1+(1)"
        assert(result)
    }

    @Test
    fun `filter input case 16`() {
        // can only place ) after a number or )
        var result = true
        for (s in SYMBOLS) {
            if (calculator.filterInput("(1${s}", ')') != "(1${s}")
                result = false
        }
        assert(result)
    }
}