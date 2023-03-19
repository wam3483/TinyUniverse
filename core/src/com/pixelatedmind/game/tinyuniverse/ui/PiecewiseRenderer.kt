package com.pixelatedmind.game.tinyuniverse.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.*
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener

class PiecewiseRenderer : Actor() {
    val piecewise : PiecewiseModel
    var lineColor : Color

    private val zoomOrigin = Vector2()

    init{
        this.touchable = Touchable.enabled
        piecewise = PiecewiseModel()
        lineColor = Color(.2f,.2f,.2f,1f)

        val zoomScale = .05f
        this.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                println("clicked: $x $y")
            }
        })

        addListener(object : InputListener() {
            override fun scrolled(event: InputEvent?, x: Float, y: Float, amountX: Float, amountY: Float): Boolean {
                println("scrolled: $x $y $amountX $amountY")
                // Get the zoom origin in local coordinates
                zoomOrigin.set(x, y)
                stageToLocalCoordinates(zoomOrigin)

                // Apply the zoom
                val zoomAmount = if (amountY > 0) ZOOM_FACTOR else 1 / ZOOM_FACTOR
                zoomBy(zoomAmount)

                return true // Return true to indicate that the event was handled
            }
        })
    }

    val shapeRenderer = ShapeRenderer()
    override fun draw(batch: Batch?, parentAlpha: Float) {
        super.draw(batch, parentAlpha)
        val resolution = 1
//        shapeRenderer.projectionMatrix = batch!!.projectionMatrix
//        shapeRenderer.transformMatrix = batch!!.transformMatrix
//        shapeRenderer.updateMatrices()

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.color = lineColor
        shapeRenderer.circle(200f, 200f, 50f)
//        var i = 0
//        while(i<width){
//            val piecewiseX = i / width
//            val piecewiseY = piecewise.evaluate(piecewiseX)
//            val renderX = piecewiseY + i
//            val renderY = y * height
//            i += resolution
//            shapeRenderer.circle(renderX, renderY, 5f)
//        }
        shapeRenderer.end()
    }

    private fun zoomBy(zoomAmount: Float) {
        // Apply the zoom to the actor
        scaleBy(zoomAmount)

        // Translate the actor to keep the zoom origin in the same position
        val newZoomOrigin = localToStageCoordinates(zoomOrigin)
        val deltaX = zoomOrigin.x - newZoomOrigin.x
        val deltaY = zoomOrigin.y - newZoomOrigin.y
        moveBy(deltaX, deltaY)
    }

    companion object {
        private const val ZOOM_FACTOR = 1.1f
    }





}