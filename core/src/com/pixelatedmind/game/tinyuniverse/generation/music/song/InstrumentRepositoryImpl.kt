package com.pixelatedmind.game.tinyuniverse.generation.music.song

import com.pixelatedmind.game.tinyuniverse.generation.music.EnvelopeFactory

class InstrumentRepositoryImpl(val instrumentMap : Map<String, EnvelopeFactory>) : InstrumentRepository {
    override fun getInstrument(name: String): EnvelopeFactory {
        return instrumentMap[name]!!
    }
}