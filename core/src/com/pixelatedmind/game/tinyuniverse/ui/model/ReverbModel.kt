package com.pixelatedmind.game.tinyuniverse.ui.model

import java.util.*

class ReverbModel(name : String, var delay : Float, var decay : Float, var sampleRate : Int = 44100) : NameableId(UUID.randomUUID().toString(), name) {
    constructor() : this("", 0f, 0f) {
    }
}