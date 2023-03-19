package com.pixelatedmind.game.tinyuniverse.ui

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector2

class PiecewiseModel(pieces: List<Piece> = listOf()) {
    private var pieces : MutableList<Piece>

    constructor(vararg pieces: Piece) : this(pieces.toList())

    init{
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

    var lastOutput : Float = 0f
    var lastPiece : PiecewiseModel.Piece? = null
    // Evaluate the piecewise function at a given x value
    fun evaluate(elapsedTime: Float): Float {
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

        return currentPiece.interpolate(endValue, alpha)
    }

    open class Piece(val start : Vector2, val interpolation : Interpolation){
        fun interpolate(end : Vector2, alpha : Float): Float{
//            val alpha = elapsedTime / (end.x - start.x)
            val result = interpolation.apply(start.y, end.y, alpha)
            if(result < 0){
                println("interpolation result < 0")
            }
            return result
        }
    }
}