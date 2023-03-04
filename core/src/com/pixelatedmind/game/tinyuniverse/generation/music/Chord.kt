package com.pixelatedmind.game.tinyuniverse.generation.music

import com.pixelatedmind.game.tinyuniverse.extensions.bits.getBit

enum class Chord(vararg val notation: Int) {
    Major(0,4,7),
    Major6th(0,4,7,9),
    Major7thSharp11th(0,4,8,11,6),
    Major7th(0, 4,7,11),
    Major9th(0,4,7,11,2),
    Major11th(0,4,7,11,2,5),
    Major13th(0,4,7,9,2),
    MinorMajor7th(0,3,7,11),
    Minor(0,3,7),
    Minor6th(0, 3, 7, 9),
    Minor7th(0, 3, 11, 7),
    Minor9th(0,3,7,10,2),
    Minor11th(0,3,7,10,2),
    Minor13th(0,3,7,10,2,5,9),
    Power(0,7),
    Suspended(0,5,7),
    DominantParallel(0,3,7),
    Dominant(0, 4, 7),
    DominantSeventh(0,4,7,10),
    Neapolitan(1,5,8)
}