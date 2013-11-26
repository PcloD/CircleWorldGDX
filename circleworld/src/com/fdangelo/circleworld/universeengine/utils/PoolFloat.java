package com.fdangelo.circleworld.universeengine.utils;

public final class PoolFloat {
	private final float[][] pool;
	private int poolSize;

	public PoolFloat(final int maxPoolSize) {
		pool = new float[maxPoolSize][];
	}

	public final float[] getArray(final int size) {
		for (int i = 0; i < poolSize; i++) {
			if (pool[i].length == size) {
				final float[] toReturn = pool[i];
				pool[i] = pool[poolSize - 1];
				poolSize--;
				return toReturn;
			}
		}

		final float[] toReturn = new float[size];

		return toReturn;
	}

	public final void returnArray(final float[] array) {
		if (array != null) {
			pool[poolSize++] = array;
		}
	}
}
