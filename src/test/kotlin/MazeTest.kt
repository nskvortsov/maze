package com.github.nskvortsov.maze

import com.github.nskvortsov.maze.Direction.*
import com.github.nskvortsov.maze.MazeAssertions.then
import com.github.nskvortsov.maze.MazeNodeContent.TREASURE
import org.assertj.core.api.BDDAssertions.then
import org.testng.annotations.Test

@Test
class MazeTest {
    fun testOneBlockEmptyMaze() {
        val m: Maze = Maze(1)

        val node: MazeNode = m[0,0]

        then(node)
                .isEmpty()
                .wall(UP)
                .wall(DOWN)
                .wall(LEFT)
                .wall(RIGHT)
    }

    fun testFourBlockEmptyMaze() {
        val m = Maze(2)

        then(m[0,0])
                .isEmpty()
                .wall(UP)
                .wall(LEFT)
                .noWall(RIGHT)
                .noWall(DOWN)

        then(m[0,1])
                .isEmpty()
                .wall(UP)
                .noWall(LEFT)
                .wall(RIGHT)
                .noWall(DOWN)

        then(m[1,0])
                .isEmpty()
                .noWall(UP)
                .wall(LEFT)
                .noWall(RIGHT)
                .wall(DOWN)

        then(m[1,1])
                .isEmpty()
                .noWall(UP)
                .noWall(LEFT)
                .wall(RIGHT)
                .wall(DOWN)
    }

    fun addATreasureToMaze() {
        val m = Maze(2)
        m[1,1] = m[1,1].copy(TREASURE)
        then(m[1,1]).hasTreasure()
    }
}

@Test
class PlayerTest {
    fun testLockedPlayer() {
        val p = Player(Maze(1))
        then(p.tryToMove(UP)).isEqualTo(Result.HIT_THE_WALL)
        then(p.tryToMove(DOWN)).isEqualTo(Result.HIT_THE_WALL)
        then(p.tryToMove(LEFT)).isEqualTo(Result.HIT_THE_WALL)
        then(p.tryToMove(RIGHT)).isEqualTo(Result.HIT_THE_WALL)
    }

    fun testSmallMazeWalk() {
        val p = Player(Maze(3), row = 1, column = 1)

        then(p.row).isEqualTo(1)
        then(p.column).isEqualTo(1)

        then(p.tryToMove(RIGHT)).isEqualTo(Result.OK)

        then(p.row).isEqualTo(1)
        then(p.column).isEqualTo(2)

        then(p.tryToMove(RIGHT)).isEqualTo(Result.HIT_THE_WALL)
    }
}

@Test
class MazeNodeTest {
    fun testDefaultCreation() {
        then(MazeNode())
                .isEmpty()
                .noWall(UP)
                .noWall(DOWN)
                .noWall(LEFT)
                .noWall(RIGHT)
    }

    fun testCopying() {
        then(MazeNode().copy(TREASURE, topWall = true, bottomWall = true))
                .noWall(LEFT)
                .noWall(RIGHT)
                .wall(UP)
                .wall(DOWN)
                .hasTreasure()
    }
}


