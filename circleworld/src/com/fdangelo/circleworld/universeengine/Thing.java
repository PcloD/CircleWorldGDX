package com.fdangelo.circleworld.universeengine;

public class Thing
{
    public short type; //type of thing (use ThingType!)
    public short childs; //number of childs

    public short parent; //Index of parent
    public short nextBrother; //Index of next Thing with same parent

    public short angle; //Rotation relative to parent (centidegrees (100 -> 1 degree, 36000 -> 360 degrees)
    public short distance; //Distance relative to parent

    public short rotationPeriod; //seconds to complete a full rotation around itself (negative -> rotation counterclockwise)
    public short orbitalPeriod; //seconds to complete a full rotation around the parent (negative -> rotation counterclockwise)

    public short radius; //Radius
    public short safeRadius; //Radius at which things can orbit this thing without colliding with any brother

    public int seed; //Seed used to create the thing

    //Cache
    public float orbitalPeriodInv;
    public float rotationPeriodInv;
}

