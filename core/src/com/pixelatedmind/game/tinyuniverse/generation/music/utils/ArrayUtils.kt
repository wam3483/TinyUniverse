package com.pixelatedmind.game.tinyuniverse.generation.music.utils
import com.badlogic.gdx.utils.Array

class ArrayUtils {
    companion object{
        fun <T> toArray(list : List<T>) : Array<T>{
            val ary = Array<T>(list.size)
            list.forEach{
                ary.add(it)
            }
            return ary
        }
    }
}