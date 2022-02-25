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
        val tokenIterator = tokens.iterator()
        var currentToken = tokenIterator.next()

        fun gotToNext(): Boolean {
            if (tokenIterator.hasNext()) {
                currentToken = tokenIterator.next()
                return true
            }
            return false
        }

        //  handling if the subtraction and addition symbol came in the start of the equation
        if (currentToken.tokenType in SINGS) {
            val newNumberSign = if (currentToken.tokenType == TokenType.SUBTRACT) -1.0
            else 1.0
            if (!gotToNext())
                return tokens
            //  number after subtraction or addition symbol at the start
            // "-1" "+1"
            if (currentToken.tokenType in listOf(
                    TokenType.NUMBER, TokenType.SQUARE_ROOT,
                    TokenType.SIN, TokenType.COS, TokenType.TAN, TokenType.LN
                )
            ) {
                newTokens.add(Token(currentToken.tokenType, currentToken.value, sign = newNumberSign))
            }

            // parenthesis after subtraction or addition symbol at the start
            //  "-(" "+(" only at the first
            //  it add the newNumberSign to the equation and multiply it by the next token
            else if (currentToken.tokenType == TokenType.L_PARENTHESIS) {
                newTokens.add(Token(TokenType.NUMBER, newNumberSign))
                newTokens.add(Token(TokenType.MULTIPLICATION))
                newTokens.add(currentToken)
            }
            if (!gotToNext())
                return newTokens
        }

        // handling the rest
        while (true) {
            // searching for the positions when - or + will act as sign
            if (currentToken.tokenType in TOKEN_TYPES_BEFORE_SIGNS) {
                newTokens.add(currentToken)
                //  save the token and then start the check
                if (!gotToNext())
                    break

                //  check if this token can be treated as signs and if there is another token after it
                if (currentToken.tokenType in SINGS && tokenIterator.hasNext()) {
                    val tokenSign = if (currentToken.tokenType == TokenType.SUBTRACT) -1.0
                    else 1.0

                    // go to the token after the sign
                    gotToNext()
                    //  if it's a number token the value of the number will multiply by the sign and saved
                    //  this the next token after the sign
                    //  the sign will not be saved
                    //  "*- 1" will be "* token(-1)"
                    if (currentToken.tokenType in TOKENS_WITH_SIGNS) {
                        newTokens.add(Token(currentToken.tokenType, currentToken.value, sign = tokenSign))
                        if (!gotToNext())
                            break
                    }
                }
            } else {
                newTokens.add(currentToken)
                if (!gotToNext())
                    break
            }
        }
        return newTokens
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