package com.pixelatedmind.game.tinyuniverse.generation.music.synth

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.AudioDevice

class CoreAudioDeviceFactory : AudioDeviceFactory {
    override fun newDevice(sampleRate: Int, isMono: Boolean): AudioDevice {
        val audio = Gdx.audio
        return audio.newAudioDevice(sampleRate, isMono)
    }
}