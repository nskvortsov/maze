/**
 * Created by Nikita.Skvortsov
 * date: 20.06.2017.
 */
package com.github.nskvortsov.maze

import java.util.*

class Game(val maze: Maze, player1Name: String, player2Name: String, vararg otherPlayers: String) {
    val playerNames = listOf(player1Name, player2Name) + otherPlayers
    lateinit var players: List<Player>
    lateinit var currentPlayer: Player
    var state: GameState = GameState.NEW
        private set(value) { field = value }

    fun start(): Game {
        val r = Random()
        players = playerNames.map { name ->
            var spawnPos: Pair<Int, Int>
            do {
                spawnPos = Pair(r.nextInt(maze.size), r.nextInt(maze.size))
            } while (maze[spawnPos].content != MazeNodeContent.EMPTY)
            Player(maze, spawnPos, name)
        }
        currentPlayer = players[0]
        state = GameState.RUNNING
        return this
    }

    fun cancel() {
        state = GameState.STOPPED
    }

    fun tryMove(direction: Direction): Result {
        val result = currentPlayer.tryToMove(direction)
        if (result != Result.HIT_THE_WALL) {
            currentPlayer = currentPlayer.nextPlayer()
        }
        return result
    }

    fun Player.nextPlayer(): Player {
        return players[(players.indexOf(this) + 1) % players.size]
    }
}

enum class GameState {
    NEW, RUNNING, STOPPED
}
