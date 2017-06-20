/**
 * Created by Nikita.Skvortsov
 * date: 20.06.2017.
 */
package com.github.nskvortsov.maze

import java.util.*

class Game(val maze: Maze, player1Name: String, player2Name: String, vararg otherPlayers: String) {
    val playerNames = listOf(player1Name, player2Name) + otherPlayers
    lateinit var players: List<Player>
    fun start(): Game {
        val r = Random()
        players = playerNames.map { name ->
            var spawnPos: Pair<Int, Int>
            do {
                spawnPos = Pair(r.nextInt(maze.size), r.nextInt(maze.size))
            } while (maze[spawnPos].content != MazeNodeContent.EMPTY)
            Player(maze, spawnPos, name)
        }
        state = GameState.RUNNING
        return this
    }

    fun cancel() {
        state = GameState.STOPPED
    }

    var state: GameState = GameState.NEW
        private set(value) { field = value }
}

enum class GameState {
    NEW, RUNNING, STOPPED
}
