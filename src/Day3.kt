fun main() {
  fun part1(input: List<String>): Int {
    val schematic = Schematic.fromInput(input)
    var validNumbersSum = 0

    for (row in 0 until schematic.rows) {
      var col = 0
      while (col < schematic.cols) {
        var len = 0
        while (schematic.get(row, col + len)?.isDigit() == true) {
          len += 1
        }

        if (len > 0 && isPartNumber(schematic, row, col, col + len - 1)) {
          val candidateChars = mutableListOf<Char>()
          for (scol in col until col+len) {
            candidateChars.add(schematic.get(row, scol)!!)
          }

          val validNumber = candidateChars.joinToString(separator = "").toInt()
          // println("Found valid number $validNumber")
          validNumbersSum += validNumber
        }

        col += len + 1
      }
    }

    return validNumbersSum
  }

  fun part2(input: List<String>): Int {
    val schematic = Schematic.fromInput(input)
    var validGearSum = 0

    for (row in 0 until schematic.rows) {
      for (col in 0 until schematic.cols) {
        if (schematic.get(row, col) == '*') {
          val numTopLeft = findNumberAround(schematic, row-1, col-1)
          val numTopRight = findNumberAround(schematic, row-1, col+1)
          val numAbove = findNumberAround(schematic, row-1, col)

          val numBottomLeft = findNumberAround(schematic, row+1, col-1)
          val numBottomRight = findNumberAround(schematic, row+1, col+1)
          val numBelow = findNumberAround(schematic, row+1, col)

          val numLeft = findNumberAround(schematic, row, col-1)
          val numRight = findNumberAround(schematic, row, col+1)

          val numbers = buildList {
            if (numAbove == null) {
              if (numTopLeft != null) add(numTopLeft)
              if (numTopRight != null) add(numTopRight)
            } else add(numAbove)

            if (numBelow == null) {
              if (numBottomLeft != null) add(numBottomLeft)
              if (numBottomRight != null) add(numBottomRight)
            } else add(numBelow)

            if (numLeft != null) add(numLeft)
            if (numRight != null) add(numRight)
          }

          if (numbers.size == 2) {
            println("Found two valid numbers, $numbers")
            validGearSum += numbers[0] * numbers[1]
          } else if (numbers.isNotEmpty()) {
            println("Found a strange number of numbers, $numbers")
          }
        }
      }
    }

    return validGearSum
  }

  // test if implementation meets criteria from the description, like:
  val testInput = readInput("Day03_test")
  // check(part1(testInput) == 4361)
  // check(part2(testInput) == 467835)

  val input = readInput("Day03")
  part1(input).println()
  part2(input).println()
}

private fun findNumberAround(schematic: Schematic, row: Int, col: Int): Int? {
  var startCol = col
  while (schematic.get(row, startCol)?.isDigit() == true) startCol--
  var endCol = col
  while (schematic.get(row, endCol)?.isDigit() == true) endCol++

  startCol += 1
  endCol -= 1
  if (startCol > endCol) return null
  if (!isPartNumber(schematic, row, startCol, endCol)) return null

  return buildString {
    for (curCol in startCol..endCol) {
      append(schematic.get(row, curCol)!!)
    }
  }.toInt()
}

private fun Char.isSymbol() = !this.isDigit() && this != '.'

private fun isPartNumber(schematic: Schematic, row: Int, startCol: Int, endCol: Int): Boolean {
  var valid =
    schematic.get(row, startCol - 1)?.isSymbol() == true
      || schematic.get(row - 1, startCol - 1)?.isSymbol() == true
      || schematic.get(row + 1, startCol - 1)?.isSymbol() == true
      || schematic.get(row, endCol + 1)?.isSymbol() == true
      || schematic.get(row - 1, endCol + 1)?.isSymbol() == true
      || schematic.get(row + 1, endCol + 1)?.isSymbol() == true
  for (scol in startCol ..endCol) {
    valid = valid
      || schematic.get(row - 1, scol)?.isSymbol() == true
      || schematic.get(row + 1, scol)?.isSymbol() == true
  }
  return valid
}

private class Schematic(private val arr: Array<Array<Char>>) {
  fun get(i: Int, j: Int): Char? =
    if (i < 0 || i >= arr.size || j < 0 || j >= arr.size) null else arr[i][j]

  val rows get() = arr.size
  val cols get() = arr[0].size

  companion object {
    fun fromInput(input: List<String>): Schematic {
      val arr: Array<Array<Char>> = Array(input.size) { Array(input[0].length) { ' ' } }
      for (i in 0 until input.size) {
        for (j in 0 until input[i].length) {
          arr[i][j] = input[i][j]
        }
      }
      return Schematic(arr)
    }
  }
}
