import kotlin.math.pow

val chain = listOf("seed", "soil", "fertilizer", "water", "light", "temperature", "humidity", "location")

data class Mapping(val interval: Interval, val to: Long)
data class Interval(val from: Long, val len: Long)
fun makemaps(input: List<String>): Map<Pair<String, String>, List<Mapping>> {
  var i = 0
  val maps = mutableMapOf<Pair<String, String>, MutableList<Mapping>>()
  while (i < input.size) {
    while (input[i].isEmpty()) i++
    val maptype = input[i].split(" ")[0]
    val (from, _, to) = maptype.split("-")
    val spec = maps.getOrPut(from to to) { mutableListOf() }
    i++
    while (i < input.size && !input[i].isEmpty()) {
      val (dest, src, len) = input[i].split(" ").map { it.toLong() }
      spec.add(Mapping(Interval(src, len), dest))
      i++
    }
  }
  return maps
}

fun chainify(initial: Long, maps: Map<Pair<String, String>, List<Mapping>>): Long {
  var current = initial
  for (pair in chain.zipWithNext()) {
    // print("> $current ")
    val spec = maps.get(pair)!!
    for (subspec in spec) {
      if (current >= subspec.interval.from
        && current < subspec.interval.from + subspec.interval.len) {
        current = subspec.to + current - subspec.interval.from
        break
      }

      // didn't find a mapping, continue with current
    }
  }
  // println("> $current")
  return current
}

fun main() {
  fun part1(input: List<String>): Long {
    val seeds = input[0].substring("seeds: ".length).trim().split(" ").map { it.toLong() }
    val maps = makemaps(input.subList(2, input.lastIndex + 1))
    val locations = seeds.map { chainify(it, maps) }
    return locations.min()
  }

  fun part2(input: List<String>): Long = 0L

  // test if implementation meets criteria from the description, like:
  val testInput = readInput("Day05_test")
  check(part1(testInput) == 35L)
  // check(part2(testInput) == 46L)

  val input = readInput("Day05")
  check(part1(input) == 910845529L)
  // part2(input).println()
}