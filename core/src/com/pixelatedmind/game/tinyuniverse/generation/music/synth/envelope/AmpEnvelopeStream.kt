package com.pixelatedmind.game.tinyuniverse.generation.music.synth.envelope

import com.pixelatedmind.game.tinyuniverse.generation.music.synth.envelope.AbstractEnvelope
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.envelope.Envelope
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream.FloatInputStream

class AmpEnvelopeStream(var envelope: Envelope, var inputStream : FloatInputStream) : AbstractEnvelope() {
    override fun release() {
        envelope.release()
        fireReleaseEvent()
    }

    override fun isComplete(): Boolean {
        return envelope.isComplete()
    }

    var epoc : Float? = null
    override fun read(timeInSeconds: Float): Float {
        if(epoc == null){
            epoc = timeInSeconds
        }
        val time = timeInSeconds - epoc!!
        val envelopeValue = envelope.read(time)
        val streamValue = inputStream.read(time)
        return streamValue * envelopeValue
    }
}