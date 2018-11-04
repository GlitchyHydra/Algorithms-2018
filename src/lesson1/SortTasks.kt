@file:Suppress("UNUSED_PARAMETER")

package lesson1

import java.io.File
import java.util.TreeMap
import java.util.TreeSet


/**
 * Сортировка времён
 *
 * Простая
 * (Модифицированная задача с сайта acmp.ru)
 *
 * Во входном файле с именем inputName содержатся моменты времени в формате ЧЧ:ММ:СС,
 * каждый на отдельной строке. Пример:
 *
 * 13:15:19
 * 07:26:57
 * 10:00:03
 * 19:56:14
 * 13:15:19
 * 00:40:31
 *
 * Отсортировать моменты времени по возрастанию и вывести их в выходной файл с именем outputName,
 * сохраняя формат ЧЧ:ММ:СС. Одинаковые моменты времени выводить друг за другом. Пример:
 *
 * 00:40:31
 * 07:26:57
 * 10:00:03
 * 13:15:19
 * 13:15:19
 * 19:56:14
 *
 * В случае обнаружения неверного формата файла бросить любое исключение.
 */
fun sortTimes(inputName: String, outputName: String) {
    /**
     * labor intensity = O(NlogN)
     * memory intensity = O(n)
     */
    val listOfTime = File(inputName).readLines().map { it -> it.split(':') }.map { it ->
        it.reversed().foldIndexed(0)
        { index, prev, elem ->
            Array(index) { 60 }.fold(1) { prev1, elem1 -> prev1 * elem1} * elem.toInt() + prev }
    }.toIntArray()
    //O(n)
    heapSort(listOfTime)
    //sort O(nlogn) > O(n) => O(NlogN) = time complexity
    //space complexity = O(1) for sort
    val outputFile = File(outputName).bufferedWriter()
    for (i in 0..listOfTime.lastIndex) {
        outputFile.write("${toRightFormat(listOfTime[i] / 3600)}:" +
                "${toRightFormat((listOfTime[i] / 60) % 60)}:" +
                toRightFormat(listOfTime[i] % 60))
        outputFile.newLine()
    }
    //O(n + n) => O(n) - time complexity
    //space complexity O(n)
    outputFile.close()
}

fun toRightFormat(partOfTime: Int): String {
    if (partOfTime in 0..9) {
        return "0$partOfTime"
    }
    return "$partOfTime"
}

/**
 * Сортировка адресов
 *
 * Средняя
 *
 * Во входном файле с именем inputName содержатся фамилии и имена жителей города с указанием улицы и номера дома,
 * где они прописаны. Пример:
 *
 * Петров Иван - Железнодорожная 3
 * Сидоров Петр - Садовая 5
 * Иванов Алексей - Железнодорожная 7
 * Сидорова Мария - Садовая 5
 * Иванов Михаил - Железнодорожная 7
 *
 * Людей в городе может быть до миллиона.
 *
 * Вывести записи в выходной файл outputName,
 * упорядоченными по названию улицы (по алфавиту) и номеру дома (по возрастанию).
 * Людей, живущих в одном доме, выводить через запятую по алфавиту (вначале по фамилии, потом по имени). Пример:
 *
 * Железнодорожная 3 - Петров Иван
 * Железнодорожная 7 - Иванов Алексей, Иванов Михаил
 * Садовая 5 - Сидоров Петр, Сидорова Мария
 *
 * В случае обнаружения неверного формата файла бросить любое исключение.
 */
fun sortAddresses(inputName: String, outputName: String) {
    /**
     * total
     * space complexity = O(N)
     * time complexity = O(N^2)
     */
    val anySymbol = Regex("""([A-Za-z]+|[А-Яа-я]+)""")
    val regex =
            Regex("""($anySymbol\s+$anySymbol\s+-\s$anySymbol\s+\d+(\n|$))""")
    File(inputName).readLines().map { it ->
        val format = regex.matches(it)
        if (!format) {
            throw IllegalArgumentException("File format")
        }
    }
    val fromFile = File(inputName).readLines()
    //space complexity = O(N)
    //time complexity = O(N)
    val treeOfAddresses = TreeMap<String, TreeSet<String>>()
    fromFile.map { treeOfAddresses.put(it.split('-')[1].trim(), TreeSet()) }
    //space complexity = O(N)
    //time complexity = O(N)
    for (key in treeOfAddresses.keys) {
        treeOfAddresses[key]!!.addAll(fromFile.filter { it.split('-')[1].trim() == key }
                .map { it -> it.split('-')[0].trim() })
    }
    //space complexity = O(N)
    //time complexity = O(N^2)
    val writer = File(outputName).bufferedWriter()
    for ((key, value) in treeOfAddresses) {
        writer.write(key + " - " + value.joinToString(postfix = "", prefix = ""))
        writer.newLine()
    }
    writer.close()
}


/**
 * Сортировка температур
 *
 * Средняя
 * (Модифицированная задача с сайта acmp.ru)
 *
 * Во входном файле заданы температуры различных участков абстрактной планеты с точностью до десятых градуса.
 * Температуры могут изменяться в диапазоне от -273.0 до +500.0.
 * Например:
 *
 * 24.7
 * -12.6
 * 121.3
 * -98.4
 * 99.5
 * -12.6
 * 11.0
 *
 * Количество строк в файле может достигать ста миллионов.
 * Вывести строки в выходной файл, отсортировав их по возрастанию температуры.
 * Повторяющиеся строки сохранить. Например:
 *
 * -98.4
 * -12.6
 * -12.6
 * 11.0
 * 24.7
 * 99.5
 * 121.3
 */
fun sortTemperatures(inputName: String, outputName: String) {
    /**
     * time complexity = O(n)
     * space complexity = O(n)
     */
    val arrayOfTemper = File(inputName).readLines().map { it -> it.toDouble() }.toDoubleArray()
    //O(n) - time complexity
    //O(n) - space complexity
    mergerSort(arrayOfTemper)
    //O(n + logn) => O(n) - time complexity
    //O(n + n) => O(n) - space complexity
    File(outputName).writeText(arrayOfTemper.joinToString(separator = "\n"))
}

/**
 * Сортировка последовательности
 *
 * Средняя
 * (Задача взята с сайта acmp.ru)
 *
 * В файле задана последовательность из n целых положительных чисел, каждое в своей строке, например:
 *
 * 1
 * 2
 * 3
 * 2
 * 3
 * 1
 * 2
 *
 * Необходимо найти число, которое встречается в этой последовательности наибольшее количество раз,
 * а если таких чисел несколько, то найти минимальное из них,
 * и после этого переместить все такие числа в конец заданной последовательности.
 * Порядок расположения остальных чисел должен остаться без изменения.
 *
 * 1
 * 3
 * 3
 * 1
 * 2
 * 2
 * 2
 */
fun sortSequence(inputName: String, outputName: String) {
    /**
     * time complexity = O(N)
     * space complexity = O(N)
     */
    val sequenceArray = File(inputName).readLines().map { it -> it.toInt() }.toIntArray()
    val sortedSeqArr = countingSortForSeq(sequenceArray, sequenceArray.max()!!)
    File(outputName).writeText(sortedSeqArr.joinToString(separator = "\n"))
}

/**
 * Соединить два отсортированных массива в один
 *
 * Простая
 *
 * Задан отсортированный массив first и второй массив second,
 * первые first.size ячеек которого содержат null, а остальные ячейки также отсортированы.
 * Соединить оба массива в массиве second так, чтобы он оказался отсортирован. Пример:
 *
 * first = [4 9 15 20 28]
 * second = [null null null null null 1 3 9 13 18 23]
 *
 * Результат: second = [1 3 4 9 9 13 15 20 23 28]
 */
fun <T : Comparable<T>> mergeArrays(first: Array<T>, second: Array<T?>) {
    /**
     * time complexity = O(N)
     * space complexity = O(M - N)
     */
    var firstIndex = 0
    var secondIndex = first.size
    var index = 0
    while (firstIndex < first.size && secondIndex < second.size) {
        if (first[firstIndex] < second[secondIndex]!!)
            second[index++] = first[firstIndex++]
        else
            second[index++] = second[secondIndex++]
    }
    //O(N) N - counts of elements in less array
    while (firstIndex < first.size)
        second[index++] = first[firstIndex++]
    while (secondIndex < second.size)
        second[index++] = second[secondIndex++]
    //M - counts of elements in bigger array
    //O(N + (M - N)) => O(N) - time complexity

}

