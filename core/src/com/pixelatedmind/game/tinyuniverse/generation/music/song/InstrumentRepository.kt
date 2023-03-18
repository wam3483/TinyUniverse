package com.pixelatedmind.game.tinyuniverse.generation.music.song

import com.pixelatedmind.game.tinyuniverse.generation.music.synth.envelope.EnvelopeFactory

interface InstrumentRepository {
    fun getInstrument(name : String) : EnvelopeFactory
}