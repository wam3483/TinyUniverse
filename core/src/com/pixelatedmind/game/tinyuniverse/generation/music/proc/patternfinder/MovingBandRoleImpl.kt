package com.pixelatedmind.game.tinyuniverse.generation.music.proc.patternfinder

import com.pixelatedmind.game.tinyuniverse.datastructure.Bitmap
import com.pixelatedmind.game.tinyuniverse.generation.music.proc.AutomataCell
import com.pixelatedmind.game.tinyuniverse.generation.music.proc.BandRolePatternFinder
import java.util.*

class MovingBandRoleImpl(val scaleNotation : List<Int>,
                         val random : Random) : BandRolePatternFinder {

    //chance that vertical note search changes when a failure to find a note in the current direction occurs.
    //if current direction isn't changed, a rest will occur instead.
    val changeDirectionChance = .2

    override fun find(bitmap: Bitmap): List<AutomataCell> {
        val queryHelper = BandRoleQueryHelper(bitmap)
        val width = bitmap.getWidth()
        var x = 0
        var lastScaleIndex = -1
        var direction = 1
        val result = mutableListOf<AutomataCell>()

        while(x<width){
            val cell = findNextNote2(queryHelper,lastScaleIndex,x,direction)
            if(cell!=null){
                result.add(cell)
                x = cell.right()
                if(random.nextDouble()>.2)
                    lastScaleIndex += direction
                if(direction == -1 && lastScaleIndex <= 0){
                    lastScaleIndex = 0
                    direction = -direction
                }else if(direction == 1 && lastScaleIndex >= scaleNotation.size-1){
                    lastScaleIndex = scaleNotation.size - 2
                    direction = -direction
                }
            }else{
                x++
            }
        }
        return result
    }
//        val result = mutableListOf<AutomataCell>()
//        val width = bitmap.getWidth()
//        val height = bitmap.getHeight()
//        var xStart = 0
//        val yIndex = (height*patternRangeStart).toInt()
//        val yEnd = (height * patternRangeEnd).toInt()
//
//        var direction = 1
//        var lastCellYIndex = yIndex
//        while(xStart < width) {
//            val cell = findNote(bitmap,lastCellYIndex, xStart, yIndex, yEnd, direction)
//            if(cell == null){
//                if(random.nextDouble()<=changeDirectionChance) {
//                    direction = -direction
//                }
//            }
//            else if(direction == 1 && cell.yIndex == yEnd-1){
//                direction = -1
//            }else if(direction == -1 && cell.yIndex == yIndex){
//                direction = 1
//            }
//            if(cell != null) {
//                result.add(cell)
//                lastCellYIndex = cell.yIndex
//                xStart = cell.xIndex + cell.width
//            } else {
//                xStart++
//            }
//        }
//        return result
//    }

    private fun findNextNote2(queryHelper: BandRoleQueryHelper, lateNoteIndex : Int, xStart : Int, direction : Int) : AutomataCell?{
        var scaleIndex = lateNoteIndex + direction
        scaleIndex = Math.min(scaleNotation.size - 1, Math.max(0, scaleIndex))
        val chromaticIndex = scaleNotation[scaleIndex]
        val cells = queryHelper.queryProgression(xStart, listOf(chromaticIndex))
        if(cells!=null){
            return cells.first()
        }
        return null
    }

    private fun getTimeSlice(bitmap : Bitmap, x : Int, yStart : Int, yEnd:Int, output : BooleanArray){
        var y = yStart
        while(y<yEnd){
            output[y-yStart] = bitmap.getValue(x,y)
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
                        return AutomataCell(x,i+yStart,1,false)
                    }
                    i++
                }
            }else{

                var i = lastY
                while(i>-1){
                    if(availableNotes[i]){
                        return AutomataCell(x,i+yStart,1,false)
                    }
                    i--
                }
            }
            x++
        }
        return AutomataCell(xStart,yStart,1,true)
    }
}