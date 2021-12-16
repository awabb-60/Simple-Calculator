package com.awab.calculator.utils
import android.util.Log
import kotlin.collections.ArrayList

fun isSolvable(list: List<Token>) {
//  must have at least one number
    if (list.count{it.tokenType == TokenType.NUMBER} == 0) {
        error(SYNTAX_ERROR)
    }

//  must ends with number or )
    if (list.last().tokenType != TokenType.NUMBER && list.last().tokenType != TokenType.R_PARENTHESIS) {
        error(SYNTAX_ERROR)
}
//  number of open p == number of closed p
    if (list.count { it.tokenType == TokenType.L_PARENTHESIS } !=
        list.count { it.tokenType == TokenType.R_PARENTHESIS }) {
        error(SYNTAX_ERROR)
    }
//  no straight division on zero
    val anyDivisionZeroL: (Token) -> (Boolean) = {
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
    if (list.find { anyDivisionZeroL(it) } != null) {
        error(DIVISION_ERROR)
    }
}

fun solve(text: String): String {
    if (text.isBlank())
        return SYNTAX_ERROR
    val answer = try {
        val lexer = Lexer(text)
        val tokens = lexer.generateTokens()
        isSolvable(tokens)
        val parser = Parser(tokens as ArrayList<Token>)
        val tree = parser.generateTree()
        tree.getValue()
    }catch (e:Exception){e.message}
    return answer.toString()
}

fun filterInput(text: String, char: Char): String {
    var newText = text + char

//    no multiplication and division at the start
//    and multiplication and division will chang any symbols before it except ( )
    if (char in ("$MULTIPLICATION_SIGN$DIVISION_SIGN")) {
//      "*/" at the start
        if (text.isEmpty()) {
            newText = text //no multiplication and division at the start
//            Log.d(TAG, "filterInput: no multiplication and division at the start")
        } else if (text.last() in ("$SUBTRACTION_SIGN$ADDITION_SIGN$MULTIPLICATION_SIGN$DIVISION_SIGN")) {

            //multiplication and division will chang any symbols before it except ( )
//            Log.d(TAG, "filterInput: multiplication and division will chang any symbols before it except ( )")
            newText = if (text.length > 1 && text.dropLast(1).last() != LEFT_PARENTHESIS) {
                text.dropLast(1) + char
            } else  // so you don't have a */ in the start or after "("
                text.dropLast(1)

        } else if (text.last() == LEFT_PARENTHESIS) {
            newText = text //cannot place */ after  (
//            Log.d(TAG, "filterInput: cannot place */ after  (")
        }

    }

//    plus symbol after sub and so will chang to the new char
    else if (text.isNotEmpty() && char in ("$SUBTRACTION_SIGN$ADDITION_SIGN")) {
        if (text.last() in ("$SUBTRACTION_SIGN$ADDITION_SIGN")) {
            newText = text.dropLast(1) + char
//            Log.d(TAG, "filterInput: plus symbol after sub and so will chang to the new char")
        }
//    ( after number auto place * before it
    } else if (text.isNotEmpty() && char == LEFT_PARENTHESIS) {
        if (text.last() in DIGITS || text.last() == RIGHT_PARENTHESIS) {
            newText = "$text$MULTIPLICATION_SIGN$char"
//            Log.d(TAG, "filterInput:  ( after number auto place * before it")
        }

//          Number after ) auto place * before it
    } else if (char in DIGITS) {
        if (text.isNotEmpty() && text.last() == RIGHT_PARENTHESIS) {
            newText = "$text$MULTIPLICATION_SIGN$char"
//            Log.d(TAG, "filterInput: Number after ) auto place * before it")
        }
//            cant place a decimal point if the number already has one
        if (char == DECIMAL_POINT) {
//            replace . with 0. at the start
            if(text.isEmpty())
                newText = text + "0."
            else{
                var alreadyHasDecimalPoint = false
                for (idx in text.length - 1 downTo 0) {
                    if (text[idx] !in DIGITS){
//            replace . with 0. after sy
                        if (idx == text.length -1){
                            newText = text + "0."
                        }
                        break
                    }
                    else if (text[idx] == DECIMAL_POINT)
                        alreadyHasDecimalPoint = true
                }
                if (alreadyHasDecimalPoint) {
                    newText = text
//                Log.d(TAG, "filterInput: cant place a decimal point if the number already has one")
                }
            }

        }
    }
//        cant place ) if the number of ( is smaller or equal to the number of )
    else if (char == RIGHT_PARENTHESIS) {
        if (text.count { it == LEFT_PARENTHESIS } <= text.count { it == RIGHT_PARENTHESIS }) {
            newText = text
//            Log.d(TAG, "filterInput: cant place ) if the number of ( is smaller or equal to the number of )")
        } else if (text.last() !in DIGITS && text.last() != RIGHT_PARENTHESIS)
            newText = text
    }
    return newText
}