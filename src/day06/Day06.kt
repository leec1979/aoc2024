package day06

import utils.readInput

fun main() {
    fun part1(input: List<String>): Int {
        val gridWithGuard = GridWithGuard(input)
        return gridWithGuard.calculateDistinctPositions()
    }

    fun part2(input: List<String>): Int {
        val gridWithGuard = GridWithGuard(input)
        return gridWithGuard.traverseWithObstacles()
    }

    val input = readInput("day06/Day06")
    //println(part1(input))
    println(part2(input))
}

data class GridPoint(var x: Int, var y: Int, var direction: Direction) {
    fun move() {
        val (nextX, nextY) = nextPosition()
        x = nextX
        y = nextY
    }

    fun currentPosition() : Pair<Int, Int> = Pair(x, y)

    fun nextPosition() : Pair<Int, Int> {
        return when (direction) {
            Direction.NORTH -> Pair(x-1, y)
            Direction.SOUTH -> Pair(x+1, y)
            Direction.EAST -> Pair(x, y+1)
            Direction.WEST -> Pair(x, y-1)
        }
    }

    fun turnRight() {
        direction = when (direction) {
            Direction.NORTH -> Direction.EAST
            Direction.EAST -> Direction.SOUTH
            Direction.SOUTH -> Direction.WEST
            Direction.WEST -> Direction.NORTH
        }
    }
}

class GridWithGuard(input: List<String>) {
    private val grid = input.map { it.toCharArray() }

    private fun getStartingPoint() : Pair<Int, Int> {
        for (x in grid.indices) {
            for (y in grid[x].indices) {
                if (grid[x][y] == '^') {
                    return Pair(x, y)
                }
            }
        }

        throw RuntimeException("Could not find starting point")
    }

    fun calculateDistinctPositions() : Int {
        val visited = mutableSetOf<Pair<Int, Int>>()
        var outOfBounds = false
        val (startingX, startingY) = getStartingPoint()
        val guard = GridPoint(startingX, startingY, Direction.NORTH)
        visited.add(guard.currentPosition())

        while (!outOfBounds) {
            val (nextX, nextY) = guard.nextPosition()
            if (!isValidPosition(nextX, nextY)) {
                outOfBounds = true
            } else {
                if (grid[nextX][nextY] == '#') {
                    guard.turnRight()
                } else {
                    guard.move()
                    visited.add(guard.currentPosition())
                }
            }
        }

        return visited.size
    }

    fun traverseWithObstacles() : Int {
        val startingPoint = getStartingPoint()
        return calculateObstacles(startingPoint)
            .first // We only want the set of positions at this point
            .filterNot { it == startingPoint }
            .count { candidate ->
                // Create a temporary candidate grid with a new obstacle
                grid[candidate.first][candidate.second] = '#'
                calculateObstacles(startingPoint).also {
                    // Reset the grid to the original state
                    grid[candidate.first][candidate.second] = '.'
                }.second
                /**
                 * Now we use the second returned value, which indicates whether or not the guard would exit the
                 * grid with the next move. If second is true the next move would be valid so this change
                 * introduced a loop.
                 */
            }
    }

    private fun calculateObstacles(startingPoint: Pair<Int, Int>?) : Pair<Set<Pair<Int, Int>>, Boolean> {
        // This now captures not just the position seen but also the direction of travel
        val visited = mutableSetOf<Pair<Pair<Int, Int>, Direction>>()
        var outOfBounds = false
        val guard = if (startingPoint == null) {
            val (startingX, startingY) = getStartingPoint()
            GridPoint(startingX, startingY, Direction.NORTH)
        } else {
            GridPoint(startingPoint.first, startingPoint.second, Direction.NORTH)
        }

        while (!outOfBounds && (Pair(guard.x, guard.y) to guard.direction) !in visited) {
            visited.add(Pair(guard.currentPosition(), guard.direction))
            val (nextX, nextY) = guard.nextPosition()
            if (!isValidPosition(nextX, nextY)) {
                outOfBounds = true
            } else {
                if (grid[nextX][nextY] == '#') {
                    guard.turnRight()
                } else {
                    guard.move()
                }
            }
        }

        val (finalNextX, finalNextY) = guard.nextPosition()
        /**
         * Return a map of positions to a flag indicating whether the next position would be valid,
         * i.e. would it take them off the grid. This helps later when filtering which visited positions
         * to check.
         */
        return visited.map { it.first }.toSet() to (
                isValidPosition(finalNextX, finalNextY)
        )
    }

    private fun isValidPosition(nextX: Int, nextY: Int) : Boolean {
        return (grid.getOrNull(nextX)?.getOrNull(nextY) != null)
    }
}