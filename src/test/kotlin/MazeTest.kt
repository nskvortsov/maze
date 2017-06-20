package com.github.nskvortsov.maze

import com.github.nskvortsov.maze.Direction.*
import com.github.nskvortsov.maze.MazeAssertions.then
import com.github.nskvortsov.maze.MazeNodeContent.Companion.TREASURE
import org.assertj.core.api.BDDAssertions.then
import org.assertj.core.api.BDDAssertions.thenThrownBy
import org.testng.annotations.Test

@Test
class MazeTest {
    fun testOneBlockEmptyMaze() {
        val m: Maze = Maze(1)

        val node: MazeNode = m[Pair(0,0)]

        then(node)
                .isEmpty()
                .wall(UP)
                .wall(DOWN)
                .wall(LEFT)
                .noWall(RIGHT) // exit is here
    }

    fun testFourBlockEmptyMaze() {
        val m = Maze(2)

        then(m[Pair(0,0)])
                .isEmpty()
                .wall(UP)
                .wall(LEFT)
                .noWall(RIGHT)
                .noWall(DOWN)

        then(m[Pair(0,1)])
                .isEmpty()
                .wall(UP)
                .noWall(LEFT)
                .noWall(RIGHT) // exit is here
                .noWall(DOWN)

        then(m[Pair(1,0)])
                .isEmpty()
                .noWall(UP)
                .wall(LEFT)
                .noWall(RIGHT)
                .wall(DOWN)

        then(m[Pair(1,1)])
                .isEmpty()
                .noWall(UP)
                .noWall(LEFT)
                .wall(RIGHT)
                .wall(DOWN)
    }

    fun addATreasureToMaze() {
        val m = Maze(2)
        m[Pair(1,1)] = m[Pair(1,1)].copy(TREASURE)
        then(m[Pair(1,1)]).hasTreasure()
    }


    fun testExitIsValidated() {
        val m = Maze(2, Pair(0,2))
        then(m.exit).isEqualTo(Pair(0, 2))
        thenThrownBy { Maze(2, Pair(0, 0)) }
                .isInstanceOf(IllegalArgumentException::class.java)
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


