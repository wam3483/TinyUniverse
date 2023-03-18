package com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream.inputs

import com.pixelatedmind.game.tinyuniverse.generation.music.synth.envelope.Envelope
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream.FloatInputStream

class PitchEnvelope(val envelope : Envelope, val startPitch :Float, val endPitch : Float) : FloatInputStream {
    val range = Math.abs(endPitch - startPitch)
    fun release(){
        envelope.release()
    }

    override fun read(timeElapsed: Float): Float {
        val envelopeValue = envelope.read(timeElapsed)
        val offset = range * envelopeValue
        val result = if(startPitch > endPitch){
            startPitch - offset
        }else{
            startPitch + offset
        }
        return result
    }
}