package com.pixelatedmind.game.tinyuniverse.generation.music.utils

class MusicUtils {
    companion object{
        val maxDb = 12.0
        fun normDb(decibels : Float) : Float{
            val sign = Math.signum(decibels)
            if(decibels == 0f)
                return 0f
            return sign * Math.pow(10.0, decibels.toDouble() / 20.0).toFloat() / Math.pow(10.0, maxDb / 20.0).toFloat()
        }
    }
}