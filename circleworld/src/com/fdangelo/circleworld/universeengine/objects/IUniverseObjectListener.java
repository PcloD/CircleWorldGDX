package com.fdangelo.circleworld.universeengine.objects;

import com.fdangelo.circleworld.universeengine.tilemap.TilemapCircle;

public interface IUniverseObjectListener {
	void onUniverseObjectUpdated(float deltaTime);

	void onParentChanged(TilemapCircle parent);
}
