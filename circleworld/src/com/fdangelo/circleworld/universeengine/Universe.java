package com.fdangelo.circleworld.universeengine;

import java.util.ArrayList;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ShortArray;
import com.fdangelo.circleworld.universeengine.objects.Avatar;
import com.fdangelo.circleworld.universeengine.objects.FollowParentParameters;
import com.fdangelo.circleworld.universeengine.objects.Ship;
import com.fdangelo.circleworld.universeengine.objects.UniverseObject;
import com.fdangelo.circleworld.universeengine.tilemap.Planet;
import com.fdangelo.circleworld.universeengine.utils.UEProfiler;
import com.fdangelo.circleworld.utils.Mathf;

public class Universe
{
    public static final int MAX_THINGS = 8192;

    //private static final float HALF_PI = MathUtils.PI * 0.5f; //90 degress in radians
    private static final float TWO_PI = MathUtils.PI * 2.0f; //360 degress in radians
    //private static final float INV_TWO_PI = 1.0f / (MathUtils.PI * 2.0f); 
    private static final float DEG_TO_RAD_OVER_100 = 0.000174532925f; //(degrees to radians / 100)
        
    private static final float POSITIONS_TIME_SCALE = 1.0f; //0.01f;

    private Thing[] things;
    private ThingPosition[] thingsPositions;
    private short thingsAmount;

    private short[] thingsToRender = new short[MAX_THINGS];
    private short thingsToRenderAmount;
    
    private short startingPlanet;
    
    private ArrayList<Planet> planets = new ArrayList<Planet>();
    
    private ArrayList<UniverseObject> tilemapObjects = new ArrayList<UniverseObject>();
    
    private UniverseFactory universeFactory = new UniverseFactory();
    
    private float time;
    
    private Avatar avatar;
    
    private Ship ship;
    
    private IUniverseListener listener;
    
    public short getStartingPlanet()
    {
        return startingPlanet;
    }
    
    public Avatar getAvatar()
    {
        return avatar;
    }
    
    public Ship getShip()
    {
        return ship;
    }
    
    public Thing[] getThings()
    {
        return things;
    }

    public ThingPosition[] getThingsPositions()
    {
        return thingsPositions;
    }
    
    public short[] getThingsToRender()
    {
        return thingsToRender;
    }
    
    public short getThingsToRenderAmount()
    {
        return thingsToRenderAmount;
    }
    
    public IUniverseListener getListener()
    {
        return listener;
    }
    
    public void setListener(IUniverseListener value)
    {
        listener = value;
    }
    
    public Universe()
    {
    	things = new Thing[MAX_THINGS];
    	for (int i = 0; i < MAX_THINGS; i++)
    		things[i] = new Thing();
    	
    	thingsPositions = new ThingPosition[MAX_THINGS];
    	for (int i = 0; i < MAX_THINGS; i++)
    		thingsPositions[i] = new ThingPosition();
    }
    
    public void Init(int seed, IUniverseListener listener)
    {
        this.listener = listener;
        
        time = 0.0f;
        
        thingsAmount = new UniverseGeneratorDefault().Generate(seed, things);

        UpdateThingsToRender();
        
        startingPlanet = thingsToRender[1];
        
        UpdateUniverse(0);
        
        AddAvatar();
        
        AddShip();
    }

    private void UpdateThingsToRender()
    {
        thingsToRenderAmount = 0;
        for (int i = 0; i < thingsAmount; i++)
        {
            short type = things[i].type;

            if (type == ThingType.Sun || type == ThingType.Planet || type == ThingType.Moon)
                thingsToRender[thingsToRenderAmount++] = (short) i;
        }
    }

    public void UpdateUniverse(float deltaTime)
    {
        time += deltaTime;

        UEProfiler.BeginSample("Universe.UpdatePositions");
        UpdatePositions(time);
        UEProfiler.EndSample();

        for (int i = 0; i < planets.size(); i++)
            planets.get(i).Update(deltaTime);
        
        for (int i = 0; i < tilemapObjects.size(); i++)
            tilemapObjects.get(i).Update(deltaTime);
    }

    private void UpdatePositions(float time)
    {
        time *= POSITIONS_TIME_SCALE;
        
        for (int index = 1; index < thingsAmount; index++)
        {
            Thing thing = things[index];

            float parentX = thingsPositions[thing.parent].x;
            float parentY = thingsPositions[thing.parent].y;

            float angle = thing.angle * DEG_TO_RAD_OVER_100;
            float distance = thing.distance;

            float normalizedOrbitalPeriod = time * thing.orbitalPeriodInv;
            normalizedOrbitalPeriod -= (int)normalizedOrbitalPeriod;

            float normalizedRotationPeriod = time * thing.rotationPeriodInv;
            normalizedRotationPeriod -= (int)normalizedRotationPeriod;

            angle += TWO_PI * normalizedOrbitalPeriod; //360 degrees to radians

            thingsPositions[index].x = parentX + (Mathf.cos(angle)) * distance;
            thingsPositions[index].y = parentY + (Mathf.sin(angle)) * distance;
            thingsPositions[index].rotation = normalizedRotationPeriod * TWO_PI; //360 degrees to radian
            thingsPositions[index].radius = thing.radius;
        }
    }

    public Thing GetThing(short thingIndex)
    {
        return things[thingIndex];
    }
    
    public ThingPosition GetThingPosition(short thingIndex)
    {
        return thingsPositions[thingIndex];
    }
    
    public Planet GetPlanet(short thingIndex)
    {
        for (int i = 0; i < planets.size(); i++)
            if (planets.get(i).getThingIndex() == thingIndex)
                return planets.get(i);
        
        if (things[thingIndex].type != ThingType.Sun &&
            things[thingIndex].type != ThingType.Planet &&
            things[thingIndex].type != ThingType.Moon)
        {
            return null;
        }
        
        Planet planet = universeFactory.GetPlanet(Planet.GetPlanetHeightWithRadius(things[thingIndex].radius));
        
        planet.InitPlanet(this, thingIndex);
        
        planets.add(planet);
        
        return planet;
    }
    
    public void ReturnPlanet(Planet planet)
    {
        if (planets.remove(planet))
        {
            if (listener != null)
                listener.OnPlanetReturned(planet);
            
            universeFactory.ReturnPlanet(planet);
        }
    }
    
    private void AddAvatar()
    {
        Planet planet = GetPlanet(startingPlanet);
        
        avatar = universeFactory.GetAvatar();
        
        Vector2 defaultPosition = planet.GetPositionFromTileCoordinate(0, planet.getHeight());
        
        avatar.Init(
    		0.75f, 1.05f,
            planet,
            FollowParentParameters.Default,
            defaultPosition.x, defaultPosition.y,
            0.0f
        );
        
        AddUniverseObject(avatar);
    }

    private void AddShip()
    {
        ship = universeFactory.GetShip();
        
        Vector2 defaultPosition = avatar.getParent().GetPositionFromTileCoordinate(0, avatar.getParent().getHeight() + 5);
        
        ship.Init(
            10.0f, 5.0f,
            avatar.getParent(),
            FollowParentParameters.None,
            defaultPosition.x, defaultPosition.y,
            MathUtils.PI * 0.5f
        );
        
        AddUniverseObject(ship);
    }   
    
    public void AddUniverseObject(UniverseObject universeObject)
    {
        tilemapObjects.add(universeObject);
        
        if (listener != null)
            listener.OnUniverseObjectAdded(universeObject);
    }
    
    public int FindClosestRenderedThing(float worldPosX, float worldPosY, float searchRadius)
    {
        short closestThingIndex = Short.MAX_VALUE;
        float closestThingDistance = Float.MAX_VALUE;
        
        for (short i = 0; i < thingsToRenderAmount; i++)
        {
            ThingPosition thingPosition = thingsPositions[thingsToRender[i]];
            
            float dx = worldPosX - thingPosition.x;
            float dy = worldPosY - thingPosition.y;
            
            float distance = (dx * dx + dy * dy);
            
            if (distance < (thingPosition.radius + searchRadius) * (thingPosition.radius + searchRadius) && 
                distance < closestThingDistance)
            {
                closestThingIndex = thingsToRender[i];
                closestThingDistance = distance;
            }
        }
        
        if (closestThingIndex != Short.MAX_VALUE)
            return closestThingIndex;
        else
            return -1;
    }
    
    public ShortArray FindClosestRenderedThings(float worldPosX, float worldPosY, float searchRadius, ShortArray toReturn)
    {
        if (toReturn == null)
            toReturn = new ShortArray();
        else
            toReturn.clear();
        
        for (short i = 0; i < thingsToRenderAmount; i++)
        {
            ThingPosition thingPosition = thingsPositions[thingsToRender[i]];
            
            float dx = worldPosX - thingPosition.x;
            float dy = worldPosY - thingPosition.y;
            
            float distance = (dx * dx + dy * dy);
            
            if (distance < (thingPosition.radius + searchRadius) * (thingPosition.radius + searchRadius))
                toReturn.add(thingsToRender[i]);
        }
        
        //TODO: Sort the array based on world pos distance!!!
        
        //ThingDistanceComparerReferenceX = worldPosX;
        //ThingDistanceComparerReferenceY = worldPosY;
        //Arrays.sort(toReturn.items, thingDistanceComparer);
        
        return toReturn;
    }
    
    
    //private float ThingDistanceComparerReferenceX;
    //private float ThingDistanceComparerReferenceY;
    
    /*
    private int ThingDistanceComparer(ushort index1, ushort index2)
    {
        Vector2 p1 = new Vector2(thingsPositions[index1].x, thingsPositions[index1].y) - ThingDistanceComparerReference;
        Vector2 p2 = new Vector2(thingsPositions[index2].x, thingsPositions[index2].y) - ThingDistanceComparerReference;
        
        float diff = p1.sqrMagnitude - p2.sqrMagnitude;
        
        if (diff < 0)
            return -1;
        else if (diff > 0)
            return 1;
        else
            return 0;
    }
    */
}



