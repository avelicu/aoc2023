import kotlin.math.max
import kotlin.math.min

val chain = listOf("seed", "soil", "fertilizer", "water", "light", "temperature", "humidity", "location")

data class Mapping(val interval: Interval, val to: Long)
data class Interval(val from: Long, val len: Long) {
  val to: Long get() = from + len - 1
  fun shiftBy(offset: Long) = Interval(from + offset, len)
  override fun toString(): String = "[$from, $to]"
}
private fun forInterval(from: Long, to: Long): Interval =
  Interval(from, to - from + 1)

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


data class IntersectionResult(val intersection: Interval, val remainders: List<Interval>)
fun intersect(a: Interval, b: Interval): IntersectionResult? {
  val candidateStart = max(a.from, b.from)
  val candidateEnd = min(a.to, b.to)
  if (candidateStart > candidateEnd) return null

  val intersection = forInterval(candidateStart, candidateEnd)
  val before = if (candidateStart - 1 >= a.from) forInterval(a.from, candidateStart - 1) else null
  val after = if (candidateEnd + 1 <= a.to) forInterval(candidateEnd + 1, a.to) else null
  return IntersectionResult(intersection, listOfNotNull(before, after))
}

fun chainify2(initial: Interval, maps: Map<Pair<String, String>, List<Mapping>>): List<Interval> {
  var currents = mutableListOf(initial)
  for (pair in chain.zipWithNext()) {
    println("> $currents ")
    val spec = maps[pair]!!
    val nextCurrents = mutableListOf<Interval>()

    var i = 0
    while (i < currents.size) {
      val current = currents[i++]
      var anyTransformed = false

      for (subspec in spec) {
        val intersectionResult = intersect(current, subspec.interval) ?: continue
        val transformed =
          intersectionResult.intersection.shiftBy(subspec.to - subspec.interval.from)
        println("$current ir $intersectionResult transformed to $transformed (remainders: ${intersectionResult.remainders}, spec: $subspec)")

        nextCurrents.add(transformed)
        currents.addAll(intersectionResult.remainders)
        anyTransformed = true
        break
      }

      if (!anyTransformed) {
        println("$current unmatched, noop-transformed")
        nextCurrents.add(current)
      }
    }
    currents = nextCurrents
  }
  println("> $currents")
  return currents
}

fun main() {
  fun part1(input: List<String>): Long {
    val seeds = input[0].substring("seeds: ".length).trim().split(" ").map { it.toLong() }
    val maps = makemaps(input.subList(2, input.lastIndex + 1))
    val locations = seeds.map { chainify2(Interval(it, 1), maps) }.flatten()
    return locations.minOf { it.from }
  }

  fun part2(input: List<String>): Long {
    val intervals = input[0]
      .substring("seeds: ".length).trim()
      .split(" ")
      .map { it.toLong() }
      .chunked(2).map { li -> Interval(li[0], li[1]) }
    val maps = makemaps(input.subList(2, input.lastIndex + 1))
    val locations = intervals.map { chainify2(it, maps) }.flatten()
    return locations.minOf { it.from }
  }

  // test if implementation meets criteria from the description, like:
  val testInput = readInput("Day05_test")
  check(part1(testInput) == 35L)
  check(part2(testInput) == 46L)

  val input = readInput("Day05")
  check(part1(input) == 910845529L)
  check(part2(input) == 77435348L)
}