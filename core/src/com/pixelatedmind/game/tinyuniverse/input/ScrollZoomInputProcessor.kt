package com.pixelatedmind.game.tinyuniverse.input

import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.graphics.OrthographicCamera
import kotlin.math.sign

class ScrollZoomInputProcessor(private val camera: OrthographicCamera, val zoomAmount : Float = .1f) : InputAdapter() {

    override fun scrolled(amountX: Float, amountY: Float): Boolean {
        camera.zoom += zoomAmount * sign(amountY)
        return true
    }
}