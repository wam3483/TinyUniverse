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

        fun SelectorDialog(title : String, skin : Skin,
                                    models : List<PiecewiseModel>,
                                    callbackFunction : (EnvelopeDialogModel<Int>?,DialogResult)->Unit
        ) : VisWindow{
            val window = VisWindow(title)
            window.columnDefaults(0).left()

            val adapter = EnvelopeSelectListViewAdapter(models, skin)
            val envList = ListView<PiecewiseModel>(adapter)
            window.add(envList.mainTable).height(300f).growX()
            window.row()

            val footerTable = buildButtonTable(skin, {result->
                standardDialogCallback(result, callbackFunction, {
                    val piecewiseModel = adapter.selection.firstOrNull()
                    if(piecewiseModel==null){
                        null
                    }else {
                        EnvelopeDialogModel<Int>(0,0, piecewiseModel)
                    }
                })
                window.remove()
            })
            window.add(footerTable).center()
            window.row()
            prepDialog(window)
            return window
        }

        private fun buildButtonTable(skin : Skin, callback : (DialogResult)->Unit) : Table {
            val btnAccept = TextButton("Accept", skin)
            val btnCancel = TextButton("Cancel", skin)
            val btnClear = TextButton("Clear", skin)

            val footerTable = Table()
            footerTable.add(btnAccept).pad(5f)
            footerTable.add(btnCancel).pad(5f)
            footerTable.add(btnClear).pad(5f)
            btnCancel.addListener(object : ClickListener(){
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    callback.invoke(DialogResult.Cancel)
                }
            })
            btnClear.addListener(object : ClickListener(){
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    callback.invoke(DialogResult.Clear)
                }
            })
            btnAccept.addListener(object : ClickListener(){
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    callback.invoke(DialogResult.Accept)
                }
            })
            return footerTable
        }

        private inline fun <reified T> standardDialogCallback(result : DialogResult, callbackFunction : (EnvelopeDialogModel<T>?,DialogResult)->Unit, modelFactory:()->EnvelopeDialogModel<T>?){
            when(result){
                    DialogResult.Cancel->callbackFunction(null, result)
                    DialogResult.Clear->callbackFunction(null, result)
                    DialogResult.Accept->{
                        callbackFunction.invoke(modelFactory.invoke(), result)
                    }
                }
        }

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

            val footerTable = buildButtonTable(skin, {result->
                standardDialogCallback(result, callbackFunction, {
                    val piecewiseModel = adapter.selection.firstOrNull()
                    if(piecewiseModel==null){
                        null
                    }else {
                        EnvelopeDialogModel(minSpinnerModel.value, maxSpinnerModel.value, piecewiseModel)
                    }
                })
                window.remove()
            })
            window.add(footerTable).center()
            window.row()
            prepDialog(window)

            return window
        }

        private fun prepDialog(window : VisWindow){
            window.isModal = true
            window.addCloseButton()
            window.left()
            window.pack()
            window.layout()
            window.centerWindow()
        }

        private fun buildRow(root : Table, skin : Skin, lblText : String, spinnerModel : AbstractSpinnerModel){
            val table = Table()
            table.left().pad(5f)
            table.add(Spinner(lblText, spinnerModel)).left()

            root.add(table)
            root.row()
        }
    }
}