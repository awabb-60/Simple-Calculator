package com.awab.calculator.utils.calculator_utils

import com.awab.calculator.utils.ORDER_OF_OPERATIONS

/**
 * the parser class is responsible of building an executable tree of Nodes
 */
class Parser {
    private var tree = ArrayList<Any>()

    /**
     * this function will make and returns an executable tree of nodes,
     * the tree is made of one Node that has other nodes as parameters and these parameters nodes
     * will have other nodes as parameters and so on the nodes will get nested inside each other as parameters,
     * so when you try to get the value of the top node... all the nested nodes will get evaluated to return the
     * value of the top one
     *for example : take 1+2*3
     * the parser will create the multiplicationNode first the tree will be:
     * 1+(MultiplicationNode between 2 and 3),
     * then the parser will create the AdditionNode, so the tree will be: AdditionNode between 1 and
     * (MultiplicationNode between 2 and 3)
     * @param tokens the tokens that will get parsed to make the tree
     *@return an executable Node
     */
    fun generateTree(tokens: ArrayList<Token>): Node {
        // the first nodes that get created will be evaluated first

        // creating the top level parenthesis nodes first
        tree = findParenthesis(tokens)

        // making Numbers Nodes
        tree = makeNumbersNodes(tree)

//        now the rest of the nodes will get created based on the order of operation
//        the first node that get created will get evaluated first
//        if there is multiple operations sharing the same order... thy will get created first from
//        left to right
        ORDER_OF_OPERATIONS.forEach {
            tree = makeNodesFor(*it)
        }

        return tree[0] as Node
    }

    /**
     * this function takes a vararg of token types then it loop in the tree and make nodes for them
     * @param targetedTokenTypes the targeted token types to create nodes for
     */
    private fun makeNodesFor(vararg targetedTokenTypes: TokenType): ArrayList<Any> {
        val iterator = tree.iterator()
        val newTree = ArrayList<Any>()
        var currentValue = Any()

        fun goToNext(): Boolean {
            return if (iterator.hasNext()) {
                currentValue = iterator.next()
                true
            } else
                false
        }

        while (goToNext()) {
            //  the number Nodes and the un targeted token types will get skipped
            if (currentValue is Token && (currentValue as Token).tokenType in targetedTokenTypes) {

                with(currentValue as Token) {
                    //  making nodes for the targeted tokens
                    when (this.tokenType) {
                        TokenType.ADDITION -> {
                            val beforeValue = newTree.last()
                            val nextValue = iterator.next()
                            val node = AdditionNode(beforeValue as Node, nextValue as Node)
                            newTree.remove(beforeValue)
                            newTree.add(node)
                        }
                        TokenType.SUBTRACT -> {
                            val beforeValue = newTree.last()
                            val nextValue = iterator.next()
                            val node = SubtractionNode(beforeValue as Node, nextValue as Node)
                            newTree.remove(beforeValue)
                            newTree.add(node)
                        }
                        TokenType.EXPONENT -> {
                            val beforeValue = newTree.last()
                            val nextValue = iterator.next()
                            val node = ExponentNode(beforeValue as Node, nextValue as Node)
                            newTree.remove(beforeValue)
                            newTree.add(node)
                        }
                        TokenType.MULTIPLICATION -> {
                            val beforeValue = newTree.last()
                            val nextValue = iterator.next()
                            val node = MultiplicationNode(beforeValue as Node, nextValue as Node)
                            newTree.remove(beforeValue)
                            newTree.add(node)
                        }
                        TokenType.DIVISION -> {
                            val beforeValue = newTree.last()
                            val nextValue = iterator.next()
                            val node = DivisionNode(beforeValue as Node, nextValue as Node)
                            newTree.remove(beforeValue)
                            newTree.add(node)
                        }
                        TokenType.SQUARE_ROOT -> {
                            val nextValue = iterator.next()
                            val node = SquareRootNode(nextValue as Node, this.sign)
                            newTree.add(node)
                        }
                        TokenType.SIN -> {
                            val nextValue = iterator.next()
                            val node = SinNode(nextValue as Node, this.sign)
                            newTree.add(node)
                        }
                        TokenType.COS -> {
                            val nextValue = iterator.next()
                            val node = CosNode(nextValue as Node, this.sign)
                            newTree.add(node)
                        }
                        TokenType.TAN -> {
                            val nextValue = iterator.next()
                            val node = TanNode(nextValue as Node, this.sign)
                            newTree.add(node)
                        }
                        TokenType.LN -> {
                            val nextValue = iterator.next()
                            val node = LnNode(nextValue as Node, this.sign)
                            newTree.add(node)
                        }
                        else -> { }
                    }
                }
            } else
                newTree.add(currentValue)
        }
        return newTree
    }

    /**
     * this function take thee tree and create all the NumberNodes
     * @return a tree with NumberNodes instead of Number Token
     */
    fun makeNumbersNodes(tree: ArrayList<Any>): ArrayList<Any> {
        val newTree = ArrayList<Any>()
        for (it in tree) {
            if (it is Token && it.tokenType == TokenType.NUMBER) {
                // multiplying the sign here so the value of the number get displayed right
                newTree.add(NumberNode(it.value * it.sign))
                continue
            }
            newTree.add(it)
        }
        return newTree
    }

    /**
     * this function takes the tokens list and create only the top level Parenthesis Nodes like:
     * 2+ '(' 1+(-3) ')'
     * and all the tokens inside of it will get passed as parameters to the ParenthesisNode
     *
     * @return a tree with ParenthesisNode instead of the top level parenthesis
     */
    fun findParenthesis(tokens: ArrayList<Token>): ArrayList<Any> {
        val newTokens = ArrayList<Any>()
        val tokenIterator = tokens.iterator()

        var currentToken = Token(TokenType.NUMBER)

        fun goToNext(): Boolean {
            return if (tokenIterator.hasNext()) {
                currentToken = tokenIterator.next()
                true
            } else false
        }

        // looping throw the list of tokens
        while (goToNext()) {
            if (currentToken.tokenType == TokenType.L_PARENTHESIS) {
                //  the sign of the parenthesis
                val parenthesisSign = currentToken.sign

                // the counter that will find the corresponding right parenthesis
                // start with 1 ... the initial left parentheses
                var leftParenthesisCounter = 1

                // the tokens inside the top level parenthesis
                val parenthesisTokens = ArrayList<Token>()


                while (goToNext()) {
                    // the LeftParenthesisCounter will get incremented
                    // every time a left Parenthesis comes, and decremented every time a right parenthesis comes
                    // so when the LeftParenthesisCounter == 0 that means its the closing right parenthesis in the top level
                    // if the top top parenthesis is not closed it will get ignored
                    if (currentToken.tokenType == TokenType.L_PARENTHESIS) {
                        leftParenthesisCounter++
                        parenthesisTokens.add(currentToken)
                    } else if (currentToken.tokenType == TokenType.R_PARENTHESIS) {
                        leftParenthesisCounter--
                        // this right parenthesis is inside the top level parenthesis
                        if (leftParenthesisCounter > 0) {
                            parenthesisTokens.add(currentToken)
                        }
                        // this right parenthesis is a closer for the top level parenthesis
                        else if (leftParenthesisCounter == 0) {
                            newTokens.add(ParenthesisNode(parenthesisTokens, parenthesisSign))
                            break
                        }
                    } else {
                        parenthesisTokens.add(currentToken)
                    }
                }
            } else {
                newTokens.add(currentToken)
            }
        }
        return newTokens
    }
}