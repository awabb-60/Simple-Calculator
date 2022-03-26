package com.awab.calculator.utils

import com.awab.calculator.utils.calculator_utils.*
import org.junit.Test
import kotlin.math.*

/**
 * this class test the nodes functionality
 * -evaluation, impossible evaluation (errors)
 */
class NodeTest{

    private val testNumber1 = 10.0
    private val testNumber2 = 20.0
    private val testNumber3 = 30.0
    private val testNumber4 = 45.0
    private val testNumber5 = 120.0
    // nodes evaluation

    @Test
    fun `number node value`(){
        val results = NumberNode(testNumber1).getValue()  == testNumber1
        assert(results)
    }

    @Test
    fun `addition node value`(){
        val numberNode1 = NumberNode(testNumber1)
        val numberNode2 = NumberNode(testNumber2)
        val results = AdditionNode(
            numberNode1,
            numberNode2
        ).getValue()  == (testNumber2 + testNumber1)
        assert(results)
    }

    /**
     * subtract small number from big number
     */
    @Test
    fun `subtraction node value`(){
        val numberNode1 = NumberNode(testNumber1)
        val numberNode2 = NumberNode(testNumber2)
        val results = SubtractionNode(
            numberNode2,
            numberNode1
        ).getValue() == (testNumber2 - testNumber1)
        assert(results)
    }

    /**
     * subtract big number from small number
     */
    @Test
    fun `subtraction node value 2`(){
        val numberNode1 = NumberNode(testNumber1)
        val numberNode2 = NumberNode(testNumber2)
        val results = SubtractionNode(
            numberNode1,
            numberNode2
        ).getValue() == (testNumber1 - testNumber2)
        assert(results)
    }

    @Test
    fun `multiplication node value`(){
        val numberNode1 = NumberNode(testNumber1)
        val numberNode2 = NumberNode(testNumber2)
        val results = MultiplicationNode(
            numberNode1,
            numberNode2
        ).getValue() == (testNumber1 * testNumber2)
        assert(results)
    }

    @Test
    fun `division node value`(){
        val numberNode1 = NumberNode(testNumber1)
        val numberNode2 = NumberNode(testNumber2)
        val results = DivisionNode(
            numberNode1,
            numberNode2
        ).getValue() == (testNumber1 / testNumber2)
        assert(results)
    }

    /**
     * division by zero throw an error
     */
    @Test
    fun `division by zero`(){
        val numberNode1 = NumberNode(testNumber1)
        val numberNode2 = NumberNode(0.0)
        try{
        DivisionNode(
            numberNode1,
            numberNode2
        ).getValue()
            // no error
            assert(false)
        }catch (e:Exception){
            assert(e.message == DIVISION_ERROR)
        }
    }

    @Test
    fun `exponent node value`(){
        val numberNode1 = NumberNode(2.0)
        val numberNode2 = NumberNode(2.0)
        val results = ExponentNode(
            numberNode1,
            numberNode2
        ).getValue() == (2.0.pow(2.0))
        assert(results)
    }

    @Test
    fun `exponent node value 2`(){
        val numberNode1 = NumberNode(2.0)
        val numberNode2 = NumberNode(-2.0)
        val results = ExponentNode(
            numberNode1,
            numberNode2
        ).getValue() == (2.0.pow(-2.0))
        assert(results)
    }

    @Test
    fun `parenthesis node value`(){
        val tokens = Lexer().generateTokens("1${ADDITION_SYMBOL}3${SUBTRACTION_SYMBOL}3")
        val result = ParenthesisNode(tokens as ArrayList<Token>,-1.0).getValue() == -1.0
        assert(result)
    }

    @Test
    fun `parenthesis node value when tokens is empty`(){
        try{
            val tokens = Lexer().generateTokens("")
            ParenthesisNode(tokens as ArrayList<Token>,1.0).getValue()
            assert(false)
        }catch (e:java.lang.Exception) {
            assert(e.message == SYNTAX_ERROR)
        }
    }

    @Test
    fun `square root value`(){
        val node = NumberNode(4.0)
        val result = SquareRootNode(node,-1.0).getValue() == -2.0
        assert(result)
    }

    @Test
    fun `square root value for negative numbers`(){
        val node = NumberNode(-4.0)
        try{
            SquareRootNode(node,1.0).getValue()
            assert(false)
        }catch (e:Exception){
            assert(e.message == MATH_ERROR)
        }
    }

    @Test
    fun `sin value`(){
        val node = NumberNode(testNumber3)
        val result = SinNode(node,-1.0).getValue() == sin(Math.toRadians(node.getValue())) * -1.0
        assert(result)
    }

    @Test
    fun `cos value`(){
        val node = NumberNode(testNumber5)
        val result = CosNode(node,-1.0).getValue() == cos(Math.toRadians(node.getValue()))* -1.0
        assert(result)
    }

    @Test
    fun `tan value`(){
        val node = NumberNode(testNumber4)
        val result = TanNode(node,-1.0).getValue() == tan(Math.toRadians(node.getValue()))* -1.0
        assert(result)
    }

    @Test
    fun `ln value`(){
        val node = NumberNode(testNumber2)
        val result = LnNode(node,-1.0).getValue() == ln(node.getValue()) * -1.0
        assert(result)
    }

    @Test
    fun `ln value for negative numbers`(){
        val node = NumberNode(-testNumber2)
        try {
            LnNode(node,1.0).getValue()
            assert(false)
        }catch (e:Exception){
            assert(e.message == MATH_ERROR)
        }
    }
}