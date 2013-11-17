package com.fdangelo.circleworld.universeengine.objects;

import com.badlogic.gdx.math.Vector2;
import com.fdangelo.circleworld.universeengine.tilemap.Planet;
import com.fdangelo.circleworld.universeengine.tilemap.TileDirection;
import com.fdangelo.circleworld.universeengine.tilemap.TileHitFlags;
import com.fdangelo.circleworld.universeengine.tilemap.TileHitInfo;
import com.fdangelo.circleworld.universeengine.tilemap.TilemapCircle;


public class UniverseObject
{
    protected TilemapCircle parent;

    protected boolean useGravity = true;
 
    protected float positionX;
    protected float positionY;
    protected float scale = 1.0f;
    protected float rotation = 0.0f; //radians
    
    protected float sizeX = 1;
    protected float sizeY = 1;
    
    protected boolean visible = true;
    
    protected float velocityX;
    protected float velocityY;
    protected float rotationVelocity;

    //Uses TileHitFlags
    protected int hitFlags;
    
    protected float distanceInTilemapCircle;
    protected float angleInTilemapCirclePosition;
    
    protected IUniverseObjectListener listener;
    
    protected boolean parentFollowScale;
    protected boolean parentFollowRotation;
    protected boolean parentCheckCollisions;
    
    static private TileHitInfo tmpHitInfo = new TileHitInfo();
    
    public float getPositionX()
    {
        return positionX;
    }
    
    public float getPositionY()
    {
        return positionY;
    }
    
    public float getScale()
    {
        return scale;
    }
    
    public float getRotation()
    {
        return rotation;
    }
    
    public float getSizeX()
    {
        return sizeX;
    }
    
    public float getSizeY()
    {
        return sizeY;
    }
    
    public float getVelocityX()
    {
        return velocityX;
    }
    
    public float getVelocityY()
    {
        return velocityY;
    }
    
    public void setVelocity(float x, float y)
    {
        velocityX = x;
        velocityY = y;
    }
    
    public int getHitFlags()
    {
        return hitFlags;
    }
    
    public IUniverseObjectListener getListener()
    {
        return listener;
    }
    
    public void setListener(IUniverseObjectListener value)
    {
        this.listener = value;
    }
    
    public boolean getVisible()
    {
        return visible;
    }
    
    public void setVisible(boolean value)
    {
        this.visible = value; 
        if (listener != null)
            listener.OnUniverseObjectUpdated(0.0f);
    }
    
    public TilemapCircle getParent()
    {
    	return parent;
    }
    
    public void Init(float sizeX, float sizeY, TilemapCircle parent, int followParentParameters, float positionX, float positionY, float rotation)
    {
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        
        SetParent(parent, followParentParameters, positionX, positionY, rotation);
    }
    
    public void SetParent(TilemapCircle parent, int followParentParameters, float positionX, float positionY, float rotation)
    {
        this.parent = parent;
        this.positionX = positionX;
        this.positionY = positionY;
        this.rotation = rotation;
        this.parentFollowScale = (followParentParameters & FollowParentParameters.FollowScale) != 0;
        this.parentFollowRotation = (followParentParameters & FollowParentParameters.FollowRotation) != 0;
        this.parentCheckCollisions = (followParentParameters & FollowParentParameters.CheckCollisions) != 0;
        
        if (parent != null)
        {
            if (parentFollowScale)
                this.scale = parent.GetScaleFromPosition(positionX, positionY);
            
            if (parentFollowRotation)
                this.rotation = parent.GetAngleFromPosition(positionX, positionY);
            
            distanceInTilemapCircle = parent.GetDistanceFromPosition(positionX, positionY);
            angleInTilemapCirclePosition = parent.GetAngleFromPosition(positionX, positionY);
        }
        
        if (listener != null)
            listener.OnParentChanged(parent);
    }
    
    public void Update(float deltaTime)
    {
        OnUpdate(deltaTime);
        
        UpdatePosition(deltaTime);
        
        if (listener != null)
            listener.OnUniverseObjectUpdated(deltaTime);
    }
    
    protected void OnUpdate(float deltaTime)
    {
        
    }
    
    protected void UpdatePosition(float deltaTime)
    {
        float normalX, normalY;
        float tangentX, tangentY;
        
        float deltaPositionX, deltaPositionY;
        float deltaRotation;
            
        if (parent != null)
        {
        	Vector2 positionInParent = parent.GetPositionFromDistanceAndAngle(distanceInTilemapCircle, angleInTilemapCirclePosition);
        	
            positionX = positionInParent.x;
            positionY = positionInParent.y;
            
            if (parentFollowRotation)
                rotation = parent.GetAngleFromPosition(positionX, positionY);
            
            if (parentFollowScale)
                scale = parent.GetScaleFromPosition(positionX, positionY);
            
            if (parent instanceof Planet && useGravity)
                velocityY -= ((Planet) parent).getGravity() * deltaTime;
            
            Vector2 normal = parent.GetNormalFromPosition(positionX, positionY); //doesn't change with vertical position
            normalX = normal.x;
            normalY = normal.y;
            
            Vector2 tangent = parent.GetTangentFromPosition(positionX, positionY); //doesn't change with vertical position
            tangentX = tangent.x;
            tangentY = tangent.y;
            
            deltaPositionX = velocityX * deltaTime * scale;
            deltaPositionY = velocityY * deltaTime * scale;
            
            if (parentFollowRotation)
                deltaRotation = 0.0f;
            else
                deltaRotation = rotationVelocity * deltaTime;
        }
        else
        {
            normalY = 1;
            normalX = 0;
            
            tangentY = 0;
            tangentX = 1;
            
            deltaPositionX = velocityX * deltaTime * scale;
            deltaPositionY = velocityY * deltaTime * scale;
            deltaRotation = rotationVelocity * deltaTime;
        }
        
        hitFlags = TileHitFlags.None;
        
        if (parent != null && parentCheckCollisions)
        {
            if (deltaPositionY > 0)
            {
                //Check against ceiling
                if (parent.RaycastSquare(
            		positionX + normalX * (sizeY * 0.5f * scale),
            		positionY + normalY * (sizeY * 0.5f * scale),
                    sizeX * scale,
                    TileDirection.Up, 
                    deltaPositionY + (sizeY * 0.5f * scale), 
                    tmpHitInfo))
                {
                    deltaPositionY = -(tmpHitInfo.hitDistance - (sizeY * 0.5f * scale));
                    velocityY = 0.0f;
                    hitFlags |= TileHitFlags.Up;
                }
            }
            else if (deltaPositionY < 0)
            {
                //Check against floor
                if (parent.RaycastSquare(
                    positionX + normalX * (sizeY * 0.5f * scale), 
                    positionY + normalY * (sizeY * 0.5f * scale),
                    sizeX * scale,
                    TileDirection.Down, 
                    -deltaPositionY + (sizeY * 0.5f * scale), 
                    tmpHitInfo))
                {
                    deltaPositionY = -(tmpHitInfo.hitDistance - (sizeY * 0.5f * scale));
                    velocityY = 0.0f;
                    hitFlags |= TileHitFlags.Down;
                }
            }
        }

        if (deltaPositionY != 0)
        {
            positionY += normalY * deltaPositionY;
            positionX += normalX * deltaPositionY;
            if (parent != null && parentFollowScale)
                scale = parent.GetScaleFromPosition(positionX, positionY);
        }
        
        if (parent != null && parentCheckCollisions)
        {
            if (deltaPositionX > 0)
            {
                //Check against right wall
                if (parent.RaycastSquare(
                    positionX + normalX * (sizeY * 0.5f * scale), 
                    positionY + normalY * (sizeY * 0.5f * scale),
                    sizeY * scale,
                    TileDirection.Right, 
                    deltaPositionX + (sizeX * 0.5f * scale), 
                    tmpHitInfo))
                {
                    deltaPositionX = (tmpHitInfo.hitDistance - (sizeX * 0.5f * scale));
                    velocityX = 0.0f;
                    hitFlags |= TileHitFlags.Right;
                }
            }
            else if (deltaPositionX < 0)
            {
                //Check against left wall
                if (parent.RaycastSquare(
                    positionX + normalX * (sizeY * 0.5f * scale), 
                    positionY + normalY * (sizeY * 0.5f * scale),
                    sizeY * scale,
                    TileDirection.Left, 
                    -deltaPositionX + (sizeX * 0.5f * scale), 
                    tmpHitInfo))
                {
                    deltaPositionX = -(tmpHitInfo.hitDistance - (sizeX * 0.5f * scale));
                    velocityX = 0.0f;
                    hitFlags |= TileHitFlags.Left;
                }
            }
        }

        if (deltaPositionX != 0)
        {
            positionX += tangentX * deltaPositionX;
            positionY += tangentY * deltaPositionX;
            if (parent != null)
            {
                Vector2 normal = parent.GetNormalFromPosition(positionX, positionY);
                normalX = normal.x;
                normalY = normal.y;
            }
        }

        if (parent != null)
        {
            if (parentFollowRotation)
                rotation = parent.GetAngleFromPosition(positionX, positionY);
            else
                rotation += deltaRotation;
            
            distanceInTilemapCircle = parent.GetDistanceFromPosition(positionX, positionY);
            angleInTilemapCirclePosition = parent.GetAngleFromPosition(positionX, positionY);
        }
        else
        {
            rotation += deltaRotation;
        }
    }
 
    /*
    public bool MoveTo(Vector2 position)
    {
        if (CanMoveTo(position))
        {
            this.position = position;
            return true;
        }

        return false;
    }

    public bool CanMoveTo(Vector2 position)
    {
        float scale = tilemapCircle.GetScaleFromPosition(position);

        int tileX, tileY;

        Vector2 right = transform.right;
        Vector2 up = transform.up;

        position += up * 0.05f;

        for (int x = -1; x <= 1; x++)
        {
            for (int y = 0; y <= 2; y++)
            {
                Vector2 pos = position + 
                    right * (size.x * 0.9f * x * 0.5f * scale) +
                        up * ((size.y * 0.9f / 2) * y * scale);

                if (tilemapCircle.GetTileCoordinatesFromPosition(pos, out tileX, out tileY))
                    if (tilemapRicle.GetTile(tileX, tileY) != 0)
                        return false;
            }
        }

        return true;
    }
    */
}
