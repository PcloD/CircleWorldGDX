package com.fdangelo.circleworld.gui.core;


public class Gui {
	static private Screen activeScreen;

	static public void setActiveScreen(final Screen screen) {
		activeScreen = screen;

		activeScreen.setActive();
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
			activeScreen.resize(width, height);
		}
	}
}
