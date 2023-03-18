package com.pixelatedmind.game.tinyuniverse.generation.music

import com.pixelatedmind.game.tinyuniverse.generation.music.model.Chord
import com.pixelatedmind.game.tinyuniverse.generation.music.model.Note
import com.pixelatedmind.game.tinyuniverse.generation.music.model.Scale
import java.lang.IndexOutOfBoundsException

class Notes {
//    MIDI                   MIDI                   MIDI
//    Note     Frequency      Note   Frequency       Note   Frequency
//    C1  0    8.1757989156    12    16.3515978313    24    32.7031956626
//    Db  1    8.6619572180    13    17.3239144361    25    34.6478288721
//    D   2    9.1770239974    14    18.3540479948    26    36.7080959897
//    Eb  3    9.7227182413    15    19.4454364826    27    38.8908729653
//    E   4   10.3008611535    16    20.6017223071    28    41.2034446141
//    F   5   10.9133822323    17    21.8267644646    29    43.6535289291
//    Gb  6   11.5623257097    18    23.1246514195    30    46.2493028390
//    G   7   12.2498573744    19    24.4997147489    31    48.9994294977
//    Ab  8   12.9782717994    20    25.9565435987    32    51.9130871975
//    A   9   13.7500000000    21    27.5000000000    33    55.0000000000
//    Bb  10  14.5676175474    22    29.1352350949    34    58.2704701898
//    B   11  15.4338531643    23    30.8677063285    35    61.7354126570
//
//    C4  36  65.4063913251    48   130.8127826503    60   261.6255653006
//    Db  37  69.2956577442    49   138.5913154884    61   277.1826309769
//    D   38  73.4161919794    50   146.8323839587    62   293.6647679174
//    Eb  39  77.7817459305    51   155.5634918610    63   311.1269837221
//    E   40  82.4068892282    52   164.8137784564    64   329.6275569129
//    F   41  87.3070578583    53   174.6141157165    65   349.2282314330
//    Gb  42  92.4986056779    54   184.9972113558    66   369.9944227116
//    G   43  97.9988589954    55   195.9977179909    67   391.9954359817
//    Ab  44  103.8261743950   56   207.6523487900    68   415.3046975799
//    A   45  110.0000000000   57   220.0000000000    69   440.0000000000
//    Bb  46  116.5409403795   58   233.0818807590    70   466.1637615181
//    B   47  123.4708253140   59   246.9416506281    71   493.8833012561
//
//    C7  72  523.2511306012   84  1046.5022612024    96  2093.0045224048
//    Db  73  554.3652619537   85  1108.7305239075    97  2217.4610478150
//    D   74  587.3295358348   86  1174.6590716696    98  2349.3181433393
//    Eb  75  622.2539674442   87  1244.5079348883    99  2489.0158697766
//    E   76  659.2551138257   88  1318.5102276515   100  2637.0204553030
//    F   77  698.4564628660   89  1396.9129257320   101  2793.8258514640
//    Gb  78  739.9888454233   90  1479.9776908465   102  2959.9553816931
//    G   79  783.9908719635   91  1567.9817439270   103  3135.9634878540
//    Ab  80  830.6093951599   92  1661.2187903198   104  3322.4375806396
//    A   81  880.0000000000   93  1760.0000000000   105  3520.0000000000
//    Bb  82  932.3275230362   94  1864.6550460724   106  3729.3100921447
//    B   83  987.7666025122   95  1975.5332050245   107  3951.0664100490
//
//    C10 108 4186.0090448096  120  8372.0180896192
//    Db  109 4434.9220956300  121  8869.8441912599
//    D   110 4698.6362866785  122  9397.2725733570
//    Eb  111 4978.0317395533  123  9956.0634791066
//    E   112 5274.0409106059  124 10548.0818212118
//    F   113 5587.6517029281  125 11175.3034058561
//    Gb  114 5919.9107633862  126 11839.8215267723
//    G   115 6271.9269757080  127 12543.8539514160
//    Ab  116 6644.8751612791
//    A   117 7040.0000000000
//    Bb  118 7458.6201842894
//    B   119 7902.1328200980
//    NOTES: Middle C is note #60. Frequency is in Hertz.

    // code to calculate an array with all of the above frequencies (ie, so that midi[0], which is midi note #0, is assigned the value of 8.1757989156).
    // Tuning is based upon A=440.


    val midi = FloatArray(127)

    fun getSemitoneIndex(note : Note) : Int{
        return when(note){
            Note.C->0
            Note.CSharp->1
            Note.D->2
            Note.DSharp->3
            Note.E->4
            Note.F->5
            Note.FSharp->6
            Note.G->7
            Note.GSharp->8
            Note.A->9
            Note.ASharp->10
            Note.B->11
            else -> -1
        }
    }


    fun getNoteFromFrequency(freq : Float) : Note {
        val tableIndex = getMidiTableIndexFor(freq)
        return getNoteFromSemitones(tableIndex)
    }

    fun getNoteFromSemitones(semitones : Int) : Note {
        val index = semitones % 12
        return when(index){
            0 -> Note.C
            1-> Note.CSharp
            2-> Note.D
            3-> Note.DSharp
            4-> Note.E
            5-> Note.F
            6-> Note.FSharp
            7-> Note.G
            8-> Note.GSharp
            9-> Note.A
            10-> Note.ASharp
            11-> Note.B
            else -> throw IllegalArgumentException("should never reach this")
        }
    }



    fun getOctaveIndex(octave: Int) : Int{
        return 12 * octave
    }

    fun increaseFrequencyBySemitones(freq : Float, semitoneOffset : Int) : Float{
        var index = getMidiTableIndexFor(freq) + semitoneOffset
        if(index >= midi.size){
            return midi[midi.size-1]
        }
        if(index<0){
            return midi[0]
        }
        return midi[index]
    }

    fun getNoteBandwidth(frequency : Float) : Float{
        val index = this.getMidiTableIndexFor(frequency)
        val semiToneUp = index + 1
        if(semiToneUp >= midi.size){
            throw IndexOutOfBoundsException("[${frequency}] Hz is out of range of midi table.")
        }
        return midi[semiToneUp] - midi[index]
    }

    fun getOctaveBandwidth(octave : Int) : Float {
        val bottomOctaveMidiIndex = octave * 12
        val topOctaveMidiIndex = bottomOctaveMidiIndex + 12

        val bottomOctaveFreq = midi[bottomOctaveMidiIndex]
        val topOctaveFreq =
                if(topOctaveMidiIndex >= midi.size){
                    bottomOctaveFreq * 2
                }else{
                    midi[topOctaveMidiIndex]
                }
        return topOctaveFreq - bottomOctaveFreq
    }

    fun getOctave(freq : Float) : Int{
        val midiIndex = getMidiTableIndexFor(freq)
        return midiIndex / 12
    }

    fun getMidiTableIndexFor(freq : Float) : Int{
        val index = midi.binarySearch(freq)
        if(index > midi.size-1){
            return midi.size-1
        }else if(index < 0){
            return (-index) - 1
        }
        return index
    }

    fun getOctave(octave : Int) : List<Float> {
        val octaveIndex = getOctaveIndex(octave)
        val list = mutableListOf<Float>()
        var index = octaveIndex
        while(list.size < 12){
            list.add(midi[index])
            ++index
        }
        return list
    }

    private fun getDiatonicChords(scale : Scale, chord : Chord) : List<DiatonicChord>{
        val scaleIntervals = scale.notation
        val chordNotation = chord.notation

        val results = mutableListOf<DiatonicChord>()
        scaleIntervals.forEachIndexed { scaleIndex, rootNote ->
            var i = scaleIndex
            val end = scaleIndex + scaleIntervals.size
            var chordIndex = 0
            var includeChord = true
            while(i < end && chordIndex < chordNotation.size && includeChord){
                val scaleOctaveOffset = i / scaleIntervals.size * 12
                val scaleInterval = scaleIntervals[i % scaleIntervals.size] + scaleOctaveOffset
                val chordInterval = chordNotation[chordIndex] + rootNote
                if(chordInterval == scaleInterval) {
                    chordIndex++
                } else if(chordInterval < scaleInterval){
                    includeChord = false
                }
                i++
            }
            if(includeChord) {
                val offsetChord = chordNotation.map { scaleIntervals[scaleIndex] + it }
                val rootNote = getNoteFromSemitones(offsetChord[0])
                val result = DiatonicChord(scale, rootNote, chord, offsetChord)
                results.add(result)
            }
        }
        return results
    }

    fun getDiatonicChords(scale : Scale) : List<DiatonicChord>{
        val result = mutableListOf<DiatonicChord>()
        Chord.values().forEach{ chord->
            val diatonicChords = getDiatonicChords(scale, chord)
            result.addAll(diatonicChords)
        }
        return result
    }

    fun getNoteMidiIndex(note : Note, octave : Int) : Int{
        val octaveIndex = getOctaveIndex(octave)
        val semitoneIndex = getSemitoneIndex(note)
        val midiIndex = octaveIndex + semitoneIndex
        return midiIndex
    }

    fun getNote(note : Note, octave : Int) : Float{
        val noteIndex = getNoteMidiIndex(note,octave)
        if(noteIndex<0 || noteIndex >= midi.size){
            throw ArrayIndexOutOfBoundsException("[$noteIndex] out of bounds for length ${midi.size}. octave=[$octave], note=[$note]")
        }
        return midi[noteIndex]
    }

    init{
        val a = 440
        midi.forEachIndexed{index,value->
            val exponent = (index-9)/12f
            midi[index] = ((a / 32f) * Math.pow(2.0, exponent.toDouble()).toFloat())
        }
    }
}

class DiatonicChord(val scale : Scale, val rootNote : Note, val chord : Chord, val semitonesFromScaleRoot : List<Int>){
}