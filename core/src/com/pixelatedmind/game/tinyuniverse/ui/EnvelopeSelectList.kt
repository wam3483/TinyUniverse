package com.pixelatedmind.game.tinyuniverse.ui

import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.kotcrab.vis.ui.VisUI
import com.kotcrab.vis.ui.util.adapter.ArrayAdapter
import com.kotcrab.vis.ui.widget.ListView
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisTable
import com.pixelatedmind.game.tinyuniverse.generation.music.utils.ArrayUtils


class EnvelopeSelectList(skin: Skin, piecewiseList : List<PiecewiseModel>) :
        ListView<PiecewiseModel>(EnvelopeSelectListViewAdapter(piecewiseList, skin)) {
}

class EnvelopeSelectListViewAdapter(models : List<PiecewiseModel> = listOf(), val skin : Skin) :
        ArrayAdapter<PiecewiseModel, VisTable>(ArrayUtils.toArray(models)) {

    private val bg: Drawable = VisUI.getSkin().getDrawable("window-bg")
    private val selection: Drawable = VisUI.getSkin().getDrawable("list-selection")
    init{
        selectionMode = SelectionMode.SINGLE

    }

    override fun createView(item: PiecewiseModel?): VisTable {
        val table = VisTable()
        table.left()
        if(item!=null) {
            val widget = PiecewiseFunctionWidget(item)
            //val widget = VisLabel(item.name)
            table.add(widget).left().fillX().expandX()
        }
        table.layout()
        table.pack()
        return table
    }

    override fun selectView(view: VisTable) {
        view.setBackground(selection)
    }

    override fun deselectView(view: VisTable) {
        view.setBackground(bg)
    }
}