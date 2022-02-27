package com.awab.calculator.utils

/**
 * the lexer create tokens from a string
 */
class Lexer {
    private lateinit var textIterator: CharIterator
    private var value = NOT_DIGIT

    /**
     * loop throw the text and create the tokens
     * if there is any chars that represent an unsupported token type it will get ignored
     * @param text the string to create tokens from
     * @return a list of tokens
     */
    fun generateTokens(text: String): MutableList<Token> {
        textIterator = text.iterator()
        var tokens = mutableListOf<Token>()

        // no chars in the text return empty list
        if (textIterator.hasNext())
            value = textIterator.next()
        else
            return tokens

        // here where the lexer create a token for every symbol
        // the const like ℮ and π will create a number token with the value of the const
        while (true) {
            when (value) {
                ADDITION_SYMBOL -> {
                    tokens.add(Token(TokenType.ADDITION))
                }
                SUBTRACTION_SYMBOL -> {
                    tokens.add(Token(TokenType.SUBTRACT))
                }
                MULTIPLICATION_SYMBOL -> {
                    tokens.add(Token(TokenType.MULTIPLICATION))
                }
                DIVISION_SYMBOL -> {
                    tokens.add(Token(TokenType.DIVISION))
                }
                EXPONENT_SYMBOL -> {
                    tokens.add(Token(TokenType.EXPONENT))
                }
                SQUARE_ROOT_SYMBOL -> {
                    tokens.add(Token(TokenType.SQUARE_ROOT))
                }
                SIN_SYMBOL -> {
                    tokens.add(Token(TokenType.SIN))
                }
                COS_SYMBOL -> {
                    tokens.add(Token(TokenType.COS))
                }
                TAN_SYMBOL -> {
                    tokens.add(Token(TokenType.TAN))
                }
                LN_SYMBOL -> {
                    tokens.add(Token(TokenType.LN))
                }
                LEFT_PARENTHESIS -> {
                    tokens.add(Token(TokenType.L_PARENTHESIS))
                }
                RIGHT_PARENTHESIS -> {
                    tokens.add(Token(TokenType.R_PARENTHESIS))
                }
                PI_SYMBOL -> {
                    tokens.add(Token(TokenType.NUMBER, PI_VALUE))
                }
                e_SYMBOL -> {
                    tokens.add(Token(TokenType.NUMBER, e_VALUE))
                }
                in DIGITS -> {
                    tokens.add(generateNumber())
                    continue
                } // continue because inside generated number iterator.next() will be called
            }

            if (textIterator.hasNext())
                value = textIterator.next()
            //  reached the end of the iterator
            else
                break
        }

        if (tokens.isNotEmpty())
            tokens = negativePositiveTokens(tokens)
        return tokens
    }


    /**
     * this function start from the current iterator value when its a number
     * and join all the number after that into one string,then
     * it return the token of that number
     * @return a number token
     */
    private fun generateNumber(): Token {
        val token = Token(TokenType.NUMBER)
        var decimalCount = 0
        var number = value.toString()

        //  increment decimalCount so when another one comes it will get ignored
        // and it will be treated as number by it self
        if (number == DECIMAL_POINT.toString()) {
            decimalCount++
        }
        while (true) {
            //  that mean there is more characters is the text
            if (textIterator.hasNext()) {
                val nextChar = textIterator.next()
                if (nextChar == DECIMAL_POINT) {
                    decimalCount++
                }
                //  add the nextChar to number String as long as it in DIGITS
                if (nextChar in DIGITS && decimalCount <= 1) {
                    number += nextChar
                }
                //  the full number is found and turn into a token
                else {
                    token.value = formatDecimal(number).toDouble()
                    value = nextChar
                    break
                }
            }
            //  reached the end of the iterator
            else {
                token.value = formatDecimal(number).toDouble()
                value = NOT_DIGIT
                break
            }
        }
        return token
    }

    /**
     * to care where the subtraction and addition symbol might act as a sign
     * like:"-1,*-1,/-1,-(1)"  "+1,*+1,/+1,+(1)"
     */
    private fun negativePositiveTokens(tokens: MutableList<Token>): MutableList<Token> {
        val newTokens = mutableListOf<Token>()
        val signsPositions = getSignsPositions(tokens)

        var addSign = false
        var sign = 1.0
        for (idx in tokens.indices) {
            val token = tokens[idx]
            // adding the saved sing
            if (addSign) {
                newTokens.add(Token(token.tokenType, value = token.value, sign = sign))
                sign = 1.0
                addSign = false
                continue
            }
            // saving the sign value to be added to the next tokens
            if (idx in signsPositions) {
                sign = if (token.tokenType == TokenType.SUBTRACT) -1.0 else 1.0
                addSign = true
            } else // add the normal tokens
                newTokens.add(token)
        }
        return newTokens
    }

    /**
     * get the positions of the - and + tokens where it might act as a sign
     * @param tokens the tokens list where you want to find the positions of the signs
     * @return a list of signs positions
     */
    private fun getSignsPositions(tokens: List<Token>): MutableList<Int> {
        val pos = mutableListOf<Int>()
        tokens.forEachIndexed { index, token ->

            // only adding the
            if (token.tokenType in SIGNS && index != tokens.lastIndex) {
                // at the start
                if (index == 0) {
                    pos.add(index)
                }

                // the rest
                if (index > 0) {
                    // check if the sign in a place where it act as a sign
                    if (tokens[index - 1].tokenType in TOKEN_TYPES_BEFORE_SIGNS &&
                        tokens[index + 1].tokenType in TOKENS_WITH_SIGNS
                    )
                        pos.add(index)
                }
            }
        }
        return pos
    }

    /**
     * this function better format the number if the number has a decimal point in the start or end
     * example: 1. will be 1.0 and .1 will be 0.1
     * @param numberStr the number that will get formatted
     * @return number string that looks like the example
     */
    private fun formatDecimal(numberStr: String): String {
        var formattedString = numberStr
        if (numberStr.startsWith(DECIMAL_POINT))
            formattedString = "0$numberStr"
        else if (numberStr.endsWith(DECIMAL_POINT))
            formattedString = "${numberStr}0"

        return formattedString
    }

    /**
     * this will take one char and return the token of that char if it has a token
     * else it return null
     * @param char the char that will be a token
     * @return the token of the char or null if the char has no token
     */
    fun generateToken(char: Char): Token? {
        val token = generateTokens(char.toString())
        return if (token.isEmpty()) null else token[0]
    }
}