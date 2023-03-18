package com.pixelatedmind.game.tinyuniverse.generation.music.proc


class AutomataCell(val xIndex : Int, val yIndex : Int, val width:Int, val isRest : Boolean = false)
{

    fun right() : Int{
        return xIndex + width
    }
    fun intersects(cell : AutomataCell) : Boolean{
        val right = right()
        if(cell.xIndex>=xIndex && cell.xIndex<right){
            return true
        }else if(xIndex >= cell.xIndex && right < cell.xIndex){
            return true
        }
        return false
    }
}