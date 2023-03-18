package com.pixelatedmind.game.tinyuniverse.generation.music.synth.envelope

import com.pixelatedmind.game.tinyuniverse.generation.music.synth.envelope.Envelope

interface EnvelopeFactory {
    fun newEnvelope(frequency : Float) : Envelope
}