package com.fdangelo.circleworld.universeengine.tilemap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.fdangelo.circleworld.universeengine.Thing;
import com.fdangelo.circleworld.universeengine.ThingPosition;
import com.fdangelo.circleworld.universeengine.ThingType;
import com.fdangelo.circleworld.universeengine.Universe;

public class Planet extends TilemapCircle {
	private float gravity = 10.0f;

	private Universe universe;
	private short thingIndex;

	public final short getThingIndex() {
		return thingIndex;
	}

	public final float getGravity() {
		return gravity;
	}

	public final void setGravity(final float value) {
		gravity = value;
	}

	public PlanetType getPlanetType() {
		if (universe.getThing(thingIndex).type == ThingType.Sun) {
			return PlanetTypes.getPlanetType((byte) (Math.abs(getSeed() % 2) + 4)); // suns!
		} else {
			return PlanetTypes.getPlanetType((byte) (Math.abs(getSeed() % 4))); // planets!
		}
	}

	@Override
	protected void updateTiles() {
		// System.Random random = new System.Random(Seed);

		final byte tileId = getPlanetType().mainTileId;

		for (int i = 0; i < tiles.length; i++) {
			// if (random.NextDouble() > 0.95f)
			// tiles[i] = 0;
			// else
			tiles[i] = tileId;
		}
	}

	public final void initPlanet(final Universe universe, final short thingIndex) {
		this.universe = universe;
		this.thingIndex = thingIndex;

		final Thing thing = universe.getThing(thingIndex);

		init(thing.seed, getPlanetHeightWithRadius(thing.radius));

		updatePlanetPosition();
	}

	@Override
	public void recycle() {
		super.recycle();

		universe = null;
		thingIndex = 0;
	}

	public void update(final float deltaTime) {

		updatePlanetPosition();

		if (listener != null) {
			listener.onTilemapParentChanged(deltaTime);
		}
	}

	private void updatePlanetPosition() {
		final ThingPosition thing = universe.getThingPosition(thingIndex);

		positionX = thing.x;
		positionY = thing.y;
		rotation = thing.rotation;
	}

	static private short[] validHeights = new short[] { 8, 16, 32, 64, 128 };
	static private short[] validRadius;

	static public short getClosestValidRadius(final short radius) {
		initValidRadius();

		for (int i = 0; i < validRadius.length; i++) {
			if (validRadius[i] > radius) {
				if (i == 0) {
					return validRadius[0];
				} else {
					return validRadius[i - 1];
				}
			}
		}

		return 0;
	}

	@SuppressWarnings("boxing")
	static private void initValidRadius() {
		if (validRadius == null) {
			validRadius = new short[validHeights.length];
			for (int i = 0; i < validHeights.length; i++) {
				validRadius[i] = getRadiusFromPlanetHeight(validHeights[i]);
			}

			for (int i = 0; i < validHeights.length; i++) {
				if (getPlanetHeightWithRadius(validRadius[i]) != validHeights[i]) {
					Gdx.app.error("Error", String.format("Invalid validRadius[] initialization, expected %d, got %d", validHeights[i],
							getPlanetHeightWithRadius(validRadius[i])));
				}
			}
		}
	}

	static public short getPlanetHeightWithRadius(final short radius) {
		initValidRadius();

		for (int i = 0; i < validRadius.length; i++) {
			if (radius == validRadius[i]) {
				return validHeights[i];
			}
		}

		return 0;
	}

	static private short getRadiusFromPlanetHeight(final short height) {
		final int width = (((int) (height * MathUtils.PI * 2.0f)) / 4) * 4;

		final float height0 = (height - 1) * TILE_SIZE;
		final float k = -((width / (MathUtils.PI * 2.0f))) / (1 - (width / (MathUtils.PI * 2.0f)));

		final float r = (float) (height0 * Math.pow(k, height));

		return (short) r;
	}
}
