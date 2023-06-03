package com.pixelatedmind.game.tinyuniverse.screen.game

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import java.util.*

class GameScreenManager : Game() {
    override fun create() {
        val bitmapFont = BitmapFont(Gdx.files.internal("fonts/consolas.fnt"))
        val screen = NewIterationScreen(bitmapFont,Gdx.graphics.width.toFloat(),Gdx.graphics.height.toFloat(), 1, Random().nextLong())
        this.setScreen(screen)
    }
}