package com.pixelatedmind.game.tinyuniverse.ui

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector2
import com.pixelatedmind.game.tinyuniverse.ui.model.NameableId
import java.util.*

class PiecewiseModel(pieces: List<Piece> = listOf()) : NameableId(UUID.randomUUID().toString(), "") {

    constructor(vararg pieces: Piece) : this(pieces.toList())

    var startY : Float = 0f
    var endY : Float = 1f
    var durationSecs = 1f
    var timesToRepeat = 0
    var repeatForever = false

    //if interactive, model should use the last piecewise as a release function
    var interactive = false

    private var pieces : MutableList<Piece>

    init{
        name = ""
        this.pieces = pieces.toMutableList()
    }

    fun copy() : PiecewiseModel{
        val newPieces = pieces.map{
            Piece(Vector2(it.start),it.interpolationName,it.interpolation)
        }
        val model = PiecewiseModel(newPieces)
        model.name = name
        model.startY = startY
        model.endY = endY
        model.interactive = interactive
        model.durationSecs = durationSecs
        model.repeatForever = repeatForever
        model.timesToRepeat = timesToRepeat
        return model
    }

    fun removePiece(piece : Piece) : Boolean{
        return pieces.remove(piece)
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
    open fun evaluate(elapsedTime: Float, normalized : Boolean = false): Float {
        if(pieces.size == 0){
            return 0f
        }
        val normalizedTime = elapsedTime / durationSecs
        var currentPieceIndex = pieces.indexOfFirst{it.start.x >= normalizedTime}
        if(currentPieceIndex > 0){
            currentPieceIndex--
        }
        if(currentPieceIndex < 0){
            return 0f
        }
        val endValue = pieces[currentPieceIndex+1].start
        val currentPiece = pieces[currentPieceIndex]

        val normalizedFunctionDuration = endValue.x - currentPiece.start.x
        val normalizedFunctionStartTime = currentPiece.start.x

        val alpha = (normalizedTime - normalizedFunctionStartTime) / normalizedFunctionDuration

        val result = currentPiece.interpolate(endValue, alpha)
        debug("func[$currentPieceIndex] = alpha=$alpha elapsed=$elapsedTime y=$result")
        if(normalized) {
            return result
        }
        else {
            val range = Math.abs(endY - startY)
            val scaled = startY + result * range
            return scaled
        }
    }

    var debug = false
    private fun debug(value : String){
        if(debug) {
            println(value)
        }
    }

    open class Piece(var start : Vector2, var interpolationName : String, var interpolation : Interpolation){
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