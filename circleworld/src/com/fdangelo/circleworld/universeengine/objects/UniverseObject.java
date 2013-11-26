package com.fdangelo.circleworld.universeengine.objects;

import com.badlogic.gdx.math.Vector2;
import com.fdangelo.circleworld.universeengine.tilemap.Planet;
import com.fdangelo.circleworld.universeengine.tilemap.TileDirection;
import com.fdangelo.circleworld.universeengine.tilemap.TileHitFlags;
import com.fdangelo.circleworld.universeengine.tilemap.TileHitInfo;
import com.fdangelo.circleworld.universeengine.tilemap.TilemapCircle;

public class UniverseObject {
	protected TilemapCircle parent;

	protected boolean useGravity = true;

	protected float positionX;
	protected float positionY;
	protected float scale = 1.0f;
	protected float rotation = 0.0f; // radians

	protected float sizeX = 1;
	protected float sizeY = 1;

	protected boolean visible = true;

	protected float velocityX;
	protected float velocityY;
	protected float rotationVelocity;

	// Uses TileHitFlags
	protected int hitFlags;

	protected float distanceInTilemapCircle;
	protected float angleInTilemapCirclePosition;

	protected IUniverseObjectListener listener;

	protected boolean parentFollowScale;
	protected boolean parentFollowRotation;
	protected boolean parentCheckCollisions;

	static private TileHitInfo tmpHitInfo = new TileHitInfo();

	public final float getPositionX() {
		return positionX;
	}

	public final float getPositionY() {
		return positionY;
	}

	public final float getScale() {
		return scale;
	}

	public final float getRotation() {
		return rotation;
	}

	public final float getSizeX() {
		return sizeX;
	}

	public final float getSizeY() {
		return sizeY;
	}

	public final float getVelocityX() {
		return velocityX;
	}

	public final float getVelocityY() {
		return velocityY;
	}

	public final void setVelocity(final float x, final float y) {
		velocityX = x;
		velocityY = y;
	}

	public final int getHitFlags() {
		return hitFlags;
	}

	public final IUniverseObjectListener getListener() {
		return listener;
	}

	public final void setListener(final IUniverseObjectListener value) {
		listener = value;
	}

	public final boolean getVisible() {
		return visible;
	}

	public final void setVisible(final boolean value) {
		visible = value;
		if (listener != null) {
			listener.onUniverseObjectUpdated(0.0f);
		}
	}

	public final TilemapCircle getParent() {
		return parent;
	}

	public void init(final float sizeX, final float sizeY, final TilemapCircle parent, final int followParentParameters, final float positionX,
			final float positionY, final float rotation) {
		this.sizeX = sizeX;
		this.sizeY = sizeY;

		setParent(parent, followParentParameters, positionX, positionY, rotation);
	}

	public final void setParent(final TilemapCircle parent, final int followParentParameters, final float positionX, final float positionY, final float rotation) {
		this.parent = parent;
		this.positionX = positionX;
		this.positionY = positionY;
		this.rotation = rotation;
		parentFollowScale = (followParentParameters & FollowParentParameters.FollowScale) != 0;
		parentFollowRotation = (followParentParameters & FollowParentParameters.FollowRotation) != 0;
		parentCheckCollisions = (followParentParameters & FollowParentParameters.CheckCollisions) != 0;

		if (parent != null) {
			if (parentFollowScale) {
				scale = parent.getScaleFromPosition(positionX, positionY);
			}

			if (parentFollowRotation) {
				this.rotation = parent.getAngleFromPosition(positionX, positionY);
			}

			distanceInTilemapCircle = parent.getDistanceFromPosition(positionX, positionY);
			angleInTilemapCirclePosition = parent.getAngleFromPosition(positionX, positionY);
		}

		if (listener != null) {
			listener.onParentChanged(parent);
		}
	}

	public final void update(final float deltaTime) {
		onUpdate(deltaTime);

		updatePosition(deltaTime);

		if (listener != null) {
			listener.onUniverseObjectUpdated(deltaTime);
		}
	}

	protected void onUpdate(final float deltaTime) {

	}

	protected final void updatePosition(final float deltaTime) {
		float normalX, normalY;
		float tangentX, tangentY;

		float deltaPositionX, deltaPositionY;
		float deltaRotation;

		if (parent != null) {
			final Vector2 positionInParent = parent.getPositionFromDistanceAndAngle(distanceInTilemapCircle, angleInTilemapCirclePosition);

			positionX = positionInParent.x;
			positionY = positionInParent.y;

			if (parentFollowRotation) {
				rotation = parent.getAngleFromPosition(positionX, positionY);
			}

			if (parentFollowScale) {
				scale = parent.getScaleFromPosition(positionX, positionY);
			}

			if (parent instanceof Planet && useGravity) {
				velocityY -= ((Planet) parent).getGravity() * deltaTime;
			}

			final Vector2 normal = parent.getNormalFromPosition(positionX, positionY); // doesn't
																						// change
																						// with
																						// vertical
																						// position
			normalX = normal.x;
			normalY = normal.y;

			final Vector2 tangent = parent.getTangentFromPosition(positionX, positionY); // doesn't
																							// change
																							// with
																							// vertical
																							// position
			tangentX = tangent.x;
			tangentY = tangent.y;

			deltaPositionX = velocityX * deltaTime * scale;
			deltaPositionY = velocityY * deltaTime * scale;

			if (parentFollowRotation) {
				deltaRotation = 0.0f;
			} else {
				deltaRotation = rotationVelocity * deltaTime;
			}
		} else {
			normalY = 1;
			normalX = 0;

			tangentY = 0;
			tangentX = 1;

			deltaPositionX = velocityX * deltaTime * scale;
			deltaPositionY = velocityY * deltaTime * scale;
			deltaRotation = rotationVelocity * deltaTime;
		}

		hitFlags = TileHitFlags.None;

		if (parent != null && parentCheckCollisions) {
			if (deltaPositionY > 0) {
				// Check against ceiling
				if (parent.raycastSquare(positionX + normalX * (sizeY * 0.5f * scale), positionY + normalY * (sizeY * 0.5f * scale), sizeX * scale,
						TileDirection.Up, deltaPositionY + (sizeY * 0.5f * scale), tmpHitInfo)) {
					deltaPositionY = -(tmpHitInfo.hitDistance - (sizeY * 0.5f * scale));
					velocityY = 0.0f;
					hitFlags |= TileHitFlags.Up;
				}
			} else if (deltaPositionY < 0) {
				// Check against floor
				if (parent.raycastSquare(positionX + normalX * (sizeY * 0.5f * scale), positionY + normalY * (sizeY * 0.5f * scale), sizeX * scale,
						TileDirection.Down, -deltaPositionY + (sizeY * 0.5f * scale), tmpHitInfo)) {
					deltaPositionY = -(tmpHitInfo.hitDistance - (sizeY * 0.5f * scale));
					velocityY = 0.0f;
					hitFlags |= TileHitFlags.Down;
				}
			}
		}

		if (deltaPositionY != 0) {
			positionY += normalY * deltaPositionY;
			positionX += normalX * deltaPositionY;
			if (parent != null && parentFollowScale) {
				scale = parent.getScaleFromPosition(positionX, positionY);
			}
		}

		if (parent != null && parentCheckCollisions) {
			if (deltaPositionX > 0) {
				// Check against right wall
				if (parent.raycastSquare(positionX + normalX * (sizeY * 0.5f * scale), positionY + normalY * (sizeY * 0.5f * scale), sizeY * scale,
						TileDirection.Right, deltaPositionX + (sizeX * 0.5f * scale), tmpHitInfo)) {
					deltaPositionX = (tmpHitInfo.hitDistance - (sizeX * 0.5f * scale));
					velocityX = 0.0f;
					hitFlags |= TileHitFlags.Right;
				}
			} else if (deltaPositionX < 0) {
				// Check against left wall
				if (parent.raycastSquare(positionX + normalX * (sizeY * 0.5f * scale), positionY + normalY * (sizeY * 0.5f * scale), sizeY * scale,
						TileDirection.Left, -deltaPositionX + (sizeX * 0.5f * scale), tmpHitInfo)) {
					deltaPositionX = -(tmpHitInfo.hitDistance - (sizeX * 0.5f * scale));
					velocityX = 0.0f;
					hitFlags |= TileHitFlags.Left;
				}
			}
		}

		if (deltaPositionX != 0) {
			positionX += tangentX * deltaPositionX;
			positionY += tangentY * deltaPositionX;
			if (parent != null) {
				final Vector2 normal = parent.getNormalFromPosition(positionX, positionY);
				normalX = normal.x;
				normalY = normal.y;
			}
		}

		if (parent != null) {
			if (parentFollowRotation) {
				rotation = parent.getAngleFromPosition(positionX, positionY);
			} else {
				rotation += deltaRotation;
			}

			distanceInTilemapCircle = parent.getDistanceFromPosition(positionX, positionY);
			angleInTilemapCirclePosition = parent.getAngleFromPosition(positionX, positionY);
		} else {
			rotation += deltaRotation;
		}
	}

	/*
	 * public bool MoveTo(Vector2 position) { if (CanMoveTo(position)) {
	 * this.position = position; return true; } return false; } public bool
	 * CanMoveTo(Vector2 position) { float scale =
	 * tilemapCircle.GetScaleFromPosition(position); int tileX, tileY; Vector2
	 * right = transform.right; Vector2 up = transform.up; position += up *
	 * 0.05f; for (int x = -1; x <= 1; x++) { for (int y = 0; y <= 2; y++) {
	 * Vector2 pos = position + right * (size.x * 0.9f * x * 0.5f * scale) + up
	 * * ((size.y * 0.9f / 2) * y * scale); if
	 * (tilemapCircle.GetTileCoordinatesFromPosition(pos, out tileX, out tileY))
	 * if (tilemapRicle.GetTile(tileX, tileY) != 0) return false; } } return
	 * true; }
	 */
}
