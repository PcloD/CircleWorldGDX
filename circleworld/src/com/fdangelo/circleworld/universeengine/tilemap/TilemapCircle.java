package com.fdangelo.circleworld.universeengine.tilemap;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.fdangelo.circleworld.universeengine.utils.DataPools;
import com.fdangelo.circleworld.utils.Vector2I;

public class TilemapCircle
{
    public static final float TILE_SIZE = 0.5f; 
    public static final float TILE_SIZE_INV = 1.0f / TILE_SIZE;

    protected int seed;
    protected int height;
    protected int width;

    protected byte[] tiles;

    protected Vector2[] circleNormals;
    protected float[] circleHeights;

    //Used when finding tileY positions!
    private float height0;
    private float k;
    private float logk;
    
    protected ITilemapCircleListener listener;
    
    protected float positionX;
    protected float positionY;
    protected float rotation;
    
    static private TileHitInfo tmpHitInfo = new TileHitInfo();
    static private Vector2 tmpv1 = new Vector2();
        
    public int getHeight()
    {
        return height;
    }
    
    public int getWidth()
    {
        return width;
    }
    
    public int getSeed()
    {
        return seed;
    }
    
    public float getPositionX()
    {
        return positionX;
    }
    
    public float getPositionY()
    {
        return positionY;
    }
    
    public void setPosition(float x, float y)
    {
    	this.positionX = x;
    	this.positionY = y;
    }
    
    public float getRotation()
    {
        return rotation;
    }
    
    public void setRotation(float value)
    {
        rotation = value;
    }
    
    public Vector2[] getCircleNormals()
    {
        return circleNormals;
    }
    
    public float[] getCircleHeights()
    {
        return circleHeights;
    }
    
    public ITilemapCircleListener getListener()
    {
        return listener;
    }
    
    public void setListener(ITilemapCircleListener value)
    {
        this.listener = value;
    }
    
    public void Init(int seed, int height)
    {
        if (height < 5)
            height = 5;
        
        this.seed = seed;
        this.height = height;
        
        InitData();
        
        UpdateTiles();
    }
    
    protected void UpdateTiles()
    {
        //TODO: Override!
    }

    private void InitData()
    {
        width = (((int)((float)height * MathUtils.PI * 2.0f)) / 4) * 4;

        circleNormals = DataPools.poolVector2.GetArray(width);
        circleHeights = DataPools.poolFloat.GetArray(height + 1);
        tiles = DataPools.poolByte.GetArray(width * height);
        
        float angleStep = ((2.0f * MathUtils.PI) / width);

        for (int i = 0; i < width; i++)
        {
            float angle = i * angleStep;
            circleNormals[i] = new Vector2(
            		MathUtils.sin(angle),
        			MathUtils.cos(angle));
        }

        height0 = (height - 1) * TILE_SIZE;
        k = -((width / (MathUtils.PI * 2.0f))) / (1 - (width / (MathUtils.PI * 2.0f)));
        logk = (float) Math.log(k);

        circleHeights[0] = height0;

        for (int i = 1; i <= height; i++)
        {
            float r1 = circleHeights[i - 1];

            //float r2 = ((-r1 * width) / (Mathf.PI * 2.0f)) / (1 - (width / (Mathf.PI * 2.0f)));
            float r2 = r1 * k;

            circleHeights[i] = r2;
        }
    }

    public byte GetTile(int tileX, int tileY)
    {
        return tiles[tileX + tileY * width];
    }

    public void SetTile(int tileX, int tileY, byte tile)
    {
        if (tiles[tileX + tileY * width] != tile)
        {
            tiles[tileX + tileY * width] = tile;
            if (listener != null)
                listener.OnTilemapTileChanged(tileX, tileY);
        }
    }

    public int GetTileYFromDistance(float distance)
    {
        //This was taken from wolfram-alpha, by solving the radius relationship function
        //Original function: http://www.wolframalpha.com/input/?i=g%280%29%3Dk%2C+g%28n%2B1%29%3Dl+*+g%28n%29
        //Solution: http://www.wolframalpha.com/input/?i=y+%3D+k+*+l%CB%86x+find+x (we use the solution over reals with y > 0)

        //int tileY = (int) (Mathf.Log (distance / height0) / Mathf.Log (k));
        int tileY = (int) (Math.log (distance / height0) / logk);

        return tileY;
    }

    public float GetDistanceFromTileY(int tileY)
    {
        return (float) (height0 * Math.pow(k, (float) tileY));
    }

    public float GetDistanceFromPosition(float positionX, float positionY)
    {
        float dx = positionX - this.positionX;
        float dy = positionY - this.positionY;
        
        return (float) Math.sqrt(dx * dx + dy * dy);
    }
    
    public int GetTileXFromAngle(float angle)
    {
        int tileX = MathUtils.floor((angle / (MathUtils.PI * 2.0f)) * width);

        tileX = tileX % width;
        if (tileX < 0)
            tileX += width;

        return tileX;
    }

    public Vector2 GetPositionFromTileCoordinate(int tileX, int tileY)
    {
        //return position + GetNormalFromTileX(tileX) * GetDistanceFromTileY(tileY);
    	
    	return GetNormalFromTileX(tileX).scl(GetDistanceFromTileY(tileY)).add(positionX, positionY);
    }
    
    public Vector2 GetPositionFromDistanceAndAngle(float distance, float angle)
    {
        //return position + GetNormalFromAngle(angle) * distance;
    	
    	Vector2 normal = GetNormalFromAngle(angle);
    	normal.scl(distance);
    	normal.add(positionX, positionY);
    	
    	return normal;
    }

    public boolean GetTileCoordinatesFromPosition(float positionX, float positionY, Vector2I tileCoordinate)
    {
        float dx = positionX - this.positionX;
        float dy = positionY - this.positionY;

        float distance = (float) Math.sqrt(dx * dx + dy * dy);
        float angle = -MathUtils.atan2(dy, dx) + MathUtils.PI * 0.5f;

        tileCoordinate.y = GetTileYFromDistance(distance);
        tileCoordinate.x = GetTileXFromAngle(angle);

        if (tileCoordinate.y >= height || tileCoordinate.y < 0)
            return false;

        return true;
    }

    public float GetScaleFromPosition(float positionX, float positionY)
    {
        float dx = positionX - this.positionX;
        float dy = positionY - this.positionY;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);
        
        float scale = MathUtils.clamp(
            (distance * 2.0f * MathUtils.PI) / width,
            (circleHeights[0] * 2.0f * MathUtils.PI) / width,
            (circleHeights[circleHeights.length - 1] * 2.0f * MathUtils.PI) / width) * TILE_SIZE_INV;

        return scale;
    }

    public Vector2 GetNormalFromPosition(float positionX, float positionY)
    {
        float dx = positionX - this.positionX;
        float dy = positionY - this.positionY;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);
        
        return tmpv1.set(dx / distance, dy / distance);
    }
    
    public Vector2 GetNormalFromAngle(float angle)
    {
    	return tmpv1.set(
            MathUtils.sin(angle), 
            MathUtils.cos(angle)
        );
    }

    public float GetAngleFromPosition(float positionX, float positionY)
    {
        float dx = positionX - this.positionX;
        float dy = positionY - this.positionY;

        float angle = -MathUtils.atan2(dy, dx) + MathUtils.PI * 0.5f;

        return angle;
    }
    
    public Vector2 GetNormalFromTileX(int tileX)
    {
        tileX = tileX % width;
        
        return tmpv1.set(circleNormals[tileX]);
    }

    public Vector2 GetTangentFromPosition(float positionX, float positionY)
    {
        Vector2 normal = GetNormalFromPosition(positionX, positionY);
        
        return tmpv1.set(normal.y, -normal.x);
    }

    public Vector2 GetTangentFromTileCoordinate(int tileX, int tileY)
    {
        Vector2 normal = GetNormalFromTileX(tileX);

        return tmpv1.set(normal.y, -normal.x);
    }
    
    public boolean RaycastSquare(float originX, float originY, float size, TileDirection direction, float len, TileHitInfo hitInfo)
    {
        size *= 0.95f;

        int iterations = Math.max(MathUtils.ceil(size / TILE_SIZE), 1);

        //Vector2 from = origin - GetTanget(origin, direction) * (size * 0.5f);
        Vector2 from = GetTanget(originX, originY, direction);
        from.scl(size * 0.5f);
        from.add(originX, originY);
        float fromX = from.x;
        float fromY = from.y;
        from = null;
        
        //Vector2 step = GetTanget(origin, direction) * (size / iterations);
        Vector2 step = GetTanget(originX, originY, direction);
        step.scl(size / iterations);
        float stepX = step.x;
        float stepY = step.y;
        step = null;

        boolean hitAny = false;

        TileHitInfo localHitInfo = tmpHitInfo;
        
        for (int i = 0; i <= iterations; i++)
        {
            if (Raycast(fromX, fromY, direction, len, localHitInfo))
            {
                if (!hitAny)
                {
                    hitAny = true;
                    hitInfo.set(localHitInfo);
                }
                else if (localHitInfo.hitDistance < hitInfo.hitDistance)
                {
                    hitInfo.set(localHitInfo);
                }
            }
            
            fromX += stepX;
            fromY += stepY;
        }

        return hitAny;
    }

    public Vector2 GetDirection(float originX, float originY, TileDirection direction)
    {
        switch(direction)
        {
            case Down:
                return GetNormalFromPosition(originX, originY).scl(-1);
            
            case Up:
                return GetNormalFromPosition(originX, originY);

            case Right:
                return GetTangentFromPosition(originX, originY);

            case Left:
                return GetTangentFromPosition(originX, originY).scl(-1);
                
            default:
                return tmpv1.set(0, 0);
        }
    }

    
    public Vector2 GetTanget(float originX, float originY, TileDirection direction)
    {
        switch(direction)
        {
            case Down:
                return GetTangentFromPosition(originX, originY);

            case Up:
                return GetTangentFromPosition(originX, originY);

            case Right:
                return GetNormalFromPosition(originX, originY);

            case Left:
                return GetNormalFromPosition(originX, originY);
                
            default:
                return tmpv1.set(0, 0);
        }
    }
    
    public boolean Raycast(float originX, float originY, TileDirection direction, float len, TileHitInfo hitInfo)
    {
        float dx = originX - positionX;
        float dy = originY - positionY;
        float originDistance = (float) Math.sqrt(dx * dx + dy * dy);

        float targetX;
        float targetY;
        float targetdx;
        float targetdy;
        float targetDistance;
        float tangentDistance;

        float segmentSize;

        if (originDistance < 0.001f)
            originDistance = 0.001f;

        float originMapAngle = -MathUtils.atan2(dy, dx) + MathUtils.PI * 0.5f;

        while (originMapAngle > MathUtils.PI2)
            originMapAngle -= MathUtils.PI2;

        while (originMapAngle < 0.0f)
            originMapAngle += MathUtils.PI2;

        float originNormalX = dx / originDistance;
        float originNormalY = dy / originDistance;
        float originTangentX = originNormalY;
        float originTangentY = -originNormalX;

        if (direction == TileDirection.Right)
        {
        	targetX = originX + originTangentX * len;
        	targetY = originY + originTangentY * len;
            targetdx = targetX - positionX;
            targetdy = targetY - positionY;
            targetDistance = (float) Math.sqrt(targetdx * targetdx + targetdy * targetdy);

            if (originDistance > circleHeights[circleHeights.length - 1])
            {
                //Origin point outside, not hit!
                return false;
            }

            for (int i = 1; i < circleHeights.length; i++)
            {
                if (originDistance < circleHeights[i])
                {
                    hitInfo.hitTileY = i - 1;
                    break;
                }
            }

            segmentSize = (circleHeights[hitInfo.hitTileY] * 2.0f * MathUtils.PI) / width;
            tangentDistance = ((originMapAngle / (MathUtils.PI2)) * width);

            hitInfo.hitTileX = (int)tangentDistance;
            hitInfo.hitTileX = (hitInfo.hitTileX + 1) % width;

            len -= segmentSize * (MathUtils.ceil(tangentDistance) - tangentDistance);

            while (hitInfo.hitTileX < width && len >= 0)
            {
                if (GetTile(hitInfo.hitTileX, hitInfo.hitTileY) != 0)
                {
                	Vector2 tanget = GetTangentFromTileCoordinate(hitInfo.hitTileX, hitInfo.hitTileY);
                	
                    hitInfo.hitNormalX = -tanget.x;
                    hitInfo.hitNormalY = -tanget.y;

                    hitInfo.hitPositionX = positionX + 
                        circleNormals[hitInfo.hitTileX].x * originDistance;
                    hitInfo.hitPositionY = positionY + 
                            circleNormals[hitInfo.hitTileX].y * originDistance;

                    float hitDistanceX = originX - hitInfo.hitPositionX;
                    float hitDistanceY = originY - hitInfo.hitPositionY;
                    
                    hitInfo.hitDistance = (float) Math.sqrt(hitDistanceX * hitDistanceX + hitDistanceY * hitDistanceY);
                    
                    return true;
                }

                len -= segmentSize;

                hitInfo.hitTileX++;
            }
        }
        else if (direction == TileDirection.Left)
        {
            targetX = originX + originTangentX * len;
            targetY = originY + originTangentY * len;
            targetdx = targetX - positionX;
            targetdy = targetY - positionY;
            targetDistance = (float) Math.sqrt(targetdx * targetdx + targetdy * targetdy);

            if (originDistance > circleHeights[circleHeights.length - 1])
            {
                //Origin point outside, not hit!
                return false;
            }

            for (int i = 1; i < circleHeights.length; i++)
            {
                if (originDistance < circleHeights[i])
                {
                    hitInfo.hitTileY = i - 1;
                    break;
                }
            }

            segmentSize = (circleHeights[hitInfo.hitTileY] * 2.0f * MathUtils.PI) / width;
            tangentDistance = ((originMapAngle / (MathUtils.PI2)) * width);

            hitInfo.hitTileX = (int)tangentDistance;
            hitInfo.hitTileX = (hitInfo.hitTileX - 1) % width;
            if (hitInfo.hitTileX < 0)
                hitInfo.hitTileX += width;

            len -= segmentSize * (tangentDistance - MathUtils.floor(tangentDistance));

            while (hitInfo.hitTileX >= 0 && len >= 0)
            {
                if (GetTile(hitInfo.hitTileX, hitInfo.hitTileY) != 0)
                {
                	Vector2 tangent = GetTangentFromTileCoordinate(hitInfo.hitTileX + 1, hitInfo.hitTileY);
                	
                    hitInfo.hitNormalX = tangent.x;
                    hitInfo.hitNormalY = tangent.y;

                    hitInfo.hitPositionX = positionX + 
                        circleNormals[(hitInfo.hitTileX + 1) % width].x * originDistance;
                    hitInfo.hitPositionX = positionY + 
                            circleNormals[(hitInfo.hitTileX + 1) % width].y * originDistance;
                    
                    float hitDistanceX = originX - hitInfo.hitPositionX;
                    float hitDistanceY = originY - hitInfo.hitPositionY;

                    hitInfo.hitDistance = (float) Math.sqrt(hitDistanceX * hitDistanceX + hitDistanceY * hitDistanceY);
                    return true;
                }

                len -= segmentSize;

                hitInfo.hitTileX--;
            }
        }
        else if (direction == TileDirection.Up)
        {
            targetX = originX + originNormalX * len;
            targetY = originY + originNormalY * len;
            targetdx = targetX - positionX;
            targetdy = targetY - positionY;
            targetDistance = (float) Math.sqrt(targetdx * targetdx + targetdy * targetdy);

            if (originDistance > circleHeights[circleHeights.length - 1])
            {
                //Origin point outside, not hit!
                return false;
            }

            hitInfo.hitTileX = (int) ((originMapAngle / (MathUtils.PI * 2.0f)) * width);
            hitInfo.hitTileX = hitInfo.hitTileX % width;

            for (int i = 1; i < circleHeights.length; i++)
            {
                if (originDistance < circleHeights[i])
                {
                    hitInfo.hitTileY = i;
                    len -= circleHeights[i] - originDistance;
                    break;
                }
            }

            while (hitInfo.hitTileY < height && len >= 0)
            {
                if (GetTile(hitInfo.hitTileX, hitInfo.hitTileY) != 0)
                {
                    hitInfo.hitNormalX = -originNormalX;
                    hitInfo.hitNormalY = -originNormalY;
                    hitInfo.hitPositionX = positionX + originNormalX * circleHeights[hitInfo.hitTileY];
                    hitInfo.hitPositionY = positionY + originNormalY * circleHeights[hitInfo.hitTileY];
                    
                    float hitDistanceX = originX - hitInfo.hitPositionX;
                    float hitDistanceY = originY - hitInfo.hitPositionY;
                    
                    hitInfo.hitDistance = (float) Math.sqrt(hitDistanceX * hitDistanceX + hitDistanceY * hitDistanceY);
                    
                    return true;
                }

                if (hitInfo.hitTileY < height - 1 )
                    len -= (circleHeights[hitInfo.hitTileY + 1] - circleHeights[hitInfo.hitTileY]);

                hitInfo.hitTileY++;
            }
        }
        else if (direction == TileDirection.Down)
        {
            targetX = originX - originNormalX * len;
            targetY = originY - originNormalY * len;
            targetdx = targetX - positionX;
            targetdy = targetY - positionY;
            targetDistance = (float) Math.sqrt(targetdx * targetdx + targetdy * targetdy);

            if (/*originDistance > circleHeights[circleHeights.Length - 1] &&*/
                targetDistance > circleHeights[circleHeights.length - 1])
            {
                //Target outside, no hit!
                return false;
            }
            else if (targetDistance < circleHeights[0])
            {
                //Target inside core, core hit!
                hitInfo.hitTileY = 0;
                hitInfo.hitNormalX = originNormalX;
                hitInfo.hitNormalY = originNormalY;
                hitInfo.hitPositionX = positionX + originNormalX * circleHeights[0];
                hitInfo.hitPositionY = positionY + originNormalY * circleHeights[0];
                
                float hitDistanceX = originX - hitInfo.hitPositionX;
                float hitDistanceY = originY - hitInfo.hitPositionY;
                
                hitInfo.hitDistance = (float) Math.sqrt(hitDistanceX * hitDistanceX + hitDistanceY * hitDistanceY);
                
                return true;
            }

            hitInfo.hitTileX = (int) ((originMapAngle / (MathUtils.PI * 2.0f)) * width);
            hitInfo.hitTileX = hitInfo.hitTileX % width;

            for (int i = circleHeights.length - 1; i >= 1; i--)
            {
                if (originDistance > circleHeights[i])
                {
                    hitInfo.hitTileY = i - 1;
                    len -= originDistance - circleHeights[i];
                    break;
                }
            }

            while (hitInfo.hitTileY >= 0 && len > 0)
            {
                if (GetTile(hitInfo.hitTileX, hitInfo.hitTileY) != 0)
                {
                    hitInfo.hitNormalX = originNormalX;
                    hitInfo.hitNormalY = originNormalY;
                    hitInfo.hitPositionX = positionX + originNormalX * circleHeights[hitInfo.hitTileY + 1];
                    hitInfo.hitPositionY = positionY + originNormalX * circleHeights[hitInfo.hitTileY + 1];
                    
                    float hitDistanceX = originX - hitInfo.hitPositionX;
                    float hitDistanceY = originY - hitInfo.hitPositionY;
                    
                    hitInfo.hitDistance = (float) Math.sqrt(hitDistanceX * hitDistanceX + hitDistanceY * hitDistanceY);

                    return true;
                }

                if (hitInfo.hitTileY > 0)
                    len -= (circleHeights[hitInfo.hitTileY] - circleHeights[hitInfo.hitTileY - 1]);
                hitInfo.hitTileY--;
            }
        }

        return false;
    }
    
    public void Recycle()
    {
        DataPools.poolVector2.ReturnArray(circleNormals);
        circleNormals = null;
        
        DataPools.poolFloat.ReturnArray(circleHeights);
        circleHeights = null;
        
        DataPools.poolByte.ReturnArray(tiles);
        tiles = null;
        
        listener = null;
    }
}
