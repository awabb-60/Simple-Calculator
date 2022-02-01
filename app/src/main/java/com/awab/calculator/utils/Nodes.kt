package com.awab.calculator.utils

import kotlin.math.*

/**
 * a Node is a segment of the math equation,
 * its like a simple math equations: (1+2), (3*1) and so,
 * all Nodes has a way to evaluate its value, but the NumberNodes the value is the number
 */
interface Node{
     fun getValue(): Double
}

/**
 * the Node takes only one number as a parameter,
 * and return that number when getValue is invoked
 * @param number the number of th node
 */
class NumberNode(private val number: Double): Node {
    override fun getValue() = number
    override fun toString(): String {
        return "Number Node :$number"
    }
}

/**
 * this node takes tow Node as parameters,
 * and return the addition of the values of these nodes
 * @param node1 the node on the left of the plus operation
 * @param node2 the node on the right of the plus operation
 */
class AdditionNode(private val node1:Node, private val node2:Node):Node{
    override fun getValue() = node1.getValue() + node2.getValue()

    override fun toString(): String {
        return "(Addition Node: $node1 + $node2)"
    }
}

/**
 * this node takes tow Node as parameters,
 * and return the subtraction of the values of these nodes
 * @param node1 the node on the left of the minus operation
 * @param node2 the node on the right of the minus operation
 */
class SubtractNode(private val node1:Node, private val node2:Node):Node{
    override fun getValue() = node1.getValue() - node2.getValue()

    override fun toString(): String {
        return "(SubtractNode: $node1 - $node2)"
    }
}

/**
 * this node takes tow Node as parameters,
 * and return the value of node1 to the power of the value of node2
 * @param node1 the node on the left of the exponent operation
 * @param node2 the node on the right of the exponent operation
 */
class ExponentNode(private val node1:Node, private val node2:Node):Node{
    override fun getValue():Double{

        return node1.getValue().pow(node2.getValue())
    }

    override fun toString(): String {
        return "(ExponentNode: $node1 ^ $node2)"
    }
}

/**
 * this node takes tow Node as parameters,
 * and return the value of node1 multiplied by the value of node2
 * @param node1 the node on the left of the multiply operation
 * @param node2 the node on the right of the multiply operation
 */
class MultiplicationNode(private val node1:Node, private val node2:Node):Node{
    override fun getValue() = node1.getValue().times(node2.getValue())

    override fun toString(): String {
        return "(MultiplyNode: $node1 * $node2)"
    }
}

/**
 * this node takes tow Node as parameters,
 * and return the value of node1 divided by the value of node2
 * @param node1 the node on the left of the divide operation
 * @param node2 the node on the right of the divide operation
 */
class DivisionNode(private val node1:Node, private val node2:Node):Node{
    override fun getValue():Double{
//        handle division by zero
        if (node2.getValue() == 0.0){
            error(DIVISION_ERROR)
        }
        return node1.getValue().div(node2.getValue())
    }

    override fun toString(): String {
        return "(DivisionNode: $node1 / $node2)"
    }
}

/**
 * this node takes a tokens list as a parameter,
 * it create an new tree form these tokens then evaluated the tree and return its value as a number
 * @param tokens the tokens inside the parenthesis
 * @param sign the sign the was left to this node in the equation it will be -1 or +1
 */
class ParenthesisNode(private var tokens:ArrayList<Token>,private val sign:Double): Node {
    private var tree = Any()
    override fun getValue(): Double {
        //  generate new tree from these tokens
        val parser = Parser()
        tree = parser.generateTree(tokens)

        //  multiplying the value of this tree with this sign and returning it
        return (tree as Node).getValue() * sign
    }
    override fun toString(): String {
        return "(ParenthesisNode: $tokens)"
    }
}

/**
 * this node takes one Node as parameter, (parenthesisNode)
 * it evaluate that parenthesisNode and get the square root of that value,
 * then it multiply the value by the sign,
 * then return the value
 * @param node the parenthesisNode
 * @param sign the sign the was left to this node in the equation it will be -1 or +1
 * @throws MATH_ERROR if the value of the parenthesisNode is negative
 */
class SquareRootNode(private val node: Node, private val sign: Double):Node{
    override fun getValue():Double{
        if (node.getValue() < 0.0)
            error(MATH_ERROR)
        return sign * sqrt(node.getValue())
    }
    override fun toString(): String {
        return "(SquareRootNode: |$node)"
    }
}

/**
 * this node takes one Node as parameter, (parenthesisNode)
 * it evaluate that parenthesisNode and get the Sin of that value,
 * then it multiply the value by the sign,
 * then return the value
 * @param node the parenthesisNode
 * @param sign the sign the was left to this node in the equation it will be -1 or +1
 */
class SinNode(private val node: Node, private val sign: Double):Node{
    override fun getValue():Double{
        return sign * sin(Math.toRadians(node.getValue()))
    }
    override fun toString(): String {
        return "(SinNode: sin{$node})"
    }
}

/**
 * this node takes one Node as parameter, (parenthesisNode)
 * it evaluate that parenthesisNode and get the Cos of that value,
 * then it multiply the value by the sign,
 * then return the value
 * @param node the parenthesisNode
 * @param sign the sign the was left to this node in the equation it will be -1 or +1
 */
class CosNode(private val node: Node, private val sign: Double):Node{
    override fun getValue():Double{
        return sign * cos(Math.toRadians(node.getValue()))
    }
    override fun toString(): String {
        return "(CosNode: cos{$node})"
    }
}

/**
 * this node takes one Node as parameter, (parenthesisNode)
 * it evaluate that parenthesisNode and get the Tan of that value,
 * then it multiply the value by the sign,
 * then return the value
 * @param node the parenthesisNode
 * @param sign the sign the was left to this node in the equation it will be -1 or +1
 */
class TanNode(private val node: Node, private val sign: Double):Node{
    override fun getValue():Double{
        return sign * tan(Math.toRadians(node.getValue()))
    }
    override fun toString(): String {
        return "(TanNode: tan{$node})"
    }
}

/**
 * this node takes one Node as parameter, (parenthesisNode)
 * it evaluate that parenthesisNode and get the Ln of that value,
 * then it multiply the value by the sign,
 * then return the value
 * @param node the parenthesisNode
 * @param sign the sign the was left to this node in the equation it will be -1 or +1
 */
class LnNode(private val node: Node,private val sign: Double):Node{
    override fun getValue():Double{
        return sign * ln(node.getValue())
    }
    override fun toString(): String {
        return "(LnNode: ln{$node})"
    }
}