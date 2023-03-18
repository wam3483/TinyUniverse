package com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream.inputs

import com.badlogic.gdx.math.Interpolation
import com.pixelatedmind.game.tinyuniverse.generation.music.Notes
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream.FloatInputStream

///Times to repeat indicates how many times to repeat animation.
///Set time to repeat to -1 to repeat infinitely.
class DetunedNoteValue(frequency : Float,
                       detunePercent : Float,
                       animationSecs : Float,
                       interpolation : Interpolation = Interpolation.linear,
                       timesToRepeat : Int = -1,
                       pingPong : Boolean = false,
                       note : Notes) : FloatInputStream {

    val interpolatedNormalValue : AnimatedValueImpl

    init{
        val noteBandwidth = note.getNoteBandwidth(frequency)
        val detunedNote = frequency + noteBandwidth * detunePercent
        val inTuneNote = frequency
        interpolatedNormalValue = AnimatedValueImpl(animationSecs, detunedNote,
                inTuneNote, interpolation,
                timesToRepeat, pingPong)
    }

    override fun read(timeElapsed: Float): Float {
        val result = interpolatedNormalValue.read(timeElapsed)
        return result
    }
}