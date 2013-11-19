package com.fdangelo.circleworld.universeview.objects;

import com.badlogic.gdx.math.Rectangle;

public class InputAreas
{
    static private Rectangle[] inputAreas = new Rectangle[10];
    static private int inputAreasCount;
    
        
    static public void ResetInputAreas()
    {
        inputAreasCount = 0;
    }
    
    static public void AddInputArea(Rectangle rect)
    {
    	if (inputAreas[inputAreasCount] == null)
    		inputAreas[inputAreasCount] = new Rectangle();
    	
        inputAreas[inputAreasCount++] = rect;
    }

    static public boolean IsInputArea(float x, float y)
    {
        //Convert from Input coordinate system to GUI coordinate system
        for (int i = 0; i < inputAreasCount; i++)
            if (inputAreas[i].contains(x, y))
                return true;
        
        return false;
    }
}


