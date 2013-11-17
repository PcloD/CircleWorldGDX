package com.fdangelo.circleworld.universeengine.objects;

import com.badlogic.gdx.math.MathUtils;
import com.fdangelo.circleworld.GameLogic;
import com.fdangelo.circleworld.GameLogicState;
import com.fdangelo.circleworld.universeengine.tilemap.Planet;
import com.fdangelo.circleworld.utils.Mathf;

public class Ship extends UniverseObject
{
    public ShipInput input = new ShipInput();
    
    private float movementSpeedMax = 100.0f;
    private float movementAcceleration = 100.0f;
    private float movementFriction = 200.0f;
    
    private float rotationSpeedMax = 135.0f * MathUtils.degreesToRadians;
    private float rotationAcceleration = 360.0f * MathUtils.degreesToRadians;
    private float rotationFriction = 360.0f * MathUtils.degreesToRadians;
    
    public Ship()
    {
        useGravity = false;
    }
            
    @Override
    protected void OnUpdate(float deltaTime)
    {
        if (GameLogic.Instace.getState() == GameLogicState.PlayingShip)
        {
            if (input.rotateDirection != 0)
                rotationVelocity += input.rotateDirection * rotationAcceleration * deltaTime;
            else
                rotationVelocity -= Mathf.Sign(rotationVelocity) * MathUtils.clamp(rotationFriction * deltaTime, 0, Math.abs(rotationVelocity));
            
            rotationVelocity = MathUtils.clamp(rotationVelocity, -rotationSpeedMax, rotationSpeedMax);
            
            float currentSpeed = (float) Math.sqrt(velocityX * velocityX + velocityY * velocityY);
            
            if (input.moveDirection > 0)
                currentSpeed += input.moveDirection * movementAcceleration * deltaTime;
            else
                currentSpeed -= Mathf.Sign(currentSpeed) * MathUtils.clamp(movementFriction * deltaTime, 0, Math.abs(currentSpeed));
            
            currentSpeed = MathUtils.clamp(currentSpeed, 0, movementSpeedMax);
                       
            velocityX = MathUtils.sin(getRotation()) * currentSpeed;
            velocityY = MathUtils.cos(getRotation()) * currentSpeed;
        }
        else if (GameLogic.Instace.getState() == GameLogicState.PlayingAvatar)
        {
            //Orbit planet!
            if (parent != null)
            {
                float orbitDistance = parent.GetDistanceFromTileY(parent.getHeight() + 7);
                
                if (distanceInTilemapCircle > orbitDistance + 1.0f)
                    velocityY -= movementAcceleration * deltaTime;
                else if (distanceInTilemapCircle < orbitDistance - 1.0f)
                    velocityY += movementAcceleration * deltaTime;
                else
                    velocityY -= Mathf.Sign(velocityY) * MathUtils.clamp(movementFriction * deltaTime, 0, Math.abs(velocityY));
                
                velocityY = MathUtils.clamp(velocityY, 0, movementSpeedMax * 0.1f);
                
                velocityX = movementSpeedMax * 0.05f;
                
                float orbitRotationDiff = (parent.GetAngleFromPosition(positionX, positionY) + MathUtils.PI * 0.5f) - rotation;
                
                if (orbitRotationDiff > MathUtils.PI)
                    orbitRotationDiff -= MathUtils.PI2;
                else if (orbitRotationDiff < -MathUtils.PI)
                    orbitRotationDiff += MathUtils.PI2;
                
                if (orbitRotationDiff < 0)
                    rotationVelocity -= rotationAcceleration * deltaTime;
                else if (orbitRotationDiff > 0)
                    rotationVelocity += rotationAcceleration * deltaTime;
                else
                    rotationVelocity -= Mathf.Sign(rotationVelocity) * MathUtils.clamp(rotationFriction * deltaTime, 0, Math.abs(rotationVelocity));
                
                rotationVelocity = MathUtils.clamp(rotationVelocity, -rotationSpeedMax * 0.1f, rotationSpeedMax * 0.1f);
            }
        }
        
        input.Reset();
    }
    
    public void BeamDownAvatar(Avatar avatar, Planet planet)
    {
        rotationVelocity = 0;
        velocityX = 0;
        velocityY = 0;
        
        SetParent(planet, FollowParentParameters.None, positionX, positionY, rotation);
        
        avatar.TravelToPlanet(planet);
    }
    
    public void BeamUpAvatar(Avatar avatar)
    {
        SetParent(null, FollowParentParameters.None, positionX, positionY, rotation);
        
        avatar.BoardShip(this);
    }
}
