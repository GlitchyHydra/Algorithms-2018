package lesson3

import java.util.ArrayDeque
import java.util.Stack

class Trie : AbstractMutableSet<String>(), MutableSet<String> {
    override var size: Int = 0
        private set

    private class Node {
        val children: MutableMap<Char, Node> = linkedMapOf()
    }

    private var root = Node()

    override fun clear() {
        root.children.clear()
        size = 0
    }

    private fun String.withZero() = this + 0.toChar()

    private fun findNode(element: String): Node? {
        var current = root
        for (char in element) {
            current = current.children[char] ?: return null
        }
        return current
    }

    override fun contains(element: String): Boolean =
            findNode(element.withZero()) != null

    override fun add(element: String): Boolean {
        var current = root
        var modified = false
        for (char in element.withZero()) {
            val child = current.children[char]
            if (child != null) {
                current = child
            } else {
                modified = true
                val newChild = Node()
                current.children[char] = newChild
                current = newChild
            }
        }
        if (modified) {
            size++
        }
        return modified
    }

    override fun remove(element: String): Boolean {
        val current = findNode(element) ?: return false
        if (current.children.remove(0.toChar()) != null) {
            size--
            return true
        }
        return false
    }

    /**
     * Итератор для префиксного дерева
     * Сложная
     */
    override fun iterator(): MutableIterator<String> = object : MutableIterator<String> {
        /**
         * n - char counts
         * time complexity = O(n)
         * m - counts of loops in traverse
         * space complexity = O(n * m)
         */
        private val st = traverseAll()

        private var next: String? = null
        private var current: String? = null

        init {
            next = st.poll()
        }

        override fun hasNext(): Boolean {
            return next != null
        }

        override fun next(): String {
            val result = next ?: throw NoSuchElementException()
            current = result
            next = if (st.size != 0) st.poll() else null
            return result
        }

        override fun remove() {
            remove(current)
        }
    }


    private fun traverseAll(): ArrayDeque<String> {
        val visitedWords = ArrayDeque<String>()
        for (element in root.children) {
            val word = Stack<Char>()
            word.add(element.key)
            traverse(visitedWords, element.value, word)
        }
        return visitedWords
    }

    private fun traverse(visitedWords: ArrayDeque<String>, trieNode: Node, word: Stack<Char>) {
        if (trieNode.children.isEmpty() && word.last() == 0.toChar()) {
            word.pop()
            val wordInString = word.joinToString(prefix = "", separator = "", postfix = "")
            visitedWords.add(wordInString)
            return
        }
        for (char in trieNode.children) {
            word.add(char.key)
            traverse(visitedWords, char.value, word)
        }
        word.pop()
        return
    }
}