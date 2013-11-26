package com.fdangelo.circleworld.gui.core;

import com.badlogic.gdx.Gdx;

public class Gui {
	static private Screen activeScreen;

	static public void setActiveScreen(final Screen screen) {
		activeScreen = screen;

		activeScreen.getStage().setViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		Gdx.input.setInputProcessor(screen.getStage());
	}

	static public Screen getActiveScreen() {
		return activeScreen;
	}

	static public void update(final float deltaTime) {
		if (activeScreen != null) {
			activeScreen.update(deltaTime);
		}
	}

	static public void draw() {
		if (activeScreen != null) {
			activeScreen.draw();
		}
	}

	public static void resize(final int width, final int height) {
		if (activeScreen != null) {
			activeScreen.getStage().setViewport(width, height);
		}
	}
}
