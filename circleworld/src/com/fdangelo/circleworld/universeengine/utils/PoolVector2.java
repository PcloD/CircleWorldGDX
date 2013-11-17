package com.fdangelo.circleworld.universeengine.utils;

import com.badlogic.gdx.math.Vector2;

public class PoolVector2 
{
    private Vector2[][] pool;
    private int poolSize;
    
    public PoolVector2(int maxPoolSize)
    {
        pool = new Vector2[maxPoolSize][];
    }
    
    public Vector2[] GetArray(int size)
    {
        for (int i = 0; i < poolSize; i++)
        {
            if (pool[i].length == size)
            {
            	Vector2[] toReturn = pool[i];
                pool[i] = pool[poolSize - 1];
                poolSize--;
                return toReturn;
            }
        }
        
        Vector2[] toReturn = new Vector2[size];
        for (int i = 0; i < size; i++)
        	toReturn[i] = new Vector2();
        
        return toReturn;
    }
    
    public void ReturnArray(Vector2[] array)
    {
        if (array != null)
            pool[poolSize++] = array;
    }
}
