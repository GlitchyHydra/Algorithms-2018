@file:Suppress("UNUSED_PARAMETER", "unused")

package lesson5

import lesson5.impl.GraphBuilder
import lesson5.Graph.Vertex
import java.util.*
import kotlin.collections.HashMap

/**
 * Эйлеров цикл.
 * Средняя
 *
 * Дан граф (получатель). Найти по нему любой Эйлеров цикл.
 * Если в графе нет Эйлеровых циклов, вернуть пустой список.
 * Соседние дуги в списке-результате должны быть инцидентны друг другу,
 * а первая дуга в списке инцидентна последней.
 * Длина списка, если он не пуст, должна быть равна количеству дуг в графе.
 * Веса дуг никак не учитываются.
 *
 * Пример:
 *
 *      G -- H
 *      |    |
 * A -- B -- C -- D
 * |    |    |    |
 * E    F -- I    |
 * |              |
 * J ------------ K
 *
 * Вариант ответа: A, E, J, K, D, C, H, G, B, C, I, F, B, A
 *
 * Справка: Эйлеров цикл -- это цикл, проходящий через все рёбра
 * связного графа ровно по одному разу
 */
fun Graph.findEulerLoop(): List<Graph.Edge> {
    /**
     * time complexity O(V + E)
     * space complexity O(V + E)
     * where V - vertices, E - edges
     */
    // time - O(V)
    if (!this.checkEulerianCircuit()) return emptyList()
    val s = Stack<Vertex>()
    val solution = ArrayDeque<Graph.Vertex>()
    s.push(vertices.first())
    val edgesList = mutableSetOf<Graph.Edge>()
    //time - O(V)
    //space - O(E)
    edgesList.addAll(edges)
    //time - O(V + E)
    while (!s.isEmpty()) {
        val w = s.lastElement()
        for (vertex in vertices) {
            val e = getConnection(w, vertex)
            if (e != null && edgesList.contains(e)) {
                s.push(vertex)
                edgesList.remove(e)
                break
            }
        }
        if (w == s.last()) {
            s.pop()
            solution.add(w)
        }
    }
    //time - O(V + E)
    //space - O(V + E)
    val solE = mutableListOf<Graph.Edge>()
    (0 until solution.size - 1).forEach {
        solE.add(getConnection(solution.poll(), solution.first)!!)
    }
    return solE
}

fun Graph.checkEulerianCircuit(): Boolean {
    //Count of incident edges should be even
    //because you need go to this vertex and out from it
    //equal counts of a time
    for (vertex in vertices) {
        if (getNeighbors(vertex).size % 2 == 1)
            return false
    }
    return true
}

/**
 * Минимальное остовное дерево.
 * Средняя
 *
 * Дан граф (получатель). Найти по нему минимальное остовное дерево.
 * Если есть несколько минимальных остовных деревьев с одинаковым числом дуг,
 * вернуть любое из них. Веса дуг не учитывать.
 *
 * Пример:
 *
 *      G -- H
 *      |    |
 * A -- B -- C -- D
 * |    |    |    |
 * E    F -- I    |
 * |              |
 * J ------------ K
 *
 * Ответ:
 *
 *      G    H
 *      |    |
 * A -- B -- C -- D
 * |    |    |
 * E    F    I
 * |
 * J ------------ K
 */
fun Graph.minimumSpanningTree(): Graph {
    /**
     * time complexity O(V + E)
     * space complexity O(V + E)
     * V - vertices
     * E - edges
     */
    val from = this.vertices.first()
    val info = mutableMapOf<Graph.Vertex, VertexInfo>()
    for (vertex in this.vertices) {
        info[vertex] = VertexInfo(vertex, Int.MAX_VALUE, null)
    }
    //O(V)
    val fromInfo = VertexInfo(from, 0, null)
    val queue = PriorityQueue<VertexInfo>()
    queue.add(fromInfo)
    info[from] = fromInfo
    while (queue.isNotEmpty()) {
        val currentInfo = queue.poll()
        val currentVertex = currentInfo.vertex
        for (vertex in this.getNeighbors(currentVertex)) {
            val newDistance = info[currentVertex]!!.distance
            if (info[vertex]!!.distance > newDistance) {
                val newInfo = VertexInfo(vertex, newDistance, currentVertex)
                queue.add(newInfo)
                info[vertex] = newInfo
            }
        }
    }
    var i = 1
    val g = GraphBuilder().apply {
        info.map {
            if (i != 1) {
                addVertex("${it.value.vertex}")
                addConnection(it.value.prev!!, it.key)
            }
            i++
        }
    }.build()
    return g
}

/**
 * Максимальное независимое множество вершин в графе без циклов.
 * Сложная
 *
 * Дан граф без циклов (получатель), например
 *
 *      G -- H -- J
 *      |
 * A -- B -- D
 * |         |
 * C -- F    I
 * |
 * E
 *
 * Найти в нём самое большое независимое множество вершин и вернуть его.
 * Никакая пара вершин в независимом множестве не должна быть связана ребром.
 *
 * Если самых больших множеств несколько, приоритет имеет то из них,
 * в котором вершины расположены раньше во множестве this.vertices (начиная с первых).
 *
 * В данном случае ответ (A, E, F, D, G, J)
 *
 * Эта задача может быть зачтена за пятый и шестой урок одновременно
 */
fun Graph.largestIndependentVertexSet(): Set<Graph.Vertex> {
    TODO()
}

/**
 * Наидлиннейший простой путь.
 * Сложная
 *
 * Дан граф (получатель). Найти в нём простой путь, включающий максимальное количество рёбер.
 * Простым считается путь, вершины в котором не повторяются.
 * Если таких путей несколько, вернуть любой из них.
 *
 * Пример:
 *
 *      G -- H
 *      |    |
 * A -- B -- C -- D
 * |    |    |    |
 * E    F -- I    |
 * |              |
 * J ------------ K
 *
 * Ответ: A, E, J, K, D, C, H, G, B, F, I
 */
fun Graph.longestSimplePath(): Path {
    TODO()
}