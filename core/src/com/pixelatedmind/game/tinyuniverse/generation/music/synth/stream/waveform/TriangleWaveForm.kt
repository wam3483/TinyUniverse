package com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream.waveform

import com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream.FloatInputStream
import kotlin.math.PI
import kotlin.math.abs

class TriangleWaveForm(frequency : FloatInputStream, sampleRate : Int): AbstractWaveformStream(frequency, sampleRate) {
    //    override fun read(timeInSeconds: Float): Float {
//        val period = 1f / frequency.read(timeInSeconds)
//        val halfPeriod = period / 2
//        val triangleFrom0To1 = 1f / halfPeriod * (halfPeriod - abs(timeInSeconds % period-halfPeriod))
//        return (triangleFrom0To1 * 2) - 1
//    }
    override fun mapPhaseToAmplitude(phase: Float): Float {
        val normalizedPhase = phase % onePeriod
        val divisor = normalizedPhase / PI.toFloat()
        if(divisor<=1){
            return divisor * 2 - 1
        }else{
            return (1-(divisor-1)) * 2 - 1
        }
    }
}