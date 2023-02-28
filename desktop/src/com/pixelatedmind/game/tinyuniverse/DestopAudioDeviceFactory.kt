package com.pixelatedmind.game.tinyuniverse

import com.badlogic.gdx.audio.AudioDevice
import com.pixelatedmind.game.tinyuniverse.generation.music.AudioDeviceFactory

class DestopAudioDeviceFactory : AudioDeviceFactory {
    override fun newDevice(sampleRate: Int, isMono: Boolean): AudioDevice {
        var channels = 1
        if(!isMono){
            channels = 2
        }
        return AsyncAudioDevice(sampleRate.toFloat(), 16, channels)
    }
}