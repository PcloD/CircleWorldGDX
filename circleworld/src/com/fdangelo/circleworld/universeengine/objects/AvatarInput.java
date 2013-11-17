package com.fdangelo.circleworld.universeengine.objects;

public class AvatarInput
{
    public float walkDirection;
    public boolean jump;
    
    public void Reset()
    {
        walkDirection = 0.0f;
        jump = false;
    }
}
