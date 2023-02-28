package com.pixelatedmind.game.tinyuniverse.generation.music.values

import com.pixelatedmind.game.tinyuniverse.generation.music.filter.Envelope

class PitchEnvelope(val envelope : Envelope, val startPitch :Float, val endPitch : Float) : NormalizedValue {
    val range = Math.abs(endPitch - startPitch)
    fun release(){
        envelope.release()
    }

    val samplingFreq = .1
    var lastUpdate = 0f
    override fun getValue(timeElapsed: Float): Float {
        val envelopeValue = envelope.read(timeElapsed)
        val offset = range * envelopeValue
        val result = if(startPitch > endPitch){
            startPitch - offset
        }else{
            startPitch + offset
        }
        val delta = timeElapsed - lastUpdate
        if(delta > samplingFreq) {
            lastUpdate = timeElapsed
            println("Time:"+timeElapsed+"\tpitch env freq: " + result + " start=" + startPitch + " end=" + endPitch + " range=" + range)
        }
        return result
    }
}