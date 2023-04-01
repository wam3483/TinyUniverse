package com.pixelatedmind.game.tinyuniverse.generation.music.synth.envelope

class MultiplexEnvelope(private val envelopes : List<Envelope>) : AbstractEnvelope() {
    override fun release() {
        envelopes.forEach{it.release()}
        fireReleaseEvent()
    }

    override fun isComplete(): Boolean {
        return envelopes.all{it.isComplete()}
    }

    override fun read(timeInSeconds: Float): Float {
        var sum = 0f
        envelopes.forEach{
            sum += it.read(timeInSeconds)
        }
        val result = sum / envelopes.size
        return result
    }
}