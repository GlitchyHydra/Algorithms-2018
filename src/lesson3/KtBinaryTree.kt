package lesson3

import java.util.*
import kotlin.NoSuchElementException

// Attention: comparable supported but comparator is not
class KtBinaryTree<T : Comparable<T>> : AbstractMutableSet<T>(), CheckableSortedSet<T> {

    private var root: Node<T>? = null

    override var size = 0
        private set

    private class Node<T>(val value: T) {

        var left: Node<T>? = null

        var right: Node<T>? = null
    }

    override fun add(element: T): Boolean {
        val closest = find(element)
        val comparison = if (closest == null) -1 else element.compareTo(closest.value)
        if (comparison == 0) {
            return false
        }
        val newNode = Node(element)
        when {
            closest == null -> root = newNode
            comparison < 0 -> {
                assert(closest.left == null)
                closest.left = newNode
            }
            else -> {
                assert(closest.right == null)
                closest.right = newNode
            }
        }
        size++
        return true
    }

    override fun checkInvariant(): Boolean =
            root?.let { checkInvariant(it) } ?: true

    private fun checkInvariant(node: Node<T>): Boolean {
        val left = node.left
        if (left != null && (left.value >= node.value || !checkInvariant(left))) return false
        val right = node.right
        return right == null || right.value > node.value && checkInvariant(right)
    }

    /**
     * Удаление элемента в дереве
     * Средняя
     * time complexity = O(logN)
     * memory intesity = O(logN)
     */
    override fun remove(element: T): Boolean {
        if (!this.contains(element)) return false
        var currentNode = root ?: return false
        var parentNode = root ?: return false
        var onRight = true
        while (currentNode.value != element) {
            parentNode = currentNode
            if (element > currentNode.value) {
                currentNode = currentNode.right ?: return false
                onRight = true
            } else if (element < currentNode.value) {
                currentNode = currentNode.left ?: return false
                onRight = false
            }
        }
        if (currentNode.left == null && currentNode.right == null) {
            //if removal point is leaf
            when {
                currentNode == root -> root = null
                onRight -> parentNode.right = null
                else -> parentNode.left = null
            }
        } else if (currentNode.left == null) {
            //if removal point have only right child
            if (currentNode == root) root = currentNode.right
            else {
                val right = currentNode.right ?: return false
                setNode(onRight, parentNode, right)
            }
        } else if (currentNode.right == null) {
            //if removal point have only left child
            if (currentNode == root) root = currentNode.left
            else {
                val left = currentNode.left ?: return false
                setNode(onRight, parentNode, left)
            }
        } else {
            //worst case - if removal point have both children
            var minNode = currentNode.right ?: return false
            var parentMinNode = currentNode.right ?: return false
            while (minNode.left != null) {
                parentMinNode = minNode
                val left = minNode.left ?: return false
                minNode = left
            }
            when {
                currentNode == root && parentMinNode == minNode -> {
                    val rootLeft = root!!.left
                    root = minNode
                    minNode.left = rootLeft
                }
                currentNode == root && parentMinNode != minNode -> {
                    parentMinNode.left = minNode.right
                    root = minNode
                    minNode.left = currentNode.left
                    minNode.right = currentNode.right
                }
                parentMinNode == minNode -> setNode(onRight, parentNode, minNode)
                else -> {
                    parentMinNode.left = minNode.right
                    minNode.right = currentNode.right
                    minNode.left = currentNode.left
                    setNode(onRight, parentNode, minNode)
                }
            }
            minNode.left = currentNode.left
        }

        size--
        return true
    }

    private fun setNode(onRight: Boolean, parentNode: Node<T>, currentNode: Node<T>) {
        if (onRight)
            parentNode.right = currentNode
        else parentNode.left = currentNode
    }

    override operator fun contains(element: T): Boolean {
        val closest = find(element)
        return closest != null && element.compareTo(closest.value) == 0
    }

    private fun find(value: T): Node<T>? =
            root?.let { find(it, value) }

    private fun find(start: Node<T>, value: T): Node<T> {
        val comparison = value.compareTo(start.value)
        return when {
            comparison == 0 -> start
            comparison < 0 -> start.left?.let { find(it, value) } ?: start
            else -> start.right?.let { find(it, value) } ?: start
        }
    }

    inner class BinaryTreeIterator : MutableIterator<T> {

        private var current: Node<T>? = null

        /**
         * Поиск следующего элемента
         * Средняя
         * time complexity = O(lognN)
         * space complexity = O(logN)
         */
        private fun findNext(): Node<T>? {
            if (size == 0) return null
            val currentNode = current ?: return find(first())
            if (currentNode.value == last()) return null
            if (currentNode.right != null) {
                var successor = currentNode.right ?: throw IllegalArgumentException()
                while (successor.left != null) {
                    successor = successor.left ?: return successor
                }
                return successor
            } else {
                var successor = root ?: throw IllegalArgumentException()
                var ancestor = root ?: throw IllegalArgumentException()
                while (ancestor != currentNode) {
                    if (currentNode.value < ancestor.value) {
                        successor = ancestor
                        ancestor = ancestor.left ?: throw IllegalArgumentException()
                    } else ancestor = ancestor.right ?: throw IllegalArgumentException()
                }
                return successor
            }
        }

        override fun hasNext(): Boolean = findNext() != null

        override fun next(): T {
            current = findNext()
            return (current ?: throw NoSuchElementException()).value
        }

        /**
         * Удаление следующего элемента
         * Сложная
         * complexity like in remove
         */
        override fun remove() {
            val cur = current
            current = findNext()
            if (cur != null)
                remove(cur.value)
            else return
        }
    }

    override fun iterator(): MutableIterator<T> = BinaryTreeIterator()

    override fun comparator(): Comparator<in T>? = null

    /**
     * Для этой задачи нет тестов (есть только заготовка subSetTest), но её тоже можно решить и их написать
     * Очень сложная
     * time complexity = O(logN)
     * memory complexity = O(1)
     */
    override fun subSet(fromElement: T, toElement: T): SortedSet<T> =
            SubSet(this, fromElement, toElement)

    /**
     * Найти множество всех элементов меньше заданного
     * Сложная
     * time complexity= O(logN)
     * space complexity = O(1)
     */
    override fun headSet(toElement: T): SortedSet<T> =
            HeadSet(this, toElement)


    /**
     * Найти множество всех элементов больше или равных заданного
     * time complexity = O(logN)
     * space intensity = O(1)
     * Сложная
     */
    override fun tailSet(fromElement: T): SortedSet<T> =
            TailSet(this, fromElement)


    override fun first(): T {
        var current: Node<T> = root ?: throw NoSuchElementException()
        while (current.left != null) {
            current = current.left!!
        }
        return current.value
    }

    override fun last(): T {
        var current: Node<T> = root ?: throw NoSuchElementException()
        while (current.right != null) {
            current = current.right!!
        }
        return current.value
    }
}