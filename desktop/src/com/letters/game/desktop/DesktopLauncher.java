package com.letters.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.letters.game.LettersGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Letters Game";
		config.width = 1920;
		config.height = 1080;
		new LwjglApplication(new LettersGame(), config);
	}
}