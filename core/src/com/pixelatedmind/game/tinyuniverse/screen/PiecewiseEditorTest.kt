package com.pixelatedmind.game.tinyuniverse.screen

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.ScreenUtils
import com.pixelatedmind.game.tinyuniverse.ui.PiecewiseModel
import com.pixelatedmind.game.tinyuniverse.ui.PiecewiseFunctionActor

class PiecewiseEditorTest : ApplicationAdapter() {
    lateinit var stage : Stage

    private fun color(r : Int, g : Int, b : Int, a: Int): Color {
        return Color(r/255f, g/255f,b/255f,a/255f)
    }

    override fun create() {
        val w = Gdx.graphics.width.toFloat()
        val h = Gdx.graphics.height.toFloat()

        stage = Stage()
        val piecewiseEditor = PiecewiseFunctionActor(
                PiecewiseModel(listOf<PiecewiseModel.Piece>(
                        PiecewiseModel.Piece(Vector2(0f, 0f), Interpolation.sine),
                        PiecewiseModel.Piece(Vector2(.5f, 1f), Interpolation.sine),
                        PiecewiseModel.Piece(Vector2(1f, 0f), Interpolation.linear)
                ))
        )
        val group = Group()
        group.addActor(piecewiseEditor)
        piecewiseEditor.width = w
        piecewiseEditor.height = h

        stage.addActor(group)
        Gdx.input.setInputProcessor(stage)
        stage.setKeyboardFocus(piecewiseEditor)
        stage.setScrollFocus(piecewiseEditor)
        super.create()
    }

    override fun render() {
        ScreenUtils.clear(1f, 1f, 1f, 1f)
        Gdx.gl.glEnable(GL20.GL_BLEND)
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
        stage.draw()
    }
}