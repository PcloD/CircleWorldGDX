package com.fdangelo.circleworld.universeengine;

public class Thing {
	// type of thing (use ThingType!)
	public short type;

	// number of child
	public short childs;

	// Index of parent
	public short parent;

	// Index of next Thing with same parent
	public short nextBrother;

	// Rotation relative to parent (centidegrees (100 -> 1 degree, 36000 -> 360
	// degrees)
	public short angle;

	// Distance relative to parent
	public short distance;

	// seconds to complete a full rotation around itself (negative -> rotation
	// counterclockwise)
	public short rotationPeriod;

	// seconds to complete a full rotation around the parent (negative ->
	// rotation counterclockwise)
	public short orbitalPeriod;

	// Radius
	public short radius;

	// Radius at which things can orbit this thing without colliding with any
	// brother
	public short safeRadius;

	// Seed used to create the thing
	public int seed;

	// Cache
	public float orbitalPeriodInv;
	public float rotationPeriodInv;
}
