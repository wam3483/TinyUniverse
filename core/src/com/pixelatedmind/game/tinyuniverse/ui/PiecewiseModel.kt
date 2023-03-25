package com.pixelatedmind.game.tinyuniverse.ui

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector2

class PiecewiseModel(pieces: List<Piece> = listOf()) {
    var name : String
    var startX : Float = 0f
    var endX : Float = 1f
    var durationSecs = 1f
    var timesToRepeat = 0
    var repeatForever = false
    private var pieces : MutableList<Piece>

    constructor(vararg pieces: Piece) : this(pieces.toList())

    init{
        name = ""
        this.pieces = pieces.toMutableList()
    }

    fun getPieces() : List<Piece>{
        return pieces.toList()
    }

    fun add(index : Int, piece : Piece){
        pieces.add(index, piece)
    }

    fun add(piece : Piece){
        pieces.add(piece)
    }

    fun clear(){
        pieces.clear()
    }

    // Evaluate the piecewise function at a given x value
    open fun evaluate(elapsedTime: Float): Float {
        if(pieces.size == 0){
            return 0f
        }
        val currentPieceIndex = pieces.indexOfFirst{it.start.x>=elapsedTime} - 1
        if(currentPieceIndex < 0){
            return 0f
        }
        val endValue = pieces[currentPieceIndex+1].start
        val currentPiece = pieces[currentPieceIndex]
        val alpha = (elapsedTime - currentPiece.start.x) / (endValue.x - currentPiece.start.x)

        val result = currentPiece.interpolate(endValue, alpha)
        val range = Math.abs(endX - startX)
        return startX + result * range
    }

    open class Piece(var start : Vector2, var interpolationName : String, private var interpolation : Interpolation){
        fun setInterpolation(name : String, interpolation : Interpolation){
            this.interpolationName = name
            this.interpolation = interpolation
        }
        fun interpolate(end : Vector2, alpha : Float): Float{
            val result = interpolation.apply(start.y, end.y, alpha)
            return result
        }
    }
}