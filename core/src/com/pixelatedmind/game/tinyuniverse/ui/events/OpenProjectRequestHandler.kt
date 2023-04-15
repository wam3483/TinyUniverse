package com.pixelatedmind.game.tinyuniverse.ui.events

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.Array
import com.kotcrab.vis.ui.widget.file.FileChooser
import com.kotcrab.vis.ui.widget.file.FileChooserListener
import com.pixelatedmind.game.tinyuniverse.ui.io.LowPassModelDTO
import com.pixelatedmind.game.tinyuniverse.ui.io.RuntimeModelContainer
import com.pixelatedmind.game.tinyuniverse.ui.io.SynthPatchSerializer
import com.pixelatedmind.game.tinyuniverse.ui.model.LowPassModel
import com.pixelatedmind.game.tinyuniverse.util.EventBus
import java.rmi.UnexpectedException

class OpenProjectRequestHandler(val stage : Stage, val eventbus : EventBus) {
    val projectSerializer = SynthPatchSerializer()

    fun onOpenProjectRequest(evt : OpenProjectRequest){
        val fileChooser = FileChooser("Open File", FileChooser.Mode.OPEN)
        fileChooser.setDirectory(Gdx.files.externalStoragePath)
        fileChooser.isMultiSelectionEnabled = false
        fileChooser.closeOnEscape()

        fileChooser.setListener(object : FileChooserListener {
            override fun selected(files: Array<FileHandle>?) {
                if(files != null && files.any()) {
                    val modelJson = files.first().readString()
                    val runtimeModel = projectSerializer.deserialize(modelJson)

                    postEventsFromProject(runtimeModel)
                }
            }

            override fun canceled() {
            }
        })

        stage.addActor(fileChooser.fadeIn())
    }

    private fun postEffectsFromProject(model : RuntimeModelContainer){
        model.effects.forEach{effect->
            if(effect is LowPassModel){
                eventbus.post(CreateLowPassEffectRequest(effect))
            }else{
                throw UnexpectedException("Cannot handle loading this type of effect")
            }
        }
    }

    private fun postEnvelopesFromProject(model : RuntimeModelContainer){
        model.envelopes.forEach{
            val request = CreateEnvelopeRequest(it)
            eventbus.post(request)
        }
    }

    /***
     * Load envelopes first as they have no dependencies on other project objects.
     * Then load effects as they don't depend on oscillators but may depend on envelops.
     * Then load oscillators as they depend on both effects & envelopes
     */
    private fun postEventsFromProject(model : RuntimeModelContainer){
        eventbus.post(ClearProjectRequest())

        postEnvelopesFromProject(model)
        postEffectsFromProject(model)
        model.oscillators.forEach{
            eventbus.post(CreateOscillatorRequest(it))
            val effects = it.effectIds.toList()
            it.effectIds.clear()
            effects.forEach {effectRef->
                eventbus.post(AddEffectToOscillatorRequest(it, effectRef))
            }
        }
    }
}