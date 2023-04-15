package com.pixelatedmind.game.tinyuniverse.ui

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Array
import com.kotcrab.vis.ui.widget.spinner.IntSpinnerModel
import com.kotcrab.vis.ui.widget.spinner.Spinner
import com.pixelatedmind.game.tinyuniverse.services.InterpolationFactory
import com.pixelatedmind.game.tinyuniverse.ui.events.DeleteEnvelopeRequest
import com.pixelatedmind.game.tinyuniverse.util.EventBus

class PiecewiseFunctionEditPanel(val function : PiecewiseModel, skin : Skin, val interpolationFactory : InterpolationFactory, val eventBus : EventBus) : Table(skin) {
    private var selectedPiece : PiecewiseModel.Piece? = null
    private val textboxName : TextField
    private val deleteBtn : TextButton
    private val editButton : TextButton
    private val durationSpinner : Spinner
    private val durationSpinnerModel : IntSpinnerModel
    private val timesToRepeatModel : IntSpinnerModel
    private val timesToRepeatSpinner : Spinner
    private val repeatForeverCheckbox : CheckBox
    private val selectBoxInterpolation : SelectBox<String>

    val piecewiseUI : MutablePiecewiseFunctionActor

    private var enabled : Boolean = false

    private val labelX : Label
    private val labelY : Label

    init{
        textboxName = TextField("", skin)
        editButton = TextButton("Edit", skin)
        deleteBtn = TextButton("Delete", skin)
        deleteBtn.isVisible = false
        durationSpinnerModel = IntSpinnerModel((function.durationSecs*1000).toInt(),1,9999999)
        timesToRepeatModel = IntSpinnerModel(function.timesToRepeat, 0, 9999999)
        durationSpinner = Spinner("Duration(ms):", durationSpinnerModel)
        timesToRepeatSpinner = Spinner("Times to repeat:", timesToRepeatModel)
        repeatForeverCheckbox = CheckBox("",skin)
        repeatForeverCheckbox.isChecked = function.repeatForever
        selectBoxInterpolation = SelectBox(skin)
        piecewiseUI = MutablePiecewiseFunctionActor(function)
        piecewiseUI.setUnitGridLabel("ms")
        labelX = Label("", skin)
        labelY = Label("", skin)

        textboxName.text = function.name

        repeatForeverCheckbox.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                val checked = repeatForeverCheckbox.isChecked
                timesToRepeatSpinner.isDisabled = checked
            }
        })

        durationSpinner.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                val value = durationSpinnerModel.value
                piecewiseUI.setMaxFunctionValue(value.toFloat())
            }
        })

        deleteBtn.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                eventBus.post(DeleteEnvelopeRequest(function))
            }
        })

        editButton.addListener(object : ClickListener(){
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                setEnabled(!enabled)
                if(!enabled){
                    editButton.setText("Edit")
                    deleteBtn.isVisible = false
                    save()
                }else{
                    editButton.setText("Save")
                    deleteBtn.isVisible = true
                }
            }
        })

        selectBoxInterpolation.width = 500f
        selectBoxInterpolation.height = 100f
        selectBoxInterpolation.items = getInterpolationNames()
        selectBoxInterpolation.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                if(selectedPiece!=null){
                    val selectedInterpolation = selectBoxInterpolation.selected
                    val interpolation = interpolationFactory.new(selectedInterpolation)
                    selectedPiece!!.setInterpolation(selectedInterpolation, interpolation)
                }
            }
        })

        piecewiseUI.addPieceSelectedListener({ piecewiseSegment ->
            selectedPiece = piecewiseSegment
            if(piecewiseSegment!=null) {
                selectBoxInterpolation.selected = piecewiseSegment.interpolationName
            }
        })

        piecewiseUI.addListener(object : ClickListener() {
            private val temp = Vector2()
            override fun mouseMoved(event: InputEvent?, x: Float, y: Float): Boolean {
                val xScale = x / width
                temp.set(x,y)
                piecewiseUI.localPointToLabelUnits(temp)
                labelX.setText("%.2f".format(temp.x))
                labelY.setText("%.2f".format(temp.y))
                return false
            }

            override fun touchDragged(event: InputEvent?, x: Float, y: Float, pointer: Int) {
                labelX.setText(x.toString())
                labelY.setText(y.toString())
            }
        })

        addNameGroup()
        row()
        addDurationGroup()
        row()
        addInterpolationGroup()
        row()

        val temp = Table()
        temp.add(piecewiseUI).expandX().fillX().expandY().fillY().height(200f).pad(5f)
        add(temp).expand().fill()
        row()
        addFooter()
        layout()
//        debug=true
        piecewiseUI.validate()

        setEnabled(false)
    }

    private fun save(){
        function.name = textboxName.text
        function.durationSecs = (durationSpinner.model as IntSpinnerModel).value / 1000f
        function.repeatForever = repeatForeverCheckbox.isChecked
        function.timesToRepeat = (timesToRepeatSpinner.model as IntSpinnerModel).value
    }

    fun setEnabled(enabled : Boolean){
        this.enabled = enabled
        this.textboxName.isDisabled = !enabled
        this.selectBoxInterpolation.isDisabled = !enabled
        this.durationSpinner.isDisabled = !enabled
        this.timesToRepeatSpinner.isDisabled = !enabled || repeatForeverCheckbox.isChecked
        this.repeatForeverCheckbox.isDisabled = !enabled
        this.piecewiseUI.setEnabled(enabled)
    }

    private fun getInterpolationNames() : Array<String>{
        val names = interpolationFactory.allInterpolationNames()
        val ary = Array<String>(names.size)
        names.forEachIndexed{
            index,value->
                ary.add(value)
        }
        return ary
    }

    private fun addFooter(){
        val t = Table()
        t.padTop(10f)
        t.add(Label("${piecewiseUI.getUnitGridLabel()}: ", skin))
        t.add(labelX).left().padRight(15f)
        t.add(Label("Value: ", skin))
        t.add(labelY).left()
        add(t).expandX().fillX()
    }

    private fun addInterpolationGroup() {
        val lbl = Label("Interpolation:",skin)
        lbl.width = 100f
        val t = Table()
        t.add(lbl).width(100f).padLeft(5f)
        t.add(selectBoxInterpolation).expandX().fillX().padTop(5f).padRight(5f)
        add(t).expandX().fillX()
    }

    private fun addDurationGroup() {
        val t = Table()
        t.add(durationSpinner).width(160f).padRight(15f)
        t.add(timesToRepeatSpinner).padRight(15f)
        t.add(Label("Repeat forever:",skin)).padRight(5f)
        t.add(repeatForeverCheckbox)
        t.left().padLeft(5f).padTop(5f)
        add(t).expandX().fillX()
    }
    private fun addNameGroup() {
        val lbl = Label("Name:", skin)
        val t = Table()
        t.add(editButton).width(40f).padTop(5f).padLeft(5f)
        t.add(lbl).width(55f).padLeft(5f)
        t.add(textboxName).expandX().fillX().padTop(5f).padRight(5f)
        t.add(deleteBtn)
        add(t).expandX().fillX()
    }
}