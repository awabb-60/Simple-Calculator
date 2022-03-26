package com.awab.calculator.utils.calculator_utils

/**
 * this model class represent a token
 * @param tokenType the type of the token
 * @param value the value of the token
 * @param sign it determine if the value of the token will be negative or a positive value
 * */
data class Token(val tokenType: TokenType, var value: Double = 1.0, val sign:Double = 1.0) {
    override fun toString(): String {
        return "$tokenType"
    }
}