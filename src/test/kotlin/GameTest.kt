/**
 * Created by Nikita.Skvortsov
 * date: 20.06.2017.
 */
package com.github.nskvortsov.maze

import org.assertj.core.api.BDDAssertions.then
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
}
