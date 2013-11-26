package com.fdangelo.circleworld.universeengine.objects;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.fdangelo.circleworld.universeengine.tilemap.Planet;
import com.fdangelo.circleworld.universeengine.tilemap.TileHitFlags;
import com.fdangelo.circleworld.utils.Mathf;
import com.fdangelo.circleworld.utils.Vector2I;

public class Avatar extends UniverseObject {
	public AvatarInput input = new AvatarInput();

	private final float jumpSpeed = 7.0f;
	private final float walkSpeedMax = 3.0f;
	private final float walkAcceleration = 10.0f;
	private final float walkFriction = 10.0f;

	private Ship onShip;

	@Override
	protected void onUpdate(final float deltaTime) {
		if (onShip == null) {
			if (canWalk()) {
				if (input.walkDirection != 0) {
					velocityX += input.walkDirection * walkAcceleration * deltaTime;
				} else {
					velocityX -= Mathf.Sign(velocityX) * MathUtils.clamp(walkFriction * deltaTime, 0, Math.abs(velocityX));
				}

				velocityX = MathUtils.clamp(velocityX, -walkSpeedMax, walkSpeedMax);
			}

			if (input.jump && canJump()) {
				velocityY = jumpSpeed;
			}
		} else {
			velocityX = 0;
			velocityY = 0;
			positionX = onShip.getPositionX();
			positionY = onShip.getPositionY();
		}

		input.reset();
	}

	public final boolean canWalk() {
		return true;
	}

	public final boolean canJump() {
		return (hitFlags & TileHitFlags.Down) != 0;
	}

	public final void boardShip(final Ship ship) {
		onShip = ship;
		scale = 1.0f;

		setParent(null, FollowParentParameters.None, ship.getPositionX(), ship.getPositionY(), rotation);

		setVisible(false);
	}

	static private Vector2I tmpvi = new Vector2I();

	public final void travelToPlanet(final Planet planet) {
		if (onShip != null) {
			Vector2I landTile = tmpvi;

			// Set position closest to the ship
			planet.getTileCoordinatesFromPosition(onShip.getPositionX(), onShip.getPositionY(), landTile);

			final int landTileX = landTile.x;
			int landTileY = landTile.y;
			landTile = null;

			landTileY = planet.getHeight();

			final Vector2 tilePositionOnPlanet = planet.getPositionFromTileCoordinate(landTileX, landTileY);

			setParent(planet, FollowParentParameters.Default, tilePositionOnPlanet.x, tilePositionOnPlanet.y, 0.0f);

			// Leave ship
			onShip = null;
		} else {
			final Vector2 tilePositionOnPlanet = planet.getPositionFromTileCoordinate(0, planet.getHeight());

			setParent(planet, FollowParentParameters.Default, tilePositionOnPlanet.x, tilePositionOnPlanet.y, 0.0f);
		}

		setVisible(true);
	}
}
