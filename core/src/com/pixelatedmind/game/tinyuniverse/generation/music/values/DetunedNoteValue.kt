package com.pixelatedmind.game.tinyuniverse.generation.music.values

import com.badlogic.gdx.math.Interpolation
import com.pixelatedmind.game.tinyuniverse.generation.music.Notes

///Times to repeat indicates how many times to repeat animation.
///Set time to repeat to -1 to repeat infinitely.
class DetunedNoteValue(frequency : Float,
                       detunePercent : Float,
                       animationSecs : Float,
                       interpolation : Interpolation = Interpolation.linear,
                       timesToRepeat : Int = -1,
                       note : Notes) : AnimatedValue {

    val interpolatedNormalValue : AnimatedValueImpl

    init{
        val noteBandwidth = note.getNoteBandwidth(frequency)
        val detunedNote = frequency + noteBandwidth * detunePercent
        val inTuneNote = frequency
        println("detuned: "+detunedNote+" intune="+inTuneNote)
        interpolatedNormalValue = AnimatedValueImpl(animationSecs, detunedNote, inTuneNote, interpolation, timesToRepeat, true)
    }

    override fun getValue(timeElapsed: Float): Float {
        val result = interpolatedNormalValue.getValue(timeElapsed)
//        println("time="+timeElapsed+" : detuned note ${result}Hz")
        return result
    }
}