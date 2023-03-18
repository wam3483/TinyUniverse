package com.pixelatedmind.game.tinyuniverse.ui

class Piecewise(pieces: List<Piece> = listOf()) {
    private var pieces : MutableList<Piece>

    constructor(vararg pieces: Piece) : this(pieces.toList())

    init{
        this.pieces = pieces.sortedBy { it.startingX }.toMutableList()
    }

    fun add(piece : Piece){
        pieces.add(piece)
        pieces = pieces.sortedBy { it.startingX }.toMutableList()
    }

    fun remove(piece : Piece){
        pieces.remove(piece)
        pieces = pieces.sortedBy { it.startingX }.toMutableList()
    }
    fun clear(){
        pieces.clear()
    }
    // Evaluate the piecewise function at a given x value
    fun evaluate(x: Float): Float {
        if(pieces.size == 0){
            return 0f
        }
        val index = pieces.binarySearch { it.startingX.compareTo(x) }
        val piece = if (index >= 0) pieces[index] else pieces[-index - 2]
        return piece.function(x)
    }

    class Piece(val function: (Float) -> Float, val startingX: Float) : Comparable<Piece> {
        override fun compareTo(other: Piece): Int {
            return startingX.compareTo(other.startingX)
        }
    }
}