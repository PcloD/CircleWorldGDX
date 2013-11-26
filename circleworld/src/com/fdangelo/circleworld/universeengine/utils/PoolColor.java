package com.fdangelo.circleworld.universeengine.utils;

import com.badlogic.gdx.graphics.Color;

public final class PoolColor {
	private final Color[][] pool;
	private int poolSize;

	public PoolColor(final int maxPoolSize) {
		pool = new Color[maxPoolSize][];
	}

	public final Color[] getArray(final int size) {
		for (int i = 0; i < poolSize; i++) {
			if (pool[i].length == size) {
				final Color[] toReturn = pool[i];
				pool[i] = pool[poolSize - 1];
				poolSize--;
				return toReturn;
			}
		}

		final Color[] toReturn = new Color[size];
		for (int i = 0; i < size; i++) {
			toReturn[i] = new Color();
		}

		return toReturn;
	}

	public final void returnArray(final Color[] array) {
		if (array != null) {
			pool[poolSize++] = array;
		}
	}
}
