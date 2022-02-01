package com.awab.calculator.utils

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
        var currentValue = iterator.next()
        while (true) {
            //  the number Nodes and the un targeted token types will get skipped
            if (currentValue !is Token || currentValue.tokenType !in targetedTokenTypes) {
                newTree.add(currentValue)
                if (iterator.hasNext()) {
                    currentValue = iterator.next()
                    continue
                } else
                    break
            }
            //  making nodes for the targeted tokens
            when (currentValue.tokenType) {
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
                    val node = SubtractNode(beforeValue as Node, nextValue as Node)
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
                    val node = SquareRootNode(nextValue as Node,currentValue.sign)
                    newTree.add(node)
                }
                TokenType.SIN -> {
                    val nextValue = iterator.next()
                    val node = SinNode(nextValue as Node,currentValue.sign)
                    newTree.add(node)
                }
                TokenType.COS -> {
                    val nextValue = iterator.next()
                    val node = CosNode(nextValue as Node,currentValue.sign)
                    newTree.add(node)
                }
                TokenType.TAN -> {
                    val nextValue = iterator.next()
                    val node = TanNode(nextValue as Node,currentValue.sign)
                    newTree.add(node)
                }
                TokenType.LN -> {
                    val nextValue = iterator.next()
                    val node = LnNode(nextValue as Node,currentValue.sign)
                    newTree.add(node)
                }
                else -> {}
            }
            if (!iterator.hasNext()) {
                break
            }
            currentValue = iterator.next()
        }
        return newTree
    }

    /**
     * this function take thee tree and create all the NumberNodes
     * @return a tree with NumberNodes instead of Number Token
     */
    private fun makeNumbersNodes(tree: ArrayList<Any>): ArrayList<Any> {
        val newTree = ArrayList<Any>()
            for (it in tree){
                if (it is Token && it.tokenType == TokenType.NUMBER) {
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
    private fun findParenthesis(tokens: ArrayList<Token>): ArrayList<Any> {
        val newTokens = ArrayList<Any>()
        val tokenIterator = tokens.iterator()
        var currentToken = tokenIterator.next()

        while (true) {
            if (currentToken.tokenType == TokenType.L_PARENTHESIS) {
                //  the sign left to the parenthesis
                val parenthesisSign = currentToken.sign
                currentToken = tokenIterator.next()
                var lPCounter = 0
                val parenthesisTokens = ArrayList<Token>()
                while (true) {
                    // the LeftParenthesisCounter {lPCounter} will get incremented
                    // every time a left Parenthesis comes, and decremented every time a right parenthesis comes
                    // so when the lPCounter == 0 that means its the closing right parenthesis in the top level
                    if (currentToken.tokenType == TokenType.L_PARENTHESIS) {
                        lPCounter++
                        parenthesisTokens.add(currentToken)
                    } else if (currentToken.tokenType == TokenType.R_PARENTHESIS) {
                        if (lPCounter > 0) {
                            lPCounter--
                            parenthesisTokens.add(currentToken)
                        }
                        if (lPCounter == 0) {
                            newTokens.add(ParenthesisNode(parenthesisTokens, parenthesisSign))
                            if (tokenIterator.hasNext())
                                currentToken = tokenIterator.next()
                            break
                        }
                    } else {
                        parenthesisTokens.add(currentToken)
                    }

                    if (tokenIterator.hasNext())
                        currentToken = tokenIterator.next()
                    else
                        break
                }
            } else {
                newTokens.add(currentToken)
                if (tokenIterator.hasNext())
                    currentToken = tokenIterator.next()
                else {
//                    if the last token in tokens is ")" it will get removed
                    if (currentToken.tokenType == TokenType.R_PARENTHESIS)
                        newTokens.removeLast()
                    break
                }
            }
        }
        return newTokens
    }
}