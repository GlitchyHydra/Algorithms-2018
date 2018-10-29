package lesson3

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
    override fun iterator(): MutableIterator<String> {
        /**
         * n - char counts
         * time complexity = O(n)
         * m - counts of loops in traverse
         * space complexity = O(n * m)
         */
        val visitedWords = mutableSetOf<String>()
        for (element in root.children) {
            val word = mutableListOf<Char>()
            word.add(element.key)
            traverse(element.value, visitedWords, word)
        }
        return visitedWords.iterator()
    }


    private fun traverse(trieNode: Node, visitedWords: MutableSet<String>, word: MutableList<Char>) {
        val checkIfWord = trieNode.children.isEmpty() || trieNode.children.containsKey(0.toChar())
        val wordInString = word.joinToString(prefix = "", separator = "", postfix = "")
        if (checkIfWord && !visitedWords.contains(wordInString)) {
            visitedWords.add(wordInString)
            word.removeAt(word.lastIndex)
            return
        }
        for (char in trieNode.children) {
            word.add(char.key)
            traverse(char.value, visitedWords, word)
        }
        word.removeAt(word.lastIndex)
        return
    }
}