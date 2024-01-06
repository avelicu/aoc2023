import kotlin.math.abs
import kotlin.math.exp
import kotlin.math.max
import kotlin.math.min

data class Galaxy(val row: Int, val col: Int)
private fun solve(input: List<String>, expansion: Int): Long {
    val galaxies = buildSet {
        for ((i, line) in input.withIndex()) {
            for ((j, char) in line.withIndex()) {
                if (char == '#') add(Galaxy(i, j))
            }
        }
    }
    val rowCount = input.size
    val colCount = input[0].length

    val emptyRows = ((0..<rowCount) subtract galaxies.map { it.row }).toSet()
    val emptyCols = ((0..<colCount) subtract galaxies.map { it.col }).toSet()

    var totalPathLen: Long = 0
    for (start in galaxies) {
        for (end in galaxies) {
            if (start == end) continue
            val emptyRowCount = emptyRows.count { it > min(start.row, end.row) && it < max(start.row, end.row) }
            val emptyColCount = emptyCols.count { it > min(start.col, end.col) && it < max(start.col, end.col) }
            val pathLen = abs(end.row - start.row) + abs(end.col - start.col) +
                    (expansion-1) * (emptyRowCount + emptyColCount)
            totalPathLen += pathLen
        }
    }
    return totalPathLen / 2 // Every pair is counted twice
}

private fun part1(input: List<String>): Long = solve(input, 2)
private fun part2(input: List<String>): Long = solve(input, 1000000)
fun main() {

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day11_test")
    check(part1(testInput) == 374L)
    check(solve(testInput, 100) == 8410L)

    val input = readInput("Day11")
    part1(input).println()
    part2(input).println()
}