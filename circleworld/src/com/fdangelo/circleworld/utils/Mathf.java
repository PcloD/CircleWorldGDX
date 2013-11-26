package com.fdangelo.circleworld.utils;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

//Replacement for functions in Unity.Mathf
public class Mathf {
	static public float lerp(final float from, final float to, final float t) {
		return from + (to - from) * t;
	}

	static private Vector2 tmplerpv2 = new Vector2();

	static public Vector2 lerp(final Vector2 from, final Vector2 to, final float t) {
		tmplerpv2.set(to.x - from.x, to.y - from.y);
		tmplerpv2.scl(t);
		tmplerpv2.add(from);
		return tmplerpv2;
	}

	static private Vector3 tmplerpv3 = new Vector3();

	static public Vector3 lerp(final Vector3 from, final Vector3 to, final float t) {
		tmplerpv3.set(to.x - from.x, to.y - from.y, to.z - from.z);
		tmplerpv3.scl(t);
		tmplerpv3.add(from);
		return tmplerpv3;
	}

	static public float Sign(final float v) {
		if (v >= 0.0f) {
			return 1.0f;
		} else {
			return -1.0f;
		}
	}

	static public float len(final float dx, final float dy) {
		return (float) Math.sqrt(dx * dx + dy * dy);
	}

	static public float atan2(final float dy, final float dx) {
		return (float) Math.atan2(dy, dx);
	}

	static public float sin(final float rad) {
		return (float) Math.sin(rad);
	}

	static public float cos(final float rad) {
		return (float) Math.cos(rad);
	}
}
