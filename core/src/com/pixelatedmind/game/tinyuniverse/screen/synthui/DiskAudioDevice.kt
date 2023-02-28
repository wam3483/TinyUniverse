package com.pixelatedmind.game.tinyuniverse.screen.synthui

import com.badlogic.gdx.audio.AudioDevice
import java.io.BufferedOutputStream
import java.io.BufferedWriter
import java.io.File

class DiskAudioDevice(val file : File) : AudioDevice {
    init{
    }
    override fun dispose() {
        TODO("Not yet implemented")
    }

    override fun isMono(): Boolean {
        TODO("Not yet implemented")
    }

    override fun writeSamples(samples: ShortArray?, offset: Int, numSamples: Int) {
        TODO("Not yet implemented")
    }

    override fun writeSamples(samples: FloatArray?, offset: Int, numSamples: Int) {

        TODO("Not yet implemented")
    }

    override fun getLatency(): Int {
        TODO("Not yet implemented")
    }

    override fun setVolume(volume: Float) {
        TODO("Not yet implemented")
    }

    override fun pause() {
        TODO("Not yet implemented")
    }

    override fun resume() {
        TODO("Not yet implemented")
    }
}