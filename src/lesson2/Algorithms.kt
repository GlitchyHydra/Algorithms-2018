@file:Suppress("UNUSED_PARAMETER")

package lesson2

import lesson3.Trie
import java.io.File

/**
 * Получение наибольшей прибыли (она же -- поиск максимального подмассива)
 * Простая
 *
 * Во входном файле с именем inputName перечислены цены на акции компании в различные (возрастающие) моменты времени
 * (каждая цена идёт с новой строки). Цена -- это целое положительное число. Пример:
 *
 * 201
 * 196
 * 190
 * 198
 * 187
 * 194
 * 193
 * 185
 *
 * Выбрать два момента времени, первый из них для покупки акций, а второй для продажи, с тем, чтобы разница
 * между ценой продажи и ценой покупки была максимально большой. Второй момент должен быть раньше первого.
 * Вернуть пару из двух моментов.
 * Каждый момент обозначается целым числом -- номер строки во входном файле, нумерация с единицы.
 * Например, для приведённого выше файла результат должен быть Pair(3, 4)
 *
 * В случае обнаружения неверного формата файла бросить любое исключение.
 */
fun optimizeBuyAndSell(inputName: String): Pair<Int, Int> {
    val price = File(inputName).readLines().map { it -> it.toInt() }.toIntArray()//O(n)
    val delta = price.zip(price.copyOfRange(1, price.size)) { el1, el2 -> el2 - el1 }.toIntArray() //O(n + m)
    var ans = 0
    var sum = 0
    var li = 0
    var fi = 0
    var p = Pair(0, 0)
    for (i in 0..delta.lastIndex) {
        if (sum + delta[i] > 0) {
            sum += delta[i]
            li++
        } else {
            fi = i + 1
            li = i
            sum = 0
        }
        if (maxOf(ans, sum) == sum) {
            p = Pair(fi + 1, li + 2)
        }
        ans = maxOf(ans, sum)
    }//total O(n + m)
    return p
}

/**
 * Задача Иосифа Флафия.
 * Простая
 *
 * Образовав круг, стоят menNumber человек, пронумерованных от 1 до menNumber.
 *
 * 1 2 3
 * 8   4
 * 7 6 5
 *
 * Мы считаем от 1 до choiceInterval (например, до 5), начиная с 1-го человека по кругу.
 * Человек, на котором остановился счёт, выбывает.
 *
 * 1 2 3
 * 8   4
 * 7 6 х
 *
 * Далее счёт продолжается со следующего человека, также от 1 до choiceInterval.
 * Выбывшие при счёте пропускаются, и человек, на котором остановился счёт, выбывает.
 *
 * 1 х 3
 * 8   4
 * 7 6 Х
 *
 * Процедура повторяется, пока не останется один человек. Требуется вернуть его номер (в данном случае 3).
 *
 * 1 Х 3
 * х   4
 * 7 6 Х
 *
 * 1 Х 3
 * Х   4
 * х 6 Х
 *
 * х Х 3
 * Х   4
 * Х 6 Х
 *
 * Х Х 3
 * Х   х
 * Х 6 Х
 *
 * Х Х 3
 * Х   Х
 * Х х Х
 */
fun josephTask(menNumber: Int, choiceInterval: Int): Int {
    return if (menNumber == 1) 1 // O(1)
    else (josephTask(menNumber - 1, choiceInterval) + choiceInterval - 1) % menNumber + 1
}

/**
 * Наибольшая общая подстрока.
 * Средняя
 *
 * Дано две строки, например ОБСЕРВАТОРИЯ и КОНСЕРВАТОРЫ.
 * Найти их самую длинную общую подстроку -- в примере это СЕРВАТОР.
 * Если общих подстрок нет, вернуть пустую строку.
 * При сравнении подстрок, регистр символов *имеет* значение.
 * Если имеется несколько самых длинных общих подстрок одной длины,
 * вернуть ту из них, которая встречается раньше в строке first.
 */
fun longestCommonSubstring(first: String, second: String): String {
    /**
     * labor intensity = O(n^2)
     * memory intensity = O(n*m)
     */
    val matrix = Array(first.length + 1) { IntArray(second.length + 1) }
    var max = 0
    var lj = 0
    var li = 0
    for (i in 1..first.length) {
        for (j in 1..second.length) {
            if (first[i - 1] == second[j - 1]) {
                matrix[i][j] = matrix[i - 1][j - 1] + 1
                if (max < matrix[i][j]) {
                    max = matrix[i][j]
                    li = i
                    lj = j
                }
            }
        }
    }
    var stroke = ""
    while (li >= 0 && lj >= 0 && matrix[li][lj] != 0) {
        stroke += first[li - 1]
        li--
        lj--
    }
    return stroke.reversed()
}

/**
 * Число простых чисел в интервале
 * Простая
 *
 * Рассчитать количество простых чисел в интервале от 1 до limit (включительно).
 * Если limit <= 1, вернуть результат 0.
 *
 * Справка: простым считается число, которое делится нацело только на 1 и на себя.
 * Единица простым числом не считается.
 */
fun calcPrimesNumber(limit: Int): Int {
    if (limit <= 1) return 0
    var count = 0
    for (i in 2..limit) {
        var isPrime = true
        for (j in 2 until i) {
            if (i % j == 0) {
                isPrime = false
                break
            }
        }
        if (isPrime)
            count++
    }
    return count
}

/**
 * Балда
 * Сложная
 *
 * В файле с именем inputName задана матрица из букв в следующем формате
 * (отдельные буквы в ряду разделены пробелами):
 *
 * И Т Ы Н
 * К Р А Н
 * А К В А
 *
 * В аргументе words содержится множество слов для поиска, например,
 * ТРАВА, КРАН, АКВА, НАРТЫ, РАК.
 *
 * Попытаться найти каждое из слов в матрице букв, используя правила игры БАЛДА,
 * и вернуть множество найденных слов. В данном случае:
 * ТРАВА, КРАН, АКВА, НАРТЫ
 *
 * И т Ы Н     И т ы Н
 * К р а Н     К р а н
 * А К в а     А К В А
 *
 * Все слова и буквы -- русские или английские, прописные.
 * В файле буквы разделены пробелами, строки -- переносами строк.
 * Остальные символы ни в файле, ни в словах не допускаются.
 */
fun baldaSearcher(inputName: String, words: Set<String>): Set<String> {
val prefixTree = Trie()
    words.map { it -> prefixTree.add(it)}
    println(prefixTree)
    return mutableSetOf("")
}
    /*val matrixOfWords = readMatrix(inputName)
    val width = matrixOfWords.size
    val height = matrixOfWords[0].size
    matrixOfWords.forEach { println(it) }
    val offsets = arrayOf(Pair(-1, 0), Pair(-1, -1), Pair(1, 0), Pair(0, 1))
    for (word in words) {
        val indexes = findIndex(matrixOfWords, word[0])
        var i = indexes.first
        var j = indexes.second
        if (indexes.first == -1) continue
        for (k in 1..word.lastIndex) {
            var flag = false
            for (offset in offsets) {
                if (i + offset.first in 1..height && j + offset.second in 1..width) {
                    if (matrixOfWords[i + offset.first][j + offset.second] == word[k]) {
                        flag = true
                        i += offset.first
                        j += offset.second
                        break
                    }
                }
            }
        }
    }
    return setOf()
}

//fun find

fun findIndex(matrix: Array<CharArray>, s: Char): Pair<Int, Int> {
    for (i in 0 until matrix.size) {
        for (j in 0 until matrix[0].size) {
            if (matrix[i][j] == s) return Pair(i, j)
        }
    }
    return Pair(-1, -1)
}

fun readMatrix(inputName: String): Array<CharArray> {
    val listOfWord = mutableListOf<MutableList<Char>>()
    for ((index, line) in File(inputName).readLines().withIndex()) {
        listOfWord.add(mutableListOf())
        for (symbol in line) {
            if (symbol != ' ')
                listOfWord[index].add(symbol)
        }
    }
    return listOfWord.map { it -> it.toCharArray() }.toTypedArray()
}*/