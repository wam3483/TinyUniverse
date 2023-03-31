package com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream.waveform

import com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream.ConstantStream
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream.FloatInputStream

class SquareWaveform(frequency : FloatInputStream, sampleRate : Int, var pulseWidth : FloatInputStream = ConstantStream(.5f)) :
        AbstractWaveformStream(frequency, sampleRate) {

    //    override fun read(timeInSeconds: Float): Float {
//        val freq = frequency.read(timeInSeconds)
//        val period = 1 / freq
//
//        val pulseWidthVal = pulseWidth.read(timeInSeconds)
//        val halfPeriod = period * pulseWidthVal
//        val phase = (timeInSeconds) % period
//        if(phase < halfPeriod){
//            return 1f
//        }
//        return -1f
//    }
    override fun mapPhaseToAmplitude(phase: Float): Float {
        val normalizedPhase = (phase % onePeriod) / onePeriod
        val highEdge = pulseWidth.read(phase)
        if(normalizedPhase<=highEdge){
            return 1f
        }else{
            return -1f
        }
    }
}