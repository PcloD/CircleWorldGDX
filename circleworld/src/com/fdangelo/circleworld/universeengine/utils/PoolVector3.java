package com.fdangelo.circleworld.universeengine.utils;

import com.badlogic.gdx.math.Vector3;

public class PoolVector3 
{
    private Vector3[][] pool;
    private int poolSize;
    
    public PoolVector3(int maxPoolSize)
    {
        pool = new Vector3[maxPoolSize][];
    }
    
    public Vector3[] GetArray(int size)
    {
        for (int i = 0; i < poolSize; i++)
        {
            if (pool[i].length == size)
            {
            	Vector3[] toReturn = pool[i];
                pool[i] = pool[poolSize - 1];
                poolSize--;
                return toReturn;
            }
        }
        
        Vector3[] toReturn = new Vector3[size];
        for (int i = 0; i < size; i++)
        	toReturn[i] = new Vector3();
        
        return toReturn;
    }
    
    public void ReturnArray(Vector3[] array)
    {
        if (array != null)
            pool[poolSize++] = array;
    }
}
