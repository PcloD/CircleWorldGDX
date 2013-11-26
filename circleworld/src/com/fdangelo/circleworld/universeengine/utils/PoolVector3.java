package com.fdangelo.circleworld.universeengine.utils;

import com.badlogic.gdx.math.Vector3;

public final class PoolVector3 {
	private final Vector3[][] pool;
	private int poolSize;

	public PoolVector3(final int maxPoolSize) {
		pool = new Vector3[maxPoolSize][];
	}

	public final Vector3[] getArray(final int size) {
		for (int i = 0; i < poolSize; i++) {
			if (pool[i].length == size) {
				final Vector3[] toReturn = pool[i];
				pool[i] = pool[poolSize - 1];
				poolSize--;
				return toReturn;
			}
		}

		final Vector3[] toReturn = new Vector3[size];
		for (int i = 0; i < size; i++) {
			toReturn[i] = new Vector3();
		}

		return toReturn;
	}

	public final void returnArray(final Vector3[] array) {
		if (array != null) {
			pool[poolSize++] = array;
		}
	}
}
