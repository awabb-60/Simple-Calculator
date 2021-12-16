package com.awab.calculator.utils

class Parser(private val tokens: ArrayList<Token>) {
    private var tree = ArrayList<Any>()

    fun generateTree(): Node {


//      find the parenthesis
        tree = findParenthesis(tokens)

//      making Numbers Nodes
        tree = makeNumbersNodes(tree)

//    # Done
    fun makeNodesFor(vararg tokenTypes: TokenType){
        mainWhile@ while (true) {
            for (node in tree) {
                if (node is Token) {
                    if (node.tokenType in tokenTypes && node.tokenType == TokenType.MULTIPLY) {
                        val idx = tree.indexOf(node)
                        val beforeNode = tree[idx - 1]
                        val afterNode = tree[idx + 1]
                        tree[idx] = MultiplyNode(beforeNode as Node, afterNode as Node)
                        tree.remove(beforeNode)
                        tree.remove(afterNode)
                        continue@mainWhile
                    } else if (node.tokenType in tokenTypes && node.tokenType == TokenType.DIVISION) {
                        val idx = tree.indexOf(node)
                        val beforeNode = tree[idx - 1]
                        val afterNode = tree[idx + 1]
                        tree[idx] = DivisionNode(beforeNode as Node, afterNode as Node)
                        tree.remove(beforeNode)
                        tree.remove(afterNode)
                        continue@mainWhile
                    }else if (node.tokenType in tokenTypes && node.tokenType == TokenType.PLUS) {
                        val idx = tree.indexOf(node)
                        val beforeNode = tree[idx - 1]
                        val afterNode = tree[idx + 1]
                        tree[idx] = AddNode(beforeNode as Node, afterNode as Node)
                        tree.remove(beforeNode)
                        tree.remove(afterNode)
                        continue@mainWhile
                    } else if (node.tokenType in tokenTypes && node.tokenType == TokenType.SUBTRACT) {
                        val idx = tree.indexOf(node)
                        val beforeNode = tree[idx - 1]
                        val afterNode = tree[idx + 1]
                        tree[idx] = SubtractNode(beforeNode as Node, afterNode as Node)
                        tree.remove(beforeNode)
                        tree.remove(afterNode)
                        continue@mainWhile
                    }

                }
            }
            break
        }
    }

//        the first nodes will get evaluated first

//      making multiplication and division Nodes
        makeNodesFor(TokenType.MULTIPLY, TokenType.DIVISION)

//      making Add and Subtraction Nodes
        makeNodesFor(TokenType.PLUS, TokenType.SUBTRACT)
    return tree[0] as Node
    }

//    turn the number tokens into number nodes  #Done
    private fun makeNumbersNodes(tree: ArrayList<Any>): ArrayList<Any> {
        val newTree = ArrayList<Any>()
//      making the numbers nodes for the tree and the number tokens inside the parenthesis nodes
//      will get generated when the p node get solved
        tree.forEach {
            if (it is Token){
                if (it.tokenType == TokenType.NUMBER) {
                    newTree.add(NumberNode(it.value as Double))
                }else
                    newTree.add(it)
            }else  // it's a parenthesis node
                newTree.add(it)
        }
        return newTree
    }
//    unneeded
    private fun findParenthesis(tokens: ArrayList<Token>): ArrayList<Any> {
        val newTokens = ArrayList<Any>()
        val tokenIterator = tokens.iterator()
        var currentToken: Token? = tokenIterator.next()
        while (true) {
            if (currentToken?.tokenType == TokenType.L_PARENTHESIS) {
                currentToken = tokenIterator.next()
                var lCounter = 0
                val parenthesisTokens = ArrayList<Token>()
                while (true) {
                    if (currentToken?.tokenType == TokenType.L_PARENTHESIS){
                        lCounter++
                        parenthesisTokens.add(currentToken!!)
                    }
                    else if (currentToken?.tokenType == TokenType.R_PARENTHESIS) {
                        if (lCounter > 0){
                            lCounter--
                            parenthesisTokens.add(currentToken!!)
                        }
                        if (lCounter == 0){
                            newTokens.add(ParenthesisNode(parenthesisTokens))
                            if (tokenIterator.hasNext())
                                currentToken = tokenIterator.next()
                            break
                        }
                    }else{
                        parenthesisTokens.add(currentToken!!)
                    }

                    if (tokenIterator.hasNext())
                        currentToken = tokenIterator.next()
                    else
                        break
                }

            }else {
                newTokens.add(currentToken!!)
                if (tokenIterator.hasNext())
                    currentToken = tokenIterator.next()
                else
                    break
            }
        }
        return newTokens
    }
}