import kotlin.math.max
import kotlin.math.min

sealed interface WorkflowStep
data object Accept: WorkflowStep
data object Reject: WorkflowStep
data class Jump(val workflowId: String): WorkflowStep

enum class Comparator { LT, GT }
data class Conditional(
    val variable: Char,
    val comparator: Comparator,
    val compareWith: Int,
    val action: WorkflowStep
): WorkflowStep

typealias Workflow = List<WorkflowStep>

typealias Part = Map<Char, Int>

private fun parseAsAction(s: String): WorkflowStep =
    when (s) {
        "A" -> Accept
        "R" -> Reject
        else -> Jump(s)
    }
private fun parseAsComparator(s: String): Comparator =
    when (s) {
        "<" -> Comparator.LT
        ">" -> Comparator.GT
        else -> throw IllegalArgumentException()
    }

private fun readWorkflows(iterator: Iterator<String>): Map<String, Workflow> = buildMap {
    val workflowRe = """^(\w+)\{(.+)}$""".toRegex()
    val conditionalRe = """^(\w)(\W)(\d+):(\w+)$""".toRegex()

    var currentLine: String = iterator.next()
    while (currentLine.trim().isNotEmpty()) {
        val (name, sequence) = workflowRe.matchEntire(currentLine)!!.destructured

        val steps = sequence.split(",").map { stepStr ->
            val conditionalMatch = conditionalRe.matchEntire(stepStr)
            if (conditionalMatch != null) {
                val (variable, comparator, compareWith, action) = conditionalMatch.destructured
                return@map Conditional(
                    variable[0],
                    parseAsComparator(comparator),
                    compareWith.toInt(),
                    parseAsAction(action)
                )
            }

            return@map parseAsAction(stepStr)
        }

        put(name, steps)

        currentLine = iterator.next()
    }
}

private fun readParts(iterator: Iterator<String>): List<Part> = buildList {
    val partRe = """^\{(.+)}$""".toRegex()
    val varRe = """^(\w)=(\d+)$""".toRegex()

    while (iterator.hasNext()) {
        val currentLine = iterator.next()
        val (partDef) = partRe.matchEntire(currentLine)!!.destructured
        val vars = partDef.split(",").map { varRe.matchEntire(it)!!.destructured }
            .associate { (varName, varValue) -> varName.first() to varValue.toInt() }
        add(vars)
    }
}
private fun accepted(part: Part, workflows: Map<String, Workflow>): Boolean {
    var currentWorkflow = workflows["in"]!!
    var currentWorkflowIndex = 0
    var step = currentWorkflow[currentWorkflowIndex]

    do {
        when (step) {
            Accept -> return true
            Reject -> return false
            is Jump -> {
                currentWorkflow = workflows[step.workflowId]!!
                currentWorkflowIndex = 0
                step = currentWorkflow[currentWorkflowIndex]
            }
            is Conditional -> {
                val matches = when (step.comparator) {
                    Comparator.LT -> part[step.variable]!! < step.compareWith
                    Comparator.GT -> part[step.variable]!! > step.compareWith
                }

                if (matches) step = step.action
                else step = currentWorkflow[++currentWorkflowIndex]
            }
        }
    } while (true)
}

data class Ranges(val subranges: Set<Pair<Int, Int>>) {
    constructor(from: Int, to: Int) : this(setOf(from to to))
    infix fun union(other: Ranges): Ranges {
        // Very inefficient but oh well...
        val allRanges = subranges union other.subranges

        val nums = buildSet {
            for (i in 0..4000) {
                for (range in allRanges) {
                    if (i >= range.first && i < range.second) add(i)
                }
            }
        }.toList().sorted()

        val ranges = buildSet {
            var currentRange: Pair<Int, Int>? = null
            for (i in nums) {
                if (currentRange == null) currentRange = i to i+1
                else if (currentRange.second == i) currentRange = currentRange.first to i+1
                else {
                    add(currentRange)
                    currentRange = i to i+1
                }
            }
            if (currentRange != null) add(currentRange)
        }
//        println("$this union $other = $ranges")
        return Ranges(ranges)
    }

    infix fun intersect(other: Pair<Int, Int>): Ranges {
        val ranges = buildSet {
            for (range in subranges) {
                val candidateRange = max(range.first, other.first) to min(range.second, other.second)
                if (candidateRange.first < candidateRange.second) add(candidateRange)
            }
        }
        return Ranges(ranges)
    }

    fun size(): Int {
        var size = 0
        for (range in subranges) size += range.second - range.first
        return size
    }
}

val letters = "s"

typealias Xmas = Map<Char, Ranges>
val Empty: Xmas = letters.toCharArray().associateWith { Ranges(emptySet()) }
infix fun Xmas.union(other: Xmas): Xmas =
    letters.toCharArray().associateWith { c -> this[c]!! union other[c]!! }
data class WorkflowPointer(val workflow: Workflow, val index: Int = 0) {
    fun advance() = copy(workflow = workflow, index = index + 1)
    fun get(): WorkflowStep = workflow[index]
}

private fun findAcceptableRanges(
    workflows: Map<String, Workflow>,
    workflowPointer: WorkflowPointer = WorkflowPointer(workflows["in"]!!, 0),
    step: WorkflowStep = workflowPointer.get(),
    ranges: Xmas = letters.toCharArray().associateWith { Ranges(1, 4001) },
): Xmas {

    when (step) {
        Accept -> {
            println("[$ranges] accepted")
            return ranges
        }
        Reject -> {
            println("[$ranges] rejected")
            return Empty
        }
        is Jump ->
            return findAcceptableRanges(workflows, WorkflowPointer(workflows[step.workflowId]!!), ranges = ranges)
        is Conditional -> {
            // Debugging
            if (!letters.contains(step.variable)) return findAcceptableRanges(workflows, workflowPointer.advance(), ranges = ranges)

            val rangesForTrueBranch = ranges.map { (c, v) ->
                if (c != step.variable) c to v
                else when (step.comparator) {
                    Comparator.LT -> c to (v intersect (0 to step.compareWith))
                    Comparator.GT -> c to (v intersect (step.compareWith + 1 to 4001))
                }
            }.toMap()

            println("[$ranges] Conditional on ${step.variable} ${step.comparator} ${step.compareWith} -> [$rangesForTrueBranch]")
            val acceptableTrue = findAcceptableRanges(workflows, workflowPointer, step.action, ranges = rangesForTrueBranch)
            val acceptableEither = findAcceptableRanges(workflows, workflowPointer.advance(), ranges = ranges)
            val acceptable = acceptableTrue union acceptableEither
            println("[$ranges] acceptableTrue=$acceptableTrue acceptableEither=$acceptableEither acceptable=$acceptable")
            return acceptable
        }
    }
}
private fun part1(input: List<String>): Int {
    val iterator = input.iterator()
    val workflows = readWorkflows(iterator)
    val parts = readParts(iterator)

    return parts
        .filter { accepted(it, workflows) }
        .sumOf { it.values.sum() }
}
private fun part2(input: List<String>): Long {
    val iterator = input.iterator()
    val workflows = readWorkflows(iterator)
    val acceptableRanges = findAcceptableRanges(workflows)
    println(acceptableRanges)
    return acceptableRanges.values.map { it.size().toLong() }.reduce { acc, v -> acc * v }.also { println(it) }
}

fun main() {

    check(part2(readInput("Day19_minitest")) == 1L)
    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day19_test")
    check(part1(testInput) == 19114)
//    check(part2(testInput) == 167409079868000L)

    val input = readInput("Day19")
    part1(input).println()
//    part2(input).println()
}