import kotlin.math.pow

fun main() {
  val LINE_RE = """^Card +(?<cardid>\d+): (?<winning>.*) \| (?<self>.*)$""".toRegex()

  fun part1(input: List<String>): Int {
    var totalScore = 0
    for (line in input) {
      val (cardid, winning, self) = LINE_RE.matchEntire(line)!!.destructured
      val winningNumbers = winning.trim().split(" +".toRegex()).map { it.toInt() }
      val selfNumbers = self.trim().split(" +".toRegex()).map { it.toInt() }
      // println("Card $cardid has winners $winningNumbers and self $selfNumbers")
      val myWinners = winningNumbers intersect selfNumbers
      val score = 2f.pow(myWinners.size - 1).toInt()
      // println("Card $cardid has ${myWinners.size} winners, score $score")
      totalScore += score
    }

    return totalScore
  }

  fun part2(input: List<String>): Int {
    var totalScore = 0

    val scratchCards = buildMap {
      for (line in input) {
        val (cardidstr, winning, self) = LINE_RE.matchEntire(line)!!.destructured
        val winningNumbers = winning.trim().split(" +".toRegex()).map { it.toInt() }
        val selfNumbers = self.trim().split(" +".toRegex()).map { it.toInt() }
        // println("Card $cardid has winners $winningNumbers and self $selfNumbers")
        val myWinners = winningNumbers intersect selfNumbers
        put(cardidstr.toInt(), myWinners.size)
      }
    }

    val cardsToProcess = ArrayDeque<Int>()
    cardsToProcess.addAll(scratchCards.keys)
    while (true) {
      val card = cardsToProcess.removeFirstOrNull() ?: break
      totalScore += 1

      val thisCardWonCards = scratchCards[card]!!
      for (wonCard in card + 1..card + thisCardWonCards) cardsToProcess.add(wonCard)
    }

    return totalScore
  }

  // test if implementation meets criteria from the description, like:
  val testInput = readInput("Day04_test")
  // check(part1(testInput) == 13)
  // check(part2(testInput) == 30)

  val input = readInput("Day04")
  // part1(input).println()
  part2(input).println()
}