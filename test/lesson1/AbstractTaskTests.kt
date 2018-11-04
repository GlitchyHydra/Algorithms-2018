package lesson1

import org.junit.jupiter.api.assertThrows
import java.io.BufferedWriter
import java.io.File
import java.io.FileNotFoundException
import java.sql.Time
import java.util.*
import kotlin.math.abs
import java.util.Random


abstract class AbstractTaskTests : AbstractFileTests() {

    private fun generateTimes(size: Int) {
        val random = Random()
        val times = mutableListOf<Int>()
        for (t in 0..500) {
            val count = random.nextInt(size)
            for (i in 1..count) {
                times += t
            }
        }

        fun BufferedWriter.writeTimes() {
            for (t in times) {
                write(Time(t.toLong()).toString())
                newLine()
            }
            close()
        }

        File("temp_unsorted.txt").bufferedWriter().writeTimes()
        times.sort()
        File("temp_sorted_expected.txt").bufferedWriter().writeTimes()
    }

    protected fun sortTimes(sortTimes: (String, String) -> Unit) {
        //regular tests
        try {
            sortTimes("input/time_in1.txt", "temp.txt")
            assertFileContent("temp.txt",
                    """
                     00:40:31
                     07:26:57
                     10:00:03
                     13:15:19
                     13:15:19
                     19:56:14
                """.trimIndent()
            )

        } finally {
            File("temp.txt").delete()
        }
        try {
            sortTimes("input/time_in2.txt", "temp.txt")
            assertFileContent("temp.txt",
                    """
                     00:00:00
                """.trimIndent()
            )
        } finally {
            File("temp.txt").delete()
        }
        try {
            sortTimes("input/time_in3.txt", "temp.txt")
            assertFileContent("temp.txt",
                    File("input/time_out3.txt").readLines().joinToString(separator = "\n"))
        } finally {
            File("temp.txt").delete()
        }
        //unexpected cases
        try {
            assertThrows<FileNotFoundException> { sortTimes("Fasdzxcxs", "FileNotFound.txt") }
            assertThrows<IllegalArgumentException> {
                sortTimes("input/time_in_unexpected.txt", "Illegal.txt")
            }
        } finally {
            File("FileNotFound.txt").delete()
            File("Illegal.txt").delete()
        }
        //large tests
        fun testGeneratedTimes(size: Int) {
            try {
                generateTimes(size)
                sortTimes("temp_unsorted.txt", "temp_sorted_actual.txt")
                assertFileContent("temp_sorted_actual.txt",
                        File("temp_sorted_expected.txt").readLines().joinToString(separator = "\n")
                )
            } finally {
                File("temp_unsorted.txt").delete()
                File("temp_sorted_expected.txt").delete()
                File("temp_sorted_actual.txt").delete()
            }
        }
        testGeneratedTimes(10)
        testGeneratedTimes(500)
    }

    private fun randomString(): String {
        val leftLimit = 97 // letter 'a'
        val rightLimit = 122 // letter 'z'
        val targetStringLength = 10
        val random = Random()
        val buffer = StringBuilder(targetStringLength)
        for (i in 0 until targetStringLength) {
            val randomLimitedInt = leftLimit + (random.nextFloat() * (rightLimit - leftLimit + 1)).toInt()
            buffer.append(randomLimitedInt.toChar())
        }
        val generatedString = buffer.toString()

        return generatedString
    }

    private fun generateAddresses(size: Int) {
        val random = Random()
        val addresses = mutableListOf<String>()
        val Addr = TreeMap<String, TreeSet<String>>()
        for (t in 0..1000) {
            val count = random.nextInt(size)
            val name = "${randomString()} ${randomString()}"
            val adr = "${randomString()}  $count"
            addresses += "$name - $adr"
            if (Addr.containsKey(adr)) {
                Addr[adr]!!.add(name)
            } else {
                val new = TreeSet<String>()
                new.add(name)
                Addr[adr] = new
            }
        }
        val sortedAddr = Addr.toSortedMap()
        fun BufferedWriter.writeAddress() {
            for (t in addresses) {
                write(t)
                newLine()
            }
            close()
        }

        fun BufferedWriter.writeSortAddress() {
            for ((addr, name) in sortedAddr) {
                write(addr + " - " + name.joinToString(postfix = "", prefix = ""))
                newLine()
            }
            close()
        }

        File("temp_sorted_expected.txt").bufferedWriter().writeSortAddress()
        File("temp_unsorted.txt").bufferedWriter().writeAddress()
    }

    protected fun sortAddresses(sortAddresses: (String, String) -> Unit) {
        //regular test
        try {
            sortAddresses("input/addr_in1.txt", "temp.txt")
            assertFileContent("temp.txt",
                    """
                    Железнодорожная 3 - Петров Иван
                    Железнодорожная 7 - Иванов Алексей, Иванов Михаил
                    Садовая 5 - Сидоров Петр, Сидорова Мария
                """.trimIndent()
            )
            //unexpected cases
            assertThrows<IllegalArgumentException> { sortAddresses("input/addr_in2.txt", "Illegal.txt") }
            assertThrows<FileNotFoundException> { sortAddresses("input/addddd_in2.txt", "FileNotFound.txt") }
        } finally {
            File("temp.txt").delete()
            File("Illegal.txt").delete()
            File("FileNotFound.txt").delete()
        }
        //large test
        fun testGeneratedAddresses(size: Int) {
            try {
                generateAddresses(size)
                sortAddresses("temp_unsorted.txt", "temp_sorted_actual.txt")
                assertFileContent("temp_sorted_actual.txt",
                        File("temp_sorted_expected.txt").readLines().joinToString(separator = "\n")
                )
            } finally {
                File("temp_unsorted.txt").delete()
                File("temp_sorted_expected.txt").delete()
                File("temp_sorted_actual.txt").delete()
            }
        }
        testGeneratedAddresses(10)
        testGeneratedAddresses(500)
    }

    private fun generateTemperatures(size: Int) {
        val random = Random()
        val temperatures = mutableListOf<Int>()
        for (t in -2730..5000) {
            val count = random.nextInt(size)
            for (i in 1..count) {
                temperatures += t
            }
        }

        fun BufferedWriter.writeTemperatures() {
            for (t in temperatures) {
                if (t < 0) write("-")
                write("${abs(t) / 10}.${abs(t) % 10}")
                newLine()
            }
            close()
        }

        File("temp_sorted_expected.txt").bufferedWriter().writeTemperatures()
        temperatures.shuffle(random)
        File("temp_unsorted.txt").bufferedWriter().writeTemperatures()
    }

    protected fun sortTemperatures(sortTemperatures: (String, String) -> Unit) {
        //regular
        try {
            sortTemperatures("input/temp_in1.txt", "temp.txt")
            assertFileContent("temp.txt",
                    """
                    -98.4
                    -12.6
                    -12.6
                    11.0
                    24.7
                    99.5
                    121.3
                """.trimIndent()
            )
        } finally {
            File("temp.txt").delete()
        }
        //unexpected cases
        try {
            assertThrows<IllegalArgumentException> {
                sortTimes("input/temperature_in_unexpected.txt", "Illegal.txt")
            }
            assertThrows<FileNotFoundException> {
                sortTimes("input/not.txt", "FileNotFound.txt")
            }
        } finally {
            File("Illegal.txt").delete()
            File("FileNotFound.txt").delete()
        }
        //large tests
        fun testGeneratedTemperatures(size: Int) {
            try {
                generateTemperatures(size)
                sortTemperatures("temp_unsorted.txt", "temp_sorted_actual.txt")
                assertFileContent("temp_sorted_actual.txt",
                        File("temp_sorted_expected.txt").readLines().joinToString(separator = "\n")
                )
            } finally {
                File("temp_unsorted.txt").delete()
                File("temp_sorted_expected.txt").delete()
                File("temp_sorted_actual.txt").delete()
            }
        }
        testGeneratedTemperatures(10)
        testGeneratedTemperatures(500)
    }

    protected fun sortSequence(sortSequence: (String, String) -> Unit) {
        //regular tests
        try {
            sortSequence("input/seq_in1.txt", "temp.txt")
            assertFileContent("temp.txt",
                    """
                        1
                        3
                        3
                        1
                        2
                        2
                        2
                    """.trimIndent())
        } finally {
            File("temp.txt").delete()
        }
        try {
            sortSequence("input/seq_in2.txt", "temp.txt")
            assertFileContent("temp.txt",
                    """
                        25
                        39
                        25
                        39
                        25
                        39
                        12
                        12
                        12
                    """.trimIndent())
        } finally {
            File("temp.txt").delete()
        }

        try {
            assertThrows<FileNotFoundException> {
                sortTimes("input/rfas.txt", "fileNotFound.txt")
            }
            assertThrows<IllegalArgumentException> {
                sortTimes("input/seq_in_unexpected.txt", "IllegalArgumentException.txt")
            }
        } finally {
            File("fileNotFound.txt").delete()
            File("IllegalArgumentException.txt").delete()
        }

        //large tests
        try {
            sortSequence("input/seq_in_3.txt", "check.txt")
            assertFileContent("check.txt", """
                 2
                 1
                 1000001
                 1000000
                 1000000
             """.trimIndent())
        } finally {
            File("check.txt").delete()
        }
        fun BufferedWriter.writeNumbers(numbers: List<Int>) {
            for (n in numbers) {
                write("$n")
                newLine()
            }
            close()
        }

        fun generateSequence(totalSize: Int, answerSize: Int) {
            val random = Random()
            val numbers = mutableListOf<Int>()

            val answer = 100000 + random.nextInt(100000)
            val count = mutableMapOf<Int, Int>()
            for (i in 1..totalSize - answerSize) {
                var next: Int
                var nextCount: Int
                do {
                    next = random.nextInt(answer - 1) + 1
                    nextCount = count[next] ?: 0
                } while (nextCount >= answerSize - 1)
                numbers += next
                count[next] = nextCount + 1
            }
            for (i in totalSize - answerSize + 1..totalSize) {
                numbers += answer
            }
            File("temp_sequence_expected.txt").bufferedWriter().writeNumbers(numbers)
            for (i in totalSize - answerSize until totalSize) {
                numbers.removeAt(totalSize - answerSize)
            }
            for (i in totalSize - answerSize until totalSize) {
                val toInsert = random.nextInt(totalSize - answerSize)
                numbers.add(toInsert, answer)

            }
            File("temp_sequence.txt").bufferedWriter().writeNumbers(numbers)
        }

        try {
            generateSequence(500000, 200)
            sortSequence("temp_sequence.txt", "temp.txt")
            assertFileContent("temp.txt", File("temp_sequence_expected.txt").readLines().joinToString("\n"))
        } finally {
            File("temp_sequence_expected.txt").delete()
            File("temp_sequence.txt").delete()
            File("temp.txt").delete()
        }
    }

    protected fun generateArrays(firstSize: Int, secondSize: Int): Triple<Array<Int>, Array<Int?>, Array<Int?>> {
        val random = Random()
        val expectedResult = Array<Int?>(firstSize + secondSize) {
            it * 10 + random.nextInt(10)
        }
        val first = mutableListOf<Int>()
        val second = mutableListOf<Int?>()
        for (i in 1..firstSize) second.add(null)
        for (element in expectedResult) {
            if (first.size < firstSize && (random.nextBoolean() || second.size == firstSize + secondSize)) {
                first += element!!
            } else {
                second += element
            }
        }
        return Triple(first.toTypedArray(), second.toTypedArray(), expectedResult)
    }
}
