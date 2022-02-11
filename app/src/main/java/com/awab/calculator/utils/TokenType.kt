package com.awab.calculator.utils

/**
 * this class hold all the possible token types that the lexer can make
 * @param typeString the String that will get returned when using .toString()
 */
enum class TokenType(private val typeString: String) {
    NUMBER("num"),
    ADDITION(ADDITION_SYMBOL.toString()),
    SUBTRACT(SUBTRACTION_SYMBOL.toString()),
    MULTIPLICATION(MULTIPLICATION_SYMBOL.toString()),
    DIVISION(DIVISION_SYMBOL.toString()),
    EXPONENT(EXPONENT_SYMBOL.toString()),
    SQUARE_ROOT(SQUARE_ROOT_SYMBOL.toString()),
    L_PARENTHESIS(LEFT_PARENTHESIS.toString()),
    R_PARENTHESIS(RIGHT_PARENTHESIS.toString()),
    SIN(SIN_SYMBOL+"in"),
    COS(COS_SYMBOL+"os"),
    TAN(TAN_SYMBOL+"an"),

    LN(LN_SYMBOL+"n");

    override fun toString(): String {
        return typeString
    }
}