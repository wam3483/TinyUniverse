package com.pixelatedmind.game.tinyuniverse.generation.music.song

class EuclideanRhythmGenerator() {
    fun getBeats(beats : Int, timeSteps : Int, repeatSequence : Int = 0) : List<Boolean>{
        if(beats>timeSteps){
            throw IllegalArgumentException("cannot have more beats than timesteps")
        }
        val offBeats = timeSteps - beats
        val beatSequence = mutableListOf<MutableList<Boolean>>()
        repeat(beats){
            beatSequence.add(mutableListOf(true))
        }
        repeat(offBeats){
            beatSequence.add(mutableListOf(false))
        }
        var num1: Int
        var num2: Int
        if(offBeats>beats){
            num1 = offBeats
            num2 = beats
        }else{
            num1 = beats
            num2 = offBeats
        }
        euclidGCDRhythm(num1, num2, beatSequence)
        val flattenedSequece = beatSequence.flatten().toMutableList()
        if(repeatSequence>0) {
            val origSequence = flattenedSequece.toList()
            repeat(repeatSequence) {
                flattenedSequece.addAll(origSequence)
            }
        }
        return flattenedSequece
    }

    private fun euclidGCDRhythm(m: Int, k:Int, beatSeq : MutableList<MutableList<Boolean>>){
        if(k != 0)
        {
            var i =0
            while(i<k){
                val indexToDelete = beatSeq.size-1
                beatSeq[i].addAll(beatSeq[indexToDelete])
                beatSeq.removeAt(indexToDelete)
                i++
            }
            euclidGCDRhythm(k, m % k, beatSeq)
        }
    }
}