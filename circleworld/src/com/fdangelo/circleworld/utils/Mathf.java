package com.fdangelo.circleworld.utils;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

//Replacement for functions in Unity.Mathf
public class Mathf 
{
	static public float lerp(float from, float to, float t)
	{
		return from + (to - from) * t;
	}
	
	static private Vector2 tmplerpv2 = new Vector2();
	static public Vector2 lerp(Vector2 from, Vector2 to, float t)
	{
		tmplerpv2.set(to.x - from.x, to.y - from.y);
		tmplerpv2.scl(t);
		tmplerpv2.add(from);
		return tmplerpv2;
	}
	
	static private Vector3 tmplerpv3 = new Vector3();
	static public Vector3 lerp(Vector3 from, Vector3 to, float t)
	{
		tmplerpv3.set(to.x - from.x, to.y - from.y, to.z - from.z);
		tmplerpv3.scl(t);
		tmplerpv3.add(from);
		return tmplerpv3;
	}
	
	static public float Sign(float v)
	{
		if (v >= 0.0f)
			return 1.0f;
		else
			return -1.0f;
	}
}
