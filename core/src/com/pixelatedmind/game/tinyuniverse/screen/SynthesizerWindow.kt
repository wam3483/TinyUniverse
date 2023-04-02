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
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.kotcrab.vis.ui.VisUI
import com.kotcrab.vis.ui.widget.*
import com.kotcrab.vis.ui.widget.tabbedpane.Tab
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPane
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPaneAdapter
import com.pixelatedmind.game.tinyuniverse.generation.music.Notes
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.AdditiveNoteGenerator
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.envelope.MultiplexEnvelopeFactory
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


class SynthesizerWindow : ApplicationAdapter(){

    private val eventbus = EventBus()

    private lateinit var stage: Stage
    private lateinit var rootTable : Table
    private lateinit var menuBar: MenuBar

    private lateinit var oscillatorTable : Table
    private lateinit var piecewiseModelRepo : EnvelopeRepository
    private lateinit var envelopeList : EnvelopeListViewAdapter

    private lateinit var keyboardMusicListener : NoteGeneratorKeyboardProcessor
    private lateinit var musicPlayer : AudioStreamPlayer
    private lateinit var multiplexEnvelopeFactory : MultiplexEnvelopeFactory

    override fun create() {
        oscillatorTable = Table()
        piecewiseModelRepo = EnvelopeRepository()
        multiplexEnvelopeFactory = MultiplexEnvelopeFactory()

        val audioDevice = Gdx.audio.newAudioDevice(44100, true)
        musicPlayer = AudioStreamPlayer(audioDevice, 44100, 1/16f)

        val additiveNoteGenerator = AdditiveNoteGenerator(multiplexEnvelopeFactory)
        musicPlayer.start(AudioStreamReader(additiveNoteGenerator, 44100))
        keyboardMusicListener = NoteGeneratorKeyboardProcessor(additiveNoteGenerator)

        registerEventHandlers()

        VisUI.load(VisUI.SkinScale.X1)

        stage = Stage(ExtendViewport(900f,480f))

        rootTable = Table()
        rootTable.setFillParent(true)
        stage.addActor(rootTable)

        rootTable.align(Align.top)


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
        rootTable.add(menuBar.getTable()).expandX().fillX().row()

        oscillatorTable.align(Align.top)

        envelopeList = EnvelopeListViewAdapter(eventbus, Array(), VisUI.getSkin(), InterpolationFactory())

        val tabRootTable = Table()
        tabRootTable.padTop(5f)
        val tabView = VisTable()
        val tabbedPane = TabbedPane()
        tabRootTable.add(tabbedPane.table).growX().align(Align.top).align(Align.left)
        tabRootTable.row()
        tabRootTable.add(tabView).grow()
        tabbedPane.addListener(object : TabbedPaneAdapter(){
            override fun switchedTab(tab: Tab?){
                tabView.clearChildren();
                if(tab!=null) {
                    tabView.add(tab.contentTable).expand().fill()
                }
            }
        })

        val editEnvelopeListView =  ListView<PiecewiseModel>(envelopeList)
        val envelopeTab = SimpleTab("Envelopes", editEnvelopeListView.mainTable)
        val testTab = SimpleTab("Filters", Table())
        tabbedPane.add(envelopeTab)
        tabbedPane.add(testTab)

        piecewiseModelRepo.addModelAddedListsener(this::newPiecewiseCreatedEvent)
        piecewiseModelRepo.addModelDeletedListsener(this::deletePiecewiseEvent)

        val splitPane = VisSplitPane(oscillatorTable, tabRootTable,//editEnvelopeListView.mainTable,
                false)
        splitPane.setSplitAmount(.42f)
        splitPane.setMaxSplitAmount(.42f)
        rootTable.add(splitPane).grow()

        rootTable.layout()


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
        val oscillatorCreateDeleteHandler  = CreateOscillatorHandler(multiplexEnvelopeFactory, oscillatorTable,
                piecewiseModelRepo, BaseWaveformStreamFactory(44100), eventbus)
        eventbus.register(oscillatorCreateDeleteHandler::onCreateOscillatorRequest)
        eventbus.register(oscillatorCreateDeleteHandler::onDeleteOscillatorRequest)
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

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width,height)
        rootTable.invalidateHierarchy()
    }

    override fun dispose() {
        VisUI.dispose()
        stage.dispose()
    }
}