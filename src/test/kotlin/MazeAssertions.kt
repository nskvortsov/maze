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