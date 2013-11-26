package com.fdangelo.circleworld.gui.core;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public abstract class Screen {

	static private Skin defaultSkin;

	private final Stage stage;
	
	private final ScreenTable defaultScreenTable;
	
	private ArrayList<Screen> subscreens = new ArrayList<Screen>();

	public Screen() {
		stage = new Stage();

		defaultScreenTable = new ScreenTable();
		
		stage.addActor(defaultScreenTable);

		initScreen();
	}
	
	protected void addSubscreen(Screen screen) {
		subscreens.add(screen);
		stage.addActor(screen.defaultScreenTable);
	}

	public final Stage getStage() {
		return stage;
	}

	public final ScreenTable getScreenTable() {
		return defaultScreenTable;
	}

	public final void update(final float deltaTime) {
		stage.act(deltaTime);
		onUpdate(deltaTime);
		
		for (int i = 0; i < subscreens.size(); i++)
			subscreens.get(i).update(deltaTime);
	}

	public final void draw() {
		stage.draw();
	}

	public final void resize(int width, int height) {
		stage.setViewport(width, height);
	}
	
	public final void setActive() {
		Gdx.input.setInputProcessor(stage);
		
		stage.setViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}
	
	public void setVisible(boolean visible) {
		defaultScreenTable.setVisible(visible);
	}
	
	static public Skin getDefaultSkin() {
		if (defaultSkin == null) {
			defaultSkin = new Skin(Gdx.files.internal("data/uiskin.json"));
		}

		return defaultSkin;
	}
	
	protected abstract void initScreen();

	protected void onUpdate(final float deltaTime) {

	}
}
