import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

private data class Race(val time: Long, val distance: Long)
private fun readRaces(input: List<String>): List<Race> {
  val times = input[0].substring("Time:    ".length).trim().split(" ").map { it.trim().toIntOrNull() }.filterNotNull()
  val distances = input[1].substring("Distance:".length).trim().split(" ").map { it.trim().toIntOrNull() }.filterNotNull()
  return times.zip(distances).map { (time, distance) -> Race(time.toLong(), distance.toLong()) }
}

private fun readRace(input: List<String>): Race {
  val time = input[0].substring("Time:    ".length).trim().split(" ").map { it.trim() }.joinToString("").toLong()
  val distance = input[1].substring("Distance:".length).trim().split(" ").map { it.trim() }.joinToString("").toLong()
  return Race(time, distance)
}

private fun howFar(buttonMillis: Long, totalTime: Long): Long {
  val movementTime = totalTime - buttonMillis
  return buttonMillis * movementTime
}

private fun getWaysToBeat(race: Race): Int {
  var waysCounter = 0
  for (hold in 1 until race.time) {
    if (howFar(hold, race.time) > race.distance) waysCounter++
  }
  return waysCounter
}

private fun getWaysToBeatWithMath(race: Race): Int {
  val firstSolution = (race.time - sqrt((race.time*race.time - 4*race.distance).toDouble())) / 2
  val secondSolution = (race.time + sqrt((race.time*race.time - 4*race.distance).toDouble())) / 2
  check(firstSolution < secondSolution)
  return (ceil(secondSolution - 1) - floor(firstSolution + 1)).toInt() + 1
}

fun main() {
  fun part1(input: List<String>): Int {
    val races = readRaces(input)
    return races.map { getWaysToBeatWithMath(it) }.fold(1) { acc, num -> acc * num }
  }

  fun part2(input: List<String>): Int {
    val race = readRace(input)
    return getWaysToBeatWithMath(race)
  }

  // test if implementation meets criteria from the description, like:
  val testInput = readInput("Day06_test")
  check(part1(testInput) == 288)
  check(part2(testInput) == 71503)

  val input = readInput("Day06")
  part1(input).println()
  part2(input).println()
}