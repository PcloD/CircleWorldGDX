package com.fdangelo.circleworld.universeengine;

import java.util.ArrayList;

import com.badlogic.gdx.utils.IntMap;
import com.fdangelo.circleworld.universeengine.objects.Avatar;
import com.fdangelo.circleworld.universeengine.objects.Ship;
import com.fdangelo.circleworld.universeengine.tilemap.Planet;

public class UniverseFactory {
	private final IntMap<ArrayList<Planet>> planetsPool = new IntMap<ArrayList<Planet>>();

	public final Avatar getAvatar() {
		return new Avatar();
	}

	public final Ship getShip() {
		return new Ship();
	}

	public final Planet getPlanet(final int height) {
		Planet planet = null;

		ArrayList<Planet> list;

		if (planetsPool.containsKey(height)) {
			list = planetsPool.get(height);
		} else {
			list = null;
		}

		if (list != null && list.size() > 0) {
			planet = list.get(list.size() - 1);

			list.remove(list.size() - 1);
		} else {
			planet = new Planet();
		}

		return planet;
	}

	public final void returnPlanet(final Planet planet) {
		final int height = planet.getHeight();

		planet.recycle();

		ArrayList<Planet> list;

		if (planetsPool.containsKey(height)) {
			list = planetsPool.get(height);
		} else {
			list = new ArrayList<Planet>();
			planetsPool.put(height, list);
		}

		list.add(planet);
	}
}
