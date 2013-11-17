package com.fdangelo.circleworld.universeengine.tilemap;

public class TileHitInfo
{
    public int hitTileX;
    public int hitTileY;

    public float hitDistance;
    public float hitNormalX;
    public float hitNormalY;
    public float hitPositionX;
    public float hitPositionY;
    
    public void set(TileHitInfo other)
    {
    	this.hitTileX = other.hitTileX;
    	this.hitTileY = other.hitTileY;
    	this.hitDistance = other.hitDistance;
    	this.hitNormalX = other.hitNormalX;
    	this.hitNormalY = other.hitNormalY;
    	this.hitPositionX = other.hitPositionX;
    	this.hitPositionY = other.hitPositionY;
    }
}
