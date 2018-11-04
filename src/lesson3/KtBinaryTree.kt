package lesson3

import java.util.*
import kotlin.NoSuchElementException

// Attention: comparable supported but comparator is not
class KtBinaryTree<T : Comparable<T>> : AbstractMutableSet<T>(), CheckableSortedSet<T> {

    private var root: Node<T>? = null
    private var isTailTree: Boolean = false
    private var isHeadTree: Boolean = false
    private var isSubTree: Boolean = false
    private var isParent = false
    private var elem: T? = null
    private var subElems: Pair<T, T>? = null
    private var parentTree: KtBinaryTree<T>? = null
    private var tailChildTrees: TreeMap<T, KtBinaryTree<T>> = TreeMap()
    private var headChildTrees: TreeMap<T, KtBinaryTree<T>> = TreeMap()
    private var subChildTrees: HashMap<Pair<T, T>, KtBinaryTree<T>> = HashMap()

    override var size = 0
        private set

    private class Node<T>(val value: T) {

        var left: Node<T>? = null

        var right: Node<T>? = null
    }

    private fun checkForRemoveOrAdd(element: T): Boolean {
        when {
            isTailTree && element < elem!! -> return false
            isHeadTree && element > elem!! -> return false
            isSubTree && element < subElems!!.first && element > subElems!!.second -> return false
        }
        return true
    }

    override fun add(element: T): Boolean {
        if ((isTailTree || isHeadTree || isSubTree) && !checkForRemoveOrAdd(element))
            return false
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
        val pt = parentTree
        if (parentTree != null && !pt!!.contains(element)) {
            if (!checkForRemoveOrAdd(element))
                return false
            parentTree!!.add(element)
        }
        if (isParent) {
            addToChildren(element)
        }
        return true
    }

    private fun addToChildren(element: T) {
        tailChildTrees.filterKeys { key -> element >= key }.map { entry -> entry.value.add(element) }
        headChildTrees.filterKeys { key -> element < key }.map { entry -> entry.value.add(element) }
        subChildTrees.filterKeys { key -> element >= key.first && element < key.second }
                .map { entry -> entry.value.add(element) }
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
        if (parentTree != null) {
            if (!checkForRemoveOrAdd(element))
                return false
            parentTree!!.remove(element)
        }
        if (isParent)
            removeFromChildren(element)
        size--
        return true
    }

    private fun removeFromChildren(element: T) {
        tailChildTrees.filterKeys { key -> element >= key }.map { entry -> entry.value.remove(element) }
        headChildTrees.filterKeys { key -> element < key }.map { entry -> entry.value.remove(element) }
        subChildTrees.filterKeys { key -> element >= key.first && element < key.second }
                .map { entry -> entry.value.remove(element) }
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
     * time complexity = O(N + M)
     * memory complexity = O(N + M)
     */
    override fun subSet(fromElement: T, toElement: T): SortedSet<T> {
        if (subChildTrees.containsKey(Pair(fromElement, toElement)))
            return subChildTrees[Pair(fromElement, toElement)]!!
        val subSet = KtBinaryTree<T>()
        subSet.addAll(tailSet(fromElement).intersect(headSet(toElement)))
        subSet.parentTree = this
        subSet.subElems = Pair(fromElement, toElement)
        subSet.isSubTree = true
        this.isParent = true
        this.subChildTrees[Pair(fromElement, toElement)] = subSet
        return subSet
    }

    /**
     * Найти множество всех элементов меньше заданного
     * Сложная
     * time complexity= O(N + M)
     * space complexity = O(M)
     */
    override fun headSet(toElement: T): SortedSet<T> {
        if (headChildTrees.containsKey(toElement)) return headChildTrees[toElement]!!
        //O(N)
        val headSet = KtBinaryTree<T>()
        headSet.parentTree = this
        headSet.elem = toElement
        val iter = iterator()
        var element: T = iter.next()
        while (element < toElement) {
            headSet.add(element)
            if (!iter.hasNext()) break
            element = iter.next()
        }
        //O(M)
        headSet.isHeadTree = true
        this.isParent = true
        this.headChildTrees[toElement] = headSet
        return headSet
    }

    /**
     * Найти множество всех элементов больше или равных заданного
     * time complexity = O(N + M)
     * space intensity = O(M)
     * Сложная
     */
    override fun tailSet(fromElement: T): SortedSet<T> {
        if (tailChildTrees.containsKey(fromElement)) return tailChildTrees[fromElement]!!
        //O(N)
        val tailSet = KtBinaryTree<T>()
        tailSet.parentTree = this
        tailSet.elem = fromElement
        val iter = this.iterator()
        var element: T = iter.next()
        if (fromElement > first()) {
            while (iter.hasNext() && element != fromElement) {
                element = iter.next()
            }
        }
        while (element <= last()) {
            tailSet.add(element)
            if (!iter.hasNext()) break
            element = iter.next()
        }
        //O(M)
        tailSet.isTailTree = true
        this.isParent = true
        this.tailChildTrees[fromElement] = tailSet
        return tailSet
    }

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