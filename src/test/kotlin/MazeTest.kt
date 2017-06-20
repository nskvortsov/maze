package com.github.nskvortsov.maze

import com.github.nskvortsov.maze.Direction.*
import com.github.nskvortsov.maze.MazeAssertions.then
import com.github.nskvortsov.maze.MazeNodeContent.TREASURE
import com.github.nskvortsov.maze.Result.*
import org.assertj.core.api.BDDAssertions.then
import org.assertj.core.api.BDDAssertions.thenThrownBy
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
                .noWall(RIGHT) // exit is here
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
                .noWall(RIGHT) // exit is here
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
        then(p.tryToMove(UP)).isEqualTo(HIT_THE_WALL)
        then(p.tryToMove(DOWN)).isEqualTo(HIT_THE_WALL)
        then(p.tryToMove(LEFT)).isEqualTo(HIT_THE_WALL)
        then(p.tryToMove(RIGHT)).isEqualTo(FOUND_EXIT)
    }

    fun testSmallMazeWalk() {
        val p = Player(Maze(3), Pair(1, 1))

        then(p.position).isEqualTo(Pair(1, 1))
        then(p.tryToMove(RIGHT)).isEqualTo(OK)
        then(p.position).isEqualTo(Pair(1, 2))
        then(p.tryToMove(RIGHT)).isEqualTo(HIT_THE_WALL)
    }

    fun testCanNotSpawnOnTreasure() {
        val m = Maze(2)
        m[1,1] = m[1,1].copy(TREASURE)

        thenThrownBy { Player(m, Pair(1, 1)) }
                .isInstanceOf(IllegalArgumentException::class.java)
                .withFailMessage("Can not spawn a player over a non-empty place")
    }

    fun testCanNotSpawnOutside() {
        thenThrownBy { Player(Maze(2), Pair(2, 2)) }
                .isInstanceOf(IllegalArgumentException::class.java)

        thenThrownBy { Player(Maze(2), Pair(2, 0)) }
                .isInstanceOf(IllegalArgumentException::class.java)

        thenThrownBy { Player(Maze(2), Pair(0, 2)) }
                .isInstanceOf(IllegalArgumentException::class.java)

        thenThrownBy { Player(Maze(2), Pair(-1, -1)) }
                .isInstanceOf(IllegalArgumentException::class.java)
    }

    fun testExitIsValidated() {
        val m = Maze(2, Pair(0,2))
        then(m.exit).isEqualTo(Pair(0, 2))
        thenThrownBy { Maze(2, Pair(0, 0)) }
                .isInstanceOf(IllegalArgumentException::class.java)
    }

    fun testVictoryWalk() {
        val m = Maze(2, Pair(0,2))
        m[0,1] = m[0,1].copy(TREASURE)

        val p = Player(m, Pair(0, 0))

        then(p.hasTreasure).isFalse()
        then(p.tryToMove(RIGHT)).isEqualTo(FOUND_TREASURE)
        then(p.hasTreasure).isTrue()

        then(p.tryToMove(UP)).isEqualTo(HIT_THE_WALL)
        then(p.tryToMove(RIGHT)).isEqualTo(VICTORY)
    }

    fun testExits() {
        // exit on the right
        var m = Maze(2, Pair(0, 2))
        var p = Player(m, Pair(0, 1))

        then(p.tryToMove(RIGHT)).isEqualTo(FOUND_EXIT)

        then(p.tryToMove(RIGHT)).isEqualTo(HIT_THE_WALL)
        then(p.tryToMove(UP)).isEqualTo(HIT_THE_WALL)
        then(p.tryToMove(DOWN)).isEqualTo(HIT_THE_WALL)
        then(p.tryToMove(LEFT)).isEqualTo(OK)

        // exit on the left
        m = Maze(2, Pair(0, -1))
        p = Player(m, Pair(0, 0))

        then(p.tryToMove(LEFT)).isEqualTo(FOUND_EXIT)

        then(p.tryToMove(LEFT)).isEqualTo(HIT_THE_WALL)
        then(p.tryToMove(UP)).isEqualTo(HIT_THE_WALL)
        then(p.tryToMove(DOWN)).isEqualTo(HIT_THE_WALL)
        then(p.tryToMove(RIGHT)).isEqualTo(OK)

        // exit on the top
        m = Maze(2, Pair(-1, 0))
        p = Player(m, Pair(0, 0))

        then(p.tryToMove(UP)).isEqualTo(FOUND_EXIT)

        then(p.tryToMove(RIGHT)).isEqualTo(HIT_THE_WALL)
        then(p.tryToMove(UP)).isEqualTo(HIT_THE_WALL)
        then(p.tryToMove(UP)).isEqualTo(HIT_THE_WALL)
        then(p.tryToMove(DOWN)).isEqualTo(OK)

        // exit on the bottom
        m = Maze(2, Pair(2, 0))
        p = Player(m, Pair(1, 0))

        then(p.tryToMove(DOWN)).isEqualTo(FOUND_EXIT)

        then(p.tryToMove(RIGHT)).isEqualTo(HIT_THE_WALL)
        then(p.tryToMove(DOWN)).isEqualTo(HIT_THE_WALL)
        then(p.tryToMove(LEFT)).isEqualTo(HIT_THE_WALL)
        then(p.tryToMove(UP)).isEqualTo(OK)
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


