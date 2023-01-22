package com.pixelatedmind.game.tinyuniverse;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.pixelatedmind.game.tinyuniverse.screen.AutoTileTester;
import com.pixelatedmind.game.tinyuniverse.util.AutoTilePacker;
import com.pixelatedmind.game.tinyuniverse.util.AutoTilePacker2;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setForegroundFPS(60);
		config.setWindowedMode(1600,768);
		config.setTitle("TinyUniverse");
		new Lwjgl3Application(
				new AutoTileTester(),
				//new AutoTilePacker(),
				//new DungeonGeneratorViewer(),
				config);
	}
}
