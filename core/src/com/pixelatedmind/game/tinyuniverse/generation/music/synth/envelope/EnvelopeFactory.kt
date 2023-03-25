package com.pixelatedmind.game.tinyuniverse.generation.music.synth.envelope

interface EnvelopeFactory {
    fun newEnvelope(frequency : Float) : Envelope
}