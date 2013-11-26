package com.fdangelo.circleworld.universeengine.tilemap;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.fdangelo.circleworld.GameLogic;

public class TileTypes {
	static private TileType[] tileTypes;

	static public TileType[] getTileTypes() {
		if (tileTypes == null) {
			initTileTypes();
		}

		return tileTypes;
	}

	static public TileType getTileType(final byte id) {
		return getTileTypes()[id];
	}

	static private TextureAtlas atlas;

	static public void initTileTypes() {
		final AssetManager assetManager = GameLogic.instace.assetManager;

		atlas = assetManager.get("atlas/tilemap.atlas", TextureAtlas.class);

		tileTypes = new TileType[256];
		for (int i = 0; i < tileTypes.length; i++) {
			tileTypes[i] = new TileType();
		}

		tileTypes[1].center = getTileSubtypeUV("grassCenter");
		tileTypes[1].top = getTileSubtypeUV("grassMid");

		tileTypes[2].center = getTileSubtypeUV("sandCenter");
		tileTypes[2].top = getTileSubtypeUV("sandMid");

		tileTypes[3].center = getTileSubtypeUV("snowCenter");
		tileTypes[3].top = getTileSubtypeUV("snowMid");

		tileTypes[4].center = getTileSubtypeUV("stoneCenter");
		tileTypes[4].top = getTileSubtypeUV("stoneMid");

		tileTypes[5].center = getTileSubtypeUV("sun1Center");
		tileTypes[5].top = getTileSubtypeUV("sun1Mid");

		tileTypes[6].center = getTileSubtypeUV("sun2Center");
		tileTypes[6].top = getTileSubtypeUV("sun2Mid");
	}

	static private TileSubtype getTileSubtypeUV(final String id) {
		final TileSubtype subtype = new TileSubtype();

		final AtlasRegion region = atlas.findRegion(id);

		subtype.uvFromX = region.getU();
		subtype.uvToX = region.getU2();
		subtype.uvFromY = region.getV();
		subtype.uvToY = region.getV2();

		return subtype;
	}
}
