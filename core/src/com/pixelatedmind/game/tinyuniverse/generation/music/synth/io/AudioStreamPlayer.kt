package com.pixelatedmind.game.tinyuniverse.generation.music.synth.io

import com.badlogic.gdx.audio.AudioDevice

class AudioStreamPlayer(val audioDevice : AudioDevice, val samplingRate : Int, val playbackBufferSecs : Float) {
    private var thread : Thread?
    private var running : Boolean
    private var runtime : Double
    private val audioOutputRunnable : Runnable
    private var input : AudioStreamReader?

    private val audioPlaybackListener = mutableListOf<(FloatArray)->Unit>()

    fun addPlaybackListener(listenerFunction : (FloatArray)->Unit){
        audioPlaybackListener.add(listenerFunction)
    }

    fun removePlaybackListener(listenerFunction : (FloatArray)->Unit){
        audioPlaybackListener.remove(listenerFunction)
    }

    init{
        thread = null
        input = null
        runtime = 0.0
        running = false
        audioOutputRunnable = object : Runnable {
            override fun run(){
                val playbackBufferSize = (samplingRate * playbackBufferSecs).toInt()
                val buffer = FloatArray(playbackBufferSize)
                while(running){
                    input!!.read(buffer,0,buffer.size)
                    audioPlaybackListener.forEach{it.invoke(buffer)}
                    audioDevice.writeSamples(buffer,0,buffer.size)
                }
            }
        }
    }
    fun start(inputStream : AudioStreamReader){
        if(!running) {
            thread = Thread(audioOutputRunnable)
            thread!!.isDaemon = true
            thread!!.name = "MusicManager"
            input = inputStream
            running = true
            runtime = 0.0
            thread!!.start()
        }
    }
    fun stop(){
        if(running) {
            running = false
            thread!!.join()
        }
    }
}