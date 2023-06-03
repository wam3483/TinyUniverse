package com.pixelatedmind.game.tinyuniverse.generation.music.synth.effect

class ReverbEffect(delaySecs: Double, private val sampleRate: Int, private var decay: Double) {
    private var delayBuffer: DoubleArray
    private var delayIndex: Int

    init {
        val delayInSamples = (delaySecs * sampleRate).toInt()
        delayBuffer = DoubleArray(delayInSamples)
        delayIndex = 0
    }

    fun processSample(input: Double) : Double {
        val output = input + decay * delayBuffer[delayIndex]
        delayBuffer[delayIndex] = output
        delayIndex = (delayIndex + 1) % delayBuffer.size
        return output
    }
}