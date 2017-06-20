package com.github.nskvortsov.maze

import com.github.nskvortsov.maze.Direction.*
import com.github.nskvortsov.maze.MazeNodeContent.Companion.EMPTY
import com.github.nskvortsov.maze.MazeNodeContent.Companion.EXIT
import com.github.nskvortsov.maze.MazeNodeContent.Companion.TREASURE

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

        val corners = listOf<Pair<Int, Int>>(Pair(-1, -1), Pair(-1, size), Pair(size, -1), Pair(size, size))
        if (exit in corners) {
            throw IllegalArgumentException("Illegal exit coordinates $exit. Exit must not be in a corner")
        }
    }

    val exitNode = createAndConnectExitNode()

    fun createAndConnectExitNode(): MazeNode {
        val exitDirection = when {
            exit.first == -1     -> Direction.DOWN
            exit.first == size   -> Direction.UP
            exit.second == -1    -> Direction.RIGHT
            exit.second == size  -> Direction.LEFT
            else -> throw IllegalStateException("Hey something is very wrong with the exit! it is at $exit")
        }

        this[exitDirection(exit)] = this[exitDirection(exit)].removeWallAt(exitDirection.reverse())
        return MazeNode(EXIT, true, true, true, true).removeWallAt(exitDirection)
    }

    operator fun get(x: Int, y: Int) = get(Pair(x, y))
    operator fun get(position: Pair<Int, Int>): MazeNode {
        if (position == exit) {
            return exitNode
        }
        return nodes[position.first][position.second]
    }

    operator fun set(x: Int, y: Int, newNode: MazeNode) = set(Pair(x, y), newNode)
    operator fun set(position: Pair<Int, Int>, newNode: MazeNode) {
        nodes[position.first][position.second] = newNode
    }

    fun  putTreasure(x: Int, y: Int): Boolean {
        val node = get(x, y)
        if (node.content == EMPTY) {
            set(x, y, node.copy(TREASURE))
            return true
        }
        return false
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
              topWall: Boolean = walls[UP]!!,
              bottomWall: Boolean = walls[DOWN]!!,
              leftWall: Boolean = walls[LEFT]!!,
              rightWall: Boolean = walls[RIGHT]!!): MazeNode {
        return MazeNode(content, topWall, bottomWall, leftWall, rightWall)
    }

    fun  removeWallAt(dir: Direction): MazeNode {
        val copyWalls = walls.toMutableMap()
        copyWalls[dir] = false
        return MazeNode(content,
                topWall = copyWalls[UP]!!,
                bottomWall = copyWalls[DOWN]!!,
                leftWall = copyWalls[LEFT]!!,
                rightWall = copyWalls[RIGHT]!!)
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

    fun reverse() = when (this) {
        UP    -> DOWN
        DOWN  -> UP
        LEFT  -> RIGHT
        RIGHT -> LEFT
    }
}

class MazeNodeContent(val allowsSpawn:Boolean = false) {
    companion object {
        val TREASURE = MazeNodeContent()
        val EMPTY = MazeNodeContent(true)
        val EXIT = MazeNodeContent()
        val SWAMP = MazeNodeContent()
    }
}

