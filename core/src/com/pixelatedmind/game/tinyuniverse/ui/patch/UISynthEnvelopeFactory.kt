package com.pixelatedmind.game.tinyuniverse.ui.patch

import com.pixelatedmind.game.tinyuniverse.generation.music.synth.envelope.Envelope
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.envelope.EnvelopeFactory

class UISynthEnvelopeFactory : EnvelopeFactory {
    override fun newEnvelope(frequency: Float): Envelope {
        throw NotImplementedError("not impl")
    }
}