package com.fdangelo.circleworld;

import com.fdangelo.circleworld.universeengine.objects.Avatar;
import com.fdangelo.circleworld.universeengine.tilemap.Planet;
import com.fdangelo.circleworld.universeview.FollowCameraParameters;
import com.fdangelo.circleworld.universeview.UniverseView;
import com.fdangelo.circleworld.universeview.UniverseViewCamera;
import com.fdangelo.circleworld.universeview.tilemap.PlanetView;
import com.fdangelo.circleworld.utils.Mathf;

public class GameLogic 
{
    static public GameLogic Instace;
    
    public UniverseViewCamera universeCamera;
    public UniverseView universeView;
    
    public int universeSeed;
    
    private GameLogicState state;
    private float stateTime;
    
    private float universeTimeMultiplier = 1.0f;
    
    public GameLogicState getState()
    {
        return state;
    }
    
    public void SwitchState(GameLogicState toState)
    {
        this.state = toState;
        stateTime = 0.0f;
        
        switch(toState)
        {
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
    
    public void Awake()
    {
        Instace = this;
    }
    
	public void Start () 
    {
        universeView.Init(universeSeed);
        
        universeCamera.cameraDistance = 10;
        
        SwitchState(GameLogicState.PlayingAvatar);
	}
	
	public void Update(float deltaTime) 
    {
        stateTime += deltaTime;
        
        switch(state)
        {
            case PlayingAvatar:
                universeTimeMultiplier = Mathf.lerp(universeTimeMultiplier, 1.0f, 0.25f);
                break;
                
            case PlayingShip:
                universeTimeMultiplier = Mathf.lerp(universeTimeMultiplier, 1.0f, 0.25f);
                break;
                
            case Travelling:
                universeTimeMultiplier = Mathf.lerp(universeTimeMultiplier, 0.1f, 0.25f);
                if (stateTime > 1.25f)
                    SwitchState(GameLogicState.PlayingAvatar);
                break;
        }
        
        universeView.UpdateUniverse(deltaTime * universeTimeMultiplier);
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
}
