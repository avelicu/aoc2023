import kotlin.math.max
import kotlin.math.min

fun main() {
  val LINE_RE = """^Game (?<gameid>\d+): (?<game>.*)$""".toRegex()
  val SUBGAME_RE = """(?<count>\d+) (?<color>.+)""".toRegex()

  fun part1(input: List<String>): Int {
    val limits = mapOf(
      "red" to 12,
      "green" to 13,
      "blue" to 14
    )

    var validGameIdSum = 0
    for (line in input) {
      val (gameidstr, gamestr) = LINE_RE.matchEntire(line)!!.destructured
      val gameid = gameidstr.toInt()

      var gameIsValid = true
      val shows = gamestr.split("; ")
      for (show in shows) {
        val colors = show.split(", ")
        for (color in colors) {
          val (countstr, colorstr) = SUBGAME_RE.matchEntire(color)!!.destructured
          val count = countstr.toInt()
          if (count > limits[colorstr]!!) gameIsValid = false
        }
      }

      if (gameIsValid) {
        // println("Valid game with ID $gameid")
        validGameIdSum += gameid
      }
    }

    return validGameIdSum
  }

  fun part2(input: List<String>): Int {
    var powerSum = 0
    for (line in input) {
      val minOfType = mutableMapOf(
        "red" to 0,
        "green" to 0,
        "blue" to 0
      )

      val (gameidstr, gamestr) = LINE_RE.matchEntire(line)!!.destructured
      val gameid = gameidstr.toInt()

      val shows = gamestr.split("; ")
      for (show in shows) {
        val colors = show.split(", ")
        for (color in colors) {
          val (countstr, colorstr) = SUBGAME_RE.matchEntire(color)!!.destructured
          val count = countstr.toInt()
          minOfType[colorstr] = max(minOfType[colorstr]!!, count)
        }
      }
      val power = minOfType["red"]!! * minOfType["green"]!! * minOfType["blue"]!!
      // println("Game $gameid: mins $minOfType; power $power")
      powerSum += power
    }

    return powerSum
  }

  // test if implementation meets criteria from the description, like:
  val testInput = readInput("Day02_test")
  check(part1(testInput) == 8)
  check(part2(testInput) == 2286)

  val input = readInput("Day02")
  part1(input).println()
  part2(input).println()
}