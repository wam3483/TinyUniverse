package com.pixelatedmind.game.tinyuniverse.ui.patch

import com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream.FloatInputStream
import com.pixelatedmind.game.tinyuniverse.ui.PiecewiseModel

class PiecewiseStream(val model : PiecewiseModel) : FloatInputStream {

    fun read(timeInSeconds: Float, normalized : Boolean = false): Float {
        if(model.repeatForever){
            val output = model.evaluate(timeInSeconds % model.durationSecs, normalized)
            return output
        }else{
            val timesRepeated = (timeInSeconds / model.durationSecs).toInt()
            if(timesRepeated > model.timesToRepeat){
                return model.evaluate(model.durationSecs, normalized)
            }else{
                return model.evaluate(timeInSeconds % model.durationSecs, normalized)
            }
        }
    }

    override fun read(timeInSeconds: Float): Float {
        return read(timeInSeconds, false)
    }
}