package com.github.nskvortsov.maze

import com.github.nskvortsov.maze.Direction.*
import com.github.nskvortsov.maze.MazeNodeContent.EMPTY

class Maze(val size: Int) {

    val nodes: Array<Array<MazeNode>> = Array(size, { row ->
        Array(size, { column ->
            MazeNode(topWall   = (row == 0),
                    bottomWall = (row == size - 1),
                    leftWall   = (column == 0),
                    rightWall  = (column == size - 1))
        })
    })

    operator fun get(row: Int, column: Int): MazeNode {
        return nodes[row][column]
    }

    operator fun set(row: Int, column: Int, newNode: MazeNode) {
        nodes[row][column] = newNode
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
    UP,DOWN,LEFT,RIGHT
}

enum class MazeNodeContent {
    EMPTY, TREASURE
}

enum class Result {
    HIT_THE_WALL, OK
}

class Player(val maze: Maze, var row: Int = 0, var column: Int = 0) {
    fun tryToMove(d: Direction): Result {
        val node = maze[row, column]
        if (node.hasWall(direction = d)) {
            return Result.HIT_THE_WALL
        } else {
            updatePosition(d)
            return Result.OK
        }
    }

    private fun updatePosition(d: Direction) = when (d) {
        UP    -> { row-- }
        DOWN  -> { row++ }
        LEFT  -> { column-- }
        RIGHT -> { column++ }
    }
}