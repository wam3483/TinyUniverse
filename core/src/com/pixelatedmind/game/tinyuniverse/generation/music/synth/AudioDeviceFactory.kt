package com.pixelatedmind.game.tinyuniverse.generation.music.synth

import com.badlogic.gdx.audio.AudioDevice

interface AudioDeviceFactory {
    fun newDevice(sampleRate:Int, isMono:Boolean) : AudioDevice
}