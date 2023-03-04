package com.pixelatedmind.game.tinyuniverse.generation.music

enum class Scale(vararg val notation : Int) {
    Major(2,2,1,2,2,2,1),// (classic, happy)
    Chromatic(1,1,1,1,1,1,1,1,1,1,1),// (random, atonal: all twelve notes)
    HarmonicMinor(2,1,2,2,1,3,1),// (haunting, creepy)
    MinorPentatonic(3,2,2,3,2), // (blues, rock)
    NaturalMinor(2,1,2,2,1,2,2), // (scary, epic)
    MelodicMinorUp(2,1,2,2,2,2,1), // (wistful, mysterious)
    MelodicMinorDown(2,2,1,2,2,1,2),  // (sombre, soulful)
    Dorian(2,1,2,2,2,1,2), // (cool, jazzy)
    Mixolydian(2,2,1,2,2,1,2), // (progressive, complex)
    AhavaRaba(1,3,1,2,1,2,2), // (exotic, unfamiliar)
    MajorPentatonic(2,2,3,2,3), // (country, gleeful)
    Diatonic(2,2,2,2,2,2) // (bizarre, symmetrical)
}