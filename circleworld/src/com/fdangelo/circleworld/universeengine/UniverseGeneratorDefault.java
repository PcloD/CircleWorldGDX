package com.fdangelo.circleworld.universeengine;

import com.fdangelo.circleworld.universeengine.tilemap.Planet;

public class UniverseGeneratorDefault extends UniverseGenerator
{
	@Override
    protected void AddGalaxy()
    {
        int galaxyOrbits = random.nextInt(10) + 10;

        short galaxySafeRadius = (short) (things[currentThing].safeRadius / 2);
        short solarSystemRadius = (short) (galaxySafeRadius / (galaxyOrbits * 2));

        for (int i = 0; i < galaxyOrbits; i++)
        {
            short solarSystemDistance = (short) ((galaxySafeRadius * i) / galaxyOrbits);
            
            int minSolarSystems = (Math.max(i * 5, 1) + 1) / 2;
            int maxSolarSystems = Math.max(i * 5, 1) + 1;

            int solarSystems = random.nextInt(maxSolarSystems - minSolarSystems) + minSolarSystems;

            short solarSystemOrbitalPeriod = (short) (random.nextInt(120) + 120);
            if (random.nextInt(2) == 0)
                solarSystemOrbitalPeriod = (short) -solarSystemOrbitalPeriod;

            for (int j = 0; j < solarSystems; j++)
            {
                short solarSystemAngle = (short) ((36000 * j) / solarSystems);

                PushThing(ThingType.SolarSystem, solarSystemAngle, solarSystemDistance, (short) 0, solarSystemOrbitalPeriod, (short) 0, solarSystemRadius, 0);
                {
                    int suns = random.nextInt(3) + 1;

                    short solarSystemSafeRadius = things[currentThing].safeRadius;

                    short minRadius = (short) ((solarSystemRadius / 8) / 2);
                    short maxRadius = (short) (solarSystemRadius / 8); 
                    
                    short sunRadius = (short) (random.nextInt(maxRadius - minRadius) + minRadius);

                    if (suns == 1)
                    {
                        PushThing(ThingType.Sun, (short) 0, (short) 0, (short) 0, (short) 0, Planet.GetClosestValidRadius(sunRadius), (short) 0, random.nextInt());
                        PopThing();
                    }
                    else
                    {
                        short sunDistance = (short) (sunRadius * 4 / 3);
                        short sunOrbitalPerdiod = (short) (random.nextInt(30) + 30);
                        if (random.nextInt(2) == 0)
                            sunOrbitalPerdiod = (short) -sunOrbitalPerdiod;

                        for (int k = 0; k < suns; k++)
                        {
                            short sunAngle = (short) ((36000 * k) / suns);

                            PushThing(ThingType.Sun, sunAngle, sunDistance, (short) 0, sunOrbitalPerdiod, Planet.GetClosestValidRadius(sunRadius), (short) 0, random.nextInt());
                            PopThing();
                        }
                    }

                    int planetsOrbits = random.nextInt(7) + 1;

                    short planetSafeRadius = (short) ((solarSystemSafeRadius - sunRadius * 6) / (planetsOrbits * 2));

                    for (int l = 0; l < planetsOrbits; l++)
                    {
                        short planetDistance = (short) (sunRadius * 6 + ((solarSystemSafeRadius - sunRadius * 6) * l) / planetsOrbits);

                        short planetAngle = (short) random.nextInt(36000);

                        short planetRotationPeriod = (short) (random.nextInt(30) + 30);
                        if (random.nextInt(2) == 0)
                            planetRotationPeriod = (short) -planetRotationPeriod;

                        short planetOrbitationPeriod = (short) (random.nextInt(30) + 30);
                        if (random.nextInt(2) == 0)
                            planetOrbitationPeriod = (short) -planetOrbitationPeriod;
                        
                        short minPlanetRadius = (short) (planetSafeRadius / 16);
                        short maxPlanetRadius = (short) (planetSafeRadius / 9);

                        short planetRadius = (short) (random.nextInt(maxPlanetRadius - minPlanetRadius) + minPlanetRadius);

                        PushThing(ThingType.Planet, planetAngle, planetDistance, planetRotationPeriod, planetOrbitationPeriod, Planet.GetClosestValidRadius(planetRadius), planetSafeRadius, random.nextInt());
                        PopThing();
                    }

                }
                PopThing();
            }
        }
    }
}
