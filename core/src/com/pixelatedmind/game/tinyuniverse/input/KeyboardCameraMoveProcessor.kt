package com.pixelatedmind.game.tinyuniverse.input

import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.graphics.OrthographicCamera

class KeyboardCameraMoveProcessor(val camera : OrthographicCamera, val dx:Float=10f,val dy:Float=10f) : InputAdapter() {

    override fun keyTyped(character: Char): Boolean {
        val result = when(character){
            'w'->
                {
                    camera.position.y+= dy
                    return true
                }
            's'->{
                camera.position.y -= dy
                return true
            }
            'a'->{
                camera.position.x -= dx
                return true
            }
            'd'->{
                camera.position.x +=dx
                return true
            }
            else->false
        }
        return result
    }
}