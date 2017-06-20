package com.github.nskvortsov.maze

enum class Result {
    HIT_THE_WALL, OK, FOUND_TREASURE, FOUND_EXIT, VICTORY, STUCK_IN_SWAMP
}

class Player(val maze: com.github.nskvortsov.maze.Maze, var position: Pair<Int, Int> = Pair(0, 0)) {

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

    fun tryToMove(direction: com.github.nskvortsov.maze.Direction): com.github.nskvortsov.maze.Result {
        val node = maze[position]
        val newPosition = direction(position)

        if (!newPosition.isInside(maze)) {
            return com.github.nskvortsov.maze.Result.HIT_THE_WALL
        } else {
            val newContent = maze[newPosition].content
            return when {
                node.hasWall(direction = direction) -> com.github.nskvortsov.maze.Result.HIT_THE_WALL
                newContent == com.github.nskvortsov.maze.MazeNodeContent.Companion.TREASURE -> { position = newPosition; hasTreasure = true; com.github.nskvortsov.maze.Result.FOUND_TREASURE
                }
                newContent == com.github.nskvortsov.maze.MazeNodeContent.Companion.EXIT -> {
                    position = newPosition
                    if (hasTreasure) {
                        return com.github.nskvortsov.maze.Result.VICTORY
                    } else {
                        return com.github.nskvortsov.maze.Result.FOUND_EXIT
                    }
                }
                newContent == com.github.nskvortsov.maze.MazeNodeContent.Companion.SWAMP -> { position = newPosition; com.github.nskvortsov.maze.Result.STUCK_IN_SWAMP
                }
                else -> { position = newPosition; com.github.nskvortsov.maze.Result.OK
                }
            }
        }
    }
}

private fun Pair<Int, Int>.isInside(maze: com.github.nskvortsov.maze.Maze): Boolean {
    if (maze.exit == this) {
        return true
    } else {
        val allowed = 0..(maze.size - 1)
        return first in allowed && second in allowed
    }
}