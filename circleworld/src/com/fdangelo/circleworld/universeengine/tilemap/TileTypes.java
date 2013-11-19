package com.fdangelo.circleworld.universeengine.tilemap;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.fdangelo.circleworld.GameLogic;

public class TileTypes
{
    static private TileType[] tileTypes;
    
    static public TileType[] GetTileTypes()
    {
    	if (tileTypes == null)
    		InitTileTypes();
    	
        return tileTypes;
    }
    
    static public TileType GetTileType(byte id)
    {
        return GetTileTypes()[id];
    }
    
    static private TextureAtlas atlas;
    
    static public void InitTileTypes()
    {
    	AssetManager assetManager = GameLogic.Instace.assetManager;
    	
    	atlas = assetManager.get("atlas/tilemap.atlas", TextureAtlas.class);
        
        tileTypes = new TileType[256];
        for (int i = 0 ; i < tileTypes.length; i++)
        	tileTypes[i] = new TileType();
        
        tileTypes[1].center = GetTileSubtypeUV("grassCenter");
        tileTypes[1].top = GetTileSubtypeUV("grassMid");
        
        tileTypes[2].center = GetTileSubtypeUV("sandCenter");
        tileTypes[2].top = GetTileSubtypeUV("sandMid");

        tileTypes[3].center = GetTileSubtypeUV("snowCenter");
        tileTypes[3].top = GetTileSubtypeUV("snowMid");

        tileTypes[4].center = GetTileSubtypeUV("stoneCenter");
        tileTypes[4].top = GetTileSubtypeUV("stoneMid");
        
        tileTypes[5].center = GetTileSubtypeUV("sun1Center");
        tileTypes[5].top = GetTileSubtypeUV("sun1Mid");
        
        tileTypes[6].center = GetTileSubtypeUV("sun2Center");
        tileTypes[6].top = GetTileSubtypeUV("sun2Mid");
    }
    
    static private TileSubtype GetTileSubtypeUV(String id)
    {
        TileSubtype subtype = new TileSubtype();
        
        AtlasRegion region = atlas.findRegion(id);
        
        subtype.uvFromX = region.getU();
        subtype.uvToX = region.getU2();
        subtype.uvFromY = region.getV();
        subtype.uvToY = region.getV2();
        
        return subtype;
    }
}
