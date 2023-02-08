package com.pixelatedmind.game.tinyuniverse.generation.music

import com.pixelatedmind.game.tinyuniverse.generation.music.waveform.AdditiveWaveform
import java.util.*

class AdditiveNoteGeneratorImpl(val inputStreamFactory : FloatInputStreamFactory) : NoteGenerator, FloatInputStream {

    private val streams : MutableMap<String, FloatInputStream>
    private val additiveWaveform = AdditiveWaveform()
    init{
        streams = mutableMapOf()
    }

    override fun startNote(frequency: Float): String {
        val id = UUID.randomUUID().toString()
        streams[id] = inputStreamFactory.newInputStream(frequency)
        return id
    }

    override fun stopNote(id: String) {
        streams.remove(id)
    }

    override fun read(timeInSeconds: Float): Float {
        additiveWaveform.waveForms = streams.values.toMutableList()
        return additiveWaveform.read(timeInSeconds)
    }

}