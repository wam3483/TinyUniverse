package com.pixelatedmind.game.tinyuniverse.ui

import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.Array
import com.kotcrab.vis.ui.util.adapter.ArrayAdapter
import com.kotcrab.vis.ui.widget.ListView
import com.pixelatedmind.game.tinyuniverse.services.InterpolationFactory

class EditEnvelopeListView(skin: Skin, interpolationFactory: InterpolationFactory) :
        ListView<PiecewiseModel>(EnvelopeListViewAdapter(Array(), skin, interpolationFactory)) {
}

class EnvelopeListViewAdapter(models : Array<PiecewiseModel> = Array(), val skin : Skin,
                              val interpolationFactory : InterpolationFactory) : ArrayAdapter<PiecewiseModel, PiecewiseFunctionEditPanel>(models) {
    override fun createView(item: PiecewiseModel?): PiecewiseFunctionEditPanel {
        var view = PiecewiseFunctionEditPanel(item!!, skin, interpolationFactory)
        return view
    }
}