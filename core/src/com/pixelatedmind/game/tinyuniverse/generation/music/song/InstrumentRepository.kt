package com.pixelatedmind.game.tinyuniverse.generation.music.song

import com.pixelatedmind.game.tinyuniverse.generation.music.EnvelopeFactory

interface InstrumentRepository {
    fun getInstrument(name : String) : EnvelopeFactory
}