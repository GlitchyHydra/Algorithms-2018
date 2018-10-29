package lesson1

import lesson3.CheckableSortedSet
import lesson3.KtBinaryTree
import java.util.*

private val random = Random(Calendar.getInstance().timeInMillis)

fun <T : Comparable<T>> insertionSort(elements: Array<T>) {
    for (i in 1 until elements.size) {
        val current = elements[i]
        var j = i - 1
        while (j >= 0) {
            if (elements[j] > current)
                elements[j + 1] = elements[j]
            else
                break
            j--
        }
        elements[j + 1] = current
    }
}

fun insertionSort(elements: IntArray) {
    for (i in 1 until elements.size) {
        val current = elements[i]
        var j = i - 1
        while (j >= 0) {
            if (elements[j] > current)
                elements[j + 1] = elements[j]
            else
                break
            j--
        }
        elements[j + 1] = current
    }
}

private fun merge(elements: DoubleArray, begin: Int, middle: Int, end: Int) {
    val left = Arrays.copyOfRange(elements, begin, middle)
    val right = Arrays.copyOfRange(elements, middle, end)
    var li = 0
    var ri = 0
    for (i in begin until end) {
        if (li < left.size && (ri == right.size || left[li] <= right[ri])) {
            elements[i] = left[li++]
        } else {
            elements[i] = right[ri++]
        }
    }
}

private fun merge(elements: IntArray, begin: Int, middle: Int, end: Int) {
    val left = Arrays.copyOfRange(elements, begin, middle)
    val right = Arrays.copyOfRange(elements, middle, end)
    var li = 0
    var ri = 0
    for (i in begin until end) {
        if (elements[i] !in -273.0..500.0) throw IllegalArgumentException("Not in range")
        if (li < left.size && (ri == right.size || left[li] <= right[ri])) {
            elements[i] = left[li++]
        } else {
            elements[i] = right[ri++]
        }
    }
}

private fun mergeSort(elements: IntArray, begin: Int, end: Int) {
    if (end - begin <= 1) return
    val middle = (begin + end) / 2
    mergeSort(elements, begin, middle)
    mergeSort(elements, middle, end)
    merge(elements, begin, middle, end)
}

private fun mergeSort(elements: DoubleArray, begin: Int, end: Int) {
    if (end - begin <= 1) return
    val middle = (begin + end) / 2
    mergeSort(elements, begin, middle)
    mergeSort(elements, middle, end)
    merge(elements, begin, middle, end)
}

fun mergerSort(elements: DoubleArray) {
    mergeSort(elements, 0, elements.size)
}

fun mergeSort(elements: IntArray) {
    mergeSort(elements, 0, elements.size)
}

private fun heapify(elements: IntArray, start: Int, length: Int) {
    val left = 2 * start + 1
    val right = left + 1
    var max = start
    if (left < length && elements[left] > elements[max]) {
        max = left
    }
    if (right < length && elements[right] > elements[max]) {
        max = right
    }
    if (max != start) {
        val temp = elements[max]
        elements[max] = elements[start]
        elements[start] = temp
        heapify(elements, max, length)
    }
}

private fun buildHeap(elements: IntArray) {
    for (start in elements.size / 2 - 1 downTo 0) {
        heapify(elements, start, elements.size)
    }
}

fun heapSort(elements: IntArray) {
    buildHeap(elements)
    for (j in elements.size - 1 downTo 1) {
        val temp = elements[0]
        elements[0] = elements[j]
        elements[j] = temp
        heapify(elements, 0, j)
    }
}

private fun partition(elements: IntArray, min: Int, max: Int): Int {
    val x = elements[min + random.nextInt(max - min + 1)]
    var left = min
    var right = max
    while (left <= right) {
        while (elements[left] < x) {
            left++
        }
        while (elements[right] > x) {
            right--
        }
        if (left <= right) {
            val temp = elements[left]
            elements[left] = elements[right]
            elements[right] = temp
            left++
            right--
        }
    }
    return right
}

private fun quickSort(elements: IntArray, min: Int, max: Int) {
    if (min < max) {
        val border = partition(elements, min, max)
        quickSort(elements, min, border)
        quickSort(elements, border + 1, max)
    }
}

fun quickSort(elements: IntArray) {
    quickSort(elements, 0, elements.size - 1)
}

//O(n + m)
fun countingSort(elements: IntArray, limit: Int): IntArray {
    val count = IntArray(limit + 1)
    for (element in elements) {
        count[element]++
    }
    for (j in 1..limit) {
        count[j] += count[j - 1]
    }
    //O(m)
    val out = IntArray(elements.size)
    for (j in elements.indices.reversed()) {
        out[count[elements[j]] - 1] = elements[j]
        count[elements[j]]--
    }
    //O(n)
    return out
}

fun countingSortForSeq(elements: IntArray, limit: Int): IntArray {
    val count = IntArray(limit + 1)
    for (element in elements) {
        count[element]++
    }
    //time complexity O(N)
    //space complexity O(N)
    val max = count.max()
    var min = 0
    for (i in 0..limit) {
        if (max == count[i]) {
            min = i
            break
        }
    }
    //time complexity O(N)
    //space complexity O(N)
    val out = IntArray(elements.size)
    var index = 0
    var offset = 0
    while (index < elements.size) {
        if (elements[index] == min) {
            offset++
        } else out[index - offset] = elements[index]
        index++
    }
    //time complexity O(N)
    //space complexity O(N)
    for (i in elements.size - max!! until elements.size)
        out[i] = min
    return out
}