//package com.pixelatedmind.game.tinyuniverse.screen
//
//import com.badlogic.gdx.ApplicationAdapter
//import com.badlogic.gdx.Gdx
//import com.badlogic.gdx.InputMultiplexer
//import com.badlogic.gdx.graphics.OrthographicCamera
//import com.badlogic.gdx.graphics.Texture
//import com.badlogic.gdx.graphics.g2d.TextureRegion
//import com.badlogic.gdx.maps.tiled.TiledMap
//import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
//import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile
//import com.badlogic.gdx.utils.ScreenUtils
//import com.pixelatedmind.game.tinyuniverse.generation.world.*
//import com.pixelatedmind.game.tinyuniverse.input.KeyboardCameraMoveProcessor
//import com.pixelatedmind.game.tinyuniverse.input.ScrollZoomInputProcessor
//import com.pixelatedmind.game.tinyuniverse.maps.tiled.BiomeTileLayerMapper
//import java.util.*
//
//class WordGeneratorViewer : ApplicationAdapter() {
//    lateinit var camera : OrthographicCamera
//    lateinit var tilemapRenderer : OrthogonalTiledMapRenderer
//    lateinit var tileLayerMapper : BiomeTileLayerMapper
//
//    private fun generateWorldModel() : WorldModel? {
//        val biomeMap = mapOf(
//                Biome.Desert to BiomeData(.5,.2,0.0),
//                Biome.Forest to BiomeData(.4,.2,.4),
//                Biome.Grassland to BiomeData(.3,.2,.5),
//                Biome.Jungle to BiomeData(.66, .3,.5),
//                Biome.Mountains to BiomeData(0.0, .5,0.0),
//                Biome.Water to BiomeData(0.0,0.0,0.0),
//                Biome.Tundra to BiomeData(0.0, .2, 0.0)
//        )
//        val resolver = BiomeResolver(biomeMap)
//        val random = Random()
//        val worldGen = WorldGenerator(resolver,100,50,random.nextLong())
//        val worldModel = worldGen.generate()
//        return worldModel
//    }
//
//    override fun create() {
//        val w = Gdx.graphics.width.toFloat()
//        val h = Gdx.graphics.height.toFloat()
//
//        camera = OrthographicCamera()
//        camera.setToOrtho(false, w, h)
//        val zoomInput = ScrollZoomInputProcessor(camera!!)
//        val moveInput = KeyboardCameraMoveProcessor(camera!!)
//        val multiplex = InputMultiplexer(zoomInput, moveInput)
//        Gdx.input.inputProcessor = multiplex
//
//        val biomeTilesTexture = Texture("worldTiles.png")
//        val biomeTiles = TextureRegion.split(biomeTilesTexture,32,32)
//        val biomeTileMap = mapOf(
//                Biome.Grassland to StaticTiledMapTile(biomeTiles[0][0]),
//                Biome.Tundra to StaticTiledMapTile(biomeTiles[0][1]),
//                Biome.Desert to StaticTiledMapTile(biomeTiles[0][2]),
//                Biome.Forest to StaticTiledMapTile(biomeTiles[0][3]),
//                Biome.Water to StaticTiledMapTile(biomeTiles[0][4]),
//                Biome.Mountains to StaticTiledMapTile(biomeTiles[0][5]),
//                Biome.Jungle to StaticTiledMapTile(biomeTiles[0][6])
//        )
//        tileLayerMapper = BiomeTileLayerMapper(biomeTileMap)
//
//        generateWorldView()
//    }
//
//    private fun generateWorldView(){
//        val worldData = generateWorldModel()!!
//        val biomeTileLayers = tileLayerMapper.mapTileLayers(worldData)
//        val tiledMap = TiledMap()
//        val layers = tiledMap.layers
//        biomeTileLayers.forEach{layers.add(it)}
//
//        tilemapRenderer = OrthogonalTiledMapRenderer(tiledMap)
//    }
//
//    var timeAccumulatorSecs = 0f
//    var newDungeonDelaySecs = 10f
//
//    override fun render() {
//        timeAccumulatorSecs += Gdx.graphics.deltaTime;
//        if(timeAccumulatorSecs>=newDungeonDelaySecs){
//            generateWorldView()
//            timeAccumulatorSecs = 0f
//        }
//        ScreenUtils.clear(0f, 0f, 0f, 1f)
//        camera.update()
//        tilemapRenderer.setView(camera)
//        tilemapRenderer.render()
//        super.render()
//    }
//}