package day09

import utils.readInput

/**
 * URL: https://adventofcode.com/2024/day/9
 * Part 2: https://adventofcode.com/2024/day/9#part2
 */
fun main() {
    fun part1(input: List<String>): Long {
        return DiskFragmenter(input).generateChecksum()
    }

    fun part2(input: List<String>): Long {
        return DiskFragmenter(input).moveEntireFilesAndChecksum()
    }

    val input = readInput("day09/Day09")
    println(part1(input))
    println(part2(input))
}

class DiskFragmenter(private val input: List<String>) {
    private val empty = Long.MIN_VALUE

    fun generateChecksum() : Long {
        // For now the input is just a single line
        val diskSpace = buildStartingArray(input.first())
        moveBlocksToFreeSpace(diskSpace)

        return generateChecksumFromDiskSpace(diskSpace)
    }

    fun moveEntireFilesAndChecksum() : Long {
        val diskSpace = buildStartingArray(input.first())
        moveFilesToFreeSpace(diskSpace)

        println(diskSpace.joinToString())
        return generateChecksumFromDiskSpace(diskSpace)
    }

    private fun moveFilesToFreeSpace(diskSpace: MutableList<Long>) {
        val freeSpaces = buildFreeSpaceMap(diskSpace)

        /**
         * Work backwards through the disk space, identifying whole blocks
         * of files that can fit into a free space and move them.
         * Update freeSpaces after each move.
         */
        var rightIndex = diskSpace.lastIndex
        var leftIndex = rightIndex - 1
        while (leftIndex > 0) {
            if (diskSpace[rightIndex] == empty) {
                rightIndex--
                leftIndex = rightIndex - 1
            } else {
                if (diskSpace[leftIndex] != diskSpace[rightIndex]) {
                    val rangeStart = leftIndex + 1
                    val spaceSize = (rightIndex - rangeStart) + 1
                    for (freeSpaceStart in freeSpaces.keys.sorted()) {
                        if (freeSpaceStart < rangeStart && freeSpaces[freeSpaceStart]!! >= spaceSize) {
                            for (i in 0..<spaceSize) {
                                diskSpace[freeSpaceStart + i] = diskSpace[rangeStart + i]
                                diskSpace[rangeStart + i] = empty
                            }
                            updateFreeSpaceMap(freeSpaces, freeSpaceStart, spaceSize)
                            break
                        }
                    }

                    rightIndex = leftIndex
                    leftIndex--
                } else {
                    leftIndex--
                }
            }
        }
    }

    private fun updateFreeSpaceMap(
        freeSpaces: MutableMap<Int, Int>,
        startingIndexFilled: Int,
        spacesTaken: Int) {
        val originalFreeSpaces = freeSpaces[startingIndexFilled]
        if (originalFreeSpaces != null) {
            if (originalFreeSpaces > spacesTaken) {
                val newStartIndex = startingIndexFilled + spacesTaken
                freeSpaces[newStartIndex] = originalFreeSpaces - spacesTaken
            }
        }

        freeSpaces.remove(startingIndexFilled)
    }

    /**
     * Build map of index -> free spaces
     * Keep the map up-to-date every time we move, especially
     * if we don't fill up the free space.
     */
    private fun buildFreeSpaceMap(
       diskSpace: List<Long>
    ) : MutableMap<Int, Int> {
        val freeSpaces = mutableMapOf<Int, Int>()

        var freeSpaceStart = -1
        var inFreeSpace = false
        diskSpace.forEachIndexed { index, value ->
            if (value == empty && !inFreeSpace) {
                inFreeSpace = true
                freeSpaceStart = index
            } else if (value != empty && inFreeSpace) {
                inFreeSpace = false
                val freeSpaceSize = index - freeSpaceStart
                freeSpaces[freeSpaceStart] = freeSpaceSize
            }
        }

        return freeSpaces
    }

    private fun moveBlocksToFreeSpace(diskSpace: MutableList<Long>) {
        /**
         * Simple two pointer solution, move left until it hits an 'empty' spot
         * and move right when it's not 'empty'
         */
        var left = 0
        var right = diskSpace.lastIndex

        while (left < right) {
            if (diskSpace[left] == empty && diskSpace[right] != empty) {
                diskSpace[left] = diskSpace[right]
                diskSpace[right] = empty

                left += 1
                right -= 1
            } else {
                if (diskSpace[left] != empty) {
                    left += 1
                }

                if (diskSpace[right] == empty) {
                    right -= 1
                }
            }
        }
    }

    private fun generateChecksumFromDiskSpace(diskSpace: List<Long>) : Long {
        var checksum = 0L

        for (i in diskSpace.indices) {
            if (diskSpace[i] != empty) {
                checksum += diskSpace[i] * i
            }
        }

        return checksum
    }

    /**
     * We need to build an array/list where each index either contains the
     * file id for that location or is empty or some value indicating emptiness, such as '.'.
     */
    private fun buildStartingArray(input: String) : MutableList<Long> {
        val diskSpace = mutableListOf<Long>()
        var fileId = 0L

        for (i in input.indices) {
            /**
             * If the index is odd we add that much blank space
             * Otherwise we add that many 'file ids'
             */
            if (i % 2 != 0) {
                for (j in 0..<input[i].digitToInt()) {
                    diskSpace.add(empty)
                }
            } else {
                for (j in 0..<input[i].digitToInt()) {
                    diskSpace.add(fileId)
                }
                fileId++
            }
        }

        return diskSpace
    }
}