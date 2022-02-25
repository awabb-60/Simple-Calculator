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
            "1+1-1${MULTIPLICATION_SYMBOL}1" +
                    "${DIVISION_SYMBOL}1${EXPONENT_SYMBOL}${PI_SYMBOL}"
        )

        val answer = listOf(
            TokenType.NUMBER, TokenType.ADDITION, TokenType.NUMBER, TokenType.SUBTRACT,
            TokenType.NUMBER, TokenType.MULTIPLICATION, TokenType.NUMBER, TokenType.DIVISION, TokenType.NUMBER,
            TokenType.EXPONENT, TokenType.NUMBER
        )

        assert(tokens.map { it.tokenType } == answer)
    }

    @Test
    fun `making the correct tokens 2`() {
        val tokens = lexer.generateTokens(
            "(${SIN_SYMBOL}ni+${COS_SYMBOL}ss${TAN_SYMBOL}1.1${LN_SYMBOL}" +
                    "-${SQUARE_ROOT_SYMBOL}()"
        )

        val answer = listOf(
            TokenType.L_PARENTHESIS,
            TokenType.SIN,
            TokenType.ADDITION,
            TokenType.COS,
            TokenType.TAN,
            TokenType.NUMBER,
            TokenType.LN,
            TokenType.SUBTRACT,
            TokenType.SQUARE_ROOT,
            TokenType.L_PARENTHESIS,
            TokenType.R_PARENTHESIS
        )
        assert(tokens.map { it.tokenType } == answer)
    }

    @Test
    fun `when text is empty`() {
        val result = lexer.generateTokens("").isEmpty()
        assert(result)
    }

    @Test
    fun `when char has token`() {
        val result = lexer.generateToken('S')
        if (result != null) {
            assert(result.tokenType == TokenType.SIN)
        } else
            assert(false)
    }

    @Test
    fun `when char has no token`() {
        val result = lexer.generateToken('a')
        assert(result == null)
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
        var result = true
        lexer.generateTokens("1..1..3").forEachIndexed { idx, it ->
            if (it.value != answer[idx])
                result = false
        }
        assert(result)
    }

    @Test
    fun `negative or positive signs at the start before a number`() {
        val negToken = lexer.generateTokens("-1")[0]
        val posTokens = lexer.generateTokens("+${LN_SYMBOL}")[0]

        val negativeCorrect = negToken.tokenType == TokenType.NUMBER && negToken.sign == -1.0
        val positiveCorrect = posTokens.tokenType == TokenType.LN && posTokens.sign == 1.0

        val result = negativeCorrect == positiveCorrect

        assert(result)
    }

    @Test
    fun `negative signs at the start before a left parenthesis`() {
        val tokens = lexer.generateTokens("-(")

        val answer = listOf(
            Token(TokenType.NUMBER, -1.0),
            Token(TokenType.MULTIPLICATION),
            Token(TokenType.L_PARENTHESIS)
        )
        val result = tokens == answer
        assert(result)
    }

    @Test
    fun `positive signs at the start before a left parenthesis`() {
        val tokens = lexer.generateTokens("+(")
        val answer = listOf(
            Token(TokenType.NUMBER, 1.0),
            Token(TokenType.MULTIPLICATION),
            Token(TokenType.L_PARENTHESIS)
        )
        val result = tokens == answer
        assert(result)
    }

    @Test
    fun `test for negative sing after the start`() {
        var result = true
        for (back in TOKEN_TYPES_BEFORE_SIGNS) {
            for (tokenWithSign in TOKENS_WITH_SIGNS) {
                val text = if (tokenWithSign == TokenType.NUMBER)
                    "${back}-1"
                else "${back}-${tokenWithSign}"

                val tokens = lexer.generateTokens(text)
                // the list that contains the expected tokens types form the lexer
                val typeAnswer = listOf(back, tokenWithSign)
                if (tokens.map { it.tokenType } != typeAnswer) {
                    result = false
                    break
                }

                // testing the signs value
                if (tokens.last().sign != -1.0) {
                    result = false
                    break
                }
            }
        }
        assert(result)
    }

    @Test
    fun `test for positive sing after the start`() {
        var result = true
        for (back in TOKEN_TYPES_BEFORE_SIGNS) {
            for (tokenWithSign in TOKENS_WITH_SIGNS) {
                val text = if (tokenWithSign == TokenType.NUMBER)
                    "${back}+1"
                else "${back}+${tokenWithSign}"

                val tokens = lexer.generateTokens(text)
                // the list that contains the expected tokens types form the lexer
                val typeAnswer = listOf(back, tokenWithSign)
                if (tokens.map { it.tokenType } != typeAnswer) {
                    result = false
                    break
                }

                // testing the signs value
                if (tokens.last().sign != 1.0) {
                    result = false
                    break
                }
            }
        }
        assert(result)
    }
}