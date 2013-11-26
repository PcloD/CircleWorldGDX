package com.fdangelo.circleworld.gui.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public abstract class Screen {

	static private Skin defaultSkin;

	private final Stage stage;
	private final Table rootTable;

	public Screen() {

		stage = new Stage();

		rootTable = new Table();

		rootTable.setFillParent(true);

		stage.addActor(rootTable);

		initScreen();
	}

	public final Stage getStage() {
		return stage;
	}

	public final Table getRootTable() {
		return rootTable;
	}

	public final void update(final float deltaTime) {
		stage.act(deltaTime);
		onUpdate(deltaTime);
	}

	static public Skin getDefaultSkin() {
		if (defaultSkin == null) {
			defaultSkin = new Skin(Gdx.files.internal("data/uiskin.json"));
		}

		return defaultSkin;
	}

	public final void draw() {
		stage.draw();
	}

	protected abstract void initScreen();

	protected void onUpdate(final float deltaTime) {

	}
}
