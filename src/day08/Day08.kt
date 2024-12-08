package day08

import utils.readInput

/**
 * URL: https://adventofcode.com/2024/day/8
 * Part 2: https://adventofcode.com/2024/day/8#part2
 */
fun main() {
    fun part1(input: List<String>): Int {
        return ResonantCollinearity(input).findAntinodes()
    }

    /**
     *   0123456789
     * 0 T......... T1=0,0
     * 1 ...T...... T2=1,3 - T1 -> T2 = x+1,y+3, so next antinode would be 2,6, then 3, 9
     * 2 .T........ T3=2,1 - T1 -> T3 = x+2,y+1, so antinodes would be 4,2 then 6,3 then 8,4
     * 3 .......... T3 -> T2 = x-1,y+2, so antinodes would be 0,5
     * 4 ..........
     * 5 ..........
     * 6 ..........
     * 7 ..........
     * 8 .........
     * 9 ..........
     *
     * Becomes:
     *   0123456789
     * 0 T....#....
     * 1 ...T......
     * 2 .T....#...
     * 3 .........#
     * 4 ..#.......
     * 5 ..........
     * 6 ...#......
     * 7 ..........
     * 8 ....#.....
     * 9 ..........
     *
     * Each of the Ts is also an antinode apparently, so there are
     * 9 antinodes in total above.
     */
    fun part2(input: List<String>): Int {
        return ResonantCollinearity(input).findEqualSpacedAntinodes()
    }

    val input = readInput("day08/Day08")
    println(part1(input))
    println(part2(input))
}

data class GridPoint(val x: Int, val y: Int) {
    fun getAntinodePositionToCheck(other: GridPoint) : GridPoint {
        val newX = (other.x - this.x) * 2
        val newY = (other.y - this.y) * 2

        return GridPoint(x = this.x + newX, y = this.y + newY)
    }

    fun getNextAntinodePosition(other: GridPoint) : GridPoint {
        val newX = (other.x - this.x)
        val newY = (other.y - this.y)

        return GridPoint(x = other.x + newX, y = other.y + newY)
    }
}

class ResonantCollinearity(input: List<String>) {
    private val grid = input.map { it.toCharArray() }
    // Map each frequency char to a set of grid points for the antenna
    private val antennaLocationsByFrequency = mutableMapOf<Char, MutableSet<GridPoint>>()
        .withDefault { mutableSetOf() }
    private val antinodeLocations = mutableSetOf<GridPoint>()

    fun findAntinodes() : Int {
        getAllAntennaLocations()

        antennaLocationsByFrequency.forEach { (_, antennas) ->
            for (i in 0 until antennas.size) {
                for (j in i+1 until antennas.size) {
                    addValidAntinodes(antennas.elementAt(i), antennas.elementAt(j))
                }
            }
        }

        return antinodeLocations.size
    }

    fun findEqualSpacedAntinodes() : Int {
        getAllAntennaLocations()

        antennaLocationsByFrequency.forEach  { (_, antennas) ->
            if (antennas.size > 1) {
                // If there is more than one antenna for a frequency each antenna will also be an antinode
                antennas.forEach { antenna -> antinodeLocations.add(antenna)}
            }
            for (i in 0 until antennas.size) {
                for (j in i+1 until antennas.size) {
                    addEqualSpacedAntinodes(antennas.elementAt(i), antennas.elementAt(j))
                    addEqualSpacedAntinodes(antennas.elementAt(j), antennas.elementAt(i))
                }
            }
        }

        return antinodeLocations.size
    }

    private fun getAllAntennaLocations() {
        if (antennaLocationsByFrequency.isEmpty()) {
            grid.forEachIndexed { rowIndex, row ->
                row.forEachIndexed { columnIndex, c ->
                    if (c != '.') {
                        val currLocations = antennaLocationsByFrequency.getValue(c)
                        currLocations.add(GridPoint(rowIndex, columnIndex))
                        antennaLocationsByFrequency[c] = currLocations
                    }
                }
            }
        }
    }

    private fun addValidAntinodes(first: GridPoint, second: GridPoint) {
        val firstCandidate = first.getAntinodePositionToCheck(second)
        val secondCandidate = second.getAntinodePositionToCheck(first)
        for (candidate in listOf(firstCandidate, secondCandidate)) {
            if (grid.validGridPoint(candidate)) {
                antinodeLocations.add(candidate)
            }
        }
    }

    private fun addEqualSpacedAntinodes(first: GridPoint, second: GridPoint) {
        // Recurse along both lines until we got out of bounds
        val candidate = first.getNextAntinodePosition(second)
        if (grid.validGridPoint(candidate)) {
            antinodeLocations.add(candidate)
            addEqualSpacedAntinodes(second, candidate)
        }
    }
}

fun List<CharArray>.validGridPoint(gridPoint: GridPoint) : Boolean {
    return this.getOrNull(gridPoint.x)?.getOrNull(gridPoint.y) != null
}