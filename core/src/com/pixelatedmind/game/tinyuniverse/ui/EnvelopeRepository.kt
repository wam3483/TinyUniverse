package com.pixelatedmind.game.tinyuniverse.ui

class EnvelopeRepository {

    private val models = mutableListOf<PiecewiseModel>()
    private val deleteListeners = mutableListOf<(PiecewiseModel)->Unit>()
    private val addListeners = mutableListOf<(PiecewiseModel)->Unit>()

    fun addModelDeletedListsener(listener : (PiecewiseModel)->Unit){
        deleteListeners.add(listener)
    }
    fun addModelAddedListsener(listener : (PiecewiseModel)->Unit){
        addListeners.add(listener)
    }
    private fun fireModelDeletedEvent(model : PiecewiseModel){
        deleteListeners.forEach{
            it.invoke(model)
        }
    }
    private fun fireModelAddedEvent(model : PiecewiseModel){
        addListeners.forEach{
            it.invoke(model)
        }
    }
    fun getAllModels() : List<PiecewiseModel>{
        return models
    }

    fun isModelIdAvailable(id : String): Boolean{
        return models.none{it.name == id}
    }
    fun add(model : PiecewiseModel){
        if(!isModelIdAvailable(model.name)){
            throw IllegalArgumentException("Model with id [${model.name}] already present. Id taken.")
        }
        models.add(model)
        fireModelAddedEvent(model)
    }

    fun remove(model : PiecewiseModel){
        if(models.remove(model)){
            fireModelDeletedEvent(model)
        }
    }

    fun getModel(id : String){

    }
}