package com.fdangelo.circleworld.universeview;

import java.util.ArrayList;

import com.badlogic.gdx.utils.IntMap;
import com.fdangelo.circleworld.universeview.objects.AvatarView;
import com.fdangelo.circleworld.universeview.objects.ShipView;
import com.fdangelo.circleworld.universeview.tilemap.PlanetView;

public class UniverseViewFactory {
	private final IntMap<ArrayList<PlanetView>> planetsPool = new IntMap<ArrayList<PlanetView>>();

	public final AvatarView getAvatar() {
		return new AvatarView();
	}

	public final ShipView getShip() {
		return new ShipView();
	}

	public final PlanetView getPlanet(final int height) {
		PlanetView planet = null;

		ArrayList<PlanetView> list;

		if (planetsPool.containsKey(height)) {
			list = planetsPool.get(height);
		} else {
			list = null;
		}

		if (list != null && list.size() > 0) {
			planet = list.get(list.size() - 1);

			list.remove(list.size() - 1);
		} else {
			planet = new PlanetView();
		}

		return planet;
	}

	public final void returnPlanet(final PlanetView planet) {
		final int height = planet.getTilemapCircle().getHeight();

		planet.recycle();

		ArrayList<PlanetView> list;

		if (planetsPool.containsKey(height)) {
			list = planetsPool.get(height);
		} else {
			list = new ArrayList<PlanetView>();
			planetsPool.put(height, list);
		}

		list.add(planet);
	}
}
