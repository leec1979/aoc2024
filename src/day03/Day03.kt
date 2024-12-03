package day03

import utils.readInput

fun main() {
    /**
     * It seems like the goal of the program is just to multiply some numbers. It does that
     * with instruction like mul(X,Y), where X and Y are each 1-3 digit numbers. For instance,
     * mul(44,46) multiplies 44 by 46 to get a result of 2024. Similarly, mul(123,4)
     * would multiply 123 by 4.
     *
     * However, because the program's memory has been corrupted, there are also many invalid
     * characters that should be ignored, even if they look like part of a mul instruction.
     * Sequences like mul(4*, mul(6,9!, ?(12,34), or mul ( 2 , 4 ) do nothing.
     *
     * For example, consider the following section of corrupted memory:
     *
     * xmul(2,4)%&mul[3,7]!@^do_not_mul(5,5)+mul(32,64]then(mul(11,8)mul(8,5))
     * Only the four highlighted sections are real mul instructions. Adding up the result of
     * each instruction produces 161 (2*4 + 5*5 + 11*8 + 8*5).
     *
     * Scan the corrupted memory for uncorrupted mul instructions. What do you get if you add
     * up all of the results of the multiplications?
     */
    fun part1(input: List<String>): Int {
        val multiplier = Multiplier()

        return input.sumOf {
            multiplier.findAndMultiply(it)
        }
    }

    /**
     * There are two new instructions you'll need to handle:
     *
     * The do() instruction enables future mul instructions.
     * The don't() instruction disables future mul instructions.
     * Only the most recent do() or don't() instruction applies. At the beginning of the
     * program, mul instructions are enabled.
     *
     * For example:
     *
     * xmul(2,4)&mul[3,7]!^don't()_mul(5,5)+mul(32,64](mul(11,8)undo()?mul(8,5))
     * This corrupted memory is similar to the example from before, but this time the
     * mul(5,5) and mul(11,8) instructions are disabled because there is a don't() instruction
     * before them. The other mul instructions function normally, including the one at the
     * end that gets re-enabled by a do() instruction.
     *
     * This time, the sum of the results is 48 (2*4 + 8*5).
     *
     * Handle the new instructions; what do you get if you add up all of the results of just
     * the enabled multiplications?
     */
    fun part2(input: List<String>): Int {
        // This one is sneaky, because we need to check across lines
        // Join the lines back together
        val multiplier = Multiplier()
        return multiplier.findAndMultiplyWithDoDont(input.joinToString("\n"))
    }

    // Test if implementation meets criteria from the description, like:
    //check(part1(listOf("test_input")) == 1)

    // Or read a large test input from the `src/Day01_test.txt` file:
    //val testInput = readInput("Day02_test")
    //check(Day2().part2(testInput) == 10)

    // Read the input from the `src/Day01.txt` file.
    val input = readInput("day03/Day03")
    println(part1(input))
    println(part2(input))
}

class Multiplier {
    /**
     * Find all instances of mul(X,Y) within line. For each instance, multiple X by Y
     * and return the sum of all the multiplications.
     */
    fun findAndMultiply(line: String) : Int {
        var currSum = 0
        val regex = "mul\\((\\d+),(\\d+)\\)".toRegex()
        val matches = regex.findAll(line)
        matches.iterator().forEach { matchResult ->
            val x = matchResult.groups[1]!!.value.toInt()
            val y = matchResult.groups[2]!!.value.toInt()
            currSum += x * y
        }

        return currSum
    }

    /**
     * Find the ranges for relevant do() and dont() blocks, then for each
     * range call findAndMultiply
     */
    fun findAndMultiplyWithDoDont(input: String) : Int {
        val combinedRegex = "mul\\((\\d+),(\\d+)\\)|do\\(\\)|don't\\(\\)".toRegex()
        val matches = combinedRegex.findAll(input)
        var enabled = true
        val doValue = "do()"
        val dontValue = "don't()"
        var currSum = 0

        matches.forEach { matchResult ->
            // matchResult.value == do() or don't()
            println("value=${matchResult.value} range=${matchResult.range}")
            if (matchResult.value == doValue) {
                enabled = true
            } else if (matchResult.value == dontValue) {
                enabled = false
            } else {
                if (enabled) {
                    val x = matchResult.groups[1]!!.value.toInt()
                    val y = matchResult.groups[2]!!.value.toInt()
                    currSum += x * y
                }
            }
        }

        return currSum
    }
}