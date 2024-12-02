import kotlin.math.abs

/**
 * The unusual data (your puzzle input) consists of many reports, one report per line.
 * Each report is a list of numbers called levels that are separated by spaces. For example:
 *
 * 7 6 4 2 1
 * 1 2 7 8 9
 * 9 7 6 2 1
 * 1 3 2 4 5
 * 8 6 4 4 1
 * 1 3 6 7 9
 * This example data contains six reports each containing five levels.
 *
 * The engineers are trying to figure out which reports are safe. The Red-Nosed reactor
 * safety systems can only tolerate levels that are either gradually increasing or
 * gradually decreasing. So, a report only counts as safe if both of the following are true:
 *
 * The levels are either all increasing or all decreasing.
 * Any two adjacent levels differ by at least one and at most three.
 * In the example above, the reports can be found safe or unsafe by checking those rules:
 *
 * 7 6 4 2 1: Safe because the levels are all decreasing by 1 or 2.
 * 1 2 7 8 9: Unsafe because 2 7 is an increase of 5.
 * 9 7 6 2 1: Unsafe because 6 2 is a decrease of 4.
 * 1 3 2 4 5: Unsafe because 1 3 is increasing but 3 2 is decreasing.
 * 8 6 4 4 1: Unsafe because 4 4 is neither an increase or a decrease.
 * 1 3 6 7 9: Safe because the levels are all increasing by 1, 2, or 3.
 * So, in this example, 2 reports are safe.
 *
 * Analyze the unusual data from the engineers. How many reports are safe?
 */

fun main() {
    fun part1(input: List<String>): Int {
        var numSafe = 0
        val maxDiff = 3

        input.stream().forEach { line ->
            val values = line.split(" ").map { it.toInt() }
            // Only check for safety if the first and last differ. If they're equal it's unsafe
            if (values.first() != values.last()) {
                val decreasing = values.first() > values.last()
                var safe = true

                for (index in 1 until values.size) {
                    val diff = abs(values[index-1] - values[index])
                    if (diff > maxDiff || diff < 1) {
                        safe = false
                        break
                    }

                    if (decreasing) {
                        if (values[index-1] < values[index]) {
                            safe = false
                            break
                        }
                    } else {
                        if (values[index-1] > values[index]) {
                            safe = false
                            break
                        }
                    }
                }

                if (safe) {
                    numSafe++
                }
            }
        }

        return numSafe
    }


    // Test if implementation meets criteria from the description, like:
    //check(part1(listOf("test_input")) == 1)

    // Or read a large test input from the `src/Day01_test.txt` file:
    //val testInput = readInput("Day02_test")
    //check(Day2().part2(testInput) == 10)

    // Read the input from the `src/Day01.txt` file.
    val input = readInput("Day02")
    part1(input).println()
    println(Day2().part2(input))
}

/**
 * Decided I didn't like the way it was all structured in the main function
 *
 * Now, the same rules apply as before, except if removing a single level from an
 * unsafe report would make it safe, the report instead counts as safe.
 * More of the above example's reports are now safe:
 *
 * 7 6 4 2 1: Safe without removing any level.
 * 1 2 7 8 9: Unsafe regardless of which level is removed.
 * 9 7 6 2 1: Unsafe regardless of which level is removed.
 * 1 3 2 4 5: Safe by removing the second level, 3.
 * 8 6 4 4 1: Safe by removing the third level, 4.
 * 1 3 6 7 9: Safe without removing any level.
 * Thanks to the Problem Dampener, 4 reports are actually safe!
 *
 * Update your analysis by handling situations where the Problem Dampener can remove
 * a single level from unsafe reports. How many reports are now safe?
 *
 * Brute forcing this a bit with checking over and over again by removing an
 * element but each list is relatively small at this point.
 */
class Day2 {
    fun part2(input: List<String>): Int {
        return input.count { line ->
            val report = line.split(" ").map { it.toInt() }
            isSafe(report) || isSafeEnough(report)
        }
    }

    private fun isSafe(report: List<Int>) : Boolean {
        var allIncreasing = true
        var allDecreasing = true
        var safe = true

        for (index in 1 until report.size) {
            if (allDecreasing && report[index-1] < report[index]) {
                allDecreasing = false
            }

            if (allIncreasing && report[index-1] > report[index]) {
                allIncreasing = false
            }

            if (abs(report[index-1] - report[index]) !in 1..3) {
                safe = false
                break
            }

            if (!allDecreasing && !allIncreasing) {
                safe = false
                break
            }
        }

        return safe
    }

    private fun isSafeEnough(report: List<Int>) : Boolean {
        return getSubReport(report).asSequence().any(::isSafe)
    }

    private fun getSubReport(report: List<Int>) = iterator {
        repeat(report.size) {
            val reportMinusOne = report.toMutableList().apply { removeAt(it) }
            yield(reportMinusOne)
        }
    }
}
