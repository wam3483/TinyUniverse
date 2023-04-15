package com.pixelatedmind.game.tinyuniverse.ui.dialogs

import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.kotcrab.vis.ui.util.adapter.AbstractListAdapter
import com.kotcrab.vis.ui.util.dialog.Dialogs
import com.kotcrab.vis.ui.widget.ListView
import com.kotcrab.vis.ui.widget.VisWindow
import com.pixelatedmind.game.tinyuniverse.generation.music.utils.ArrayUtils
import com.pixelatedmind.game.tinyuniverse.ui.EffectListViewAdapter
import com.pixelatedmind.game.tinyuniverse.ui.EffectRepository
import com.pixelatedmind.game.tinyuniverse.ui.model.NameableId

class EffectDialog(title : String = "", effectRepository: EffectRepository, val callback : (selection : List<NameableId>)->Unit) : VisWindow(title) {
    private val padding = 5f
    init{
        val effectListAdapter = EffectListViewAdapter(ArrayUtils.toArray(effectRepository.getAllIds()))
        val listView = ListView(effectListAdapter)
        effectListAdapter.selectionMode = AbstractListAdapter.SelectionMode.MULTIPLE
        add(listView.mainTable).growX().height(300f)
        row()
        val buttonTable = buildButtonTable(effectListAdapter)
        add(buttonTable).padTop(padding).padLeft(padding).padRight(padding)
        prepDialog()
    }

    private fun prepDialog(){
        isModal = true
        addCloseButton()
        left()
        pack()
        layout()
        centerWindow()
        fadeIn()
    }

    private fun buildButtonTable(effectList : EffectListViewAdapter) : Table{
        val table = Table()

        val btnOk = TextButton("OK", skin)
        btnOk.addListener(object : ClickListener(){
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                val selection = effectList.selection
                if(selection.isEmpty){
                    Dialogs.showOKDialog(stage, "Missing Effect","Select an effect to continue.")
                }else {
                    callback.invoke(selection.toList())
                    close()
                }
            }
        })

        val btnCancel = TextButton("Cancel",skin)
        btnCancel.addListener(object : ClickListener(){
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                close()
            }
        })

        table.add(btnOk).padRight(padding)
        table.add(btnCancel)
        return table
    }
}