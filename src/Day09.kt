private enum class Direction { LEFT, RIGHT }
private fun appendNumber(nums: List<Int>, direction: Direction): Int {
    if (nums.all { it == 0 }) return 0
    val subseq = nums.windowed(2) { (first, second) -> second - first }

    return if (direction == Direction.LEFT) {
        nums.first() - appendNumber(subseq, Direction.LEFT)
    } else {
        nums.last() + appendNumber(subseq, Direction.RIGHT)
    }
}
private fun solve(sequence: String, direction: Direction): Int {
    val nums = sequence.split(" ").map { it.toInt() }
    return appendNumber(nums, direction)
}
private fun part1(input: List<String>): Int = input.map { solve(it, Direction.RIGHT) }.sum()
private fun part2(input: List<String>): Int = input.map { solve(it, Direction.LEFT) }.sum()

fun main() {

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day09_test")
    check(part1(testInput) == 114)
    check(part2(testInput) == 2)

    val input = readInput("Day09")
    part1(input).println()
    part2(input).println()
}