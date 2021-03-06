package com.awab.calculator.utils.calculator_utils

import com.awab.calculator.utils.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Calculator
@Inject
constructor(){

    /**
     * this function take a list of token and check if it solvable and ready to get passed to the Parser
     */
    fun isSolvable(list: List<Token>) {
        // its empty
        if (list.isEmpty())
            error(SYNTAX_ERROR)

        //  must have at least one number
        if (list.count { it.tokenType == TokenType.NUMBER } == 0)
            error(SYNTAX_ERROR)

        // it find if there is a number in wrong position e.g 1Sin
        val anyWrongPositionedNumber: (List<Token>) -> Boolean = { listTokens ->
            var anyWrongPositionedNumber = false

            // looping throw the number tokens
            for (numberIdx in listTokens.indices) {
                if (listTokens[numberIdx].tokenType != TokenType.NUMBER)
                    continue
                // check it its not the first token
                if (numberIdx != 0) {
                    val beforeToken = listTokens[numberIdx - 1].tokenType
                    // the this number token not in a correct position
                    if (beforeToken !in TOKENS_BEFORE_NUMBERS) {
                        anyWrongPositionedNumber = true
                        break
                    }
                }

                // check if its not the last token
                if (numberIdx != listTokens.lastIndex) {
                    val afterToken = listTokens[numberIdx + 1].tokenType
                    // the this number token not in a correct position
                    if (afterToken !in TOKENS_AFTER_NUMBERS) {
                        anyWrongPositionedNumber = true
                        break
                    }
                }
            }
            anyWrongPositionedNumber
        }

        // check the number positions
        if (anyWrongPositionedNumber(list))
            error(SYNTAX_ERROR)

        // must ends with number or ")"
        if (list.last().tokenType !in listOf(TokenType.NUMBER, TokenType.R_PARENTHESIS))
            error(SYNTAX_ERROR)

        // number of open p == number of closed p
        if (list.count { it.tokenType == TokenType.L_PARENTHESIS } !=
            list.count { it.tokenType == TokenType.R_PARENTHESIS })
            error(SYNTAX_ERROR)

        //
        val anyUnClosedParentheses:(List<Token>) ->Boolean = {
            var lCounter = 0
            for (token in it){
            if (token.tokenType == TokenType.L_PARENTHESIS)
                lCounter++

                // only start counting right parentheses after a left parentheses in found
            if (token.tokenType == TokenType.R_PARENTHESIS && lCounter > 0)
                lCounter--
            }
            lCounter!=0
        }

        // all open parenthesis
        if (anyUnClosedParentheses(list))
            error(SYNTAX_ERROR)

        //  no straight division on zero
        val divideByZero: (Token) -> (Boolean) = {
            var byZero = false
            if (it.tokenType == TokenType.DIVISION) {
                val idx = list.indexOf(it)
                val afterToken = list[idx + 1]
                if (afterToken.value == 0.toDouble()) {
                    byZero = true
                }
            }
            byZero
        } // return true if any division by zero
        if (list.find { divideByZero(it) } != null)
            error(DIVISION_ERROR)
    }

    /**
     * this the main function of the calculator
     * it takes the equation as a string and return the value of it
     * @param text the equation text
     * @return the value of the equation text or an error message
     * @throws  Exception when any mathematical errors occur or the equation is unsolvable,
     */
    fun solve(text: String): Double {
        //  making the tokens
        val tokens = Lexer().generateTokens(text)
        return solve(tokens)
    }

    /**
     * this the main function of the calculator
     * it takes tokens and get the value of these tokens
     * @param tokens the equation text
     * @return the value of the equation tokens or an error message
     * @throws  Exception when any mathematical errors occur or the equation is unsolvable,
     */
    fun solve(tokens: List<Token>): Double {
        //  trying to solve the equation
        try {
            //  if the tokens are not solvable and error will occur
            isSolvable(tokens)
            //  making the tree
            val tree = Parser().generateTree(tokens as ArrayList<Token>)
            //  returning the value of the tree
            return tree.getValue()
        } catch (e: Exception) {
            //  returning the message of the error that occur while solving the equation
            throw Exception(e.message ?: SYNTAX_ERROR)
        }
    }

    /**
     * this function takes a the equation text and check if adding the new char to the equation will case
     * any  error, if adding the new char will make an error it return just the old equation text,
     * if not it will return the equation text with the new chat at the end
     * @param text the current equation text
     * @param char the new that might get added to the equation text
     * @return the equation text with no wrong placed chars that might case an error
     */
    fun filterInput(text: String, char: Char): String {
        var newText = text + char

        // no multiplication and division at the start
        // and multiplication and division will chang any symbols before it except ( )
        if (char in operationsWillAnyBefore) {
            // "*/" at the start
            when {
                text.isEmpty() -> {
                    newText = text // no multiplication, division or exponent at the start
                }
                text.last() in SYMBOLS -> {
                    // multiplication, division and exponent will chang any symbols before it except ( )
                    newText = if (text.length > 1 && text.dropLast(1).last() != LEFT_PARENTHESIS) {
                        // drop the last symbol and check if there is any more symbol to drop e.g : 1????-'??'
                        filterInput(text.dropLast(1), char)
                    } else  // so you don't have a */ in the start or after (
                        text.dropLast(1)
                }
                //cannot place */ after  (
                text.last() == LEFT_PARENTHESIS -> {
                    newText = text
                }
            }
        }

        // plus symbol after sub and so will chang to the new char
        else if (text.isNotEmpty() && char in ("$SUBTRACTION_SYMBOL$ADDITION_SYMBOL")) {
            if (text.last() in ("$SUBTRACTION_SYMBOL$ADDITION_SYMBOL")) {
                newText = text.dropLast(1) + char
            }
        }

        // autoPlaceMulBefore after a number or ")" auto place "*" before it
        else if (text.isNotEmpty() && char in autoPlaceMultiplicationBefore) {
            if (text.last() in (DIGITS + PI_SYMBOL + e_SYMBOL + RIGHT_PARENTHESIS)) {
                newText = "$text$MULTIPLICATION_SYMBOL$char"
            }
        }

        // Number after ")" auto place "*" before it
        else if (char in (DIGITS + PI_SYMBOL + e_SYMBOL)) {
            if (text.isNotEmpty()) {
                if (text.last() in listOf(RIGHT_PARENTHESIS, PI_SYMBOL, e_SYMBOL))
                    newText = "$text$MULTIPLICATION_SYMBOL$char"

                // Pi after DIGITS or Pi or e auto place "*" before it
                // it will work with Pi and e but not work with DIGITS
                if (text.last() in (DIGITS + PI_SYMBOL + e_SYMBOL) && char in "$PI_SYMBOL$e_SYMBOL")
                    newText = "$text$MULTIPLICATION_SYMBOL$char"
            }

            // can't place a decimal point if the number already has one
            if (char == DECIMAL_POINT) {
                //  replace . with 0. at the start
                if (text.isEmpty())
                    newText = text + "0."
                //  replace . with 0. anywhere
                else {
                    var alreadyHasDecimalPoint = false
                    // check if the last part of the text
                    for (idx in text.length - 1 downTo 0) {

                        // if the last part is not a number
                        if (text[idx] !in DIGITS) {

                            // if its the first iteration.. just format the decimal point normally
                            if (idx == text.length - 1) {
                                newText = text + "0."
                            }
                            break
                        } else if (text[idx] == DECIMAL_POINT)
                        // the last part of the text is a number and it already has a decimal point
                            alreadyHasDecimalPoint = true
                    }
                    // if the number already has a decimal point the new one will get ignored
                    if (alreadyHasDecimalPoint) {
                        newText = text
                    }
                }
            }
            // if none of these case happened... it means the last part of the text is a number
            // and it dose not already has a decimal point
            // and the decimal point will get add to the number normally :)
        } else if (char == RIGHT_PARENTHESIS) {
            //  can't place ) if the number of ( is equal to the number of ")...  all ( are closed
            if (text.count { it == LEFT_PARENTHESIS } == text.count { it == RIGHT_PARENTHESIS }) {
                newText = text
                //  can only place ")" after a number or another ")"
            } else if (text.last() !in (DIGITS + PI_SYMBOL + e_SYMBOL) && text.last() != RIGHT_PARENTHESIS)
                newText = text
        }
        return newText
    }
}