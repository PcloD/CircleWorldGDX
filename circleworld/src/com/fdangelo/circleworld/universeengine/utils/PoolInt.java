package com.fdangelo.circleworld.universeengine.utils;

public final class PoolInt {
	private final int[][] pool;
	private int poolSize;

	public PoolInt(final int maxPoolSize) {
		pool = new int[maxPoolSize][];
	}

	public final int[] getArray(final int size) {
		for (int i = 0; i < poolSize; i++) {
			if (pool[i].length == size) {
				final int[] toReturn = pool[i];
				pool[i] = pool[poolSize - 1];
				poolSize--;
				return toReturn;
			}
		}

		final int[] toReturn = new int[size];

		return toReturn;
	}

	public final void returnArray(final int[] array) {
		if (array != null) {
			pool[poolSize++] = array;
		}
	}
}
