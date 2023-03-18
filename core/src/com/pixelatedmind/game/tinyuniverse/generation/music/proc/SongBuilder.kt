package com.pixelatedmind.game.tinyuniverse.generation.music.proc

import com.pixelatedmind.game.tinyuniverse.datastructure.Bitmap
import com.pixelatedmind.game.tinyuniverse.generation.cellautomata.CellularAutomataBuilder
import com.pixelatedmind.game.tinyuniverse.datastructure.BitmapImpl
import com.pixelatedmind.game.tinyuniverse.generation.music.model.Scale
import com.pixelatedmind.game.tinyuniverse.generation.music.song.model.InstrumentStreamModel
import com.pixelatedmind.game.tinyuniverse.generation.music.song.model.NoteTone
import com.pixelatedmind.game.tinyuniverse.generation.music.song.model.SongModel

class SongBuilder {

    val rolePatternFinders = mutableListOf<BandRoleModel>()

    private val patternCreator = CellularAutomataBuilder()

    private var scale = AutomataScaleMapperImpl(3, patternCreator.getHeight(), NoteTone.C, Scale.Major)

    private var automataNoteMapper = PatternNoteSequenceMapper(scale)

    var beatsPerMinute = 100
    var timeSignatureBottomNumber = 4

    fun setPattern(height : Int, ruleNumber : Int, initialState : List<Boolean>){
        patternCreator.setHeight(height)
        patternCreator.setRule(ruleNumber)
        patternCreator.setInitialState(initialState)
        setBottomOctave(scale.lowestOctave)
    }

    fun setBottomOctave(lowestOctave : Int){
        scale = AutomataScaleMapperImpl(lowestOctave, patternCreator.getHeight(), NoteTone.C, Scale.Chromatic)
        automataNoteMapper = PatternNoteSequenceMapper(scale)
    }

    fun newSong() : SongResult{
        val pattern = patternCreator.build()
        val bitmap = BitmapImpl(pattern, true)
        bitmap.rotate90(true)

        val roleAutomataPatterns = mutableListOf<Pair<BandRoleModel, List<AutomataCell>>>()
        val instrumentStreamModels = rolePatternFinders.map{
            val cellPattern = it.patternFinder.find(bitmap)
            val notes = automataNoteMapper.map(cellPattern)

            roleAutomataPatterns.add(Pair(it, cellPattern))
            InstrumentStreamModel(it.instrumentName, it.gain, notes)
        }
        val songModel = SongModel("",beatsPerMinute,timeSignatureBottomNumber,instrumentStreamModels)
        return SongResult(songModel, bitmap, roleAutomataPatterns)
    }
}
class SongResult(val songModel : SongModel, val fullPattern : Bitmap, val voicePatterns : List<Pair<BandRoleModel, List<AutomataCell>>>){

}