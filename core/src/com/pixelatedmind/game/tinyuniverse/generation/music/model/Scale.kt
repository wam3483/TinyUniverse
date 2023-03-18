package com.pixelatedmind.game.tinyuniverse.generation.music.model

enum class Scale(vararg val notation : Int) {
    Major(0, 2, 4, 5, 7, 9, 11),// (classic, happy)
    Chromatic(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11),// (random, atonal: all twelve notes)
    //2,1,2,2,1,3,1
    HarmonicMinor(0, 2, 3, 5, 7, 8, 11),// (haunting, creepy)
    //3,2,2,3,2
    MinorPentatonic(0, 3, 5, 7, 10), // (blues, rock)
    //2,1,2,2,1,2,2
    NaturalMinor(0,2,3,5,7,8,10), // (scary, epic)
    //2,1,2,2,2,2,1
    MelodicMinorUp(0, 2, 3, 5, 7, 9, 11), // (wistful, mysterious)
    //2,2,1,2,2,1,2
    MelodicMinorDown(0, 2, 4, 5, 7, 9, 10),  // (sombre, soulful)
    //2,1,2,2,2,1,2
    Dorian(0, 2, 3, 5, 7, 9, 10), // (cool, jazzy)
    //2,2,1,2,2,1,2
    Mixolydian(0, 2, 4, 5, 7, 9, 10), // (progressive, complex)
    //1,3,1,2,1,2,2
    AhavaRaba(0, 1, 4, 5, 7, 8, 10), // (exotic, unfamiliar)
    //2,2,3,2,3
    MajorPentatonic(0, 2, 4, 7, 9), // (country, gleeful)
    //2,2,2,2,2,2
    Diatonic(0,2,4,6,8,10) // (bizarre, symmetrical)
}