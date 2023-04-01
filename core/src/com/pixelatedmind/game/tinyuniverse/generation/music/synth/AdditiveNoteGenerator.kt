package com.pixelatedmind.game.tinyuniverse.generation.music.synth

import com.pixelatedmind.game.tinyuniverse.generation.music.synth.envelope.EnvelopeImpl
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.envelope.Envelope
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream.FloatInputStream
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.effect.MultiplexGainEffect
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.envelope.EnvelopeFactory
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream.SilentInputStream
import java.util.*

class AdditiveNoteGenerator(val inputStreamFactory : EnvelopeFactory) : NoteGenerator, FloatInputStream {
    private val streams : MutableMap<String, Envelope> = mutableMapOf()
    private val additiveWaveform = MultiplexGainEffect()

    override fun startNote(frequency: Float): String {
        val id = UUID.randomUUID().toString()
        synchronized(streams) {
            streams[id] = inputStreamFactory.newEnvelope(frequency)
        }
        return id
    }

    override fun stopNote(id: String) {
        synchronized(streams) {
            if(streams.containsKey(id)) {
                streams[id]!!.release()
            }
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
            additiveWaveform.waveForms = waveforms
        }
        return additiveWaveform.read(timeInSeconds)
    }

}