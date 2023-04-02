package com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream.waveform

import com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream.ConstantStream
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream.FloatInputStream

class SquareWaveform(frequency : FloatInputStream, sampleRate : Int, var pulseWidth : FloatInputStream = ConstantStream(.5f)) :
        AbstractWaveformStream(frequency, sampleRate) {

    override fun mapPhaseToAmplitude(timeElapsed : Float, phase: Float): Float {
        val normalizedPhase = (phase % onePeriod) / onePeriod
        val highEdge = pulseWidth.read(timeElapsed)
        if(normalizedPhase<=highEdge){
            return 1f
        }else{
            return -1f
        }
    }
}