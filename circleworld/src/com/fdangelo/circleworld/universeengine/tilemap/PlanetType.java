package com.fdangelo.circleworld.universeengine.tilemap;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class PlanetType
{
    public byte id;
    
    public byte mainTileId;
    
    public TextureRegion planetSprite;
    
    public Color backColorFrom = new Color();
    public Color backColorTo = new Color();
}

