package com.fdangelo.circleworld.universeengine;

import java.util.Random;

public class UniverseGenerator {
	protected Thing[] things;
	protected Random random;
	protected short thingsAmount;
	protected short currentThing;

	public final short generate(final int seed, final Thing[] things) {
		this.things = things;
		random = new Random(seed);

		things[0].type = ThingType.Galaxy;
		things[0].safeRadius = Short.MAX_VALUE;

		currentThing = 0;
		thingsAmount = 1;

		addGalaxy();

		updateBrothers((short) 0);

		this.things = null;
		random = null;

		return thingsAmount;
	}

	protected void addGalaxy() {

	}

	private final short updateBrothers(short index) {
		final int childs = things[index].childs;
		index++;

		short prevBrother = 0;

		for (int i = 0; i < childs; i++) {
			prevBrother = index;

			index = updateBrothers(index);

			if (i + 1 < childs) {
				things[prevBrother].nextBrother = index;
			}
		}

		return index;
	}

	protected final void pushThing(final short type, final short angle, final short distance, short rotationPeriod, final short orbitalPeriod,
			final short radius, final short safeRadius, final int seed) {

		// TODO: Remove once planet rotations are correctly implemented
		rotationPeriod = 0;

		// TEST
		// orbitalPeriod = 0;

		things[currentThing].childs++;

		things[thingsAmount].parent = currentThing;
		things[thingsAmount].type = type;

		things[thingsAmount].angle = angle;
		things[thingsAmount].distance = distance;
		things[thingsAmount].rotationPeriod = rotationPeriod;
		things[thingsAmount].orbitalPeriod = orbitalPeriod;
		things[thingsAmount].radius = radius;
		things[thingsAmount].safeRadius = safeRadius;
		things[thingsAmount].seed = seed;

		if (orbitalPeriod != 0) {
			things[thingsAmount].orbitalPeriodInv = 1.0f / orbitalPeriod;
		} else {
			things[thingsAmount].orbitalPeriodInv = 0.0f;
		}

		if (rotationPeriod != 0) {
			things[thingsAmount].rotationPeriodInv = 1.0f / rotationPeriod;
		} else {
			things[thingsAmount].rotationPeriodInv = 0.0f;
		}

		currentThing = thingsAmount;
		thingsAmount++;
	}

	protected void popThing() {
		currentThing = things[currentThing].parent;
	}
}
