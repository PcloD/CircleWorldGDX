package com.fdangelo.circleworld.universeview.objects;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.ShortArray;
import com.fdangelo.circleworld.GameLogic;
import com.fdangelo.circleworld.GameLogicState;
import com.fdangelo.circleworld.universeview.UniverseView;

public class ShipView extends UniverseObjectView {
	private final ShipViewInput input;
	private final TextureRegion sprite;

	public ShipView() {
		input = new ShipViewInput(this);
		sprite = GameLogic.instace.assetManager.get("atlas/ships.atlas", TextureAtlas.class).findRegion("Battleship");
	}

	@Override
	public void act(final float delta) {
		super.act(delta);
		input.Update(delta);
	}

	@Override
	public void draw(final Batch batch, final float parentAlpha) {
		batch.draw(sprite, getX() - getWidth() * 0.5f, getY() - getHeight() * 0.5f, getWidth() * 0.5f, getHeight() * 0.5f, getWidth(), getHeight(),
				getScaleX(), getScaleY(), -getRotation() + 90.0f); // ,
																	// getUniverseObject().getSizeX(),
																	// getUniverseObject().getSizeY());
	}

	/*
	 * public override void OnDrawGizmos () { float sizeY = 1.0f; float sizeX =
	 * 1.0f; Gizmos.color = Color.red; Gizmos.DrawLine(transform.position,
	 * transform.position + transform.up * sizeY); Gizmos.color = Color.blue;
	 * Gizmos.DrawLine(transform.position - transform.right * sizeX * 0.5f,
	 * transform.position + transform.right * sizeX * 0.5f); }
	 */

	private ShortArray closeThings;

	@Override
	public void onUniverseObjectUpdated(final float deltaTime) {
		super.onUniverseObjectUpdated(deltaTime);

		if (GameLogic.instace.getState() == GameLogicState.PlayingShip) {
			closeThings = universeView.getUniverse().findClosestRenderedThings(universeObject.getPositionX(), universeObject.getPositionY(), 100.0f,
					closeThings);

			for (int i = 0; i < closeThings.size && i < UniverseView.MaxActivePlanetViews; i++) {
				universeView.getPlanetView(closeThings.get(i));
			}
		}
	}
}
