package com.awab.calculator.utils

class Lexer(text: String) {
    private val textIterator = text.iterator()
    private var value = NOT_DIGIT

//    # Done
    fun generateTokens(): MutableList<Token> {
        var tokens = mutableListOf<Token>()
        value = textIterator.next()
        while (true) {
            when (value){
                ADDITION_SIGN->{tokens.add(Token(TokenType.PLUS))}
                SUBTRACTION_SIGN->{tokens.add(Token(TokenType.SUBTRACT))}
                MULTIPLICATION_SIGN->{tokens.add(Token(TokenType.MULTIPLY))}
                DIVISION_SIGN->{tokens.add(Token(TokenType.DIVISION))}
                LEFT_PARENTHESIS->{tokens.add(Token(TokenType.L_PARENTHESIS))}
                RIGHT_PARENTHESIS->{tokens.add(Token(TokenType.R_PARENTHESIS))}
                in DIGITS-> {tokens.add(generateNumber())
                    continue} // continue because inside generated number iterator.next will be called
                              // before it ends
            }

            if (textIterator.hasNext())
                value = textIterator.next()
//            reached the end of the iterator
            else
                break
        }

        tokens = negativePositiveTokens(tokens)
        return tokens
    }

//    "-1,*-1,/-1,-(1)"  "+1,*+1,/+1,+(1)"
//    toke care about sign -+ Tokens' that are not between two number #Done
    fun negativePositiveTokens(tokens: MutableList<Token>): MutableList<Token> {
        val newTokens = mutableListOf<Token>()
        val beforeTokenTypes = listOf(TokenType.MULTIPLY, TokenType.DIVISION)
        val negativePositiveSigns = listOf(TokenType.SUBTRACT, TokenType.PLUS)
        val tokenIterator = tokens.iterator()
        var currentToken = tokenIterator.next()

        fun gotToNext(): Boolean {
            if (tokenIterator.hasNext()) {
                currentToken = tokenIterator.next()
                return true
            }
            return false
        }

//      the first token in the list
        if (currentToken.tokenType in negativePositiveSigns) {
            val newNumberSign = if (currentToken.tokenType == TokenType.SUBTRACT) -1
                                else 1
            if (!gotToNext())
                return tokens
//            "-1" "+1"
            if (currentToken.tokenType == TokenType.NUMBER){
                newTokens.add(Token(currentToken.tokenType, currentToken.value!! * newNumberSign))
            }

//            "-(" "+(" only at the first
            else if (currentToken.tokenType == TokenType.L_PARENTHESIS){
                newTokens.add(Token(TokenType.NUMBER, newNumberSign.toDouble()))
                newTokens.add(Token(TokenType.MULTIPLY))
                newTokens.add(currentToken)
            }
            if (!gotToNext())
                return newTokens
        }
        while (true) {

//          checking if there is subtraction after the before types
            if (currentToken.tokenType in beforeTokenTypes) {
                newTokens.add(currentToken)
//              save the token and then start the check
                if (!gotToNext())
                    break
//              check
                if (currentToken.tokenType in negativePositiveSigns) {
                    val newNumberSign = if (currentToken.tokenType == TokenType.SUBTRACT) -1
                                        else 1
//                  no need to check the next because - must not be in the end
                    currentToken = tokenIterator.next()
//                  this the next token after the sign
//                  if it's a number token the value of the number will multiply by the sign and saved
//                  the sign will not be saved
//                    "* - 1" == "* token(-1)"
                    if (currentToken.tokenType == TokenType.NUMBER) {
                        newTokens.add(Token(currentToken.tokenType, currentToken.value!! * newNumberSign))
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

//    takes the string .1 and 1. and format it #Done
    private fun formatDecimal(numberStr: String): String {
        var formattedString = numberStr
        if (numberStr.startsWith(DECIMAL_POINT))
            formattedString = "0$numberStr"
        else if (numberStr.endsWith(DECIMAL_POINT))
            formattedString = "${numberStr}0"

        return formattedString
    }

//    it will generate number tokens #Done
    private fun generateNumber(): Token {
        val token = Token(TokenType.NUMBER)
        var decimalCount = 0
        var number = value.toString()

//        increment decimalCount so when another one comes it will get ignored
        if (number == DECIMAL_POINT.toString()) {
            decimalCount++
        }
        while (true) {
//          that mean there is more characters is the text
            if (textIterator.hasNext()) {
                val nextChar = textIterator.next()
                if (nextChar == DECIMAL_POINT) {
                    decimalCount++
                }
                if (nextChar in DIGITS && decimalCount <= 1) {
                    number += nextChar
                }
//              the full number is found and turn into a token
                else {
                    token.value = formatDecimal(number).toDouble()
                    value = nextChar
                    break
                }
            }
//          reached the end of the iterator
            else {
                token.value = formatDecimal(number).toDouble()
                value = NOT_DIGIT
                break
            }
        }
        return token
    }
}