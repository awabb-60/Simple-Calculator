package com.awab.calculator.utils

import org.junit.Test

class CalculatorTest {
    /**
     * theses isWorking functions test if the calculator can work probably in general
     */
    @Test
    fun isWorking1(){
        val results = Calculator().solve("")
        assert(results == SYNTAX_ERROR)
    }

    @Test
    fun `solving empty equation return syntax error`() {
        val results = Calculator().solve("")
        assert(results == SYNTAX_ERROR)
    }

}