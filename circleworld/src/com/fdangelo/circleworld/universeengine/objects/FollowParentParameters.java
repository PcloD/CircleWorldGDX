package com.fdangelo.circleworld.universeengine.objects;

public class FollowParentParameters
{
    static public final int None = 0;
    static public final int FollowRotation = 1 << 1;
    static public final int FollowScale = 1 << 2;
    static public final int CheckCollisions = 1 << 3;
    static public final int Default = FollowRotation | FollowScale | CheckCollisions;
}
