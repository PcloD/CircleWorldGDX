package com.fdangelo.circleworld.universeengine.utils;

import com.badlogic.gdx.graphics.Color;

public class PoolColor 
{
    private Color[][] pool;
    private int poolSize;
    
    public PoolColor(int maxPoolSize)
    {
        pool = new Color[maxPoolSize][];
    }
    
    public Color[] GetArray(int size)
    {
        for (int i = 0; i < poolSize; i++)
        {
            if (pool[i].length == size)
            {
            	Color[] toReturn = pool[i];
                pool[i] = pool[poolSize - 1];
                poolSize--;
                return toReturn;
            }
        }
        
        Color[] toReturn = new Color[size];
        for (int i = 0; i < size; i++)
        	toReturn[i] = new Color();
        
        return toReturn;
    }
    
    public void ReturnArray(Color[] array)
    {
        if (array != null)
            pool[poolSize++] = array;
    }
}
