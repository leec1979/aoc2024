package day07

import utils.readInput

fun main() {
    fun part1(input: List<String>): Long {
        return BridgeRepair(input).findAndSumValidEquations()
    }

    fun part2(input: List<String>): Long {
        return BridgeRepair(input).findAndSumValidEquations()
    }

    val input = readInput("day07/Day07")
    //println(part1(input))
    println(part2(input))
}

class BridgeRepair(input: List<String>) {
    private val equations = input.map { row ->
        row.split("""\D+""".toRegex()).map { it.toLong() }
    }

    private val operators: List<(Long, Long) -> Long> = listOf(
        { a, b -> a + b },
        { a, b -> a * b },
        { a, b -> "$a$b".toLong() }
    )

    /**
     * Brute force time!
     * For each number we will branch off and perform each operator with the next number
     */
    fun findAndSumValidEquations() : Long {
        return equations.filter {
            canBeSolved(
                operators = operators,
                target = it[0],
                currSum = it[1],
                remainingNumbers = it.subList(2, it.size)
            )
        }.sumOf {
            // We're filtered down to only the equations which can be solved, and sum the target from each
            it.first()
        }
    }

    private fun canBeSolved(
        operators: List<(Long, Long) -> Long>,
        target: Long,
        currSum: Long,
        remainingNumbers: List<Long>
    ) : Boolean {
        if (remainingNumbers.isEmpty()) {
            return target == currSum
        }

        if (currSum > target) {
            return false
        }

        // Recurse, once for each operator, and if any can be solved we return true
        return operators.any { operator ->
            canBeSolved(
                operators = operators,
                target = target,
                currSum = operator.invoke(currSum, remainingNumbers[0]), // Invoke the current operation against index 0 to get the new currSum
                remainingNumbers = remainingNumbers.subList(1, remainingNumbers.size)
            )
        }
    }
}