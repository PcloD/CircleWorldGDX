package com.fdangelo.circleworld.universeview.objects;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.fdangelo.circleworld.universeengine.objects.IUniverseObjectListener;
import com.fdangelo.circleworld.universeengine.objects.UniverseObject;
import com.fdangelo.circleworld.universeengine.tilemap.TilemapCircle;
import com.fdangelo.circleworld.universeview.UniverseView;
import com.fdangelo.circleworld.universeview.tilemap.TilemapCircleView;

public class UniverseObjectView extends Actor implements IUniverseObjectListener {
	protected UniverseObject universeObject;
	protected UniverseView universeView;
	protected TilemapCircleView parentView;

	protected boolean visible = true;

	public final UniverseObject getUniverseObject() {
		return universeObject;
	}

	public final TilemapCircleView getParentView() {
		return parentView;
	}

	public final UniverseView getUniverseView() {
		return universeView;
	}

	public UniverseObjectView() {
	}

	public final void init(final UniverseObject universeObject, final UniverseView universeView) {
		this.universeView = universeView;
		this.universeObject = universeObject;

		universeObject.setListener(this);

		parentView = universeView.getPlanetView(universeObject.getParent());

		setSize(universeObject.getSizeX(), universeObject.getSizeY());

		updatePosition();
	}

	@Override
	public void onUniverseObjectUpdated(final float deltaTime) {
		updatePosition();
	}

	@Override
	public void onParentChanged(final TilemapCircle parent) {
		parentView = universeView.getPlanetView(universeObject.getParent());

		updatePosition();
	}

	protected final void updatePosition() {
		if (universeObject.getVisible()) {
			if (!visible) {
				visible = true;
				setVisible(true);
			}

			setPosition(universeObject.getPositionX(), universeObject.getPositionY());
			setScale(universeObject.getScale());
			setRotation(universeObject.getRotation() * MathUtils.radiansToDegrees);
		} else {
			if (visible) {
				visible = false;
				setVisible(false);
			}
		}
	}

	/*
	 * public virtual void OnDrawGizmosSelected() { OnDrawGizmos(); } public
	 * virtual void OnDrawGizmos() { if (universeObject != null) { Gizmos.color
	 * = Color.red; Gizmos.DrawLine(transform.position, transform.position +
	 * transform.up * universeObject.Size.y); Gizmos.color = Color.blue;
	 * Gizmos.DrawLine(transform.position + transform.up * universeObject.Size.y
	 * * 0.5f, transform.position + transform.up * universeObject.Size.y * 0.5f
	 * + transform.right * universeObject.Size.x * 0.5f); } }
	 */
}
