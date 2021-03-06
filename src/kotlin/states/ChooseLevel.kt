package com.ericharm
import java.io.File
import com.googlecode.lanterna.screen.TerminalScreen
import com.googlecode.lanterna.TerminalPosition
import com.googlecode.lanterna.TerminalSize
import com.googlecode.lanterna.input.KeyType

class ChooseLevel(): State {
    init {
        ScreenPosition.updateOffsetsForSize(TerminalSize(24, 6))
    }

    data class LevelOption(val name: String, val filename: String, val location: Point)

    val descriptors = listOf(
        LevelOption("Level 1", "./data/1a.lvl", Point(0, 0)),
        LevelOption("Level 2", "./data/1b.lvl", Point(12, 0)),
        LevelOption("Level 3", "./data/2a.lvl", Point(0, 2)),
        LevelOption("Level 4", "./data/2b.lvl", Point(12, 2)),
        LevelOption("Level 5", "./data/3a.lvl", Point(0, 4)),
        LevelOption("Level 6", "./data/3b.lvl", Point(12, 4)),
        LevelOption("Level 7", "./data/4a.lvl", Point(0, 6)),
        LevelOption("Level 8", "./data/4b.lvl", Point(12, 6))
    )

    enum class Sides { LEFT, RIGHT }

    var selectedLevel = 0

    fun drawCursor(screen: TerminalScreen) {
        val location = descriptors[selectedLevel].location
        val cursorX = location.x + ScreenPosition.offsetX - 2
        val cursorY = location.y + ScreenPosition.offsetY
        screen.setCursorPosition(TerminalPosition(cursorX, cursorY))
    }

    override fun render(screen: TerminalScreen) {
        screen.clear()
        val graphics = screen.newTextGraphics()
        descriptors.forEach {
            val x = ScreenPosition.offsetX + it.location.x
            val y = ScreenPosition.offsetY + it.location.y
            graphics.putString(x, y, it.name)
        }
        drawCursor(screen)
        screen.refresh()
    }

    override fun handleInput(key: KeyType) {
        val column = if (selectedLevel % 2 == 0) Sides.LEFT else Sides.RIGHT
        when (key) {
            KeyType.Escape -> App.swapCurrentState(MainMenu())
            KeyType.ArrowLeft -> if (key == KeyType.ArrowLeft && selectedLevel > 0) selectedLevel -= 1
            KeyType.ArrowRight -> if (selectedLevel < descriptors.size - 1) selectedLevel += 1
            KeyType.ArrowUp -> {
                if (column == Sides.LEFT && selectedLevel > 0) selectedLevel -= 2
                else if (column == Sides.RIGHT && selectedLevel > 1) selectedLevel -= 2
            }
            KeyType.ArrowDown -> {
                if (column == Sides.LEFT && selectedLevel <= descriptors.size / 2) selectedLevel += 2
                else if (column == Sides.RIGHT && selectedLevel < descriptors.size - 1) selectedLevel += 2
            }
            KeyType.Enter -> {
                val descriptor = File(descriptors[selectedLevel].filename)
                App.swapCurrentState(Game().apply { level = Level.fromDescriptor(descriptor) })
            }
            else -> {}
        }
    }

    override fun update() {}
}
