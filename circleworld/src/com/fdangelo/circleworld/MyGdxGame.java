package com.fdangelo.circleworld;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.fdangelo.circleworld.gui.HudScreen;
import com.fdangelo.circleworld.gui.core.Gui;
import com.fdangelo.circleworld.universeengine.utils.UEProfiler;

public class MyGdxGame implements ApplicationListener {
	private GameLogic gamelogic;

	@Override
	public void create() {
		gamelogic = new GameLogic();

		initGUI();
	}

	private void initGUI() {
		Gui.setActiveScreen(new HudScreen());
	}

	@Override
	public void dispose() {
		gamelogic.dispose();
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		gamelogic.updateAndRender(Gdx.graphics.getDeltaTime());

		Gui.update(Gdx.graphics.getDeltaTime());
		Gui.draw();

		updatePerformance();
	}

	private void updatePerformance() {
		UEProfiler.Update();
		UEProfiler.Clear();
	}

	@Override
	public void resize(final int width, final int height) {
		Gui.resize(width, height);
		gamelogic.resize(width, height);
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}
}
