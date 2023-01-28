package com.pixelatedmind.game.tinyuniverse;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.pixelatedmind.game.tinyuniverse.screen.AutoTileMapperTester;
import com.pixelatedmind.game.tinyuniverse.screen.TiledMapRegionViewer;
import com.pixelatedmind.game.tinyuniverse.screen.VoronoiGraphViewer;
import com.pixelatedmind.game.tinyuniverse.screen.WordGeneratorViewer;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setForegroundFPS(60);
		config.setWindowedMode(1024,600);
		config.setBackBufferConfig(0,0,0,1,0,0,3);
		config.setTitle("TinyUniverse");
		new Lwjgl3Application(
//				new WordGeneratorViewer(),
				new VoronoiGraphViewer(),
				//new TiledMapRegionViewer(),
				//new AutoTileMapperTester(),
				//new RegionGeneratorRawModelViewer(),
				config);
	}
}
