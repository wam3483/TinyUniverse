package com.pixelatedmind.game.tinyuniverse.generation.music.synth.envelope

abstract class AbstractEnvelope : Envelope {
    protected val releaseListeners = mutableListOf<()->Unit>()

    protected fun fireReleaseEvent(){
        releaseListeners.forEach{
            it.invoke()
        }
    }

    override fun removeReleaseListener(listener:()->Unit) : Boolean{
        return releaseListeners.remove(listener)
    }

    override fun addReleaseListener(listener:()->Unit){
        releaseListeners.add(listener)
    }
}