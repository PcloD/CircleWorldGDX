package com.fdangelo.circleworld.universeengine;

import java.util.ArrayList;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ShortArray;
import com.fdangelo.circleworld.universeengine.objects.Avatar;
import com.fdangelo.circleworld.universeengine.objects.FollowParentParameters;
import com.fdangelo.circleworld.universeengine.objects.Ship;
import com.fdangelo.circleworld.universeengine.objects.UniverseObject;
import com.fdangelo.circleworld.universeengine.tilemap.Planet;
import com.fdangelo.circleworld.universeengine.utils.UEProfiler;
import com.fdangelo.circleworld.utils.Mathf;

public class Universe {
	// Max number of things
	public static final int MAX_THINGS = 8192;

	// 360 degress in radians
	private static final float TWO_PI = MathUtils.PI * 2.0f;

	// (degrees to radians / 100)
	private static final float DEG_TO_RAD_OVER_100 = 0.000174532925f;

	private static final float POSITIONS_TIME_SCALE = 0.01f;

	private final Thing[] things;
	private final ThingPosition[] thingsPositions;
	private short thingsAmount;

	private final short[] thingsToRender = new short[MAX_THINGS];
	private short thingsToRenderAmount;

	private short startingPlanet;

	private final ArrayList<Planet> planets = new ArrayList<Planet>();

	private final ArrayList<UniverseObject> tilemapObjects = new ArrayList<UniverseObject>();

	private final UniverseFactory universeFactory = new UniverseFactory();

	private float time;

	private Avatar avatar;

	private Ship ship;

	private IUniverseListener listener;

	public final short getStartingPlanet() {
		return startingPlanet;
	}

	public final Avatar getAvatar() {
		return avatar;
	}

	public final Ship getShip() {
		return ship;
	}

	public final Thing[] getThings() {
		return things;
	}

	public final ThingPosition[] getThingsPositions() {
		return thingsPositions;
	}

	public final short[] getThingsToRender() {
		return thingsToRender;
	}

	public final short getThingsToRenderAmount() {
		return thingsToRenderAmount;
	}

	public final IUniverseListener getListener() {
		return listener;
	}

	public final void setListener(final IUniverseListener value) {
		listener = value;
	}

	public Universe() {
		things = new Thing[MAX_THINGS];
		for (int i = 0; i < MAX_THINGS; i++) {
			things[i] = new Thing();
		}

		thingsPositions = new ThingPosition[MAX_THINGS];
		for (int i = 0; i < MAX_THINGS; i++) {
			thingsPositions[i] = new ThingPosition();
		}
	}

	public final void init(final int seed, final IUniverseListener listener) {
		this.listener = listener;

		time = 0.0f;

		thingsAmount = new UniverseGeneratorDefault().generate(seed, things);

		updateThingsToRender();

		startingPlanet = thingsToRender[1];

		updateUniverse(0);

		addAvatar();

		addShip();
	}

	private final void updateThingsToRender() {
		thingsToRenderAmount = 0;
		for (int i = 0; i < thingsAmount; i++) {
			final short type = things[i].type;

			if (type == ThingType.Sun || type == ThingType.Planet || type == ThingType.Moon) {
				thingsToRender[thingsToRenderAmount++] = (short) i;
			}
		}
	}

	public final void updateUniverse(final float deltaTime) {
		time += deltaTime;

		UEProfiler.BeginSample("Universe.UpdatePositions");
		updatePositions(time);
		UEProfiler.EndSample();

		for (int i = 0; i < planets.size(); i++) {
			planets.get(i).update(deltaTime);
		}

		for (int i = 0; i < tilemapObjects.size(); i++) {
			tilemapObjects.get(i).update(deltaTime);
		}
	}

	private final void updatePositions(float time) {
		time *= POSITIONS_TIME_SCALE;

		for (int index = 1; index < thingsAmount; index++) {
			final Thing thing = things[index];

			final float parentX = thingsPositions[thing.parent].x;
			final float parentY = thingsPositions[thing.parent].y;

			float angle = thing.angle * DEG_TO_RAD_OVER_100;
			final float distance = thing.distance;

			float normalizedOrbitalPeriod = time * thing.orbitalPeriodInv;
			normalizedOrbitalPeriod -= (int) normalizedOrbitalPeriod;

			float normalizedRotationPeriod = time * thing.rotationPeriodInv;
			normalizedRotationPeriod -= (int) normalizedRotationPeriod;

			angle += TWO_PI * normalizedOrbitalPeriod;

			thingsPositions[index].x = parentX + (Mathf.cos(angle)) * distance;
			thingsPositions[index].y = parentY + (Mathf.sin(angle)) * distance;
			thingsPositions[index].rotation = normalizedRotationPeriod * TWO_PI;
			thingsPositions[index].radius = thing.radius;
		}
	}

	public final Thing getThing(final short thingIndex) {
		return things[thingIndex];
	}

	public final ThingPosition getThingPosition(final short thingIndex) {
		return thingsPositions[thingIndex];
	}

	public final Planet getPlanet(final short thingIndex) {
		for (int i = 0; i < planets.size(); i++) {
			if (planets.get(i).getThingIndex() == thingIndex) {
				return planets.get(i);
			}
		}

		if (things[thingIndex].type != ThingType.Sun && things[thingIndex].type != ThingType.Planet && things[thingIndex].type != ThingType.Moon) {
			return null;
		}

		final Planet planet = universeFactory.getPlanet(Planet.getPlanetHeightWithRadius(things[thingIndex].radius));

		planet.initPlanet(this, thingIndex);

		planets.add(planet);

		return planet;
	}

	public final void returnPlanet(final Planet planet) {
		if (planets.remove(planet)) {
			if (listener != null) {
				listener.onPlanetReturned(planet);
			}

			universeFactory.returnPlanet(planet);
		}
	}

	private final void addAvatar() {
		final Planet planet = getPlanet(startingPlanet);

		avatar = universeFactory.getAvatar();

		final Vector2 defaultPosition = planet.getPositionFromTileCoordinate(0, planet.getHeight());

		avatar.init(0.75f, 1.05f, planet, FollowParentParameters.Default, defaultPosition.x, defaultPosition.y, 0.0f);

		addUniverseObject(avatar);
	}

	private final void addShip() {
		ship = universeFactory.getShip();

		final Vector2 defaultPosition = avatar.getParent().getPositionFromTileCoordinate(0, avatar.getParent().getHeight() + 5);

		ship.init(10.0f, 5.0f, avatar.getParent(), FollowParentParameters.None, defaultPosition.x, defaultPosition.y, MathUtils.PI * 0.5f);

		addUniverseObject(ship);
	}

	public final void addUniverseObject(final UniverseObject universeObject) {
		tilemapObjects.add(universeObject);

		if (listener != null) {
			listener.onUniverseObjectAdded(universeObject);
		}
	}

	public final int findClosestRenderedThing(final float worldPosX, final float worldPosY, final float searchRadius) {

		short closestThingIndex = Short.MAX_VALUE;
		float closestThingDistance = Float.MAX_VALUE;

		for (short i = 0; i < thingsToRenderAmount; i++) {
			final ThingPosition thingPosition = thingsPositions[thingsToRender[i]];

			final float dx = worldPosX - thingPosition.x;
			final float dy = worldPosY - thingPosition.y;

			final float distance = (dx * dx + dy * dy);

			if (distance < (thingPosition.radius + searchRadius) * (thingPosition.radius + searchRadius) && distance < closestThingDistance) {
				closestThingIndex = thingsToRender[i];
				closestThingDistance = distance;
			}
		}

		if (closestThingIndex != Short.MAX_VALUE) {
			return closestThingIndex;
		} else {
			return -1;
		}
	}

	public final ShortArray findClosestRenderedThings(final float worldPosX, final float worldPosY, final float searchRadius, ShortArray toReturn) {

		if (toReturn == null) {
			toReturn = new ShortArray();
		} else {
			toReturn.clear();
		}

		for (short i = 0; i < thingsToRenderAmount; i++) {
			final ThingPosition thingPosition = thingsPositions[thingsToRender[i]];

			final float dx = worldPosX - thingPosition.x;
			final float dy = worldPosY - thingPosition.y;

			final float distance = (dx * dx + dy * dy);

			if (distance < (thingPosition.radius + searchRadius) * (thingPosition.radius + searchRadius)) {
				toReturn.add(thingsToRender[i]);
			}
		}

		// TODO: Sort the array based on world pos distance!!!

		// ThingDistanceComparerReferenceX = worldPosX;
		// ThingDistanceComparerReferenceY = worldPosY;
		// Arrays.sort(toReturn.items, thingDistanceComparer);

		return toReturn;
	}

	// private float ThingDistanceComparerReferenceX;
	// private float ThingDistanceComparerReferenceY;

	/*
	 * private int ThingDistanceComparer(ushort index1, ushort index2) { Vector2
	 * p1 = new Vector2(thingsPositions[index1].x, thingsPositions[index1].y) -
	 * ThingDistanceComparerReference; Vector2 p2 = new
	 * Vector2(thingsPositions[index2].x, thingsPositions[index2].y) -
	 * ThingDistanceComparerReference; float diff = p1.sqrMagnitude -
	 * p2.sqrMagnitude; if (diff < 0) return -1; else if (diff > 0) return 1;
	 * else return 0; }
	 */
}
