private enum class HandType {
  Nothing {
    override fun matches(hand: Hand): Boolean = hand.matchesCountsInOrder(1, 1, 1, 1, 1)
  },
  OnePair {
    override fun matches(hand: Hand): Boolean = hand.matchesCountsInOrder(2, 1, 1, 1)
  },
  TwoPair {
    override fun matches(hand: Hand): Boolean = hand.matchesCountsInOrder(2, 2, 1)
  },
  ThreeOfAKind {
    override fun matches(hand: Hand): Boolean = hand.matchesCountsInOrder(3, 1, 1)
  },
  FullHouse {
    override fun matches(hand: Hand): Boolean = hand.matchesCountsInOrder(3, 2)
  },
  FourOfAKind {
    override fun matches(hand: Hand): Boolean = hand.matchesCountsInOrder(4, 1)
  },
  FiveOfAKind {
    override fun matches(hand: Hand): Boolean = hand.matchesCountsInOrder(5)
  };

  abstract fun matches(hand: Hand): Boolean
}

data class CardWithCount(val card: Char, val count: Int)
class Hand(val cards: List<Char>, val allowJokers: Boolean = false) : Comparable<Hand> {

  private companion object {
    const val CARD_ORDER = "AKQJT98765432"
    const val CARD_ORDER_WITH_JOKERS = "AKQT98765432J"
  }

  private val cardCounts: List<CardWithCount>
  private val handType: HandType
  private val jokers: Int

  init {
    val cardMap = buildMap {
      for (card in cards) compute(card) { _, v -> (v ?: 0) + 1 }
    }
    cardCounts =
      cardMap.entries
        .map { (card, count) -> CardWithCount(card, count) }
        .filter { if (!allowJokers) true else it.card != 'J' }
        .sortedByDescending { cardWithCount -> cardWithCount.count }
    jokers = if (!allowJokers) 0 else cardMap.getOrDefault('J', 0)

    // println("$cards $cardCounts")
    handType = HandType.values().toList().reversed().first { it.matches(this) }
  }

  fun matchesCountsInOrder(vararg counts: Int): Boolean {
    var remainingJokers = jokers
    for (countIndex in counts.indices) {
      val cardCountsAtIndex = if (countIndex < cardCounts.size) cardCounts[countIndex].count else 0
      val remainderNeededCount = counts[countIndex] - cardCountsAtIndex
      if (remainderNeededCount <= remainingJokers) {
        remainingJokers -= remainderNeededCount
      } else {
        return false
      }
    }

    return true

    // cardCounts.size == counts.size
    //   && counts.mapIndexed { index, count -> cardCounts[index].count == count }.all { it }
  }

  override fun compareTo(other: Hand): Int {
    if (handType > other.handType) return 1
    if (handType < other.handType) return -1

    // check(cardCounts.size == other.cardCounts.size)
    // for (cardIndex in cardCounts.indices) {
    //   if (cardCounts[cardIndex].card > other.cardCounts[cardIndex].card) return 1
    //   if (cardCounts[cardIndex].card < other.cardCounts[cardIndex].card) return -1
    // }
    for (cardIndex in 0 until 5) {
      if (getCardOrder.indexOf(cards[cardIndex]) < getCardOrder.indexOf(other.cards[cardIndex])) return 1
      if (getCardOrder.indexOf(cards[cardIndex]) > getCardOrder.indexOf(other.cards[cardIndex])) return -1
    }

    return 0
  }

  private val getCardOrder: String get() = if (allowJokers) CARD_ORDER_WITH_JOKERS else CARD_ORDER

  override fun toString(): String = "{$handType of $cards}"
}
private data class HandWithBid(val hand: Hand, val bid: Int): Comparable<HandWithBid> {
  override fun compareTo(other: HandWithBid): Int = hand.compareTo(other.hand)
}

fun main() {
  fun part1(input: List<String>): Int {
    val handsWithBids = input.map { line ->
      val (cardChars, bidString) = line.split(" ")
      HandWithBid(
        hand = Hand(cardChars.toCharArray().toList()),
        bid = bidString.toInt()
      )
    }

    val sortedHandsWithBids = handsWithBids.sorted()
    return sortedHandsWithBids
      .mapIndexed { rank, handWithBid -> (rank + 1) * handWithBid.bid }.sum()
  }

  fun part2(input: List<String>): Int {
    val handsWithBids = input.map { line ->
      val (cardChars, bidString) = line.split(" ")
      HandWithBid(
        hand = Hand(cardChars.toCharArray().toList(), allowJokers = true),
        bid = bidString.toInt()
      )
    }

    val sortedHandsWithBids = handsWithBids.sorted()
    return sortedHandsWithBids
      .mapIndexed { rank, handWithBid -> (rank + 1) * handWithBid.bid }.sum()
  }

  // test if implementation meets criteria from the description, like:
  val testInput = readInput("Day07_test")
  check(part1(testInput) == 6440)
  check(part2(testInput) == 5905)

  val input = readInput("Day07")
  part1(input).println()
  part2(input).println()
}