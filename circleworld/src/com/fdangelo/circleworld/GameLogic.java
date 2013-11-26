package com.fdangelo.circleworld;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Disposable;
import com.fdangelo.circleworld.universeengine.objects.Avatar;
import com.fdangelo.circleworld.universeengine.tilemap.Planet;
import com.fdangelo.circleworld.universeview.FollowCameraParameters;
import com.fdangelo.circleworld.universeview.UniverseView;
import com.fdangelo.circleworld.universeview.UniverseViewCamera;
import com.fdangelo.circleworld.universeview.tilemap.PlanetView;
import com.fdangelo.circleworld.utils.Mathf;

public class GameLogic implements Disposable {
	static public GameLogic instace;

	public AssetManager assetManager;

	private UniverseViewCamera universeCamera;
	private UniverseView universeView;

	private int universeSeed;

	private GameLogicState state = GameLogicState.PlayingAvatar;
	private float stateTime;

	private float universeTimeMultiplier = 1.0f;

	public UniverseView getUniverseView() {
		return universeView;
	}

	public GameLogicState getState() {
		return state;
	}

	public GameLogic() {
		instace = this;

		assetManager = new AssetManager();

		assetManager.load("atlas/gui.atlas", TextureAtlas.class);
		assetManager.load("atlas/planets.atlas", TextureAtlas.class);
		assetManager.load("atlas/player1.atlas", TextureAtlas.class);
		assetManager.load("atlas/ships.atlas", TextureAtlas.class);
		assetManager.load("atlas/tilemap.atlas", TextureAtlas.class);

		switchState(GameLogicState.Loading);
	}

	public void switchState(final GameLogicState toState) {
		state = toState;
		stateTime = 0.0f;

		switch (toState) {
			case Loading:
				// Do nothing
				break;

			case PlayingAvatar:
				universeCamera.followObject(getUniverseView().avatarView, FollowCameraParameters.FollowRotation | FollowCameraParameters.FollowScale, true);
				break;

			case PlayingShip:
				universeCamera.followObject(getUniverseView().shipView, FollowCameraParameters.None, true);
				break;

			case Travelling:
				// Do nothing
				break;
		}
	}

	public void updateAndRender(float deltaTime) {
		if (deltaTime > 0.1f) {
			deltaTime = 0.1f;
		}

		stateTime += deltaTime;

		switch (state) {
			case Loading:
				if (assetManager.update()) {
					// Loading complete!

					// Create layers
					universeView = new UniverseView();
					universeView.init(universeSeed);

					universeCamera = new UniverseViewCamera(getUniverseView().getCamera());

					switchState(GameLogicState.PlayingAvatar);
				}
				break;

			case PlayingAvatar:
				universeTimeMultiplier = Mathf.lerp(universeTimeMultiplier, 1.0f, 0.25f);
				getUniverseView().updateUniverse(deltaTime * universeTimeMultiplier);
				universeCamera.update(deltaTime);
				break;

			case PlayingShip:
				universeTimeMultiplier = Mathf.lerp(universeTimeMultiplier, 1.0f, 0.25f);
				getUniverseView().updateUniverse(deltaTime * universeTimeMultiplier);
				universeCamera.update(deltaTime);
				break;

			case Travelling:
				universeTimeMultiplier = Mathf.lerp(universeTimeMultiplier, 0.1f, 0.25f);
				if (stateTime > 1.25f) {
					switchState(GameLogicState.PlayingAvatar);
				}
				getUniverseView().updateUniverse(deltaTime * universeTimeMultiplier);
				universeCamera.update(deltaTime);
				break;
		}

		if (getUniverseView() != null) {
			getUniverseView().updateLayers(deltaTime);
		}
	}

	public void travelToPlanet(final PlanetView targetPlanetView) {
		((Avatar) getUniverseView().avatarView.getUniverseObject()).travelToPlanet((Planet) targetPlanetView.getTilemapCircle());

		// Force update to update AvatarView position
		getUniverseView().updateUniverse(0);

		switchState(GameLogicState.Travelling);
	}

	public void playerBoardShip() {
		getUniverseView().getUniverse().getShip().beamUpAvatar(getUniverseView().getUniverse().getAvatar());

		switchState(GameLogicState.PlayingShip);
	}

	public void playerLeaveShip(final Planet planet) {
		getUniverseView().getUniverse().getShip().beamDownAvatar(getUniverseView().getUniverse().getAvatar(), planet);

		switchState(GameLogicState.PlayingAvatar);
	}

	@Override
	public void dispose() {
		if (getUniverseView() != null) {
			getUniverseView().dispose();
		}

		assetManager.dispose();
	}

	public void resize(final int width, final int height) {

		if (getUniverseView() != null) {
			getUniverseView().resize(width, height);
		}

	}
}
