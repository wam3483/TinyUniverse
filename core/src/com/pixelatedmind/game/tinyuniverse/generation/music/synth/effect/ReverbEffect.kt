package com.pixelatedmind.game.tinyuniverse.generation.music.synth.effect

class ReverbEffect(delaySecs: Double, private val sampleRate : Double, private var decay: Double) {
    private var delayBuffer: DoubleArray
    private var delayIndex: Int

    fun setDecay(decay : Double) {
        this.decay = decay
    }

    fun setDelay(delaySecs : Double) {
        val delayInSamples = (delaySecs * sampleRate).toInt()
        delayBuffer = DoubleArray(delayInSamples)
        delayIndex = 0
    }

    init {
        val delayInSamples = (delaySecs * sampleRate).toInt()
        delayBuffer = DoubleArray(delayInSamples)
        delayIndex = 0
    }

    fun processSample(input: Double) : Double {
        val output = input + delayBuffer[delayIndex]
        delayBuffer[delayIndex] = output * decay
        delayIndex = (delayIndex + 1) % delayBuffer.size
        return output
    }
}