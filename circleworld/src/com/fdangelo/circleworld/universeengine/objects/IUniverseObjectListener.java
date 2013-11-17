package com.fdangelo.circleworld.universeengine.objects;

import com.fdangelo.circleworld.universeengine.tilemap.TilemapCircle;

public interface IUniverseObjectListener
{
    void OnUniverseObjectUpdated(float deltaTime);
    
    void OnParentChanged(TilemapCircle parent);
}


