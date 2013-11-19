package com.fdangelo.circleworld.universeengine.objects;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.fdangelo.circleworld.universeengine.tilemap.Planet;
import com.fdangelo.circleworld.universeengine.tilemap.TileHitFlags;
import com.fdangelo.circleworld.utils.Mathf;
import com.fdangelo.circleworld.utils.Vector2I;

public class Avatar extends UniverseObject
{
    public AvatarInput input = new AvatarInput();
    
    private float jumpSpeed = 7.0f;
    private float walkSpeedMax = 3.0f;
    private float walkAcceleration = 10.0f;
    private float walkFriction = 10.0f;
    
    private Ship onShip;
    
    @Override
    protected void OnUpdate(float deltaTime)
    {
        if (onShip == null)
        {
            if (CanWalk())
            {
                if (input.walkDirection != 0)
                    velocityX += input.walkDirection * walkAcceleration * deltaTime;
                else
                    velocityX -= Mathf.Sign(velocityX) * MathUtils.clamp(walkFriction * deltaTime, 0, Math.abs(velocityX));
                    
                velocityX = MathUtils.clamp(velocityX, -walkSpeedMax, walkSpeedMax);
            }

            if (input.jump && CanJump())
                velocityY = jumpSpeed;
        }
        else
        {
            velocityX = 0;
            velocityY = 0;
            positionX = onShip.getPositionX();
            positionY = onShip.getPositionY();
        }
        
        input.Reset();
    }
    
    public boolean CanWalk()
    {
        return true;
    }
            
    public boolean CanJump()
    {
        return (hitFlags & TileHitFlags.Down) != 0;
    }
    
    public void BoardShip(Ship ship)
    {
        this.onShip = ship;
        this.scale = 1.0f;
        
        SetParent(
            null,
            FollowParentParameters.None,
            ship.getPositionX(),
            ship.getPositionY(),
            rotation);
        
        setVisible(false);
    }
    
    static private Vector2I tmpvi = new Vector2I();
    
    public void TravelToPlanet(Planet planet)
    {
        if (this.onShip != null)
        {
        	Vector2I landTile = tmpvi;
        	
            //Set position closest to the ship
            planet.GetTileCoordinatesFromPosition(onShip.getPositionX(), onShip.getPositionY(), landTile);
            
            int landTileX = landTile.x;
            int landTileY = landTile.y;
            landTile = null;
            
            landTileY = planet.getHeight();
            
            Vector2 tilePositionOnPlanet = planet.GetPositionFromTileCoordinate(landTileX, landTileY);
            
            SetParent(
                planet,
                FollowParentParameters.Default,
                tilePositionOnPlanet.x,
                tilePositionOnPlanet.y,
                0.0f
            );
            
            //Leave ship
            this.onShip = null;
        }
        else
        {
        	Vector2 tilePositionOnPlanet = planet.GetPositionFromTileCoordinate(0, planet.getHeight());
        	
            SetParent(
                planet,
                FollowParentParameters.Default,
                tilePositionOnPlanet.x,
                tilePositionOnPlanet.y,
                0.0f
            );
        }
        
        setVisible(true);
    }
}
