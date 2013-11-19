package com.fdangelo.circleworld.universeengine.tilemap;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.fdangelo.circleworld.GameLogic;

public class PlanetTypes
{
    static private PlanetType[] planetTypes;
    
    static public PlanetType[] GetPlanetTypes()
    {
    	if (planetTypes == null)
    		InitPlanetTypes();
    	
        return planetTypes;
    }
    
    static public PlanetType GetPlanetType(byte id)
    {
        return GetPlanetTypes()[id];
    }
        
    static public void InitPlanetTypes()
    {
    	AssetManager assetManager = GameLogic.Instace.assetManager;
    	
    	TextureAtlas atlas = assetManager.get("atlas/planets.atlas", TextureAtlas.class);
        planetTypes = new PlanetType[256];
        
        for (int i = 0; i < planetTypes.length; i++)
        {
            planetTypes[i] = new PlanetType();
            planetTypes[i].id = (byte) i;
        }
        
        planetTypes[0].planetSprite = atlas.findRegion("planet-grass");
        planetTypes[0].mainTileId = 1;
        planetTypes[0].backColorFrom = new Color(60, 179, 113, 255);
        planetTypes[0].backColorTo = new Color(60, 179, 113, 0);
        
        planetTypes[1].planetSprite = atlas.findRegion("planet-sand");
        planetTypes[1].mainTileId = 2;
        planetTypes[1].backColorFrom = new Color(238, 221, 130, 255);
        planetTypes[1].backColorTo = new Color(238, 221, 130, 0);
        
        planetTypes[2].planetSprite = atlas.findRegion("planet-snow");
        planetTypes[2].mainTileId = 3;
        planetTypes[2].backColorFrom = new Color(135, 206, 250, 255);
        planetTypes[2].backColorTo = new Color(135, 206, 250, 0);
        
        planetTypes[3].planetSprite = atlas.findRegion("planet-stone");
        planetTypes[3].mainTileId = 4;
        planetTypes[3].backColorFrom = new Color(153, 50, 204, 255);
        planetTypes[3].backColorTo = new Color(153, 50, 204, 0);
        
        planetTypes[4].planetSprite = atlas.findRegion("sun-1");
        planetTypes[4].mainTileId = 5;
        planetTypes[4].backColorFrom.set(Color.YELLOW);
        planetTypes[4].backColorTo.set(Color.YELLOW);
        planetTypes[4].backColorTo.a = 0;
        
        planetTypes[5].planetSprite = atlas.findRegion("sun-2");
        planetTypes[5].mainTileId = 6;
        planetTypes[5].backColorFrom.set(Color.RED);
        planetTypes[5].backColorTo.set(Color.RED);
        planetTypes[5].backColorTo.a = 0;
    }
}
