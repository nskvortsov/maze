/**
 * Created by Nikita.Skvortsov
 * date: 20.06.2017.
 */
package com.github.nskvortsov.maze

import com.github.nskvortsov.maze.Direction.*
import com.github.nskvortsov.maze.Result.*
import org.assertj.core.api.BDDAssertions.then
import com.github.nskvortsov.maze.GameAssertions.scenario
import org.testng.annotations.Test

@Test
class GameTest {
    fun testStartStop() {
        val maze = Maze(2)
        then(maze.putTreasure(1,1)).isTrue()

        val g = Game(maze, "Player 1", "Player 2").start()
        then(g.players).extracting("name").containsExactly("Player 1", "Player 2")
        then(g.state).isEqualTo(GameState.RUNNING)

        g.players.forEach { println("Player [${it.name}] is spawned at ${it.position}") }
        g.cancel()

        then(g.state).isEqualTo(GameState.STOPPED)
    }

    fun testPlayersOrder() {
        val maze = Maze(2)
        maze.putTreasure(1, 1)

        val g = Game(maze, "p1", "p2", "p3").start()

        then(g.state).isEqualTo(GameState.RUNNING)
        then(g.players).hasSize(3)

        val firstPlayer = g.players[0]
        val secondPlayer = g.players[1]
        val thirdPlayer = g.players[2]
        then(g.currentPlayer).isEqualTo(firstPlayer)

        // place all players in the corner for test
        firstPlayer.position = Pair(0, 0)
        secondPlayer.position = Pair(0, 0)
        thirdPlayer.position = Pair(0, 0)

        with(scenario(g)) {
            isRunning()
            move(LEFT)
            result(HIT_THE_WALL)
            current(firstPlayer)

            move(UP)
            result(HIT_THE_WALL)
            current(firstPlayer)

            move(RIGHT)
            result(OK)
            current(secondPlayer)

            move(DOWN)
            result(OK)
            current(thirdPlayer)

            move(DOWN)
            result(OK)
            current(firstPlayer)

            move(DOWN)
            result(FOUND_TREASURE)
            current(secondPlayer)
        }
    }
}
