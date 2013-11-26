package com.fdangelo.circleworld.universeengine.tilemap;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.fdangelo.circleworld.universeengine.utils.DataPools;
import com.fdangelo.circleworld.utils.Mathf;
import com.fdangelo.circleworld.utils.Vector2I;

public abstract class TilemapCircle {
	public static final float TILE_SIZE = 0.5f;
	public static final float TILE_SIZE_INV = 1.0f / TILE_SIZE;

	protected int seed;
	protected int height;
	protected int width;

	protected byte[] tiles;

	protected Vector2[] circleNormals;
	protected float[] circleHeights;

	// Used when finding tileY positions!
	private float height0;
	private float k;
	private float logk;

	protected ITilemapCircleListener listener;

	protected float positionX;
	protected float positionY;
	protected float rotation;

	static private TileHitInfo tmpHitInfo = new TileHitInfo();
	static private Vector2 tmpv1 = new Vector2();

	public final int getHeight() {
		return height;
	}

	public final int getWidth() {
		return width;
	}

	public final int getSeed() {
		return seed;
	}

	public final float getPositionX() {
		return positionX;
	}

	public final float getPositionY() {
		return positionY;
	}

	public final void setPosition(final float x, final float y) {
		positionX = x;
		positionY = y;
	}

	public final float getRotation() {
		return rotation;
	}

	public final void setRotation(final float value) {
		rotation = value;
	}

	public final Vector2[] getCircleNormals() {
		return circleNormals;
	}

	public float[] getCircleHeights() {
		return circleHeights;
	}

	public final ITilemapCircleListener getListener() {
		return listener;
	}

	public final void setListener(final ITilemapCircleListener value) {
		listener = value;
	}

	public final void init(final int seed, int height) {
		if (height < 5) {
			height = 5;
		}

		this.seed = seed;
		this.height = height;

		initData();

		updateTiles();
	}

	protected abstract void updateTiles();

	private final void initData() {
		width = (((int) (height * MathUtils.PI * 2.0f)) / 4) * 4;

		circleNormals = DataPools.poolVector2.getArray(width);
		circleHeights = DataPools.poolFloat.getArray(height + 1);
		tiles = DataPools.poolByte.getArray(width * height);

		final float angleStep = ((2.0f * MathUtils.PI) / width);

		for (int i = 0; i < width; i++) {
			final float angle = i * angleStep;
			circleNormals[i] = new Vector2(Mathf.sin(angle), Mathf.cos(angle));
		}

		height0 = (height - 1) * TILE_SIZE;
		k = -((width / (MathUtils.PI * 2.0f))) / (1 - (width / (MathUtils.PI * 2.0f)));
		logk = (float) Math.log(k);

		circleHeights[0] = height0;

		for (int i = 1; i <= height; i++) {
			final float r1 = circleHeights[i - 1];

			// float r2 = ((-r1 * width) / (Mathf.PI * 2.0f)) / (1 - (width /
			// (Mathf.PI * 2.0f)));
			final float r2 = r1 * k;

			circleHeights[i] = r2;
		}
	}

	public final byte getTile(final int tileX, final int tileY) {
		return tiles[tileX + tileY * width];
	}

	public final void getTile(final int tileX, final int tileY, final byte tile) {
		if (tiles[tileX + tileY * width] != tile) {
			tiles[tileX + tileY * width] = tile;
			if (listener != null) {
				listener.onTilemapTileChanged(tileX, tileY);
			}
		}
	}

	public final int getTileYFromDistance(final float distance) {
		// This was taken from wolfram-alpha, by solving the radius relationship
		// function
		// Original function:
		// http://www.wolframalpha.com/input/?i=g%280%29%3Dk%2C+g%28n%2B1%29%3Dl+*+g%28n%29
		// Solution:
		// http://www.wolframalpha.com/input/?i=y+%3D+k+*+l%CB%86x+find+x (we
		// use the solution over reals with y > 0)

		// int tileY = (int) (Mathf.Log (distance / height0) / Mathf.Log (k));
		final int tileY = (int) (Math.log(distance / height0) / logk);

		return tileY;
	}

	public final float getDistanceFromTileY(final int tileY) {
		return (float) (height0 * Math.pow(k, tileY));
	}

	public final float getDistanceFromPosition(final float positionX, final float positionY) {
		final float dx = positionX - this.positionX;
		final float dy = positionY - this.positionY;

		return (float) Math.sqrt(dx * dx + dy * dy);
	}

	public final int getTileXFromAngle(final float angle) {
		int tileX = MathUtils.floor((angle / (MathUtils.PI * 2.0f)) * width);

		tileX = tileX % width;
		if (tileX < 0) {
			tileX += width;
		}

		return tileX;
	}

	public final Vector2 getPositionFromTileCoordinate(final int tileX, final int tileY) {
		// return position + GetNormalFromTileX(tileX) *
		// GetDistanceFromTileY(tileY);

		return getNormalFromTileX(tileX).scl(getDistanceFromTileY(tileY)).add(positionX, positionY);
	}

	public final Vector2 getPositionFromDistanceAndAngle(final float distance, final float angle) {
		// return position + GetNormalFromAngle(angle) * distance;

		final Vector2 normal = getNormalFromAngle(angle);
		normal.scl(distance);
		normal.add(positionX, positionY);

		return normal;
	}

	public final boolean getTileCoordinatesFromPosition(final float positionX, final float positionY, final Vector2I tileCoordinate) {
		final float dx = positionX - this.positionX;
		final float dy = positionY - this.positionY;

		final float distance = (float) Math.sqrt(dx * dx + dy * dy);
		final float angle = -Mathf.atan2(dy, dx) + MathUtils.PI * 0.5f;

		tileCoordinate.y = getTileYFromDistance(distance);
		tileCoordinate.x = getTileXFromAngle(angle);

		if (tileCoordinate.y >= height || tileCoordinate.y < 0) {
			return false;
		}

		return true;
	}

	public final float getScaleFromPosition(final float positionX, final float positionY) {
		final float dx = positionX - this.positionX;
		final float dy = positionY - this.positionY;
		final float distance = (float) Math.sqrt(dx * dx + dy * dy);

		final float scale = MathUtils.clamp((distance * 2.0f * MathUtils.PI) / width, (circleHeights[0] * 2.0f * MathUtils.PI) / width,
				(circleHeights[circleHeights.length - 1] * 2.0f * MathUtils.PI) / width) * TILE_SIZE_INV;

		return scale;
	}

	public final Vector2 getNormalFromPosition(final float positionX, final float positionY) {
		final float dx = positionX - this.positionX;
		final float dy = positionY - this.positionY;
		final float distance = (float) Math.sqrt(dx * dx + dy * dy);

		return tmpv1.set(dx / distance, dy / distance);
	}

	public final Vector2 getNormalFromAngle(final float angle) {
		return tmpv1.set(Mathf.sin(angle), Mathf.cos(angle));
	}

	public final float getAngleFromPosition(final float positionX, final float positionY) {
		final float dx = positionX - this.positionX;
		final float dy = positionY - this.positionY;

		final float angle = -Mathf.atan2(dy, dx) + MathUtils.PI * 0.5f;

		return angle;
	}

	public final Vector2 getNormalFromTileX(int tileX) {
		tileX = tileX % width;

		return tmpv1.set(circleNormals[tileX]);
	}

	public final Vector2 getTangentFromPosition(final float positionX, final float positionY) {
		final Vector2 normal = getNormalFromPosition(positionX, positionY);

		return tmpv1.set(normal.y, -normal.x);
	}

	public final Vector2 getTangentFromTileCoordinate(final int tileX, final int tileY) {
		final Vector2 normal = getNormalFromTileX(tileX);

		return tmpv1.set(normal.y, -normal.x);
	}

	public final boolean raycastSquare(final float originX, final float originY, float size, final TileDirection direction, final float len,
			final TileHitInfo hitInfo) {
		size *= 0.95f;

		final int iterations = Math.max(MathUtils.ceil(size / TILE_SIZE), 1);

		// Vector2 from = origin - GetTanget(origin, direction) * (size * 0.5f);
		Vector2 from = getTanget(originX, originY, direction);
		from.scl(size * 0.5f);
		from.add(originX, originY);
		float fromX = from.x;
		float fromY = from.y;
		from = null;

		// Vector2 step = GetTanget(origin, direction) * (size / iterations);
		Vector2 step = getTanget(originX, originY, direction);
		step.scl(size / iterations);
		final float stepX = step.x;
		final float stepY = step.y;
		step = null;

		boolean hitAny = false;

		final TileHitInfo localHitInfo = tmpHitInfo;

		for (int i = 0; i <= iterations; i++) {
			if (raycast(fromX, fromY, direction, len, localHitInfo)) {
				if (!hitAny) {
					hitAny = true;
					hitInfo.set(localHitInfo);
				} else if (localHitInfo.hitDistance < hitInfo.hitDistance) {
					hitInfo.set(localHitInfo);
				}
			}

			fromX += stepX;
			fromY += stepY;
		}

		return hitAny;
	}

	public final Vector2 getDirection(final float originX, final float originY, final TileDirection direction) {
		switch (direction) {
			case Down:
				return getNormalFromPosition(originX, originY).scl(-1);

			case Up:
				return getNormalFromPosition(originX, originY);

			case Right:
				return getTangentFromPosition(originX, originY);

			case Left:
				return getTangentFromPosition(originX, originY).scl(-1);

			default:
				return tmpv1.set(0, 0);
		}
	}

	public final Vector2 getTanget(final float originX, final float originY, final TileDirection direction) {
		switch (direction) {
			case Down:
				return getTangentFromPosition(originX, originY);

			case Up:
				return getTangentFromPosition(originX, originY);

			case Right:
				return getNormalFromPosition(originX, originY);

			case Left:
				return getNormalFromPosition(originX, originY);

			default:
				return tmpv1.set(0, 0);
		}
	}

	public final boolean raycast(final float originX, final float originY, final TileDirection direction, float len, final TileHitInfo hitInfo) {
		final float dx = originX - positionX;
		final float dy = originY - positionY;
		float originDistance = (float) Math.sqrt(dx * dx + dy * dy);

		float targetX;
		float targetY;
		float targetdx;
		float targetdy;
		float targetDistance;
		float tangentDistance;

		float segmentSize;

		if (originDistance < 0.001f) {
			originDistance = 0.001f;
		}

		float originMapAngle = -Mathf.atan2(dy, dx) + MathUtils.PI * 0.5f;

		while (originMapAngle > MathUtils.PI2) {
			originMapAngle -= MathUtils.PI2;
		}

		while (originMapAngle < 0.0f) {
			originMapAngle += MathUtils.PI2;
		}

		final float originNormalX = dx / originDistance;
		final float originNormalY = dy / originDistance;
		final float originTangentX = originNormalY;
		final float originTangentY = -originNormalX;

		if (direction == TileDirection.Right) {
			targetX = originX + originTangentX * len;
			targetY = originY + originTangentY * len;
			targetdx = targetX - positionX;
			targetdy = targetY - positionY;
			targetDistance = (float) Math.sqrt(targetdx * targetdx + targetdy * targetdy);

			if (originDistance > circleHeights[circleHeights.length - 1]) {
				// Origin point outside, not hit!
				return false;
			}

			for (int i = 1; i < circleHeights.length; i++) {
				if (originDistance < circleHeights[i]) {
					hitInfo.hitTileY = i - 1;
					break;
				}
			}

			segmentSize = (circleHeights[hitInfo.hitTileY] * 2.0f * MathUtils.PI) / width;
			tangentDistance = ((originMapAngle / (MathUtils.PI2)) * width);

			hitInfo.hitTileX = (int) tangentDistance;
			hitInfo.hitTileX = (hitInfo.hitTileX + 1) % width;

			len -= segmentSize * (MathUtils.ceil(tangentDistance) - tangentDistance);

			while (hitInfo.hitTileX < width && len >= 0) {
				if (getTile(hitInfo.hitTileX, hitInfo.hitTileY) != 0) {
					final Vector2 tanget = getTangentFromTileCoordinate(hitInfo.hitTileX, hitInfo.hitTileY);

					hitInfo.hitNormalX = -tanget.x;
					hitInfo.hitNormalY = -tanget.y;
					hitInfo.hitPositionX = positionX + circleNormals[hitInfo.hitTileX].x * originDistance;
					hitInfo.hitPositionY = positionY + circleNormals[hitInfo.hitTileX].y * originDistance;
					hitInfo.hitDistance = Mathf.len(originX - hitInfo.hitPositionX, originY - hitInfo.hitPositionY);

					return true;
				}

				len -= segmentSize;

				hitInfo.hitTileX++;
			}
		} else if (direction == TileDirection.Left) {
			targetX = originX + originTangentX * len;
			targetY = originY + originTangentY * len;
			targetdx = targetX - positionX;
			targetdy = targetY - positionY;
			targetDistance = (float) Math.sqrt(targetdx * targetdx + targetdy * targetdy);

			if (originDistance > circleHeights[circleHeights.length - 1]) {
				// Origin point outside, not hit!
				return false;
			}

			for (int i = 1; i < circleHeights.length; i++) {
				if (originDistance < circleHeights[i]) {
					hitInfo.hitTileY = i - 1;
					break;
				}
			}

			segmentSize = (circleHeights[hitInfo.hitTileY] * 2.0f * MathUtils.PI) / width;
			tangentDistance = ((originMapAngle / (MathUtils.PI2)) * width);

			hitInfo.hitTileX = (int) tangentDistance;
			hitInfo.hitTileX = (hitInfo.hitTileX - 1) % width;
			if (hitInfo.hitTileX < 0) {
				hitInfo.hitTileX += width;
			}

			len -= segmentSize * (tangentDistance - MathUtils.floor(tangentDistance));

			while (hitInfo.hitTileX >= 0 && len >= 0) {
				if (getTile(hitInfo.hitTileX, hitInfo.hitTileY) != 0) {
					final Vector2 tangent = getTangentFromTileCoordinate(hitInfo.hitTileX + 1, hitInfo.hitTileY);

					hitInfo.hitNormalX = tangent.x;
					hitInfo.hitNormalY = tangent.y;
					hitInfo.hitPositionX = positionX + circleNormals[(hitInfo.hitTileX + 1) % width].x * originDistance;
					hitInfo.hitPositionX = positionY + circleNormals[(hitInfo.hitTileX + 1) % width].y * originDistance;
					hitInfo.hitDistance = Mathf.len(originX - hitInfo.hitPositionX, originY - hitInfo.hitPositionY);
					return true;
				}

				len -= segmentSize;

				hitInfo.hitTileX--;
			}
		} else if (direction == TileDirection.Up) {
			targetX = originX + originNormalX * len;
			targetY = originY + originNormalY * len;
			targetdx = targetX - positionX;
			targetdy = targetY - positionY;
			targetDistance = (float) Math.sqrt(targetdx * targetdx + targetdy * targetdy);

			if (originDistance > circleHeights[circleHeights.length - 1]) {
				// Origin point outside, not hit!
				return false;
			}

			hitInfo.hitTileX = (int) ((originMapAngle / (MathUtils.PI * 2.0f)) * width);
			hitInfo.hitTileX = hitInfo.hitTileX % width;

			for (int i = 1; i < circleHeights.length; i++) {
				if (originDistance < circleHeights[i]) {
					hitInfo.hitTileY = i;
					len -= circleHeights[i] - originDistance;
					break;
				}
			}

			while (hitInfo.hitTileY < height && len >= 0) {
				if (getTile(hitInfo.hitTileX, hitInfo.hitTileY) != 0) {
					hitInfo.hitNormalX = -originNormalX;
					hitInfo.hitNormalY = -originNormalY;
					hitInfo.hitPositionX = positionX + originNormalX * circleHeights[hitInfo.hitTileY];
					hitInfo.hitPositionY = positionY + originNormalY * circleHeights[hitInfo.hitTileY];
					hitInfo.hitDistance = Mathf.len(originX - hitInfo.hitPositionX, originY - hitInfo.hitPositionY);

					return true;
				}

				if (hitInfo.hitTileY < height - 1) {
					len -= (circleHeights[hitInfo.hitTileY + 1] - circleHeights[hitInfo.hitTileY]);
				}

				hitInfo.hitTileY++;
			}
		} else if (direction == TileDirection.Down) {
			targetX = originX - originNormalX * len;
			targetY = originY - originNormalY * len;
			targetdx = targetX - positionX;
			targetdy = targetY - positionY;
			targetDistance = (float) Math.sqrt(targetdx * targetdx + targetdy * targetdy);

			if (/* originDistance > circleHeights[circleHeights.Length - 1] && */
			targetDistance > circleHeights[circleHeights.length - 1]) {
				// Target outside, no hit!
				return false;
			} else if (targetDistance < circleHeights[0]) {
				// Target inside core, core hit!
				hitInfo.hitTileY = 0;
				hitInfo.hitNormalX = originNormalX;
				hitInfo.hitNormalY = originNormalY;
				hitInfo.hitPositionX = positionX + originNormalX * circleHeights[hitInfo.hitTileY + 1];
				hitInfo.hitPositionY = positionY + originNormalY * circleHeights[hitInfo.hitTileY + 1];
				hitInfo.hitDistance = Mathf.len(originX - hitInfo.hitPositionX, originY - hitInfo.hitPositionY);

				return true;
			}

			hitInfo.hitTileX = (int) ((originMapAngle / (MathUtils.PI * 2.0f)) * width);
			hitInfo.hitTileX = hitInfo.hitTileX % width;

			for (int i = circleHeights.length - 1; i >= 1; i--) {
				if (originDistance > circleHeights[i]) {
					hitInfo.hitTileY = i - 1;
					len -= originDistance - circleHeights[i];
					break;
				}
			}

			while (hitInfo.hitTileY >= 0 && len > 0) {
				if (getTile(hitInfo.hitTileX, hitInfo.hitTileY) != 0) {
					hitInfo.hitNormalX = originNormalX;
					hitInfo.hitNormalY = originNormalY;
					hitInfo.hitPositionX = positionX + originNormalX * circleHeights[hitInfo.hitTileY + 1];
					hitInfo.hitPositionY = positionY + originNormalY * circleHeights[hitInfo.hitTileY + 1];
					hitInfo.hitDistance = Mathf.len(originX - hitInfo.hitPositionX, originY - hitInfo.hitPositionY);

					return true;
				}

				if (hitInfo.hitTileY > 0) {
					len -= (circleHeights[hitInfo.hitTileY] - circleHeights[hitInfo.hitTileY - 1]);
				}
				hitInfo.hitTileY--;
			}
		}

		return false;
	}

	public void recycle() {
		DataPools.poolVector2.returnArray(circleNormals);
		circleNormals = null;

		DataPools.poolFloat.returnArray(circleHeights);
		circleHeights = null;

		DataPools.poolByte.returnArray(tiles);
		tiles = null;

		listener = null;
	}
}
