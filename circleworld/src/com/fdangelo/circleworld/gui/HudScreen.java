package com.fdangelo.circleworld.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.StringBuilder;
import com.esotericsoftware.tablelayout.Value;
import com.fdangelo.circleworld.GameLogic;
import com.fdangelo.circleworld.gui.core.Screen;
import com.fdangelo.circleworld.universeengine.Universe;
import com.fdangelo.circleworld.universeengine.objects.UniverseObject;
import com.fdangelo.circleworld.universeview.objects.AvatarInputMode;
import com.fdangelo.circleworld.universeview.objects.AvatarViewInput;

public class HudScreen extends Screen {

	private Label performance;
	private StringBuilder sb;
	private TextButton boardShipButton;
	private TextButton editButton;
	
	private AvatarMoveControlScreen avatarMoveControlScreen;
	private AvatarEditControlScreen avatarEditControlScreen;
	
	@Override
	protected void initScreen() {

		getScreenTable().left().top();
		
		Table top = new Table();
		
		getScreenTable().add(top).width(Value.percentWidth(1));

		performance = new Label("", getDefaultSkin());
		top.add(performance).top().left();

		boardShipButton = new TextButton("BOARD SHIP", getDefaultSkin());
		boardShipButton.addListener(new ChangeListener() {
			@Override
			public void changed(final ChangeEvent event, final Actor actor) {
				onBoardShipButtonClicked();
			}
		});
		
		editButton = new TextButton("EDIT", getDefaultSkin());
		editButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				onEditButtonClicked();
			}
		});
		
		top.add(editButton).top().right().width(200).height(100).expandX();
		top.add(boardShipButton).top().right().width(200).height(100);
		
		avatarMoveControlScreen = new AvatarMoveControlScreen();
		addSubscreen(avatarMoveControlScreen);

		avatarEditControlScreen = new AvatarEditControlScreen();
		addSubscreen(avatarEditControlScreen);
		
		avatarMoveControlScreen.setVisible(true);
		avatarEditControlScreen.setVisible(false);
		
		sb = new StringBuilder();
	}
	
	private void onEditButtonClicked() {
		if (AvatarViewInput.mode == AvatarInputMode.Edit) {
			AvatarViewInput.mode = AvatarInputMode.Move;
			
			avatarMoveControlScreen.setVisible(true);
			avatarEditControlScreen.setVisible(false);
			editButton.setText("EDIT");
		} else {
			AvatarViewInput.mode = AvatarInputMode.Edit;
			
			avatarMoveControlScreen.setVisible(false);
			avatarEditControlScreen.setVisible(true);
			editButton.setText("EXIT EDIT");
		}
	}

	private void onBoardShipButtonClicked() {

		UniverseObject ship = GameLogic.getInstace().getUniverseView().getShipView().getUniverseObject();
		Universe universe = GameLogic.getInstace().getUniverseView().getUniverse();

		switch (GameLogic.getInstace().getState()) {
			case PlayingAvatar:
				boardShipButton.setText("LEAVE SHIP");
				GameLogic.getInstace().playerBoardShip();
				break;

			case PlayingShip:
				final int clickedThingIndex = universe.findClosestRenderedThing(ship.getPositionX(), ship.getPositionY(), 30.0f);

				if (clickedThingIndex >= 0) {
					boardShipButton.setText("BOARD SHIP");
					GameLogic.getInstace().playerLeaveShip(universe.getPlanet((short) clickedThingIndex));
				}
				break;

			default:
				// Nothing
				break;
		}
	}

	@Override
	protected void onUpdate(final float deltaTime) {
		updatePerformance();
	}

	private final void updatePerformance() {
		// int totalRenderCalls = guistage.getSpriteBatch().renderCalls +
		// gamelogic.getStage().getSpriteBatch().renderCalls;
		final int totalRenderCalls = -1;

		sb.length = 0;
		sb.append("FPS: ").append(Gdx.graphics.getFramesPerSecond()).append(" Used Memory: ").append(Gdx.app.getJavaHeap() / 1024).append("kb Used Native: ")
				.append(Gdx.app.getNativeHeap() / 1024).append("kb Render Calls: ").append(totalRenderCalls);

		performance.setText(sb);
	}
}
