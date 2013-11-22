package com.fdangelo.circleworld.universeview.tilemap;

import com.fdangelo.circleworld.universeengine.tilemap.Planet;
import com.fdangelo.circleworld.universeview.UniverseView;

public class PlanetView extends TilemapCircleView
{
    private UniverseView universeView;
    private Planet planet;
    
    public UniverseView getUniverseView()
    {
        return universeView;
    }
    
    public Planet getPlanet()
    {
        return planet;
    }
    
    public void InitPlanet(Planet planet, UniverseView universeView)
    {
        this.planet = planet;
        this.universeView = universeView;
        
        Init(planet);
    }   
        
	@Override
    public void Recycle()
    {
		remove();
		
        universeView = null;
        planet = null;
        
        super.Recycle();
    }
}
