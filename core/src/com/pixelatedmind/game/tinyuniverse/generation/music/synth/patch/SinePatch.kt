package com.pixelatedmind.game.tinyuniverse.generation.music.synth.patch

import com.pixelatedmind.game.tinyuniverse.generation.music.synth.envelope.EnvelopeFactory
import com.pixelatedmind.game.tinyuniverse.generation.music.Notes
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.envelope.AmpEnvelopeStream
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.envelope.Envelope
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.envelope.EnvelopeImpl
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream.ConstantStream
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream.waveform.SineWaveform

class SinePatch : EnvelopeFactory {
    val notes = Notes()
    override fun newEnvelope(frequency: Float): Envelope {
        val downOctave = notes.getOctave(frequency)-1
        val note = notes.getNoteFromFrequency(frequency)
        val newFrequency = notes.getNote(note, downOctave)

        val stream = SineWaveform(ConstantStream(newFrequency), 44100)
        val envelope = EnvelopeImpl.buildEnvelope(stream)
        return AmpEnvelopeStream(envelope, stream)
    }
}