package com.awab.calculator.utils

import com.awab.calculator.utils.calculator_utils.*
import org.junit.Before
import org.junit.Test

class ParserTest {

    private lateinit var lexer: Lexer
    private lateinit var parser: Parser

    @Before
    fun setup() {
        lexer = Lexer()
        parser = Parser()
    }

    @Test
    fun `finding top level parentheses test 1`() {
        val tokens = lexer.generateTokens("(12+21)-((+0)-0)")
        val tree = parser.findParenthesis(tokens as ArrayList<Token>)

        // parentheses nodes at the start and end has been made
        val result = (tree.first() is ParenthesisNode && tree.last() is ParenthesisNode) && tree.size == 3
        assert(result)
    }

    @Test
    fun `finding top level parentheses test 2`() {
        val tokens = lexer.generateTokens("(()+1")
        val tree = parser.findParenthesis(tokens as ArrayList<Token>)

        // unclosed top level parentheses will get ignored
        val result = tree.isEmpty()
        assert(result)
    }

    @Test
    fun `finding top level parentheses test 3`() {
        val tokens = lexer.generateTokens("(+))")
        val tree = parser.findParenthesis(tokens as ArrayList<Token>)

        // parentheses nodes at the start and end has been made
        val result = (tree.first() is ParenthesisNode && tree.last() is Token) && tree.size == 2
        assert(result)
    }

    @Test
    fun `parentheses nodes with signs 1`() {
        val tokens = lexer.generateTokens("-(9)")
        val tree = parser.findParenthesis(tokens as ArrayList)[0]
        val result = tree is ParenthesisNode && tree.getValue() == -9.0
        assert(result)
    }

    @Test
    fun `parentheses nodes with signs 2`() {
        val tokens = lexer.generateTokens("(-(9))")
        val tree = parser.findParenthesis(tokens as ArrayList).first()
        val result = tree is ParenthesisNode && tree.getValue() == -9.0
        assert(result)
    }

    @Test
    fun `tokens inside parenthesis is correct 1`() {
        val tokens = lexer.generateTokens("(-2+(1-1))+1")
        val tree = parser.findParenthesis(tokens as ArrayList<Token>)

        val firstNode = tree.first()
        if (firstNode is ParenthesisNode && tree.size == 3) {
            val result =
                firstNode.tokens == lexer.generateTokens("-2+(1-1)")
            assert(result)
        } else
            assert(false)
    }

    @Test
    fun `tokens inside parenthesis is correct 2`() {
        val tokens = lexer.generateTokens("1+()")
        val tree = parser.findParenthesis(tokens as ArrayList<Token>)

        val lastNode = tree.last()
        if (lastNode is ParenthesisNode && tree.size == 3) {
            val result =
                lastNode.tokens.isEmpty()
            assert(result)
        } else
            assert(false)
    }

    @Test
    fun `tokens inside parenthesis is correct 3`() {
        val tokens = lexer.generateTokens("3(-2+(1-1)()+1")
        val tree = parser.findParenthesis(tokens as ArrayList<Token>)

        val result = tree.size == 1 && tokens[0].tokenType == TokenType.NUMBER &&
                tokens[0].value == 3.0
        assert(result)
    }

    @Test
    fun `making the number nodes 1`() {
        val tokens = lexer.generateTokens("30+12")
        val tree = parser.makeNumbersNodes(ArrayList<Any>().apply { addAll(tokens) })
        val firstNumber = tree.first()
        val lastNumber = tree.last()

        val result = if (firstNumber is NumberNode && lastNumber is NumberNode) {
            firstNumber.getValue() == 30.0 && lastNumber.getValue() == 12.0
        } else
            false
        assert(result)

    }

    @Test
    fun `making the number nodes 2`() {
        // checking the sign value
        val tokens = lexer.generateTokens("30${MULTIPLICATION_SYMBOL}-12")
        val tree = parser.makeNumbersNodes(ArrayList<Any>().apply { addAll(tokens) })
        val firstNumber = tree.first()
        val lastNumber = tree.last()

        val result = if (firstNumber is NumberNode && lastNumber is NumberNode) {
            firstNumber.getValue() == 30.0 && lastNumber.getValue() == -12.0
        } else
            false
        assert(result)

    }

    @Test
    fun `making the number nodes 3`() {
        // checking the sign value
        val tokens = lexer.generateTokens("-30${MULTIPLICATION_SYMBOL}-12")
        val tree = parser.makeNumbersNodes(ArrayList<Any>().apply { addAll(tokens) })
        val firstNumber = tree.first()
        val lastNumber = tree.last()

        val result = if (firstNumber is NumberNode && lastNumber is NumberNode) {
            firstNumber.getValue() == -30.0 && lastNumber.getValue() == -12.0
        } else
            false
        assert(result)
    }

    /*
    testing the makeNodeFor function in the calculator test
    because the best test for it is finding the value of the tree
    */
}