package com.pixelatedmind.game.tinyuniverse.ui.events

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.Array
import com.kotcrab.vis.ui.util.dialog.Dialogs
import com.kotcrab.vis.ui.widget.file.FileChooser
import com.kotcrab.vis.ui.widget.file.FileChooserListener
import com.pixelatedmind.game.tinyuniverse.ui.EffectRepository
import com.pixelatedmind.game.tinyuniverse.ui.EnvelopeRepository
import com.pixelatedmind.game.tinyuniverse.ui.OscillatorRepository
import com.pixelatedmind.game.tinyuniverse.ui.io.SynthPatchSerializer


class SaveProjectHandler(val stage : Stage, val envelopeRepository : EnvelopeRepository, val effectRepository: EffectRepository, val oscillatorRepository: OscillatorRepository) {
    val projectSerializer = SynthPatchSerializer()
    fun onSaveProjectEvent(evt : SaveProjectRequest){
        val fileChooser = FileChooser("Save File", FileChooser.Mode.SAVE)
        fileChooser.setDirectory(Gdx.files.externalStoragePath)
        fileChooser.isMultiSelectionEnabled = false
        fileChooser.closeOnEscape()

        fileChooser.setListener(object : FileChooserListener {
            override fun selected(files: Array<FileHandle>?) {
                if(files!=null && files.any()) {
                    val oscillators = oscillatorRepository.getOscillators()
                    val envelopes = envelopeRepository.getAllModels()
                    val effects = effectRepository.getAllIds()
                    val model = projectSerializer.mapRuntimeToDTO(oscillators, envelopes, effects)
                    val modelJson = projectSerializer.serialize(model)
                    val file = files.first()
                    file.writeString(modelJson, false)
                }
            }

            override fun canceled() {
            }
        })

        stage.addActor(fileChooser.fadeIn())
    }
}