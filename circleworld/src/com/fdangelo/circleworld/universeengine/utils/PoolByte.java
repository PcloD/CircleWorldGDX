package com.fdangelo.circleworld.universeengine.utils;

public class PoolByte
{
    private byte[][] pool;
    private int poolSize;
    
    public PoolByte(int maxPoolSize)
    {
        pool = new byte[maxPoolSize][];
    }
    
    public byte[] GetArray(int size)
    {
        for (int i = 0; i < poolSize; i++)
        {
            if (pool[i].length == size)
            {
            	byte[] toReturn = pool[i];
                pool[i] = pool[poolSize - 1];
                poolSize--;
                return toReturn;
            }
        }
        
        byte[] toReturn = new byte[size];
        
        return toReturn;
    }
    
    public void ReturnArray(byte[] array)
    {
        if (array != null)
            pool[poolSize++] = array;
    }
}
