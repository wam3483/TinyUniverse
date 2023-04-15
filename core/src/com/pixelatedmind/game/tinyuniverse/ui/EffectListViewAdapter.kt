package com.pixelatedmind.game.tinyuniverse.ui

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.utils.Array
import com.kotcrab.vis.ui.VisUI
import com.kotcrab.vis.ui.util.adapter.ArrayAdapter
import com.kotcrab.vis.ui.widget.VisCheckBox
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisTable
import com.pixelatedmind.game.tinyuniverse.ui.model.EffectRef
import com.pixelatedmind.game.tinyuniverse.ui.model.NameableId

class EffectListViewAdapter(items : Array<NameableId> = Array()) : ArrayAdapter<NameableId, VisTable>(items) {
    private val bg: Drawable = VisUI.getSkin().getDrawable("window-bg")
    private val selection: Drawable = VisUI.getSkin().getDrawable("list-selection")
    override fun createView(item: NameableId?): VisTable {
        val label = VisLabel(item!!.name)
        val table = VisTable()
        table.left()
        table.add(label)
        return table
    }

    override fun selectView(view: VisTable) {
        view.setBackground(selection)
    }

    override fun deselectView(view: VisTable) {
        view.setBackground(bg)
    }
}

class EffectRefListViewAdapter(items : Array<EffectRef> = Array()) : ArrayAdapter<EffectRef, VisTable>(items) {
    private val bg: Drawable = VisUI.getSkin().getDrawable("window-bg")
    private val selection: Drawable = VisUI.getSkin().getDrawable("list-selection")
    override fun createView(item: EffectRef?): VisTable {
        val label = VisLabel(item!!.nameId.name)
        val table = VisTable()
        table.left()
        val checkbox = VisCheckBox("", item.enabled)
        checkbox.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                item.enabled = checkbox.isChecked
            }
        })
        table.add(checkbox).padRight(10f).padLeft(5f)
        table.add(label)
        return table
    }

    override fun selectView(view: VisTable) {
        view.setBackground(selection)
    }

    override fun deselectView(view: VisTable) {
        view.setBackground(bg)
    }
}