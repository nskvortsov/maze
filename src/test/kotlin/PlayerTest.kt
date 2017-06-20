package com.github.nskvortsov.maze

import org.testng.annotations.Test

@Test
class PlayerTest {
    fun testLockedPlayer() {
        val p = Player(Maze(1))
        then(p.tryToMove(Direction.UP)).isEqualTo(Result.HIT_THE_WALL)
        then(p.tryToMove(Direction.DOWN)).isEqualTo(Result.HIT_THE_WALL)
        then(p.tryToMove(Direction.LEFT)).isEqualTo(Result.HIT_THE_WALL)
        then(p.tryToMove(Direction.RIGHT)).isEqualTo(Result.FOUND_EXIT)
    }

    fun testSmallMazeWalk() {
        val p = Player(Maze(3), Pair(1, 1))

        then(p.position).isEqualTo(Pair(1, 1))
        then(p.tryToMove(Direction.RIGHT)).isEqualTo(Result.OK)
        then(p.position).isEqualTo(Pair(1, 2))
        then(p.tryToMove(Direction.RIGHT)).isEqualTo(Result.HIT_THE_WALL)
    }

    fun testCanNotSpawnOnTreasure() {
        val m = Maze(2)
        m[Pair(1,1)] = m[Pair(1,1)].copy(MazeNodeContent.TREASURE)

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

    fun testVictoryWalk() {
        val m = Maze(2, Pair(0, 2))
        m[Pair(0,1)] = m[Pair(0,1)].copy(MazeNodeContent.TREASURE)

        val p = Player(m, Pair(0, 0))

        then(p.hasTreasure).isFalse()
        then(p.tryToMove(Direction.RIGHT)).isEqualTo(Result.FOUND_TREASURE)
        then(p.hasTreasure).isTrue()

        then(p.tryToMove(Direction.UP)).isEqualTo(Result.HIT_THE_WALL)
        then(p.tryToMove(Direction.RIGHT)).isEqualTo(Result.VICTORY)
    }

    fun testExits() {
        // exit on the right
        var m = Maze(2, Pair(0, 2))
        var p = Player(m, Pair(0, 1))

        then(p.tryToMove(Direction.RIGHT)).isEqualTo(Result.FOUND_EXIT)

        then(p.tryToMove(Direction.RIGHT)).isEqualTo(Result.HIT_THE_WALL)
        then(p.tryToMove(Direction.UP)).isEqualTo(Result.HIT_THE_WALL)
        then(p.tryToMove(Direction.DOWN)).isEqualTo(Result.HIT_THE_WALL)
        then(p.tryToMove(Direction.LEFT)).isEqualTo(Result.OK)

        // exit on the left
        m = Maze(2, Pair(0, -1))
        p = Player(m, Pair(0, 0))

        then(p.tryToMove(Direction.LEFT)).isEqualTo(Result.FOUND_EXIT)

        then(p.tryToMove(Direction.LEFT)).isEqualTo(Result.HIT_THE_WALL)
        then(p.tryToMove(Direction.UP)).isEqualTo(Result.HIT_THE_WALL)
        then(p.tryToMove(Direction.DOWN)).isEqualTo(Result.HIT_THE_WALL)
        then(p.tryToMove(Direction.RIGHT)).isEqualTo(Result.OK)

        // exit on the top
        m = Maze(2, Pair(-1, 0))
        p = Player(m, Pair(0, 0))

        then(p.tryToMove(Direction.UP)).isEqualTo(Result.FOUND_EXIT)

        then(p.tryToMove(Direction.RIGHT)).isEqualTo(Result.HIT_THE_WALL)
        then(p.tryToMove(Direction.UP)).isEqualTo(Result.HIT_THE_WALL)
        then(p.tryToMove(Direction.UP)).isEqualTo(Result.HIT_THE_WALL)
        then(p.tryToMove(Direction.DOWN)).isEqualTo(Result.OK)

        // exit on the bottom
        m = Maze(2, Pair(2, 0))
        p = Player(m, Pair(1, 0))

        then(p.tryToMove(Direction.DOWN)).isEqualTo(Result.FOUND_EXIT)

        then(p.tryToMove(Direction.RIGHT)).isEqualTo(Result.HIT_THE_WALL)
        then(p.tryToMove(Direction.DOWN)).isEqualTo(Result.HIT_THE_WALL)
        then(p.tryToMove(Direction.LEFT)).isEqualTo(Result.HIT_THE_WALL)
        then(p.tryToMove(Direction.UP)).isEqualTo(Result.OK)
    }

    fun testSwamp() {
        val m = Maze(2)
        m[0, 1] = m[0, 1].copy(MazeNodeContent.SWAMP)
        val p = Player(m)

        then(p.tryToMove(Direction.RIGHT)).isEqualTo(Result.STUCK_IN_SWAMP)
    }
}