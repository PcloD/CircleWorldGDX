package com.fdangelo.circleworld.universeengine.utils;

public class PoolInt
{
    private int[][] pool;
    private int poolSize;
    
    public PoolInt(int maxPoolSize)
    {
        pool = new int[maxPoolSize][];
    }
    
    public int[] GetArray(int size)
    {
        for (int i = 0; i < poolSize; i++)
        {
            if (pool[i].length == size)
            {
            	int[] toReturn = pool[i];
                pool[i] = pool[poolSize - 1];
                poolSize--;
                return toReturn;
            }
        }
        
        int[] toReturn = new int[size];
        
        return toReturn;
    }
    
    public void ReturnArray(int[] array)
    {
        if (array != null)
            pool[poolSize++] = array;
    }
}
