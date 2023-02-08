package com.pixelatedmind.game.tinyuniverse

import com.badlogic.gdx.audio.AudioDevice
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.SourceDataLine
import kotlin.experimental.and

class AsyncAudioDevice(samplingRate : Float, sampleSizeInBits : Int, channels : Int) : AudioDevice{
    var sourceDataLine : SourceDataLine
    val audioFormat : AudioFormat
    init{
        audioFormat = AudioFormat(samplingRate, sampleSizeInBits, channels, true, false)
        sourceDataLine = AudioSystem.getSourceDataLine(audioFormat)
    }
    override fun dispose() {
        sourceDataLine.drain()
        sourceDataLine.stop()
    }

    override fun isMono(): Boolean {
        return audioFormat.channels == 1
    }

    override fun writeSamples(samples: ShortArray?, offset: Int, numSamples: Int) {
        sourceDataLine.open()
        sourceDataLine.start()

        val end: Int = Math.min(offset + numSamples, samples!!.size)
        var i = offset
        val byteBuffer = ByteBuffer.allocate(numSamples*2)
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN)
        while (i < end) {
            val sample = samples!![i]
            byteBuffer.putShort(sample)
            i++
        }
        val bytes = ByteArray(numSamples * 2)
        byteBuffer.get(bytes)
        sourceDataLine.write(bytes, offset, numSamples)
    }

    override fun writeSamples(samples: FloatArray?, offset: Int, numSamples: Int) {
        val shortAry = samples!!.map{(it * Short.MAX_VALUE).toShort()}.toShortArray()
        writeSamples(shortAry, 0, numSamples)
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