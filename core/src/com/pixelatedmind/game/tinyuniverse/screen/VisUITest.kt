package com.pixelatedmind.game.tinyuniverse.screen

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.kotcrab.vis.ui.VisUI
import com.kotcrab.vis.ui.widget.*
import com.kotcrab.vis.ui.widget.tabbedpane.Tab
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPane
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPaneAdapter
import com.pixelatedmind.game.tinyuniverse.generation.music.Notes
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.AdditiveNoteGenerator
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.io.AudioStreamPlayer
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.io.AudioStreamReader
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream.BaseWaveformStreamFactory
import com.pixelatedmind.game.tinyuniverse.input.NoteGeneratorKeyboardProcessor
import com.pixelatedmind.game.tinyuniverse.services.InterpolationFactory
import com.pixelatedmind.game.tinyuniverse.ui.*
import com.pixelatedmind.game.tinyuniverse.ui.events.*
import com.pixelatedmind.game.tinyuniverse.ui.model.OscillatorModel
import com.pixelatedmind.game.tinyuniverse.ui.patch.OscillatorModelEnvelopeFactory
import com.pixelatedmind.game.tinyuniverse.ui.patch.UIInteractableEnvelope
import com.pixelatedmind.game.tinyuniverse.util.EventBus


class VisUITest : ApplicationAdapter(){

    private val eventbus = EventBus()

    private lateinit var stage: Stage
    private lateinit var menuBar: MenuBar

    private lateinit var oscPanel : OscilatorPanel
    private lateinit var oscillatorTable : Table
    private lateinit var piecewiseModelRepo : EnvelopeRepository
    private lateinit var envelopeList : EnvelopeListViewAdapter

    private lateinit var keyboardMusicListener : NoteGeneratorKeyboardProcessor
    private lateinit var musicPlayer : AudioStreamPlayer

    override fun create() {
        oscillatorTable = Table()
        piecewiseModelRepo = EnvelopeRepository()

        registerEventHandlers()


        val audioDevice = Gdx.audio.newAudioDevice(44100, true)
        musicPlayer = AudioStreamPlayer(audioDevice, 44100, 1/16f)

        VisUI.load(VisUI.SkinScale.X1)

        stage = Stage(ScreenViewport())

        val root = Table()
        root.setFillParent(true)
        stage.addActor(root)

        root.align(Align.top)


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


        oscillatorTable.align(Align.top)
        oscillatorTable.width = 25f
        buildOscillatorPanel(oscillatorTable)

        envelopeList = EnvelopeListViewAdapter(eventbus, Array(), VisUI.getSkin(), InterpolationFactory())

        val tabContainer = VisTable()
        val tabbedPane = TabbedPane()
        tabbedPane.addListener(object : TabbedPaneAdapter(){
            override fun switchedTab(tab: Tab?) {
                tabContainer.clearChildren();
                tabContainer.add(tab!!.contentTable).expand().fill()
            }
        })

        val editEnvelopeListView =  ListView<PiecewiseModel>(envelopeList)
        val envelopeTab = SimpleTab("Envelopes", editEnvelopeListView.mainTable)
        val testTab = SimpleTab("Test", Table())
        tabbedPane.add(envelopeTab)
        tabbedPane.add(testTab)

        piecewiseModelRepo.addModelAddedListsener(this::newPiecewiseCreatedEvent)
        piecewiseModelRepo.addModelDeletedListsener(this::deletePiecewiseEvent)

        val splitPane = VisSplitPane(oscillatorTable, editEnvelopeListView.mainTable, false)
        splitPane.setSplitAmount(.42f)
        splitPane.setMaxSplitAmount(.42f)
        root.add(splitPane).expand().fill()

        root.layout()


        val multiplex = InputMultiplexer(stage, keyboardMusicListener)
        Gdx.input.setInputProcessor(multiplex)
    }

    private fun deletePiecewiseEvent(model : PiecewiseModel){
        envelopeList.removeValue(model, true)
    }

    private fun newPiecewiseCreatedEvent(model : PiecewiseModel){
        envelopeList.add(model)
    }

    fun registerEventHandlers(){
        eventbus.register(CreateEnvelopeHandler(piecewiseModelRepo)::handle)
        eventbus.register(DeleteEnvelopeHandler(piecewiseModelRepo)::handle)
        eventbus.register(CreateOscillatorHandler(oscillatorTable, piecewiseModelRepo, BaseWaveformStreamFactory(44100), eventbus)::handle)
    }

    fun buildOscillatorPanel(table : Table)  {
        val model = OscillatorModel()
        oscPanel = OscilatorPanel(model, BaseWaveformStreamFactory(44100), piecewiseModelRepo, eventbus)

        val oscillatorFactory = OscillatorModelEnvelopeFactory(model, BaseWaveformStreamFactory(44100), Notes())

        val additiveNoteGenerator = AdditiveNoteGenerator(oscillatorFactory)
        musicPlayer.start(AudioStreamReader(additiveNoteGenerator, 44100))

        keyboardMusicListener = NoteGeneratorKeyboardProcessor(additiveNoteGenerator)
        table.add(oscPanel).expandX().fillX().padTop(7f)
        oscPanel.height = 100f
        oscPanel.width = 20f
        table.row()
    }

    private fun buildMenuBar(){
        val fileMenu = Menu("File")
        val newProject = MenuItem("New project")
        val newEnv = MenuItem("New envelope")
        newEnv.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor) {
                eventbus.post(CreateEnvelopeRequest())
            }
        })
        val newOscillator = MenuItem("New oscillator")
        newOscillator.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor) {
                eventbus.post(CreateOscillatorRequest())
            }
        })
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