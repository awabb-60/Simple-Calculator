package com.awab.calculator.utils

abstract class Node{


    abstract fun getValue(): Double
}

class NumberNode(private val num: Double): Node() {
    override fun getValue() = num
    override fun toString(): String {
        return "NumberNode :$num"
    }
}

class AddNode(private val n1: Node, private val n2: Node): Node(){
    override fun getValue() = n1.getValue() + n2.getValue()

    override fun toString(): String {
        return "(AddNode: $n1 + $n2)"
    }

}

class SubtractNode(private val n1: Node, private val n2: Node): Node(){
    override fun getValue() = n1.getValue() - n2.getValue()

    override fun toString(): String {
        return "(SubtractNode: $n1 - $n2)"
    }

}

class MultiplyNode(private val n1: Node, private val n2: Node): Node(){
    override fun getValue() = n1.getValue() * n2.getValue()

    override fun toString(): String {
        return "(MultiplyNode: $n1 * $n2)"
    }


}

class DivisionNode(private val n1: Node, private val n2: Node): Node(){
    override fun getValue():Double{
//        handle division by zero
        if (n2.getValue() == 0.toDouble()){
            error("cannot divide by zero")
        }
        return n1.getValue() / n2.getValue()
    }

    override fun toString(): String {
        return "(DivisionNode: $n1 / $n2)"
    }

}

class ParenthesisNode(private var tokens:ArrayList<Token>): Node() {
    private var tree = Any()

    override fun getValue(): Double {
        val lexer = Lexer("")
        tokens = lexer.negativePositiveTokens(tokens) as ArrayList<Token>
        val parser = Parser(tokens)
        println("(ParenthesisNode: $tokens)")
        tree = parser.generateTree()
        return (tree as Node).getValue()
    }


    override fun toString(): String {
        return "(ParenthesisNode: $tokens)"
    }
}
