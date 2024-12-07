package day05

import utils.readInput

fun main() {
    fun part1(input: List<String>): Int {
        return InstructionManualSorter(input).findSortedAndSumMiddlePages()
    }

    fun part2(input: List<String>): Int {
        return InstructionManualSorter(input).sortUnsortedAndSumMiddlePages()
    }

    val input = readInput("day05/Day05")
    //println(part1(input))
    println(part2(input))
}

class InstructionManualSorter(private val input: List<String>) {
    // Key = first page, value = list of second pages
    private val rules = mutableMapOf<Int, MutableSet<Int>>().withDefault { mutableSetOf() }
    private val pages = mutableListOf<String>()

    init {
        // Generate the rules and list of page numbers
        var pagesStart = 0

        for (index in input.indices) {
            if (input[index].isBlank() || input[index].isEmpty()) {
                // We've hit the start of the pages
                pagesStart = index + 1
                break
            }

            val pageRules = input[index].split("|")
            val firstPage = pageRules.first().toInt()
            val secondPage = pageRules.last().toInt()
            val currRules = rules.getValue(firstPage)
            currRules.add(secondPage)
            rules[pageRules.first().toInt()] = currRules
        }

        pages.addAll(input.slice(pagesStart..input.lastIndex))
    }

    fun findSortedAndSumMiddlePages() : Int {
        var currSum = 0
        for (line in pages) {
            val pageNumbers = line.split(",").map { it.toInt() }
            if (isSorted(pageNumbers)) {
                currSum += pageNumbers[pageNumbers.size / 2]
            }
        }

        return currSum
    }

    fun sortUnsortedAndSumMiddlePages() : Int {
        var currSum = 0
        for (line in pages) {
            val pageNumbers = line.split(",").map { it.toInt() }
            if (!isSorted(pageNumbers)) {
                currSum += sortAndGetMiddleNumber(pageNumbers)
            }
        }

        return currSum
    }

    private fun sortAndGetMiddleNumber(pageNumbers: List<Int>) : Int {
        val sortedPageNumbers = pageNumbers.sortedWith(comparator)

        return sortedPageNumbers[pageNumbers.size / 2]
    }

    /**
     * Standard comparator using the rules
     */
    private val comparator: Comparator<Int> = java.util.Comparator { a, b ->
        when {
            rules.containsKey(a) && rules[a]!!.contains(b) -> -1
            rules.containsKey(b) && rules[b]!!.contains(a) -> 1
            else -> 0
        }
    }

    private fun isSorted(pageNumbers: List<Int>) : Boolean {
        // Map each page to it's index
        val pageIndexes = mutableMapOf<Int, Int>()
        for (index in pageNumbers.indices) {
            pageIndexes[pageNumbers[index]] = index
        }

        // Now we can check each rule and make sure this line works
        for (firstPage in rules.keys) {
            // Only need to check if both pages were in the line
            if (pageIndexes.containsKey(firstPage)) {
                for (secondPage in rules.getValue(firstPage)) {
                    if (pageIndexes.containsKey(secondPage)) {
                        if (pageIndexes[firstPage]!! > pageIndexes[secondPage]!!) {
                            return false
                        }
                    }
                }
            }
        }

        return true
    }
}
