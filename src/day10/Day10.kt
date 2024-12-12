package day10

import utils.readInput
import java.util.*

/**
 * URL: https://adventofcode.com/2024/day/10
 * Part 2: https://adventofcode.com/2024/day/10#part2
 */
fun main() {
    fun part1(input: List<String>): Int {
        return HoofIt(input).findGoodHikingTrails()
    }

    fun part2(input: List<String>): Int {
        return HoofIt(input).findTrailRatings()
    }

    val input = readInput("day10/Day10")
    println(part1(input))
    println(part2(input))
}

data class GridPoint(val x: Int, val y: Int) {
    fun isValidNextStep(grid: List<CharArray>, gp: GridPoint) : Boolean {
        return grid[gp.x][gp.y] == grid[x][y] + 1
    }

    fun getSurroundingValidSteps(grid: List<CharArray>) : List<GridPoint> {
        val validGridPoints = mutableListOf<GridPoint>()

        val candidates = listOf(
            GridPoint(this.x-1, this.y),
            GridPoint(this.x, this.y+1),
            GridPoint(this.x+1, this.y),
            GridPoint(this.x, this.y-1)
        )

        candidates.forEach { gp ->
            if (grid.isValidPoint(gp)) {
                validGridPoints.add(gp)
            }
        }

        return validGridPoints.filter { isValidNextStep(grid, it) }
    }
}

class HoofIt(input: List<String>) {
    private val grid: List<CharArray> = input.map { it.toCharArray() }
    private val startingPoints = mutableListOf<GridPoint>()

    init {
        grid.forEachIndexed { x, chars ->
            chars.forEachIndexed { y, c ->
                if (c.digitToInt() == 0) {
                    startingPoints.add(GridPoint(x, y))
                }
            }
        }
    }

    fun findTrailRatings() : Int {
        var trailScoreCount = 0
        val stack = LinkedList<GridPoint>()

        startingPoints.forEach {
            stack.addLast(it)
        }

        while (stack.isNotEmpty()) {
            val gp = stack.removeLast()
            if (grid[gp.x][gp.y] == '9') {
                trailScoreCount++
            } else {
                val nextSteps = gp.getSurroundingValidSteps(grid)
                nextSteps.forEach {
                    stack.addLast(it)
                }
            }
        }

        return trailScoreCount
    }

    /**
     * Need a DFS from each starting point to find how many 9s can be reached
     */
    fun findGoodHikingTrails() : Int {
        var trailScoreCount = 0

        startingPoints.forEach {
            trailScoreCount += dfs(it)
        }

        return trailScoreCount
    }

    fun dfs(start: GridPoint) : Int {
        var trailScoreCount = 0
        val stack = LinkedList<GridPoint>()
        val visited = mutableSetOf<GridPoint>()

        stack.addLast(start)
        visited.add(start)

        while (stack.isNotEmpty()) {
            val gp = stack.removeLast()
            if (grid[gp.x][gp.y] == '9') {
                trailScoreCount++
            } else {
                val nextSteps = gp.getSurroundingValidSteps(grid)
                nextSteps.forEach {
                    if (it !in visited) {
                        stack.addLast(it)
                        visited.add(it)
                    }
                }
            }
        }

        return trailScoreCount
    }
}

fun List<CharArray>.isValidPoint(gp: GridPoint) : Boolean {
    return this.getOrNull(gp.x)?.getOrNull(gp.y) != null
}