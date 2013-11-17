package com.fdangelo.circleworld.universeengine.tilemap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.fdangelo.circleworld.universeengine.Thing;
import com.fdangelo.circleworld.universeengine.ThingPosition;
import com.fdangelo.circleworld.universeengine.ThingType;
import com.fdangelo.circleworld.universeengine.Universe;

public class Planet extends TilemapCircle
{
    private float gravity = 10.0f;
    
    private Universe universe;
    private short thingIndex;
    
    public short getThingIndex()
    {
        return thingIndex;
    }
    
    public float getGravity()
    {
        return gravity;
    }
    
    public void setGravity(float value)
    {
        this.gravity = value;
    }
    
    public PlanetType getPlanetType()
    {
        if (universe.GetThing(thingIndex).type == ThingType.Sun)
            return PlanetTypes.GetPlanetType((byte) (Math.abs(getSeed() % 2) + 4)); //suns!
        else
            return PlanetTypes.GetPlanetType((byte) (Math.abs(getSeed() % 4))); //planets!
    }
    
    @Override
    protected void UpdateTiles ()
    {
        //System.Random random = new System.Random(Seed);
        
        byte tileId = getPlanetType().mainTileId;
        
        for (int i = 0; i < tiles.length; i++)
        {
            //if (random.NextDouble() > 0.95f)
            //    tiles[i] = 0;
            //else
                tiles[i] = tileId;
        }        
    }
    
    public void InitPlanet(Universe universe, short thingIndex)
    {
        this.universe = universe;
        this.thingIndex = thingIndex;
        
        Thing thing = universe.GetThing(thingIndex);
        
        Init(thing.seed, GetPlanetHeightWithRadius(thing.radius));
        
        UpdatePlanetPosition();
    }
    
    @Override
    public void Recycle ()
    {
        super.Recycle ();
        
        universe = null;
        thingIndex = 0;
    }
    
    public void Update(float deltaTime)
    {
        UpdatePlanetPosition();
        
        if (listener != null)
            listener.OnTilemapParentChanged(deltaTime);
    }
    
    private void UpdatePlanetPosition()
    {
        ThingPosition thing = universe.GetThingPosition(thingIndex);
        
        positionX = thing.x;
        positionY = thing.y;
        rotation = thing.rotation;
    }
    
    static private short[] validHeights = new short[] { 8, 16, 32, 64, 128 };
    static private short[] validRadius;
    
    static public short GetClosestValidRadius(short radius)
    {
        InitValidRadius();
        
        for (int i = 0; i < validRadius.length; i++)
        {
            if (validRadius[i] > radius)
            {
                if (i == 0)
                    return validRadius[0];
                else
                    return validRadius[i - 1];
            }
        }
        
        return 0;
    }   
    

    static private void InitValidRadius()
    {
        if (validRadius == null)
        {
            validRadius = new short[validHeights.length];
            for (int i = 0; i < validHeights.length; i++)
                validRadius[i] = GetRadiusFromPlanetHeight(validHeights[i]);
            
            for (int i = 0; i < validHeights.length; i++)
                if (GetPlanetHeightWithRadius(validRadius[i]) != validHeights[i])
                	Gdx.app.error("Error", String.format("Invalid validRadius[] initialization, expected %d, got %d", validHeights[i], GetPlanetHeightWithRadius(validRadius[i])));
        }
    }
            
    static public short GetPlanetHeightWithRadius(short radius)
    {
        InitValidRadius();
            
        for (int i = 0; i < validRadius.length; i++)
            if (radius == validRadius[i])
                return validHeights[i];
        
        return 0;
    }
    
    static private short GetRadiusFromPlanetHeight(short height)
    {
        int width = (((int)((float)height * MathUtils.PI * 2.0f)) / 4) * 4;
        
        float height0 = (height - 1) * TILE_SIZE;
        float k = -((width / (MathUtils.PI * 2.0f))) / (1 - (width / (MathUtils.PI * 2.0f)));
        
        float r = (float) (height0 * Math.pow(k, (float) height));
        
        return (short) r;
    } 
}
