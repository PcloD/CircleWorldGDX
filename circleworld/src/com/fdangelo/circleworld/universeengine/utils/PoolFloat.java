package com.fdangelo.circleworld.universeengine.utils;

public class PoolFloat
{
    private float[][] pool;
    private int poolSize;
    
    public PoolFloat(int maxPoolSize)
    {
        pool = new float[maxPoolSize][];
    }
    
    public float[] GetArray(int size)
    {
        for (int i = 0; i < poolSize; i++)
        {
            if (pool[i].length == size)
            {
            	float[] toReturn = pool[i];
                pool[i] = pool[poolSize - 1];
                poolSize--;
                return toReturn;
            }
        }
        
        float[] toReturn = new float[size];
        
        return toReturn;
    }
    
    public void ReturnArray(float[] array)
    {
        if (array != null)
            pool[poolSize++] = array;
    }
}
