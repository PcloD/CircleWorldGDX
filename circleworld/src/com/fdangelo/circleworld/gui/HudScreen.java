package com.fdangelo.circleworld.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.StringBuilder;
import com.fdangelo.circleworld.GameLogic;
import com.fdangelo.circleworld.gui.core.Screen;

public class HudScreen extends Screen {

	private Label performance;
	private StringBuilder sb;
	private TextButton boardShipButton;

	@Override
	protected void initScreen() {

		getRootTable().left();
		getRootTable().top();

		performance = new Label("", getDefaultSkin());

		getRootTable().add(performance);

		boardShipButton = new TextButton("BOARD SHIP", getDefaultSkin());

		boardShipButton.addListener(new ChangeListener() {

			@Override
			public void changed(final ChangeEvent event, final Actor actor) {

				switch (GameLogic.instace.getState()) {
					case PlayingAvatar:
						boardShipButton.setText("LEAVE SHIP");
						GameLogic.instace.playerBoardShip();
						break;

					case PlayingShip:
						final int clickedThingIndex = GameLogic.instace
								.getUniverseView()
								.getUniverse()
								.findClosestRenderedThing(GameLogic.instace.getUniverseView().shipView.getUniverseObject().getPositionX(),
										GameLogic.instace.getUniverseView().shipView.getUniverseObject().getPositionY(), 30.0f);

						if (clickedThingIndex >= 0) {
							boardShipButton.setText("BOARD SHIP");
							GameLogic.instace.playerLeaveShip(GameLogic.instace.getUniverseView().getUniverse().getPlanet((short) clickedThingIndex));
						}
						break;

					default:
						// Nothing
						break;
				}
			}
		});

		getRootTable().add(boardShipButton).right().expandX().width(200).height(100);

		sb = new StringBuilder();
	}

	@Override
	protected void onUpdate(final float deltaTime) {
		// int totalRenderCalls = guistage.getSpriteBatch().renderCalls +
		// gamelogic.getStage().getSpriteBatch().renderCalls;
		final int totalRenderCalls = -1;

		sb.length = 0;
		sb.append("FPS: ").append(Gdx.graphics.getFramesPerSecond()).append(" Used Memory: ").append(Gdx.app.getJavaHeap() / 1024).append("kb Used Native: ")
				.append(Gdx.app.getNativeHeap() / 1024).append("kb Render Calls: ").append(totalRenderCalls);

		performance.setText(sb);
	}
}
