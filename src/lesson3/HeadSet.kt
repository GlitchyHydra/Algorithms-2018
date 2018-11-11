package lesson3

import java.util.*

class HeadSet<T : Comparable<T>>(private val delegate: SortedSet<T>, private val toElement: T) : AbstractMutableSet<T>(), SortedSet<T> {
    override fun comparator(): Comparator<in T>? = delegate.comparator()

    override fun subSet(fromElement: T, toElement: T): SortedSet<T> {
        return SubSet(this, fromElement, toElement)
    }

    override fun headSet(toElement: T): SortedSet<T> {
        return HeadSet(this, toElement)
    }

    override fun tailSet(fromElement: T): SortedSet<T> {
        return TailSet(this, fromElement)
    }

    override fun last(): T {
        val iter = iterator()
        var element = iter.next()
        while (iter.hasNext()) {
            element = iter.next()
        }
        return element
    }

    override fun first(): T {
        return iterator().next()
    }

    override fun add(element: T): Boolean {
        if (element >= toElement) return false
        return delegate.add(element)
    }

    override fun remove(element: T): Boolean {
        if (element >= toElement) return false
        return delegate.remove(element)
    }

    override fun iterator(): MutableIterator<T> = object : MutableIterator<T> {
        private val delegate = this@HeadSet.delegate.iterator()

        private var next: T? = null

        init {
            while (delegate.hasNext()) {
                val next = delegate.next()
                if (next < toElement) {
                    this.next = next
                    break
                }
            }
        }

        override fun hasNext(): Boolean {
            val n = next ?: return false
            return n < toElement
        }

        override fun next(): T {
            val result = next ?: throw NoSuchElementException()
            next = if (delegate.hasNext()) delegate.next() else null
            return result
        }

        override fun remove() {
            delegate.remove()
        }

    }

    override val size: Int
        get() = delegate.count { it < toElement }

}