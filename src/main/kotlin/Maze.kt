package com.github.nskvortsov.maze

import com.github.nskvortsov.maze.Direction.*
import com.github.nskvortsov.maze.MazeNodeContent.*

// Maze with default exit location on top right corner
class Maze(val size: Int, val exit: Pair<Int, Int> = Pair(0, size)) {

    val nodes: Array<Array<MazeNode>> = Array(size, { row ->
        Array(size, { column ->
            MazeNode(topWall   = (row == 0),
                    bottomWall = (row == size - 1),
                    leftWall   = (column == 0),
                    rightWall  = (column == size - 1))
        })
    })

    init {
        val allowed = listOf<Int>(-1, size)
        if ((exit.first !in allowed) && (exit.second !in allowed)) {
            throw IllegalArgumentException("Illegal exit coordinates $exit. One coordinates should be one of $allowed")
        }
    }

    val exitNode = MazeNode(EXIT)

    operator fun get(row: Int, column: Int): MazeNode {
        return nodes[row][column]
    }

    operator fun get(position: Pair<Int, Int>): MazeNode {
        if (position == exit) {
            return exitNode
        }
        return nodes[position.first][position.second]
    }

    operator fun set(row: Int, column: Int, newNode: MazeNode) {
        nodes[row][column] = newNode
    }

    operator fun set(position: Pair<Int, Int>, newNode: MazeNode) {
        nodes[position.first][position.second] = newNode
    }
}

open class MazeNode(val content: MazeNodeContent = EMPTY,
                    topWall: Boolean = false,
                    bottomWall: Boolean = false,
                    leftWall: Boolean = false,
                    rightWall: Boolean = false) {

    val walls: Map<Direction, Boolean> = mapOf(
            UP to topWall,
            DOWN to bottomWall,
            LEFT to leftWall,
            RIGHT to rightWall
    )

    fun hasWall(direction: Direction): Boolean {
        return walls[direction] ?: false
    }

    fun copy( content: MazeNodeContent = this.content,
              topWall: Boolean = this.walls[UP]!!,
              bottomWall: Boolean = this.walls[DOWN]!!,
              leftWall: Boolean = this.walls[LEFT]!!,
              rightWall: Boolean = this.walls[RIGHT]!!): MazeNode {
        return MazeNode(content, topWall, bottomWall, leftWall, rightWall)
    }
}

enum class Direction {
    UP, DOWN, LEFT, RIGHT;

    operator fun invoke(position: Pair<Int, Int>): Pair<Int, Int> =
            when (this) {
                UP    -> Pair(position.first - 1, position.second)
                DOWN  -> Pair(position.first + 1, position.second)
                LEFT  -> Pair(position.first, position.second - 1)
                RIGHT -> Pair(position.first, position.second + 1)
            }
}

enum class MazeNodeContent {
    EMPTY, TREASURE, EXIT
}

enum class Result {
    HIT_THE_WALL, OK, FOUND_TREASURE, FOUND_EXIT, VICTORY
}

class Player(val maze: Maze, var position: Pair<Int, Int> = Pair(0, 0)) {

    init {
        if (position.first !in 0..maze.size - 1 || position.second !in 0..maze.size - 1) {
            throw IllegalArgumentException("Can not spawn outside of the maze")
        }

        if (maze[position].content != MazeNodeContent.EMPTY) {
            throw IllegalArgumentException("Can not spawn a player over a non-empty place")
        }
    }

    var hasTreasure = false
        get() = field
        private set(value) { field = value }

    fun tryToMove(direction: Direction): Result {
        val node = maze[position]
        val newPosition = direction(position)
        return when {
            node.hasWall(direction = direction) && newPosition != maze.exit -> Result.HIT_THE_WALL
            maze[newPosition].content == TREASURE -> { position = newPosition; hasTreasure = true; Result.FOUND_TREASURE }
            maze[newPosition].content == MazeNodeContent.EXIT -> {
                position = newPosition
                if (hasTreasure) {
                    return Result.VICTORY
                } else {
                    return Result.FOUND_EXIT
                }
            }
            else -> { position = newPosition; Result.OK }
        }
    }
}