package com.fdangelo.circleworld.universeengine.tilemap;

public interface ITilemapCircleListener
{
    void OnTilemapTileChanged(int tileX, int tileY);
    
    void OnTilemapParentChanged(float deltaTime);
}
