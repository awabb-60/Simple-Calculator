package com.awab.calculator.utils

import org.junit.Before
import org.junit.Test

class LexerTest {

    private lateinit var lexer: Lexer

    @Before
    fun setup() {
        lexer = Lexer()
    }

    @Test
    fun `making the correct tokens 1`() {
        val tokens = lexer.generateTokens(
            "1+1-1" +
                    "${MULTIPLICATION_SYMBOL}1${DIVISION_SYMBOL}1" +
                    "${EXPONENT_SYMBOL}${PI_SYMBOL}"
        )

        val answer = listOf(
            TokenType.NUMBER, TokenType.ADDITION, TokenType.NUMBER, TokenType.SUBTRACT, TokenType.NUMBER,
            TokenType.MULTIPLICATION, TokenType.NUMBER, TokenType.DIVISION, TokenType.NUMBER,
            TokenType.EXPONENT, TokenType.NUMBER
        )
        val result = tokens.map { it.tokenType } == answer
        assert(result)
    }

    @Test
    fun `making the correct tokens 2`() {
        val tokens = lexer.generateTokens(
            "(${SIN_SYMBOL}ni+${COS_SYMBOL}ss" +
                    "${TAN_SYMBOL}1.1${LN_SYMBOL}" +
                    "-${SQUARE_ROOT_SYMBOL}()"
        )
        val answer = listOf(
            TokenType.L_PARENTHESIS, TokenType.SIN, TokenType.ADDITION, TokenType.COS,
            TokenType.TAN, TokenType.NUMBER, TokenType.LN,
            TokenType.SUBTRACT, TokenType.SQUARE_ROOT, TokenType.L_PARENTHESIS, TokenType.R_PARENTHESIS
        )
        val result = tokens.map { it.tokenType } == answer
        assert(result)
    }

    @Test
    fun `making only the available tokens`() {
        val result = lexer.generateTokens("XD  >_<  :/  :O").isEmpty()
        assert(result)
    }

    @Test
    fun `when text is empty`() {
        val result = lexer.generateTokens("").isEmpty()
        assert(result)
    }

    @Test
    fun `when char has token`() {
        val result = lexer.generateToken('S')?.tokenType == TokenType.SIN
        assert(result)
    }

    @Test
    fun `when char has no token`() {
        val result = lexer.generateToken('a') == null
        assert(result)
    }

    @Test
    fun `numbers with decimal point 1`() {
        val answer = listOf(TokenType.NUMBER, TokenType.NUMBER)
        val result = lexer.generateTokens(".1.").map { it.tokenType } == answer
        assert(result)
    }

    @Test
    fun `numbers with decimal point 2-1`() {
        // format decimal function
        val result = lexer.generateTokens("1.")[0].value == 1.0
        assert(result)
    }

    @Test
    fun `numbers with decimal point 2-2`() {
        // format decimal function
        val result = lexer.generateTokens(".1")[0].value == 0.1
        assert(result)
    }

    @Test
    fun `numbers with decimal point 2-3`() {
        // format decimal function
        val result = lexer.generateTokens("..").map { it.value } == listOf(0.0, 0.0)
        assert(result)
    }

    @Test
    fun `numbers with decimal point 3`() {
        // multiple inline decimal point
        val answer = listOf(1.0, 0.1, 0.0, 0.3)
        val result = lexer.generateTokens("1..1..3").map { it.value } == answer
        assert(result)
    }

    @Test
    fun `negative sign at the start 1`() {
        val result = lexer.generateTokens("-")[0].tokenType == TokenType.SUBTRACT
        assert(result)
    }

    @Test
    fun `positive sign at the start 1`() {
        val result = lexer.generateTokens("+")[0].tokenType == TokenType.ADDITION
        assert(result)
    }

    @Test
    fun `negative sign at the start 2`() {
        val tokens = lexer.generateTokens("(-)")
        val answer = listOf(TokenType.L_PARENTHESIS, TokenType.SUBTRACT, TokenType.R_PARENTHESIS)
        val result = tokens.map { it.tokenType } == answer
        assert(result)
    }

    @Test
    fun `positive sign at the start 2`() {
        val tokens = lexer.generateTokens("(+)")
        val answer = listOf(TokenType.L_PARENTHESIS, TokenType.ADDITION, TokenType.R_PARENTHESIS)
        val result = tokens.map { it.tokenType } == answer
        assert(result)
    }

    @Test
    fun `negative signs at the start before a left parenthesis`() {
        val tokens = lexer.generateTokens("-(")
        val result = tokens.size == 1 && tokens.first().tokenType == TokenType.L_PARENTHESIS &&
                tokens.first().sign == -1.0
        assert(result)
    }

    @Test
    fun `positive signs at the start before a left parenthesis`() {
        val tokens = lexer.generateTokens("+(")
        val result = tokens.size == 1 && tokens.first().tokenType == TokenType.L_PARENTHESIS &&
                tokens.first().sign == 1.0
        assert(result)
    }

    @Test
    fun `test for negative sing in the start`() {
        var result = true
        for (tokenWithSign in TOKENS_WITH_SIGNS) {
            val text = if (tokenWithSign == TokenType.NUMBER)
                "$-1"
            else "-${tokenWithSign}"

            val tokens = lexer.generateTokens(text)
            // the list that contains the expected tokens types form the lexer

            // testing the token type and value
            if (tokens.size != 1 ||
                tokens.first().tokenType != tokenWithSign ||
                tokens.first().sign != -1.0
            ) {
                result = false
                break
            }
        }
        assert(result)
    }

    @Test
    fun `test for negative sing after the start`() {
        for (beforeToken in TOKEN_TYPES_BEFORE_SIGNS) {
            for (tokenWithSign in TOKENS_WITH_SIGNS) {
                val text = if (tokenWithSign == TokenType.NUMBER)
                    "${beforeToken}-1"
                else "${beforeToken}-${tokenWithSign}"

                val tokens = lexer.generateTokens(text)
                // the list that contains the expected tokens types form the lexer
                val typeAnswer = listOf(beforeToken, tokenWithSign)
                if (tokens.map { it.tokenType } != typeAnswer ||
                    tokens.last().sign != -1.0) {
                    assert(false)
                    return
                }
            }
        }
        assert(true)
    }

    @Test
    fun `test for positive sing after the start`() {
        for (back in TOKEN_TYPES_BEFORE_SIGNS) {
            for (tokenWithSign in TOKENS_WITH_SIGNS) {
                val text = if (tokenWithSign == TokenType.NUMBER)
                    "${back}+1"
                else "${back}+${tokenWithSign}"

                val tokens = lexer.generateTokens(text)
                // the list that contains the expected tokens types form the lexer
                val typeAnswer = listOf(back, tokenWithSign)
                if (tokens.map { it.tokenType } != typeAnswer ||
                    tokens.last().sign != 1.0) {
                    assert(false)
                    return
                }
            }
        }
        assert(true)
    }

    @Test
    fun `test for signs in a row 1`() {
        val tokens = lexer.generateTokens("-0-4")
        val result =
            tokens.size == 3 &&
                    tokens.first().tokenType == TokenType.NUMBER && tokens.first().sign == -1.0 &&
                    tokens.last().tokenType == TokenType.NUMBER && tokens.last().sign == 1.0
        assert(result)
    }

    @Test
    fun `test for signs in a row 2`() {
        val tokens = lexer.generateTokens("-(-4)${MULTIPLICATION_SYMBOL}-2")
        val result =
            tokens.size == 5 &&
                    tokens[0].tokenType == TokenType.L_PARENTHESIS && tokens[0].sign == -1.0 &&
                    tokens[1].tokenType == TokenType.NUMBER && tokens[1].sign == -1.0 && tokens[1].value == 4.0 &&
                    tokens[4].tokenType == TokenType.NUMBER && tokens[4].sign == -1.0 && tokens[4].value == 2.0
        assert(result)
    }

    @Test
    fun `test for signs in a row 3`() {
        val tokens = lexer.generateTokens("1${DIVISION_SYMBOL}-(-4)${MULTIPLICATION_SYMBOL}+")
        val result =
            tokens.size == 7 &&
                    tokens[2].tokenType == TokenType.L_PARENTHESIS && tokens[2].sign == -1.0 &&
                    tokens[3].tokenType == TokenType.NUMBER && tokens[3].sign == -1.0 && tokens[3].value == 4.0 &&
                    tokens[6].tokenType == TokenType.ADDITION
        assert(result)
    }
}