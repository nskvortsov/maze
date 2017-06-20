package com.github.nskvortsov.maze

enum class Result {
    HIT_THE_WALL, OK, FOUND_TREASURE, FOUND_EXIT, VICTORY, STUCK_IN_SWAMP
}

class Player(val maze: Maze, var position: Pair<Int, Int> = Pair(0, 0), val name: String = "Player") {

    init {
        if (position.first !in 0..maze.size - 1 || position.second !in 0..maze.size - 1) {
            throw IllegalArgumentException("Can not spawn outside of the maze")
        }

        if (!maze[position].content.allowsSpawn) {
            throw IllegalArgumentException("Can not spawn a player over a non-empty place")
        }
    }

    var hasTreasure = false
        get() = field
        private set(value) { field = value }

    fun tryToMove(direction: Direction): Result {
        val node = maze[position]
        val newPosition = direction(position)

        if (!newPosition.isInside(maze)) {
            return Result.HIT_THE_WALL
        } else {
            val newContent = maze[newPosition].content
            return when {
                node.hasWall(direction = direction) -> Result.HIT_THE_WALL
                newContent == MazeNodeContent.TREASURE -> { position = newPosition; hasTreasure = true; Result.FOUND_TREASURE }
                newContent == MazeNodeContent.EXIT -> {
                    position = newPosition
                    if (hasTreasure) {
                        return Result.VICTORY
                    } else {
                        return Result.FOUND_EXIT
                    }
                }
                newContent == MazeNodeContent.SWAMP -> { position = newPosition; Result.STUCK_IN_SWAMP }
                else -> { position = newPosition; Result.OK }
            }
        }
    }
}

private fun Pair<Int, Int>.isInside(maze: Maze): Boolean {
    if (maze.exit == this) {
        return true
    } else {
        val allowed = 0..(maze.size - 1)
        return first in allowed && second in allowed
    }
}