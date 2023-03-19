package com.pixelatedmind.game.tinyuniverse.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener

class PiecewiseFunctionActor(private val function: PiecewiseModel) : Actor() {

    private val shapeRenderer = ShapeRenderer()

    init {
        addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                if (event!!.button == 0 && tapCount == 2) {
//                    val pieces = function.getPieces()
//                    val clickedX = x / width
//                    val newPiece = Piecewise.Piece({ inputX -> 2*inputX + 1 }, clickedX)
//
//                    var i = 0
//                    while (i < pieces.size && pieces[i].startingX < newPiece.startingX) {
//                        i++
//                    }
//                    if (i > 0 && pieces[i - 1].end > newPiece.startingX) {
//                        // Combine with previous piece
//                        val prevPiece = pieces[i - 1]
//                        pieces[i - 1] = Piece(prevPiece.startingX, newPiece.startingX, { x -> if (x < newPiece.startingX) prevPiece.evaluate(x) else newPiece.evaluate(x) })
//                    }
//                    if (i < pieces.size && pieces[i].startingX < newPiece.end) {
//                        // Combine with subsequent piece
//                        val nextPiece = pieces[i]
//                        pieces[i] = Piece(newPiece.startingX, nextPiece.startingX, { x -> if (x < newPiece.startingX) newPiece .evaluate(x) else nextPiece.evaluate(x) })
//                    }
//                    function.add(i, newPiece)
//                    function.pieces = pieces.toList()
                }
            }
        })
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        super.draw(batch, parentAlpha)

        shapeRenderer.projectionMatrix = batch!!.projectionMatrix
        shapeRenderer.transformMatrix = batch!!.transformMatrix
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        shapeRenderer.color = Color.GRAY
        var startX = 0f
        val resolution = 1f
        var lastTime = 0f
        var lastY = 0f
        while(startX<width){
            val currentTime = startX / width
            val y = function.evaluate(currentTime)

            val x1 = lastTime * width
            val x2 = currentTime * width
            val y1 = lastY * height
            val y2 = y * height
            shapeRenderer.line(x1, y1, x2, y2)

            lastY = y
            lastTime = currentTime
            startX += resolution
        }
        shapeRenderer.end()
        drawInterpolationPoints()
    }

    private fun drawInterpolationPoints(){
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.setColor(Color.FIREBRICK)
        function.getPieces().forEach{
            val point = it.start
            shapeRenderer.circle(point.x*width, point.y*height, 5f)
        }
        shapeRenderer.end()
    }

    companion object {
        private const val LINE_SEGMENT_LENGTH = 1f
    }
}