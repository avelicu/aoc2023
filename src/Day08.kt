import java.lang.IllegalStateException
import java.util.*

data class MutableNode(val name: String, var left: MutableNode? = null, var right: MutableNode? = null) {
    override fun toString(): String = "$name (${left?.name}, ${right?.name})"
    override fun hashCode(): Int = Objects.hash(name, left?.name, right?.name)
}

private data class Line(val start: String, val left: String, val right: String)
private fun readLine(line: String): Line = Line(line.substring(0, 3), line.substring(7, 10), line.substring(12, 15))

data class LR(val left: String, val right: String)
private fun readGraph(def: List<String>): Map<String, MutableNode> {
    val lines = def.map { readLine(it) }.map { it.start to LR(it.left, it.right) }.toMap()
    val nodeMap = mutableMapOf<String, MutableNode>()
    for (entry in lines.entries) {
        val rootNode = nodeMap.getOrPut(entry.key) { MutableNode(entry.key) }
        val leftNode = nodeMap.getOrPut(entry.value.left) { MutableNode(entry.value.left) }
        val rightNode = nodeMap.getOrPut(entry.value.right) { MutableNode(entry.value.right) }
        rootNode.left = leftNode
        rootNode.right = rightNode
    }
    return nodeMap
}

data class PreambleAndPeriod(val preamble: Int, val period: Int)
private fun findPeriod(startNode: MutableNode, path: String): PreambleAndPeriod {
    var currentNode = startNode
    val touched = mutableMapOf<MutableNode, Int>()
    touched.put(currentNode, 0)

    var steps = 0
    do {
        val direction = path[steps % path.length]
        steps++
        currentNode = if (direction == 'L') currentNode.left!! else currentNode.right!!
        if (touched.keys.contains(currentNode)) {
            return PreambleAndPeriod(touched[currentNode]!!, steps - touched[currentNode]!!)
        }
        touched[currentNode] = steps
    } while (currentNode != startNode)

    throw IllegalStateException()
}

private fun findZed(startNode: MutableNode, path: String): Int {
    var current = startNode
    var steps = 0
    while (!current.name.endsWith('Z')) {
        val direction = path[steps % path.length]
        steps++
        current = if (direction == 'L') current.left!! else current.right!!
    }
    return steps
}

fun main() {

    fun part1(input: List<String>): Int {
        val path = input[0]
        var current = readGraph(input.subList(2, input.size))["AAA"]!!
        var steps = 0
        while (current.name != "ZZZ") {
            val direction = path[steps % path.length]
            steps++
            current = if (direction == 'L') current.left!! else current.right!!
        }
        return steps
    }

    fun part2(input: List<String>): Long {
        val path = input[0]
        var map = readGraph(input.subList(2, input.size))

        val startingNodesWithLengthToZed = map.filterKeys { it.endsWith('A') }.values
            .associateWith { findZed(it, path) }
        println(startingNodesWithLengthToZed.values)

        // This solution is stupid

        return 0L
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day08_test")
    check(part1(testInput) == 2)

    val testInput2 = readInput("Day08_test2")
//    check(part2(testInput2) == 6L)

    val input = readInput("Day08")
    part1(input).println()
    part2(input).println()
}