package com.github.nskvortsov.maze

import org.assertj.core.api.AbstractAssert

object MazeAssertions {
    fun assertThat(actual: MazeNode): MazeNodeAssert {
        return MazeNodeAssert(actual)
    }

    fun then(actual: MazeNode): MazeNodeAssert {
        return MazeNodeAssert(actual)
    }
}

object GameAssertions {
    fun scenario(actual: Game): GameAssert {
        return GameAssert(actual)
    }
}

class GameAssert(actual: Game): AbstractAssert<GameAssert, Game>(actual, GameAssert::class.java) {
    lateinit var lastResult: Result

    fun isRunning(): GameAssert {
        if (actual.state != GameState.RUNNING) {
            failWithMessage("Expecting game to be running")
        }
        return this
    }

    fun move(direction: Direction): GameAssert {
        lastResult = actual.tryMove(direction)
        return this
    }

    fun result(expected: Result): GameAssert {
        if (lastResult != expected) {
            failWithMessage("Wrong last result state. Expected $expected, but was $lastResult")
        }
        return this
    }

    fun current(player: Player): GameAssert {
        if (actual.currentPlayer != player) {
            failWithMessage("Wrong current player. Expected ${player.name}, but was ${actual.currentPlayer.name}")
        }
        return this
    }

    fun winner(player: Player?): GameAssert {
        if (player != actual.winner) {
            failWithMessage("Wrong winner. Expected ${player?.name ?: "no one"} but was ${actual.winner?.name ?: "no one"}")
        }
        return this
    }

    fun winner(playerName: String?): GameAssert {
        if (playerName != actual.winner?.name) {
            failWithMessage("Wrong winner. Expected ${playerName ?: "no one"} but was ${actual.winner?.name ?: "no one"}")
        }
        return this
    }

}

class MazeNodeAssert(actual: MazeNode): AbstractAssert<MazeNodeAssert, MazeNode>(actual, MazeNodeAssert::class.java) {
    fun isEmpty(): MazeNodeAssert {
        if (actual.content != MazeNodeContent.EMPTY) {
            failWithMessage("Maze node should be empty, but it is ${actual.content}")
        }
        return this
    }

    fun wall(d: Direction): MazeNodeAssert {
        if (!actual.hasWall(d)) {
            failWithMessage("wall is missing at direction $d of maze node")
        }
        return this
    }

    fun noWall(d: Direction): MazeNodeAssert {
        if (actual.hasWall(d)) {
            failWithMessage("Unexpected wall at direction $d of maze node")
        }
        return this
    }

    fun hasTreasure(): MazeNodeAssert {
        if (actual.content != MazeNodeContent.TREASURE) {
            failWithMessage("Expected a treasure in the node")
        }
        return this
    }


}