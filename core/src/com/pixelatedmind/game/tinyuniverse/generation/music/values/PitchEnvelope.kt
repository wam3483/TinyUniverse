package com.pixelatedmind.game.tinyuniverse.generation.music.values

import com.pixelatedmind.game.tinyuniverse.generation.music.filter.Envelope

class PitchEnvelope(val envelope : Envelope, val startPitch :Float, val endPitch : Float) : AnimatedValue {
    val range = Math.abs(endPitch - startPitch)
    fun release(){
        envelope.release()
    }

    override fun getValue(timeElapsed: Float): Float {
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