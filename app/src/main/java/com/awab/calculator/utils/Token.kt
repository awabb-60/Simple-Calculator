package com.awab.calculator.utils

data class Token(var tokenType: TokenType, var value: Double? = null) {
    override fun toString(): String {
        return "$tokenType: $value"
    }
}



