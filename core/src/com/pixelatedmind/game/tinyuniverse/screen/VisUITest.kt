package com.pixelatedmind.game.tinyuniverse.screen

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.kotcrab.vis.ui.VisUI
import com.kotcrab.vis.ui.widget.*
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream.BaseWaveformStreamFactory
import com.pixelatedmind.game.tinyuniverse.ui.EditEnvelopeListView
import com.pixelatedmind.game.tinyuniverse.ui.OscilatorPanel
import com.pixelatedmind.game.tinyuniverse.ui.PiecewiseModel
import com.pixelatedmind.game.tinyuniverse.services.InterpolationFactory
import com.pixelatedmind.game.tinyuniverse.ui.PiecewiseModelRepository


class VisUITest : ApplicationAdapter(){

    private lateinit var stage: Stage
    private lateinit var menuBar: MenuBar

    private lateinit var oscPanel : OscilatorPanel
    private lateinit var piecewiseModelRepo : PiecewiseModelRepository

    override fun create() {
        VisUI.load(VisUI.SkinScale.X1)

        stage = Stage(ScreenViewport())

        piecewiseModelRepo = PiecewiseModelRepository()

        val root = Table()
        root.setFillParent(true)
        stage.addActor(root)

        root.align(Align.top)

        Gdx.input.setInputProcessor(stage)

        menuBar = MenuBar()
        buildMenuBar()
        menuBar.setMenuListener(object : MenuBar.MenuBarListener {
            override fun menuOpened(menu: Menu) {
                System.out.println("Opened menu: " + menu.getTitle())
            }

            override fun menuClosed(menu: Menu) {
                System.out.println("Closed menu: " + menu.getTitle())
            }
        })
//        root.debug = true
        root.add(menuBar.getTable()).expandX().fillX().row()


        val waveformTable = Table()
        waveformTable.align(Align.top)
        waveformTable.width = 25f
        buildOscillatorPanel(waveformTable)

        val editEnvelopeListView = EditEnvelopeListView(VisUI.getSkin(), InterpolationFactory())
        val model = buildPiecewiseModel()
        piecewiseModelRepo.add(model)
        editEnvelopeListView.adapter.add(model)

        val splitPane = VisSplitPane(waveformTable, editEnvelopeListView.mainTable, false)
        splitPane.setSplitAmount(.42f)
        splitPane.setMaxSplitAmount(.42f)
        root.add(splitPane).expand().fill()

        root.layout()
    }

    fun buildPiecewiseModel() : PiecewiseModel{
        val model =
                PiecewiseModel(listOf<PiecewiseModel.Piece>(
                        PiecewiseModel.Piece(Vector2(0f, 0f), "sine", Interpolation.sine),
                        PiecewiseModel.Piece(Vector2(.25f, .6f), "sine", Interpolation.sine),
                        PiecewiseModel.Piece(Vector2(.5f, .8f), "sine", Interpolation.sine),
                        PiecewiseModel.Piece(Vector2(1f, 0f), "linear", Interpolation.linear)
                ))
        return model
    }

    fun buildOscillatorPanel(table : Table)  {
        oscPanel = OscilatorPanel(BaseWaveformStreamFactory(), piecewiseModelRepo)
        table.add(oscPanel).expandX().fillX().padTop(7f)
        oscPanel.height = 100f
        oscPanel.width = 20f
        table.row()
    }

    private fun buildMenuBar(){
        val fileMenu = Menu("File")
        val newProject = MenuItem("New project")
        val newEnv = MenuItem("New envelope")
        val newOscillator = MenuItem("New oscillator")
        fileMenu.addItem(newProject)
        fileMenu.addItem(newEnv)
        fileMenu.addItem(newOscillator)
        menuBar.addMenu(fileMenu)
    }

    override fun render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f))
        stage.draw()
    }

    override fun dispose() {
        VisUI.dispose()
        stage.dispose()
    }
}