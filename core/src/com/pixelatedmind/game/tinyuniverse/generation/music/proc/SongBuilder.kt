package com.pixelatedmind.game.tinyuniverse.generation.music.proc

import com.pixelatedmind.game.tinyuniverse.datastructure.Bitmap
import com.pixelatedmind.game.tinyuniverse.generation.cellautomata.CellularAutomataBuilder
import com.pixelatedmind.game.tinyuniverse.datastructure.BitmapImpl
import com.pixelatedmind.game.tinyuniverse.generation.music.Scale
import com.pixelatedmind.game.tinyuniverse.generation.music.song.model.InstrumentStreamModel
import com.pixelatedmind.game.tinyuniverse.generation.music.song.model.NoteTone
import com.pixelatedmind.game.tinyuniverse.generation.music.song.model.SongModel

class SongBuilder {

    val rolePatternFinders = mutableMapOf<String, BandRolePatternFinder>()

    private val patternCreator = CellularAutomataBuilder()

    private var scale = AutomataScaleMapperImpl(3, patternCreator.getHeight(), NoteTone.C, Scale.Major)

    private var automataNoteMapper = PatternNoteSequenceMapper(scale)

    var beatsPerMinute = 100
    var timeSignatureBottomNumber = 4

    fun setPattern(height : Int, ruleNumber : Int, initialState : List<Boolean>){
        patternCreator.setHeight(height)
        patternCreator.setRule(ruleNumber)
        patternCreator.setInitialState(initialState)

        setScale(scale.lowestOctave, scale.rootNote, scale.intervals)
    }

    fun setScale(lowestOctave: Int, rootNote : NoteTone, scale : Scale){
        setScale(lowestOctave, rootNote, scale.notation.toList())
    }

    fun setScale(lowestOctave : Int, rootNote : NoteTone, intervals : List<Int>){
        scale = AutomataScaleMapperImpl(lowestOctave, patternCreator.getHeight(),rootNote, intervals)
        automataNoteMapper = PatternNoteSequenceMapper(scale)
    }

    fun newSong() : SongResult{
        val pattern = patternCreator.build()
        val bitmap = BitmapImpl(pattern, true)
        bitmap.rotate90(true)
        val roleAutomataPatterns = rolePatternFinders.map{
            it.key to it.value.find(bitmap)
        }

        val instrumentStreamModels = roleAutomataPatterns.map{
            InstrumentStreamModel(it.first, automataNoteMapper.map(it.second))
        }
        val songModel = SongModel("",beatsPerMinute,timeSignatureBottomNumber,instrumentStreamModels)
        return SongResult(songModel, bitmap, roleAutomataPatterns)
    }
}
class SongResult(val songModel : SongModel, val fullPattern : Bitmap, val voicePatterns : List<Pair<String, List<AutomataCell>>>){

}