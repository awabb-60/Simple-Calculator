package com.awab.calculator.utils

enum class TokenType {
    NUMBER,
    PLUS,
    SUBTRACT,
    MULTIPLY,
    DIVISION,
    L_PARENTHESIS,
    R_PARENTHESIS;

    private var tokenValue: String? = null
    fun setValue(value: String) {
        tokenValue = value
    }
}