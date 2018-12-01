@file:Suppress("UNUSED_PARAMETER")

package lesson6

import java.io.File

/**
 * Наибольшая общая подпоследовательность.
 * Средняя
 *
 * Дано две строки, например "nematode knowledge" и "empty bottle".
 * Найти их самую длинную общую подпоследовательность -- в примере это "emt ole".
 * Подпоследовательность отличается от подстроки тем, что её символы не обязаны идти подряд
 * (но по-прежнему должны быть расположены в исходной строке в том же порядке).
 * Если общей подпоследовательности нет, вернуть пустую строку.
 * При сравнении подстрок, регистр символов *имеет* значение.
 */
fun longestCommonSubSequence(first: String, second: String): String {
    /**
     * N = first.length
     * M = second.length
     * time complexity O(N * M)
     * space complexity O(N * M)
     */
    val matrix = Array(first.length + 1) { IntArray(second.length + 1) }
    for (i in 1 until matrix.size) {
        for (j in 1 until matrix[i].size) {
            if (first[i - 1] == second[j - 1]) {
                matrix[i][j] = matrix[i - 1][j - 1] + 1
            } else {
                matrix[i][j] = maxOf(matrix[i][j - 1], matrix[i - 1][j])
            }
        }
    }
    var maxSubString = ""
    var i = first.length
    var j = second.length
    //Вывод подпоследовательности
    while (i > 0 && j > 0) {
        when {
            //если символ из первой и второй равен то нашли нужный
            first[i - 1] == second[j - 1] -> {
                maxSubString += first[i - 1]
                i--; j--
            }
            //берем больший из левого и верхнего
            matrix[i][j - 1] > matrix[i - 1][j] -> j--
            matrix[i][j - 1] < matrix[i - 1][j] -> i--
            //верхний = левому идем в любую сторону (в данном случае влево)
            else -> i--
        }
    }
    return maxSubString.reversed()
}

/**
 * Наибольшая возрастающая подпоследовательность
 * Средняя
 *
 * Дан список целых чисел, например, [2 8 5 9 12 6].
 * Найти в нём самую длинную возрастающую подпоследовательность.
 * Элементы подпоследовательности не обязаны идти подряд,
 * но должны быть расположены в исходном списке в том же порядке.
 * Если самых длинных возрастающих подпоследовательностей несколько (как в примере),
 * то вернуть ту, в которой числа расположены раньше (приоритет имеют первые числа).
 * В примере ответами являются 2, 8, 9, 12 или 2, 5, 9, 12 -- выбираем первую из них.
 */
fun longestIncreasingSubSequence(list: List<Int>): List<Int> {
    /**
     * row size - arr.size
     * time complexity O(n * n/2)
     * space complexity O(3n) -> O(n)
     */
    if (list.isEmpty()) return emptyList()
    //initial array
    val arr = list.toIntArray()
    //array of lengths, all single elements is subseq and they length = 1
    val lengthArray = IntArray(list.size) { 1 }
    //index of last larger element in subseq
    val indexArray = IntArray(list.size)

    (0 until arr.size).forEach { i -> lengthArray[i] = 1; indexArray[i] = i; }

    //time complexity O(arr.size * arr.size/2)
    //outer cycle O(arr.size)
    (1 until arr.size).forEach { i ->
        //inner cycle(arr.size/2)
        (0 until i).forEach { j ->
            if (arr[i] > arr[j]) {
                if (lengthArray[j] + 1 > lengthArray[i]) {
                    lengthArray[i] = lengthArray[j] + 1
                    indexArray[i] = j
                }
            }
        }
    }

    //find the index of max number
    var maxIndex = 0
    for (i in 0 until lengthArray.size) {
        if (lengthArray[i] > lengthArray[maxIndex]) {
            maxIndex = i
        }
    }

    //look for largest subseq by taking indexes in IndexArray
    //and add element from initial array
    var t: Int
    val l = mutableListOf<Int>()
    var newT = maxIndex
    do {
        t = newT
        l.add(arr[t])
        newT = indexArray[t]
    } while (t != newT)

    return l.reversed()
}

/**
 * Самый короткий маршрут на прямоугольном поле.
 * Сложная
 *
 * В файле с именем inputName задано прямоугольное поле:
 *
 * 0 2 3 2 4 1
 * 1 5 3 4 6 2
 * 2 6 2 5 1 3
 * 1 4 3 2 6 2
 * 4 2 3 1 5 0
 *
 * Можно совершать шаги длиной в одну клетку вправо, вниз или по диагонали вправо-вниз.
 * В каждой клетке записано некоторое натуральное число или нуль.
 * Необходимо попасть из верхней левой клетки в правую нижнюю.
 * Вес маршрута вычисляется как сумма чисел со всех посещенных клеток.
 * Необходимо найти маршрут с минимальным весом и вернуть этот минимальный вес.
 *
 * Здесь ответ 2 + 3 + 4 + 1 + 2 = 12
 */
fun shortestPathOnField(inputName: String): Int {
    /**
     * time complexity = O(m * n)
     * space complexity = O(m * n)
     */
    val taskMatrix = File(inputName).readLines()
            .map { it -> it.split(' ').map { it.toInt() }.toIntArray() }
            .toTypedArray()
    val m = taskMatrix.size - 1
    val n = taskMatrix[0].size - 1
    val temp = Array(m + 1) { IntArray(n + 1) { 0 } }
    var sum = 0

    (0..n).forEach { i ->
        sum += taskMatrix[0][i]
        temp[0][i] = sum
    }

    sum = 0

    (0..m).forEach { i ->
        sum += taskMatrix[i][0]
        temp[i][0] = sum
    }

    (1..m).forEach { i ->
        (1..n).forEach { j ->
            temp[i][j] = taskMatrix[i][j] + minOf(temp[i - 1][j - 1], temp[i - 1][j], temp[i][j - 1])
        }
    }

    return temp[m][n]
}

// Задачу "Максимальное независимое множество вершин в графе без циклов"
// смотрите в уроке 5