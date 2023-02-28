package com.pixelatedmind.game.tinyuniverse.generation.music

import com.pixelatedmind.game.tinyuniverse.generation.music.filter.Envelope

interface EnvelopeFactory {
    fun newEnvelope(frequency : Float) : Envelope
}