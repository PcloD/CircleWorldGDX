package com.fdangelo.circleworld.universeengine;

import com.fdangelo.circleworld.universeengine.objects.UniverseObject;
import com.fdangelo.circleworld.universeengine.tilemap.Planet;

public interface IUniverseListener
{
    void OnUniverseObjectAdded(UniverseObject universeObject);
    
    void OnPlanetReturned(Planet planet);
}


