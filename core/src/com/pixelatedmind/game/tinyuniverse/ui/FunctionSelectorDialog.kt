package com.pixelatedmind.game.tinyuniverse.ui

import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.kotcrab.vis.ui.widget.ListView
import com.kotcrab.vis.ui.widget.VisWindow
import com.kotcrab.vis.ui.widget.spinner.AbstractSpinnerModel
import com.kotcrab.vis.ui.widget.spinner.IntSpinnerModel
import com.kotcrab.vis.ui.widget.spinner.Spinner
import com.pixelatedmind.game.tinyuniverse.services.InterpolationFactory

class EnvelopeDialogModel<T>(val min : T, val max : T, val model : PiecewiseModel){

}

enum class DialogResult{
    Accept,
    Cancel,
    Clear
}

class FunctionSelectorDialog(title : String) : VisWindow(title) {
    companion object{
        fun IntBoundFunctionDialog(title : String = "",
                              skin : Skin,
                              models : List<PiecewiseModel>,
                              minText : String, maxText : String,
                              startValue : Int, minValue : Int, maxValue : Int, step : Int = 1,
                               callbackFunction : (EnvelopeDialogModel<Int>?,DialogResult)->Unit) : VisWindow{
            val window = VisWindow(title)
            window.columnDefaults(0).left()
            val minSpinnerModel = IntSpinnerModel(startValue, minValue, maxValue, step)
            val maxSpinnerModel = IntSpinnerModel(startValue, minValue, maxValue, step)
            buildRow(window, skin, minText, minSpinnerModel)
            buildRow(window, skin, maxText, maxSpinnerModel)

            val adapter = EnvelopeSelectListViewAdapter(models, skin)
            val envList = ListView<PiecewiseModel>(adapter)
            envList.updatePolicy = ListView.UpdatePolicy.ON_DRAW
            window.isModal = true
            window.left()
            window.add(envList.mainTable).height(300f).growX()
            window.row()
            val btnAccept = TextButton("Accept", skin)
            val btnCancel = TextButton("Cancel", skin)
            val btnClear = TextButton("Clear", skin)

            val footerTable = Table()
            footerTable.add(btnAccept).pad(5f)
            footerTable.add(btnCancel).pad(5f)
            footerTable.add(btnClear).pad(5f)
            window.add(footerTable).center()
            window.row()

            window.pack()
            window.layout()
            window.centerWindow()
            btnCancel.addListener(object : ClickListener(){
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    callbackFunction.invoke(null, DialogResult.Cancel)
                    window.remove()
                }
            })
            btnClear.addListener(object : ClickListener(){
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    callbackFunction.invoke(null, DialogResult.Clear)
                    window.remove()
                }
            })
            btnAccept.addListener(object : ClickListener(){
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    val piecewiseModel = adapter.selection.firstOrNull()
                    if(piecewiseModel == null){
                        callbackFunction.invoke(null, DialogResult.Accept)
                    }else {
                        val model = EnvelopeDialogModel(minSpinnerModel.value, maxSpinnerModel.value, piecewiseModel!!)
                        callbackFunction.invoke(model, DialogResult.Accept)
                    }
                    window.remove()
                }
            })
            return window
        }

        private fun buildRow(root : Table, skin : Skin, lblText : String, spinnerModel : AbstractSpinnerModel){
            val table = Table()
            table.left().pad(5f)
//            table.debug = true
            table.add(Spinner(lblText, spinnerModel)).left()

            root.add(table)
            root.row()
        }
    }
}