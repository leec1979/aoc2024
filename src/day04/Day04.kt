package day04

import utils.readInput

fun main() {
    fun part1(input: List<String>): Int {
        return Finder(input, "XMAS".toCharArray()).findXmas()
    }

    /**
     * Looking for the instructions, you flip over the word search to find that this isn't actually an XMAS puzzle;
     * it's an X-MAS puzzle in which you're supposed to find two MAS in the shape of an X. One way to achieve
     * that is like this:
     *
     * M.S
     * .A.
     * M.S
     * Irrelevant characters have again been replaced with . in the above diagram. Within the X, each MAS can be
     * written forwards or backwards.
     *
     * Here's the same example from before, but this time all of the X-MASes have been kept instead:
     *
     * .M.S......
     * ..A..MSMS.
     * .M.S.MAA..
     * ..A.ASMSM.
     * .M.S.M....
     * ..........
     * S.S.S.S.S.
     * .A.A.A.A..
     * M.M.M.M.M.
     * ..........
     * In this example, an X-MAS appears 9 times.
     *
     * Flip the word search from the instructions back over to the word search side and try again. How many times
     * does an X-MAS appear?
     */
    fun part2(input: List<String>): Int {
        return Finder(input, "".toCharArray()).findMasInAnX()
    }

    val input = readInput("day04/Day04")
    //println(part1(input))
    println(part2(input))
}

class Finder(private val input: List<String>, private val forwards: CharArray) {
    fun findMasInAnX() : Int {
        /**
         * Similar to the first part, but find an A then search around it for the M and Ss in relevant spots.
         * The Ms and Ses can be in any order around the A. So:
         * M . S
         * . A .
         * M . S
         * Is valid, as is:
         * S . S    S . M   M . M   M . S
         * . A .    . A .   . A .   . A .
         * M . M    S . M   S . S   M . S
         *
         * Grid rotation? Or check for M and S then make sure they line up correctly (Each character either on the
         * same colum or same row, not both).
         * Other ideas: Get all four letters, make sure there's only two of each and that the opposites don't
         * match i.e. NW != SE and SW != NE
         */
        var xCount = 0

        for (rowIndex in input.indices) {
            println("Checking ${input[rowIndex]}")
            for (columnIndex in input[rowIndex].indices) {
                if (input[rowIndex][columnIndex] == 'A') {
                    println("Found A at $rowIndex,$columnIndex")
                    val compassPointChars = getFourChars(rowIndex, columnIndex) ?: continue
                    if (!validateExtractedChars(compassPointChars)) continue
                    xCount++
                }
            }
        }

        return xCount
    }

    private fun getFourChars(startingRow: Int, startingColumn: Int) : Map<CompassPoints, Char>? {
        val compassPointChars = mutableMapOf<CompassPoints, Char>()
        // Setup the points and ensure they're valid
        val upperLeft = Pair(startingRow-1, startingColumn-1)
        if (upperLeft.first < 0 || upperLeft.second < 0) {
            return null
        } else {
            compassPointChars[CompassPoints.NW] = input[upperLeft.first][upperLeft.second]
        }

        val lowerLeft = Pair(startingRow+1, startingColumn-1)
        if (lowerLeft.first > input.lastIndex || lowerLeft.second < 0) {
            return null
        } else {
            compassPointChars[CompassPoints.SW] = input[lowerLeft.first][lowerLeft.second]
        }

        val upperRight = Pair(startingRow-1, startingColumn+1)
        if (upperRight.first < 0 || upperRight.second > input[startingColumn].lastIndex) {
            return null
        } else {
            compassPointChars[CompassPoints.NE] = input[upperRight.first][upperRight.second]
        }

        val lowerRight = Pair(startingRow+1, startingColumn+1)
        if (lowerRight.first > input.lastIndex || lowerRight.second > input[startingColumn].lastIndex) {
            return null
        } else {
            compassPointChars[CompassPoints.SE] = input[lowerRight.first][lowerRight.second]
        }

        return compassPointChars
    }

    private fun validateExtractedChars(compassChars: Map<CompassPoints, Char>) : Boolean {
        var mCount = 0
        var sCount = 0
        for (c in compassChars.values) {
            if (c !in listOf('M', 'S')) {
                return false
            }

            if (c == 'M') mCount++
            if (c == 'S') sCount++
        }

        if (mCount != 2 || sCount != 2) return false

        // Make sure the opposite compass points are not equal
        return (compassChars[CompassPoints.NW] != compassChars[CompassPoints.SE] &&
                compassChars[CompassPoints.NE] != compassChars[CompassPoints.SW])
    }

    fun findXmas() : Int {
        var xmasCount = 0

        // Convert the input into an array of arrays so we can multi-dimensional map
        for (rowIndex in input.indices) {
            println("Checking ${input[rowIndex]}")
            for (columnIndex in input[rowIndex].indices) {
                if (input[rowIndex][columnIndex] == forwards.first()) {
                    // Search around to see if we get a complete instance of forwards
                    if (searchUpFrom(rowIndex, columnIndex)) xmasCount++
                    if (searchDownFrom(rowIndex, columnIndex)) xmasCount++
                    if (searchLeftFrom(rowIndex, columnIndex)) xmasCount++
                    if (searchRightFrom(rowIndex, columnIndex)) xmasCount++
                    if (searchNorthWestFrom(rowIndex, columnIndex)) xmasCount++
                    if (searchNorthEastFrom(rowIndex, columnIndex)) xmasCount++
                    if (searchSouthWestFrom(rowIndex, columnIndex)) xmasCount++
                    if (searchSouthEastFrom(rowIndex, columnIndex)) xmasCount++
                }
            }
        }

        return xmasCount
    }

    fun searchUpFrom(row: Int, column: Int) : Boolean {
        // Search upwards in the grid by decrementing rowIndex
        var currRow = row
        for (i in forwards.indices) {
            if (input[currRow][column] != forwards[i]) {
                return false
            } else {
                currRow--
            }

            if (currRow < 0) {
                if (i != forwards.lastIndex) {
                    return false
                } else {
                    break
                }
            }
        }

        println("Found between $row,$column and $currRow,$column")
        return true
    }

    fun searchDownFrom(row: Int, column: Int) : Boolean {
        // Search downwards in the grid by incrementing rowIndex
        var currRow = row
        for (i in forwards.indices) {
            if (input[currRow][column] != forwards[i]) {
                return false
            } else {
                currRow++
            }

            if (currRow > input.lastIndex) {
                if (i != forwards.lastIndex) {
                    return false
                } else {
                    break
                }
            }
        }

        println("Found between $row,$column and $currRow,$column")
        return true
    }

    fun searchLeftFrom(row: Int, column: Int) : Boolean {
        // Search backwards in the row by decrementing columnIndex
        var currColumn = column
        for (i in forwards.indices) {
            if (input[row][currColumn] != forwards[i]) {
                return false
            } else {
                currColumn--
            }

            if (currColumn < 0) {
                if (i != forwards.lastIndex) {
                    return false
                } else {
                    break
                }
            }
        }

        println("Found between $row,$column and $row,$currColumn")
        return true
    }

    fun searchRightFrom(row: Int, column: Int) : Boolean {
        // Search forwards in the row by incrementing columnIndex
        var currColumn = column
        for (i in forwards.indices) {
            if (input[row][currColumn] != forwards[i]) {
                return false
            } else {
                currColumn++
            }

            if (currColumn > input[row].lastIndex) {
                if (i != forwards.lastIndex) {
                    return false
                } else {
                    break
                }
            }
        }

        println("Found between $row,$column and $row,$currColumn")
        return true
    }

    fun searchNorthWestFrom(row: Int, column: Int) : Boolean {
        // Search diagonally NW by decrementing both column and row indexes
        var currColumn = column
        var currRow = row
        for (i in forwards.indices) {
            if (input[currRow][currColumn] != forwards[i]) {
                return false
            } else {
                currColumn--
                currRow--
            }

            if (currColumn < 0 || currRow < 0) {
                if (i != forwards.lastIndex) {
                    return false
                } else {
                    break
                }
            }
        }

        println("Found between $row,$column and $currRow,$currColumn")
        return true
    }

    fun searchNorthEastFrom(row: Int, column: Int) : Boolean {
        // Search diagonally NE by decrementing row index and incrementing column index
        var currColumn = column
        var currRow = row
        for (i in forwards.indices) {
            if (input[currRow][currColumn] != forwards[i]) {
                return false
            } else {
                currColumn++
                currRow--
            }

            if (currRow < 0 || currColumn > input[currRow].lastIndex) {
                if (i != forwards.lastIndex) {
                    return false
                } else {
                    break
                }
            }
        }

        println("Found between $row,$column and $currRow,$currColumn")
        return true
    }

    fun searchSouthWestFrom(row: Int, column: Int) : Boolean {
        // Search diagonally SW by decrementing column index and incrementing row index
        var currColumn = column
        var currRow = row
        for (i in forwards.indices) {
            if (input[currRow][currColumn] != forwards[i]) {
                return false
            } else {
                currColumn--
                currRow++
            }

            if (currColumn < 0 || currRow > input.lastIndex) {
                if (i != forwards.lastIndex) {
                    return false
                } else {
                    break
                }
            }
        }

        println("Found between $row,$column and $currRow,$currColumn")
        return true
    }

    fun searchSouthEastFrom(row: Int, column: Int) : Boolean {
        // Search diagonally SE by incrementing column index and row index
        var currColumn = column
        var currRow = row
        for (i in forwards.indices) {
            if (input[currRow][currColumn] != forwards[i]) {
                return false
            } else {
                currColumn++
                currRow++
            }

            if (currRow > input.lastIndex || currColumn > input[currRow].lastIndex) {
                if (i != forwards.lastIndex) {
                    return false
                } else {
                    break
                }
            }
        }

        println("Found between $row,$column and $currRow,$currColumn")
        return true
    }
}

enum class CompassPoints {
    NW,
    NE,
    SW,
    SE
}