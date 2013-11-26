package com.fdangelo.circleworld.universeengine.tilemap;

public interface ITilemapCircleListener {
	void onTilemapTileChanged(int tileX, int tileY);

	void onTilemapParentChanged(float deltaTime);
}
