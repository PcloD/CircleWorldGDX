package com.fdangelo.circleworld.universeengine.utils;

import com.badlogic.gdx.math.Vector2;

public final class PoolVector2 {
	private final Vector2[][] pool;
	private int poolSize;

	public PoolVector2(final int maxPoolSize) {
		pool = new Vector2[maxPoolSize][];
	}

	public final Vector2[] getArray(final int size) {
		for (int i = 0; i < poolSize; i++) {
			if (pool[i].length == size) {
				final Vector2[] toReturn = pool[i];
				pool[i] = pool[poolSize - 1];
				poolSize--;
				return toReturn;
			}
		}

		final Vector2[] toReturn = new Vector2[size];
		for (int i = 0; i < size; i++) {
			toReturn[i] = new Vector2();
		}

		return toReturn;
	}

	public final void returnArray(final Vector2[] array) {
		if (array != null) {
			pool[poolSize++] = array;
		}
	}
}
