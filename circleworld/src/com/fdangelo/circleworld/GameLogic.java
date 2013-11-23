package com.fdangelo.circleworld;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Disposable;
import com.fdangelo.circleworld.universeengine.objects.Avatar;
import com.fdangelo.circleworld.universeengine.tilemap.Planet;
import com.fdangelo.circleworld.universeview.FollowCameraParameters;
import com.fdangelo.circleworld.universeview.UniverseView;
import com.fdangelo.circleworld.universeview.UniverseViewCamera;
import com.fdangelo.circleworld.universeview.tilemap.PlanetView;
import com.fdangelo.circleworld.utils.Mathf;

public class GameLogic implements Disposable
{
    static public GameLogic Instace;
    
    public AssetManager assetManager;
    
    public UniverseViewCamera universeCamera;
    public UniverseView universeView;
    
    public int universeSeed;
    
    private GameLogicState state = GameLogicState.PlayingAvatar;
    private float stateTime;
    
    private float universeTimeMultiplier = 1.0f;
    
    public GameLogicState getState()
    {
        return state;
    }
    
	public GameLogic () 
    {
		Instace = this;
		
		assetManager = new AssetManager();
		
		assetManager.load("atlas/gui.atlas", TextureAtlas.class);
		assetManager.load("atlas/planets.atlas", TextureAtlas.class);
		assetManager.load("atlas/player1.atlas", TextureAtlas.class);
		assetManager.load("atlas/ships.atlas", TextureAtlas.class);
		assetManager.load("atlas/tilemap.atlas", TextureAtlas.class);
		
		SwitchState(GameLogicState.Loading);
    }
	
    
    public void SwitchState(GameLogicState toState)
    {
        this.state = toState;
        stateTime = 0.0f;
        
        switch(toState)
        {
        	case Loading:
        		//Do nothing
        		break;
        		
            case PlayingAvatar:
                universeCamera.FollowObject(universeView.avatarView, FollowCameraParameters.FollowRotation | FollowCameraParameters.FollowScale, true);
                break;
                
            case PlayingShip:
                universeCamera.FollowObject(universeView.shipView, FollowCameraParameters.None, true);
                break;
                
            case Travelling:
            	//Do nothing
            	break;
        }
    }
    
	public void UpdateAndRender(float deltaTime) 
    {
		if (deltaTime > 0.1f)
			deltaTime = 0.1f;
		
        stateTime += deltaTime;
        
        switch(state)
        {
        	case Loading:
        		if (assetManager.update())
        		{
        			//Loading complete!
        			
        			//Create layers
		    		universeView = new UniverseView();
		            universeView.Init(universeSeed);
		            
		            universeCamera = new UniverseViewCamera(universeView.getCamera());
		            
		            SwitchState(GameLogicState.PlayingAvatar);
        		}
	            break;
	            
            case PlayingAvatar:
                universeTimeMultiplier = Mathf.lerp(universeTimeMultiplier, 1.0f, 0.25f);
                universeView.UpdateUniverse(deltaTime * universeTimeMultiplier);
                universeCamera.Update(deltaTime);
                break;
                
            case PlayingShip:
                universeTimeMultiplier = Mathf.lerp(universeTimeMultiplier, 1.0f, 0.25f);
                universeView.UpdateUniverse(deltaTime * universeTimeMultiplier);
                universeCamera.Update(deltaTime);
                break;
                
            case Travelling:
                universeTimeMultiplier = Mathf.lerp(universeTimeMultiplier, 0.1f, 0.25f);
                if (stateTime > 1.25f)
                    SwitchState(GameLogicState.PlayingAvatar);
                universeView.UpdateUniverse(deltaTime * universeTimeMultiplier);
                universeCamera.Update(deltaTime);
                break;
        }
        
        if (universeView != null)
        	universeView.updateStages(deltaTime);
	}
    
    public void TravelToPlanet(PlanetView targetPlanetView)
    {
        ((Avatar) universeView.avatarView.getUniverseObject()).TravelToPlanet((Planet) targetPlanetView.getTilemapCircle());
        
        //Force update to update AvatarView position
        universeView.UpdateUniverse(0);
        
        SwitchState(GameLogicState.Travelling);
    }
    
    public void PlayerBoardShip()
    {
        universeView.getUniverse().getShip().BeamUpAvatar(universeView.getUniverse().getAvatar());
        
        SwitchState(GameLogicState.PlayingShip);
    }

    public void PlayerLeaveShip(Planet planet)
    {
        universeView.getUniverse().getShip().BeamDownAvatar(universeView.getUniverse().getAvatar(), planet);
        
        SwitchState(GameLogicState.PlayingAvatar);
    }
    
    public void dispose()
    {
		if (universeView != null)
			universeView.dispose();
		
    	assetManager.dispose();
    }

	public void resize(int width, int height) {
		
		if (universeView != null)
			universeView.resize(width, height);
		
	}
}
