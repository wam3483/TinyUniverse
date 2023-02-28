package com.pixelatedmind.game.tinyuniverse.generation.music

import com.badlogic.gdx.audio.AudioDevice

interface AudioDeviceFactory {
    fun newDevice(sampleRate:Int, isMono:Boolean) : AudioDevice
}