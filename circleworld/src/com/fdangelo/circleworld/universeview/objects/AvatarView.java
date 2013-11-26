package com.fdangelo.circleworld.universeview.objects;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.fdangelo.circleworld.GameLogic;

public class AvatarView extends UniverseObjectView {
	private final AvatarViewInput input;
	private final TextureRegion sprite;

	public AvatarView() {
		input = new AvatarViewInput(this);
		sprite = GameLogic.instace.assetManager.get("atlas/player1.atlas", TextureAtlas.class).findRegion("p1_stand");
	}

	@Override
	public void act(final float delta) {
		super.act(delta);
		input.update(delta);
	}

	@Override
	public void draw(final Batch batch, final float parentAlpha) {
		batch.draw(sprite, getX(), getY(), getOriginX(), getOriginY(), getWidth(), getHeight(), getScaleX(), getScaleY(), -getRotation());
	}

	/*
	 * public override void OnDrawGizmos () { float sizeY = 1.05f; float sizeX =
	 * 0.75f; Gizmos.color = Color.red; Gizmos.DrawLine(transform.position,
	 * transform.position + transform.up * sizeY); Gizmos.color = Color.blue;
	 * Gizmos.DrawLine(transform.position + transform.up * sizeY * 0.5f -
	 * transform.right * sizeX * 0.5f, transform.position + transform.up * sizeY
	 * * 0.5f + transform.right * sizeX * 0.5f); }
	 */
}
