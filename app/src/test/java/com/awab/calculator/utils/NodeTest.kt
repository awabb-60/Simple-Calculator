package com.awab.calculator.utils

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
        val results = NumberNode(testNumber1).getValue()
        assert(results == testNumber1)
    }

    @Test
    fun `addition node value`(){
        val numberNode1 = NumberNode(testNumber1)
        val numberNode2 = NumberNode(testNumber2)
        val results = AdditionNode(
            numberNode1,
            numberNode2
        ).getValue()
        assert(results == (testNumber2 + testNumber1))
    }

    /**
     * subtract small number from big number
     */
    @Test
    fun `subtraction node value`(){
        val numberNode1 = NumberNode(testNumber2)
        val numberNode2 = NumberNode(testNumber1)
        val results = SubtractionNode(
            numberNode1,
            numberNode2
        ).getValue()
        assert(results == (testNumber2 - testNumber1))
    }

    /**
     * subtract big number from small number
     */
    @Test
    fun `subtraction node value 2`(){
        val numberNode1 = NumberNode(testNumber1)
        val numberNode2 = NumberNode(testNumber3)
        val results = SubtractionNode(
            numberNode1,
            numberNode2
        ).getValue()
        assert(results == (testNumber1 - testNumber3))
    }

    @Test
    fun `multiplication node value`(){
        val numberNode1 = NumberNode(testNumber1)
        val numberNode2 = NumberNode(testNumber2)
        val results = MultiplicationNode(
            numberNode1,
            numberNode2
        ).getValue()
        assert(results == (testNumber1 * testNumber2))
    }

    @Test
    fun `division node value`(){
        val numberNode1 = NumberNode(testNumber1)
        val numberNode2 = NumberNode(testNumber2)
        val results = DivisionNode(
            numberNode1,
            numberNode2
        ).getValue()
        assert(results == (testNumber1/testNumber2))
    }

    /**
     * division by zero throw an error
     */
    @Test
    fun `division by zero`(){
        val numberNode1 = NumberNode(testNumber1)
        val numberNode2 = NumberNode(0.0)
        val result = try{
        DivisionNode(
            numberNode1,
            numberNode2
        ).getValue()
            // no error
            false
        }catch (e:Exception){
            e.message == DIVISION_ERROR
        }
        assert(result)
    }

    @Test
    fun `exponent node value`(){
        val numberNode1 = NumberNode(2.0)
        val numberNode2 = NumberNode(2.0)
        val results = ExponentNode(
            numberNode1,
            numberNode2
        ).getValue()
        assert(results == (2.0.pow(2.0)))
    }
    @Test
    fun `exponent node value 2`(){
        val numberNode1 = NumberNode(2.0)
        val numberNode2 = NumberNode(-2.0)
        val results = ExponentNode(
            numberNode1,
            numberNode2
        ).getValue()
        assert(results == (2.0.pow(-2.0)))
    }

    @Test
    fun `parenthesis node value`(){
        val tokens = Lexer().generateTokens("1${ADDITION_SYMBOL}2${SUBTRACTION_SYMBOL}3")
        val result = ParenthesisNode(tokens as ArrayList<Token>,1.0).getValue()
        assert(result == (1.0+2.0-3.0))
    }

    @Test
    fun `parenthesis node value when tokens is empty`(){
        val result = try{
            val tokens = Lexer().generateTokens("")
            ParenthesisNode(tokens as ArrayList<Token>,1.0).getValue()
            false
        }catch (e:java.lang.Exception) {
            e.message == SYNTAX_ERROR
        }
        assert(result)
    }
    // ((-1)) * 2


    @Test
    fun `square root value`(){
        val node = NumberNode(4.0)
        val result = SquareRootNode(node,1.0).getValue()
        assert(result == 2.0)
    }

    @Test
    fun `square root value for negative numbers`(){
        val node = NumberNode(-4.0)
        val result = try{
            SquareRootNode(node,1.0).getValue()
            false
        }catch (e:Exception){
            e.message == MATH_ERROR
        }
        assert(result)
    }

    @Test
    fun `sin value`(){
        val node = NumberNode(testNumber3)
        val result = SinNode(node,1.0).getValue()
        assert(result == sin(Math.toRadians(node.getValue())))
    }

    @Test
    fun `cos value`(){
        val node = NumberNode(testNumber5)
        val result = CosNode(node,1.0).getValue()
        assert(result == cos(Math.toRadians(node.getValue())))
    }

    @Test
    fun `tan value`(){
        val node = NumberNode(testNumber4)
        val result = TanNode(node,1.0).getValue()
        assert(result == tan(Math.toRadians(node.getValue())))
    }

    @Test
    fun `ln value`(){
        val node = NumberNode(testNumber2)
        val result = LnNode(node,1.0).getValue()
        assert(result == ln(node.getValue()))
    }

    @Test
    fun `ln value for negative numbers`(){
        val node = NumberNode(-testNumber2)
        val result = try {
            LnNode(node,1.0).getValue()
            false
        }catch (e:Exception){
            e.message == MATH_ERROR
        }
        assert(result)
    }
}