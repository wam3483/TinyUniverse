package com.pixelatedmind.game.tinyuniverse.ui.io

import com.pixelatedmind.game.tinyuniverse.ui.model.NameableId

class PiecewiseModelDTO : NameableId("",""){
    var pieces = mutableListOf<PieceModelDTO>()
    var startY : Float = 0f
    var endY : Float = 1f
    var durationSecs = 1f
    var timesToRepeat = 0
    var repeatForever = false

    //if interactive, model should use the last piecewise as a release function
    var interactive = false
}

class PieceModelDTO{
    var interpolationName : String =""
    var x : Float = 0f
    var y : Float = 0f
}