package com.fdangelo.circleworld.universeengine;

import java.util.ArrayList;

import com.badlogic.gdx.utils.IntMap;
import com.fdangelo.circleworld.universeengine.objects.Avatar;
import com.fdangelo.circleworld.universeengine.objects.Ship;
import com.fdangelo.circleworld.universeengine.tilemap.Planet;

public class UniverseFactory
{
    private IntMap<ArrayList<Planet>> planetsPool = new IntMap<ArrayList<Planet>>();
    
    public Avatar GetAvatar()
    {
        return new Avatar();
    }
    
    public Ship GetShip()
    {
        return new Ship();
    }
    
    public Planet GetPlanet(int height)
    {
        Planet planet = null;
        
        ArrayList<Planet> list;
        
        if (planetsPool.containsKey(height))
        	list = planetsPool.get(height);
        else
        	list = null;
        
        if (list != null && list.size() > 0)
        {
            planet = list.get(list.size() - 1);
            
            list.remove(list.size() - 1);
        }
        else
        {
            planet = new Planet();
        }
        
        return planet;
    }
    
    public void ReturnPlanet(Planet planet)
    {
        int height = planet.getHeight();
        
        planet.Recycle();
        
        ArrayList<Planet> list;
        
        if (planetsPool.containsKey(height))
        {
        	list = planetsPool.get(height);
        }
        else
        {
        	list = new ArrayList<Planet>();
        	planetsPool.put(height, list);
        }
        
        list.add(planet);
    }
}
