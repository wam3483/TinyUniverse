package com.pixelatedmind.game.tinyuniverse.generation.music.proc

import com.pixelatedmind.game.tinyuniverse.datastructure.Bitmap
import java.util.*

class BassMovingBandRoleImpl(val random : Random, val patternRangeStart : Float = .33f, val patternRangeEnd : Float = .66f) : BandRolePatternFinder {

    //chance that vertical note search changes when a failure to find a note in the current direction occurs.
    //if current direction isn't changed, a rest will occur instead.
    val changeDirectionChance = .2

    override fun find(bitmap: Bitmap): List<AutomataCell> {
        val result = mutableListOf<AutomataCell>()
        val width = bitmap.getWidth()
        val height = bitmap.getHeight()
        var xStart = 0
        val yIndex = (height*patternRangeStart).toInt()
        val yEnd = (height * patternRangeEnd).toInt()

        var direction = 1
        var lastCellYIndex = yIndex
        while(xStart < width) {
            val cell = findNote(bitmap,lastCellYIndex, xStart, yIndex, yEnd, direction)
            if(cell == null){
                if(random.nextDouble()<=changeDirectionChance) {
                    direction = -direction
                }
            }
            else if(direction == 1 && cell.yIndex == yEnd-1){
                direction = -1
            }else if(direction == -1 && cell.yIndex == yIndex){
                direction = 1
            }
            if(cell != null) {
                result.add(cell)
                lastCellYIndex = cell.yIndex
                xStart = cell.xIndex + cell.width
            } else {
                xStart++
            }
        }
        return result
    }

    private fun getTimeSlice(bitmap : Bitmap, x : Int, yStart : Int, yEnd:Int, output : BooleanArray){
        var y = yStart
        while(y<yEnd){
            output[yEnd - y-1] = bitmap.getValue(x,y)
            y++
        }
    }

    private fun findNote(bitmap : Bitmap, lastYIndex: Int, xStart : Int, yStart:Int, yEnd:Int, direction : Int) : AutomataCell?{
        var x = xStart

        if(xStart >= bitmap.getWidth()){
            return null
        }

        val lastY = lastYIndex - yStart + direction
        val availableNotes = BooleanArray(yEnd- yStart){false}
        while(x<bitmap.getWidth()){
            getTimeSlice(bitmap,x,yStart,yEnd,availableNotes)

            //select note
            if(direction > 0){
                var i = lastY
                while(i<availableNotes.size){
                    if(availableNotes[i]){
                        return AutomataCell(xStart,i+yStart,1,false)
                    }
                    i++
                }
            }else{

                var i = lastY
                while(i>-1){
                    if(availableNotes[i]){
                        return AutomataCell(xStart,i+yStart,1,false)
                    }
                    i--
                }
            }
            x++
        }
        return AutomataCell(xStart,yStart,1,true)
    }

    private fun selectRandomNote(availableNotes:BooleanArray) : Int{
        val possibleRange = availableNotes.count{it}
        if(possibleRange>0){
            val noteToPick = random.nextInt(possibleRange+1)
            var i =0
            var notesFound = 0
            while(i<availableNotes.size){
                if(availableNotes[i]){
                    notesFound++
                }
                if(notesFound == noteToPick){
                    return i
                }
                i++
            }
        }
        return -1
    }
}