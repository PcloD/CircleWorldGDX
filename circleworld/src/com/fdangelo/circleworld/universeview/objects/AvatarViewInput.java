package com.fdangelo.circleworld.universeview.objects;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;
import com.fdangelo.circleworld.GameLogic;
import com.fdangelo.circleworld.GameLogicState;
import com.fdangelo.circleworld.universeengine.objects.Avatar;
import com.fdangelo.circleworld.universeengine.objects.AvatarInput;
import com.fdangelo.circleworld.universeview.UniverseViewCamera;
import com.fdangelo.circleworld.utils.Vector2I;

public class AvatarViewInput {
	private final AvatarView avatarView;

	static public AvatarInputMode mode = AvatarInputMode.Move;
	static public AvatarInputEditTool editTool = AvatarInputEditTool.None;

	static private Vector3 tmpv = new Vector3();
	static private Vector2I tmpvi = new Vector2I();

	public AvatarViewInput(final AvatarView avatarView) {
		this.avatarView = avatarView;
	}

	public final void update(final float deltaTime) {
		if (GameLogic.getInstace().getState() != GameLogicState.PlayingAvatar) {
			return;
		}

		switch (mode) {
			case Edit:
				updateTilesModification();
				UniverseViewCamera.getInstance().updateZoomInput(deltaTime);
				break;

			case Move:
				updateWalkAndJump();
				UniverseViewCamera.getInstance().updateZoomInput(deltaTime);
				break;

			case TravelToPlanet:
				UniverseViewCamera.getInstance().updateZoomInput(deltaTime);
				UniverseViewCamera.getInstance().updateClickOnPlanetToTravel(avatarView.getUniverseView());
				break;
		}
	}

	private final void updateWalkAndJump() {
		final AvatarInput avatarInput = ((Avatar) avatarView.getUniverseObject()).input;

		if (Gdx.app.getType() == ApplicationType.Android || Gdx.app.getType() == ApplicationType.iOS) {
			int touchCount = 0;
			int touch1x = -1, touch1y = -1;
			int touch2x = -1, touch2y = -1;

			if (Gdx.input.isTouched(0)) {
				touch1x = Gdx.input.getX(0);
				touch1y = Gdx.input.getY(0);
				touchCount++;
			}

			if (Gdx.input.isTouched(1)) {
				touch2x = Gdx.input.getX(1);
				touch2y = Gdx.input.getY(1);
				touchCount++;
			}

			final float screenWidth = Gdx.graphics.getWidth();
			final float screenHeight = Gdx.graphics.getHeight();

			if (touchCount >= 1) {
				if (touch1x > screenWidth / 2.0f && touch1y > screenHeight * 0.75f || touchCount > 1 && touch2x > screenWidth / 2.0f
						&& touch2y > screenHeight * 0.75f) {
					avatarInput.jump = true;
				}

				if (touch1x < screenWidth / 4.0f && touch1y > screenHeight * 0.75f || touchCount > 1 && touch2x < screenWidth / 4.0f
						&& touch2y > screenHeight * 0.75f) {
					avatarInput.walkDirection = -1.0f;
				} else if (touch1x < screenWidth / 2.0f && touch1y > screenHeight * 0.75f || touchCount > 1 && touch2x < screenWidth / 2.0f
						&& touch2y > screenHeight * 0.75f) {
					avatarInput.walkDirection = 1.0f;
				}
			}
		} else {
			if (Gdx.input.isKeyPressed(Input.Keys.A)) {
				avatarInput.walkDirection = -1;
			} else if (Gdx.input.isKeyPressed(Input.Keys.D)) {
				avatarInput.walkDirection = 1;
			} else {
				avatarInput.walkDirection = 0;
			}

			avatarInput.jump = Gdx.input.isKeyPressed(Input.Keys.SPACE);
		}
	}

	public final void updateTilesModification() {
		boolean modifyTile = false;
		int tileX = 0;
		int tileY = 0;

		if (Gdx.app.getType() == ApplicationType.Android || Gdx.app.getType() == ApplicationType.iOS) {
			if (Gdx.input.isTouched(0) && !Gdx.input.isTouched(1) && !InputAreas.isInputArea(Gdx.input.getX(), Gdx.input.getX())) {
				modifyTile = getTileCoordinatesUnderTouch(tmpvi);
				tileX = tmpvi.x;
				tileY = tmpvi.y;
			}
		} else {
			if (Gdx.input.justTouched() && !InputAreas.isInputArea(Gdx.input.getX(), Gdx.input.getX())) {
				modifyTile = getTileCoordinatesUnderMouse(tmpvi);
				tileX = tmpvi.x;
				tileY = tmpvi.y;
			}
		}

		switch (editTool) {
			case Add:
				if (modifyTile) {
					avatarView.getParentView().getTilemapCircle().getTile(tileX, tileY, (byte) 1);
				}
				break;

			case Remove:
				if (modifyTile) {
					avatarView.getParentView().getTilemapCircle().getTile(tileX, tileY, (byte) 0);
				}
				break;

			case MoveCamera:
				UniverseViewCamera.getInstance().updateMove();
				break;

			case None:
				break;
		}
	}

	private final boolean getTileCoordinatesUnderMouse(final Vector2I tileCoordinates) {
		final Camera cam = UniverseViewCamera.getInstance().getCamera();

		tmpv.x = Gdx.input.getX();
		tmpv.y = Gdx.input.getY();

		cam.unproject(tmpv);

		return avatarView.getParentView().getTilemapCircle().getTileCoordinatesFromPosition(tmpv.x, tmpv.y, tileCoordinates);
	}

	private final boolean getTileCoordinatesUnderTouch(final Vector2I tileCoordinates) {
		final Camera cam = UniverseViewCamera.getInstance().getCamera();

		tmpv.x = Gdx.input.getX();
		tmpv.y = Gdx.input.getY();

		cam.unproject(tmpv);

		return avatarView.getParentView().getTilemapCircle().getTileCoordinatesFromPosition(tmpv.x, tmpv.y, tileCoordinates);
	}
}
