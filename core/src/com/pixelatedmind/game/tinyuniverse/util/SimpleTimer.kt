package com.pixelatedmind.game.tinyuniverse.util

class SimpleTimer(var duration : Float, var callback: () -> Unit) {
    private var elapsedSecs = 0f
    var enabled = true
    fun reset(){
        elapsedSecs = 0f
    }

    fun elapsedSecs() : Float{
        return elapsedSecs
    }

    fun update(delta : Float){
        if(enabled) {
            elapsedSecs += delta
            if (elapsedSecs >= duration) {
                callback.invoke()
                elapsedSecs %= duration
            }
        }
    }
}