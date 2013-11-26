package com.fdangelo.circleworld.universeengine;

import com.fdangelo.circleworld.universeengine.tilemap.Planet;

public class UniverseGeneratorDefault extends UniverseGenerator {

	@Override
	protected void addGalaxy() {
		final int galaxyOrbits = random.nextInt(10) + 10;

		final short galaxySafeRadius = (short) (things[currentThing].safeRadius / 2);
		final short solarSystemRadius = (short) (galaxySafeRadius / (galaxyOrbits * 2));

		for (int i = 0; i < galaxyOrbits; i++) {
			final short solarSystemDistance = (short) ((galaxySafeRadius * i) / galaxyOrbits);

			final int minSolarSystems = (Math.max(i * 5, 1) + 1) / 2;
			final int maxSolarSystems = Math.max(i * 5, 1) + 1;

			final int solarSystems = random.nextInt(maxSolarSystems - minSolarSystems) + minSolarSystems;

			short solarSystemOrbitalPeriod = (short) (random.nextInt(120) + 120);
			if (random.nextInt(2) == 0) {
				solarSystemOrbitalPeriod = (short) -solarSystemOrbitalPeriod;
			}

			for (int j = 0; j < solarSystems; j++) {
				final short solarSystemAngle = (short) ((36000 * j) / solarSystems);

				pushThing(ThingType.SolarSystem, solarSystemAngle, solarSystemDistance, (short) 0, solarSystemOrbitalPeriod, (short) 0, solarSystemRadius, 0);
				{
					final int suns = random.nextInt(3) + 1;

					final short solarSystemSafeRadius = things[currentThing].safeRadius;

					final short minRadius = (short) ((solarSystemRadius / 8) / 2);
					final short maxRadius = (short) (solarSystemRadius / 8);

					final short sunRadius = (short) (random.nextInt(maxRadius - minRadius) + minRadius);

					if (suns == 1) {
						pushThing(ThingType.Sun, (short) 0, (short) 0, (short) 0, (short) 0, Planet.getClosestValidRadius(sunRadius), (short) 0,
								random.nextInt());
						popThing();
					} else {
						final short sunDistance = (short) (sunRadius * 4 / 3);
						short sunOrbitalPerdiod = (short) (random.nextInt(30) + 30);
						if (random.nextInt(2) == 0) {
							sunOrbitalPerdiod = (short) -sunOrbitalPerdiod;
						}

						for (int k = 0; k < suns; k++) {
							final short sunAngle = (short) ((36000 * k) / suns);

							pushThing(ThingType.Sun, sunAngle, sunDistance, (short) 0, sunOrbitalPerdiod, Planet.getClosestValidRadius(sunRadius), (short) 0,
									random.nextInt());
							popThing();
						}
					}

					final int planetsOrbits = random.nextInt(7) + 1;

					final short planetSafeRadius = (short) ((solarSystemSafeRadius - sunRadius * 6) / (planetsOrbits * 2));

					for (int l = 0; l < planetsOrbits; l++) {
						final short planetDistance = (short) (sunRadius * 6 + ((solarSystemSafeRadius - sunRadius * 6) * l) / planetsOrbits);

						final short planetAngle = (short) random.nextInt(36000);

						short planetRotationPeriod = (short) (random.nextInt(30) + 30);
						if (random.nextInt(2) == 0) {
							planetRotationPeriod = (short) -planetRotationPeriod;
						}

						short planetOrbitationPeriod = (short) (random.nextInt(30) + 30);
						if (random.nextInt(2) == 0) {
							planetOrbitationPeriod = (short) -planetOrbitationPeriod;
						}

						final short minPlanetRadius = (short) (planetSafeRadius / 16);
						final short maxPlanetRadius = (short) (planetSafeRadius / 9);

						short planetRadius;
						if (maxPlanetRadius - minPlanetRadius > 0) {
							planetRadius = (short) (random.nextInt(maxPlanetRadius - minPlanetRadius) + minPlanetRadius);
						} else {
							planetRadius = minPlanetRadius;
						}

						pushThing(ThingType.Planet, planetAngle, planetDistance, planetRotationPeriod, planetOrbitationPeriod,
								Planet.getClosestValidRadius(planetRadius), planetSafeRadius, random.nextInt());
						popThing();
					}

				}
				popThing();
			}
		}
	}
}
