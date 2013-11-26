package com.fdangelo.circleworld.universeengine.tilemap;

public class TileHitInfo {
	public int hitTileX;
	public int hitTileY;

	public float hitDistance;
	public float hitNormalX;
	public float hitNormalY;
	public float hitPositionX;
	public float hitPositionY;

	public final void set(final TileHitInfo other) {
		hitTileX = other.hitTileX;
		hitTileY = other.hitTileY;
		hitDistance = other.hitDistance;
		hitNormalX = other.hitNormalX;
		hitNormalY = other.hitNormalY;
		hitPositionX = other.hitPositionX;
		hitPositionY = other.hitPositionY;
	}
}
