package com.fdangelo.circleworld.universeengine.objects;

public class ShipInput {
	public float moveDirection;
	public float rotateDirection;

	public final void reset() {
		moveDirection = 0;
		rotateDirection = 0;
	}
}
