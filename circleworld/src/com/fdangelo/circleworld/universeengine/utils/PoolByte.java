package com.fdangelo.circleworld.universeengine.utils;

public final class PoolByte {
	private final byte[][] pool;
	private int poolSize;

	public PoolByte(final int maxPoolSize) {
		pool = new byte[maxPoolSize][];
	}

	public final byte[] getArray(final int size) {
		for (int i = 0; i < poolSize; i++) {
			if (pool[i].length == size) {
				final byte[] toReturn = pool[i];
				pool[i] = pool[poolSize - 1];
				poolSize--;
				return toReturn;
			}
		}

		final byte[] toReturn = new byte[size];

		return toReturn;
	}

	public final void returnArray(final byte[] array) {
		if (array != null) {
			pool[poolSize++] = array;
		}
	}
}
