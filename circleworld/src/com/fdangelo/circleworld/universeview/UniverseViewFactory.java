package com.fdangelo.circleworld.universeview;

import java.util.ArrayList;

import com.badlogic.gdx.utils.IntMap;
import com.fdangelo.circleworld.universeview.objects.AvatarView;
import com.fdangelo.circleworld.universeview.objects.ShipView;
import com.fdangelo.circleworld.universeview.tilemap.PlanetView;

public class UniverseViewFactory
{
    private IntMap<ArrayList<PlanetView>> planetsPool = new IntMap<ArrayList<PlanetView>>();
    
    public AvatarView GetAvatar()
    {
    	return new AvatarView();
    }
    
    public ShipView GetShip()
    {
    	return new ShipView();
    }
    
    public PlanetView GetPlanet(int height)
    {
        PlanetView planet = null;
        
        ArrayList<PlanetView> list;
        
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
        	planet = new PlanetView();
        }
        
        return planet;
    }
    
    public void ReturnPlanet(PlanetView planet)
    {
        int height = planet.getTilemapCircle().getHeight();
        
        planet.Recycle();
        
        ArrayList<PlanetView> list;
        
        if (planetsPool.containsKey(height))
        {
        	list = planetsPool.get(height);
        }
        else
        {
        	list = new ArrayList<PlanetView>();
        	planetsPool.put(height, list);
        }
        
        list.add(planet);
    }
}

