package com.fdangelo.circleworld.universeview.tilemap;

import com.fdangelo.circleworld.universeengine.tilemap.Planet;
import com.fdangelo.circleworld.universeview.UniverseView;

public class PlanetView extends TilemapCircleView {
	private UniverseView universeView;
	private Planet planet;

	public UniverseView getUniverseView() {
		return universeView;
	}

	public Planet getPlanet() {
		return planet;
	}

	public final void initPlanet(final Planet planet, final UniverseView universeView) {
		this.planet = planet;
		this.universeView = universeView;

		init(planet);
	}

	@Override
	public void recycle() {
		remove();

		universeView = null;
		planet = null;

		super.recycle();
	}
}
