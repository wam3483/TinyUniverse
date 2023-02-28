package com.pixelatedmind.game.tinyuniverse.generation.music

import com.pixelatedmind.game.tinyuniverse.generation.music.filter.EnvelopeImpl
import com.pixelatedmind.game.tinyuniverse.generation.music.filter.Envelope
import com.pixelatedmind.game.tinyuniverse.generation.music.waveform.WeightedInputStreamDecoratorImpl
import java.util.*

class AdditiveNoteGenerator(val inputStreamFactory : EnvelopeFactory) : NoteGenerator, FloatInputStream {

    private val silentStream = EnvelopeImpl.buildEnvelope(SilentInputStream())
    private val streams : MutableMap<String, Envelope> = mutableMapOf()
    private val additiveWaveform = WeightedInputStreamDecoratorImpl()

    override fun startNote(frequency: Float): String {
        val id = UUID.randomUUID().toString()
        synchronized(streams) {
            streams[id] = inputStreamFactory.newEnvelope(frequency)
        }
        return id
    }

    override fun stopNote(id: String) {
        synchronized(streams) {
            streams[id]!!.release()
        }
    }

    override fun read(timeInSeconds: Float): Float {
        synchronized(streams) {
            val filtersToRemove = streams.values.filter { it.isComplete()}
            if(filtersToRemove.any()) {
                println("${filtersToRemove.size} notes complete and removed from processing")
            }
            streams.values.removeAll(filtersToRemove)
            val waveforms = streams.values.toMutableList()
            if(!waveforms.any())
                waveforms.add(silentStream)
            additiveWaveform.waveForms = waveforms
        }
        return additiveWaveform.read(timeInSeconds)
    }

}