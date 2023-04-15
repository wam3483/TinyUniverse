package com.pixelatedmind.game.tinyuniverse.screen

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.kotcrab.vis.ui.VisUI
import com.kotcrab.vis.ui.widget.*
import com.kotcrab.vis.ui.widget.tabbedpane.Tab
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPane
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPaneAdapter
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.AdditiveNoteGenerator
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.envelope.MultiplexEnvelopeFactory
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.io.AudioStreamPlayer
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.io.AudioStreamReader
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream.BaseWaveformStreamFactory
import com.pixelatedmind.game.tinyuniverse.input.NoteGeneratorKeyboardProcessor
import com.pixelatedmind.game.tinyuniverse.services.InterpolationFactory
import com.pixelatedmind.game.tinyuniverse.ui.*
import com.pixelatedmind.game.tinyuniverse.ui.events.*
import com.pixelatedmind.game.tinyuniverse.util.EventBus


class SynthesizerWindow : ApplicationAdapter(){

    private val eventbus = EventBus()

    private lateinit var stage: Stage
    private lateinit var rootTable : Table
    private lateinit var menuBar: MenuBar

    private lateinit var oscillatorTable : Table
    private lateinit var effectsTable : Table
    private lateinit var oscillatorRepository: OscillatorRepository
    private lateinit var piecewiseModelRepo : EnvelopeRepository
    private lateinit var effectRepository : EffectRepository
    private lateinit var envelopeList : EnvelopeListViewAdapter

    private lateinit var keyboardMusicListener : NoteGeneratorKeyboardProcessor
    private lateinit var musicPlayer : AudioStreamPlayer
    private lateinit var multiplexEnvelopeFactory : MultiplexEnvelopeFactory

    private lateinit var skin : Skin

    override fun create() {
        VisUI.load(VisUI.SkinScale.X1)
        skin = VisUI.getSkin()
        val width = Gdx.graphics.width.toFloat()
        val height = Gdx.graphics.height.toFloat()

        oscillatorTable = Table()
        effectsTable = Table()
        oscillatorRepository = OscillatorRepository()
        piecewiseModelRepo = EnvelopeRepository()
        effectRepository = EffectRepository()
        multiplexEnvelopeFactory = MultiplexEnvelopeFactory()

        val audioDevice = Gdx.audio.newAudioDevice(44100, true)
        musicPlayer = AudioStreamPlayer(audioDevice, 44100, 1/16f)

        val additiveNoteGenerator = AdditiveNoteGenerator(multiplexEnvelopeFactory)
        musicPlayer.start(AudioStreamReader(additiveNoteGenerator, 44100))
        keyboardMusicListener = NoteGeneratorKeyboardProcessor(additiveNoteGenerator)

        envelopeList = EnvelopeListViewAdapter(eventbus, Array(), skin, InterpolationFactory())

        stage = Stage(ExtendViewport(width,height))

        registerEventHandlers()


        rootTable = Table()
        rootTable.setFillParent(true)
        stage.addActor(rootTable)

        rootTable.align(Align.top)


        menuBar = MenuBar()
        buildMenuBar()
        menuBar.setMenuListener(object : MenuBar.MenuBarListener {
            override fun menuOpened(menu: Menu) {
                println("Opened menu: " + menu.title)
            }

            override fun menuClosed(menu: Menu) {
                println("Closed menu: " + menu.title)
            }
        })
        rootTable.add(menuBar.getTable()).expandX().fillX().row()

        oscillatorTable.top().left()
        effectsTable.top().left()


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
                    tabView.add(tab.contentTable).grow()
                }
            }
        })

        val editEnvelopeListView =  ListView<PiecewiseModel>(envelopeList)
        val envelopeTab = SimpleTab("Envelopes", editEnvelopeListView.mainTable)
        val testTab = SimpleTab("Effects", effectsTable)

        val audioWaveformTable = Table()
        val waveformActor = AudioWaveformWidget(musicPlayer)
        waveformActor.width = 830f
        audioWaveformTable.setFillParent(true)
        audioWaveformTable.add(waveformActor).grow()
        val audioWaveform = SimpleTab("Waveform", audioWaveformTable)
        tabbedPane.add(envelopeTab)
        tabbedPane.add(testTab)
        tabbedPane.add(audioWaveform)

        val splitPane = VisSplitPane(oscillatorTable, tabRootTable,
                false)
        splitPane.setSplitAmount(.42f)
        splitPane.setMaxSplitAmount(.42f)
        rootTable.add(splitPane).grow()

        rootTable.layout()


        val multiplex = InputMultiplexer(stage, keyboardMusicListener)
        Gdx.input.setInputProcessor(multiplex)
    }

    fun registerEventHandlers(){
        eventbus.register(CreateEnvelopeHandler(piecewiseModelRepo, envelopeList)::onCreateEnvelopeRequest)
        eventbus.register(DeleteEnvelopeHandler(piecewiseModelRepo, envelopeList)::handle)
        val oscillatorCreateDeleteHandler  = CreateOscillatorHandler(multiplexEnvelopeFactory, oscillatorTable,
                effectRepository,
                piecewiseModelRepo, oscillatorRepository, BaseWaveformStreamFactory(44100), eventbus)
        eventbus.register(oscillatorCreateDeleteHandler::onCreateOscillatorRequest)
        eventbus.register(oscillatorCreateDeleteHandler::onDeleteOscillatorRequest)

        val lowPassEffectHandler = CreateLowPassEffectHandler(eventbus, effectsTable, piecewiseModelRepo, skin)
        eventbus.register(lowPassEffectHandler::handleCreateLowPassEffect)
        val createEffectHandler = EffectCRUDEventHandler(effectRepository)
        eventbus.register(createEffectHandler::handleCreateEffectEvent)
        eventbus.register(createEffectHandler::handleDeleteEffectEvent)

        val saveProjectHandler = SaveProjectHandler(stage, piecewiseModelRepo, effectRepository,oscillatorRepository)
        eventbus.register(saveProjectHandler::onSaveProjectEvent)

        val openProjectHandler = OpenProjectRequestHandler(stage, eventbus)
        eventbus.register(openProjectHandler::onOpenProjectRequest)

        val clearProjectListener = ClearProjectHandler(
                oscillatorTable,oscillatorRepository,
                multiplexEnvelopeFactory,
                piecewiseModelRepo, envelopeList,
                effectRepository, effectsTable)
        eventbus.register(clearProjectListener::onClearProjectEvent)
    }

    private fun buildEffectsSubmenu() : MenuItem{
        val newEffect = MenuItem("New effect")

        val effectsMenu = PopupMenu()
        val lowPassMenuItem = MenuItem("Low Pass")
        effectsMenu.addItem(lowPassMenuItem)
        lowPassMenuItem.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor) {
                eventbus.post(CreateLowPassEffectRequest())
            }
        })

        newEffect.subMenu = effectsMenu

        return newEffect
    }

    private fun buildMenuBar(){
        val fileMenu = Menu("File")
        val newProject = MenuItem("New project")
        val saveProject = MenuItem("Save project")
        val openProject = MenuItem("Open project")
        val effectsMenu = buildEffectsSubmenu()

        val newEnv = MenuItem("New envelope")

        saveProject.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor) {
                eventbus.post(SaveProjectRequest())
            }
        })

        openProject.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor) {
                eventbus.post(OpenProjectRequest())
            }
        })

        newEnv.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor) {
                eventbus.post(CreateEnvelopeRequest())
            }
        })
        val newOscillator = MenuItem("New oscillator")
        newOscillator.setShortcut(Input.Keys.CONTROL_LEFT, Input.Keys.O)
        newOscillator.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor) {
                eventbus.post(CreateOscillatorRequest())
            }
        })
        fileMenu.addItem(newProject)
        fileMenu.addItem(openProject)
        fileMenu.addItem(saveProject)
        fileMenu.addItem(newEnv)
        fileMenu.addItem(effectsMenu)
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