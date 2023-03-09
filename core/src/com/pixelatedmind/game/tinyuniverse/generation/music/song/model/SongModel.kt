package com.pixelatedmind.game.tinyuniverse.generation.music.song.model

class SongModel(val name : String,
                val beatsPerMinute : Int,
                val timeSignatureBottomNumber : Int,
                val instrumentStreams : List<InstrumentStreamModel>) {

}