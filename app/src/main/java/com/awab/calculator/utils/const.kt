package com.awab.calculator.utils

const val DIGITS = "0123456789."
const val DECIMAL_POINT = '.'
const val NOT_DIGIT = '?'

const val ADDITION_SYMBOL = '+'
const val SUBTRACTION_SYMBOL = '-'
const val EXPONENT_SYMBOL = '^'
const val MULTIPLICATION_SYMBOL = '×'
const val DIVISION_SYMBOL = '÷'

const val SQUARE_ROOT_SYMBOL = '|'
const val SIN_SYMBOL = 'S'
const val COS_SYMBOL = 'C'
const val TAN_SYMBOL = 'T'
const val LN_SYMBOL = 'L'

const val PI_SYMBOL = 'π'
const val PI_VALUE = 3.141592654

const val e_SYMBOL = '℮'
const val e_VALUE = 2.718281185

const val LEFT_PARENTHESIS = '('
const val RIGHT_PARENTHESIS = ')'

const val SYNTAX_ERROR = "syntax error!"
const val DIVISION_ERROR = "cannot divide by zero!"
const val MATH_ERROR = "Math error!"


/**
 * the order of the math operations
 */
val ORDER_OF_OPERATIONS = arrayOf(
    arrayOf(TokenType.SQUARE_ROOT, TokenType.SIN, TokenType.COS, TokenType.TAN, TokenType.LN),
    arrayOf(TokenType.EXPONENT),
    arrayOf(TokenType.MULTIPLICATION, TokenType.DIVISION),
    arrayOf(TokenType.ADDITION, TokenType.SUBTRACT),
)


/**
 * the negative and the positive sign... the
 */
val SINGS = listOf(TokenType.SUBTRACT, TokenType.ADDITION)

/**
 * if any subtraction or addition
 * on symbol came after these tokes, it will be treated as a sign - +
 * example: 1×-2 , 1÷- 1
 */
val TOKEN_TYPES_BEFORE_SIGNS =
    listOf(TokenType.L_PARENTHESIS, TokenType.EXPONENT, TokenType.MULTIPLICATION, TokenType.DIVISION)

/**
 * the token types that creates Nodes the work with signs
 */
val TOKENS_WITH_SIGNS = listOf(
    TokenType.NUMBER, TokenType.L_PARENTHESIS, TokenType.SQUARE_ROOT, TokenType.SIN, TokenType.COS,
    TokenType.TAN, TokenType.LN
)

/**
 * all the None words symbols
 */
val SYMBOLS = arrayOf(
    EXPONENT_SYMBOL, MULTIPLICATION_SYMBOL, DIVISION_SYMBOL,
    SUBTRACTION_SYMBOL, ADDITION_SYMBOL
)

/**
 * this has all the symbols that must chang any symbol before it when it get placed
 */
val symbolsWillChangeAnyBeforeIt = arrayOf(EXPONENT_SYMBOL, MULTIPLICATION_SYMBOL, DIVISION_SYMBOL)

/**
 * this has the symbols that must have a multiplication symbol before it in some cases when get placed
 */
val autoPlaceMultiplicationBefore =
    arrayOf(LEFT_PARENTHESIS, SQUARE_ROOT_SYMBOL, SIN_SYMBOL, COS_SYMBOL, TAN_SYMBOL, LN_SYMBOL)


/**
 * the cursor must always be after one of these
 */
val cursorCantBeAfter = arrayOf(
    TokenType.SIN, TokenType.COS, TokenType.TAN,
    TokenType.LN, TokenType.SQUARE_ROOT)