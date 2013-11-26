package com.fdangelo.circleworld.universeview;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Disposable;
import com.fdangelo.circleworld.universeengine.IUniverseListener;
import com.fdangelo.circleworld.universeengine.Thing;
import com.fdangelo.circleworld.universeengine.ThingPosition;
import com.fdangelo.circleworld.universeengine.ThingType;
import com.fdangelo.circleworld.universeengine.Universe;
import com.fdangelo.circleworld.universeengine.objects.Avatar;
import com.fdangelo.circleworld.universeengine.objects.Ship;
import com.fdangelo.circleworld.universeengine.objects.UniverseObject;
import com.fdangelo.circleworld.universeengine.tilemap.Planet;
import com.fdangelo.circleworld.universeengine.tilemap.PlanetType;
import com.fdangelo.circleworld.universeengine.tilemap.PlanetTypes;
import com.fdangelo.circleworld.universeengine.tilemap.TilemapCircle;
import com.fdangelo.circleworld.universeengine.utils.UEProfiler;
import com.fdangelo.circleworld.universeview.objects.AvatarView;
import com.fdangelo.circleworld.universeview.objects.ShipView;
import com.fdangelo.circleworld.universeview.objects.UniverseObjectView;
import com.fdangelo.circleworld.universeview.tilemap.PlanetView;

public final class UniverseView extends Actor implements IUniverseListener, Disposable {
	static private final int LAYER_COUNT = 3;
	static private final int LAYER_BACKGROUND = 0;
	static private final int LAYER_PLANETS = 1;
	static private final int LAYER_FOREGROUND = 2;
	
	static public final int MAX_ACTIVE_PLANET_VIEWS = 5;
	
	private final UniverseViewFactory universeFactory = new UniverseViewFactory();

	private final PlanetView[] planetViews = new PlanetView[Universe.MAX_THINGS];
	private final ArrayList<PlanetView> activePlanetViews = new ArrayList<PlanetView>(32);
	private final ArrayList<UniverseObjectView> tilemapObjectViews = new ArrayList<UniverseObjectView>(32);
	
	private AvatarView avatarView;
	private ShipView shipView;
	private Universe universe;

	private final OrthographicCamera camera;
	private final PlanetType[] planetTypes;
	private final Stage[] layers;

	public final Universe getUniverse() {
		return universe;
	}

	public final OrthographicCamera getCamera() {
		return camera;
	}

	public final AvatarView getAvatarView() {
		return avatarView;
	}

	public final ShipView getShipView() {
		return shipView;
	}

	public UniverseView() {
		camera = new OrthographicCamera();

		layers = new Stage[LAYER_COUNT];
		for (int i = 0; i < layers.length; i++) {
			layers[i] = new Stage();
			layers[i].setCamera(camera);
			layers[i].setViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		}

		planetTypes = PlanetTypes.getPlanetTypes();

		layers[LAYER_BACKGROUND].addActor(this);
	}

	public final void init(final int seed) {
		createUniverse(seed);

		updateMesh(true);
	}

	private final void createUniverse(final int seed) {
		universe = new Universe();

		universe.init(seed, this);
	}

	public final PlanetView getPlanetView(final TilemapCircle tilemapCircle) {
		if (tilemapCircle instanceof Planet) {
			return getPlanetView(((Planet) tilemapCircle).getThingIndex());
		}

		return null;
	}

	public final boolean existsPlanetView(final TilemapCircle tilemapCircle) {
		if (tilemapCircle instanceof Planet) {
			return planetViews[((Planet) tilemapCircle).getThingIndex()] != null;
		}

		return false;
	}

	public final PlanetView getPlanetView(final short thingIndex) {
		if (planetViews[thingIndex] != null) {
			return planetViews[thingIndex];
		}

		if (activePlanetViews.size() >= MAX_ACTIVE_PLANET_VIEWS) {
			universe.returnPlanet(activePlanetViews.get(0).getPlanet());
		}

		final Planet planet = universe.getPlanet(thingIndex);

		if (planet == null) {
			return null;
		}

		final PlanetView planetView = universeFactory.getPlanet(planet.getHeight());

		planetView.initPlanet(planet, this);

		activePlanetViews.add(planetView);

		layers[LAYER_PLANETS].addActor(planetView);

		planetViews[thingIndex] = planetView;

		// Debug.Log(planetViews.Count);

		return planetView;
	}

	@Override
	public void onPlanetReturned(final Planet planet) {
		planetViews[planet.getThingIndex()] = null;

		for (int i = 0; i < activePlanetViews.size(); i++) {
			if (activePlanetViews.get(i).getPlanet() == planet) {
				final PlanetView planetView = activePlanetViews.get(i);

				// remove from stage
				planetView.remove();

				universeFactory.returnPlanet(planetView);

				activePlanetViews.remove(i);

				break;
			}
		}
	}

	@Override
	public void onUniverseObjectAdded(final UniverseObject universeObject) {
		if (universeObject instanceof Avatar) {
			avatarView = universeFactory.getAvatar();
			avatarView.init(universeObject, this);

			tilemapObjectViews.add(avatarView);

			layers[LAYER_FOREGROUND].addActor(avatarView);
		} else if (universeObject instanceof Ship) {
			shipView = universeFactory.getShip();
			shipView.init(universeObject, this);

			tilemapObjectViews.add(shipView);

			layers[LAYER_FOREGROUND].addActor(shipView);
		}
	}

	// / <summary>
	// / Called by GameLogic
	// / </summary>
	public final void updateUniverse(final float deltaTime) {
		universe.updateUniverse(deltaTime);

		if (isVisible()) {
			UEProfiler.BeginSample("UniverseView.UpdateMesh");
			updateMesh(false);
			UEProfiler.EndSample();
		}
	}

	@Override
	public void draw(final Batch batch, final float parentAlpha) {
		final Thing[] things = universe.getThings();
		final ThingPosition[] thingsPositions = universe.getThingsPositions();
		final short[] thingsToRender = universe.getThingsToRender();
		final short thingsToRenderAmount = universe.getThingsToRenderAmount();

		for (int i = 0; i < thingsToRenderAmount; i++) {
			final short thingIndex = thingsToRender[i];

			if (planetViews[thingIndex] != null) {
				continue;
			}

			final ThingPosition position = thingsPositions[thingIndex];
			final Thing thing = things[thingIndex];

			PlanetType planetType;

			if (thing.type == ThingType.Sun) {
				planetType = planetTypes[Math.abs(thing.seed % 2) + 4]; // suns!
			} else {
				planetType = planetTypes[Math.abs(thing.seed % 4)]; // planets!
			}

			batch.draw(planetType.planetSprite, position.x - position.radius, position.y - position.radius, position.radius * 2.0f, position.radius * 2.0f);
		}
	}

	private final void updateMesh(final boolean firstTime) {
		/*
		 * Thing[] things = universe.getThings(); ThingPosition[]
		 * thingsPositions = universe.getThingsPositions(); short[]
		 * thingsToRender = universe.getThingsToRender(); short
		 * thingsToRenderAmount = universe.getThingsToRenderAmount(); int
		 * vertexCount = thingsToRenderAmount * 4; int triangleCount =
		 * thingsToRenderAmount * 6; Vector3[] vertices =
		 * DataPools.poolVector3.GetArray(vertexCount); int vertexOffset = 0;
		 * //Update all positions for (int i = 0; i < thingsToRenderAmount; i++)
		 * { ushort thingIndex = thingsToRender[i]; ThingPosition position =
		 * thingsPositions[thingIndex]; vertices[vertexOffset + 0].x =
		 * position.x - position.radius; vertices[vertexOffset + 0].y =
		 * position.y - position.radius; vertices[vertexOffset + 1].x =
		 * position.x - position.radius; vertices[vertexOffset + 1].y =
		 * position.y + position.radius; vertices[vertexOffset + 2].x =
		 * position.x + position.radius; vertices[vertexOffset + 2].y =
		 * position.y + position.radius; vertices[vertexOffset + 3].x =
		 * position.x + position.radius; vertices[vertexOffset + 3].y =
		 * position.y - position.radius; vertexOffset += 4; } //Don't draw
		 * preview of active planets (except the first time that everything is
		 * draw) if (!firstTime) { for (int i = 0; i < activePlanetViews.Count;
		 * i++) { ushort thingIndex = activePlanetViews[i].Planet.ThingIndex;
		 * int thingsToRenderIndex = System.Array.BinarySearch(thingsToRender,
		 * 0, thingsToRenderAmount, thingIndex); if (thingsToRenderIndex >= 0) {
		 * vertexOffset = thingsToRenderIndex * 4; vertices[vertexOffset + 0].x
		 * = 0; vertices[vertexOffset + 0].y = 0; vertices[vertexOffset + 1].x =
		 * 0; vertices[vertexOffset + 1].y = 0; vertices[vertexOffset + 2].x =
		 * 0; vertices[vertexOffset + 2].y = 0; vertices[vertexOffset + 3].x =
		 * 0; vertices[vertexOffset + 3].y = 0; } } } if (firstTime) { //Update
		 * triangles and uvs only the first time that the mesh is updated int
		 * triangleOffset = 0; vertexOffset = 0; int[] triangles =
		 * DataPools.poolInt.GetArray(triangleCount); Vector2[] uvs =
		 * DataPools.poolVector2.GetArray(vertexCount); for (ushort i = 0; i <
		 * thingsToRenderAmount; i++) { Thing thing = things[thingsToRender[i]];
		 * PlanetType planetType; if (thing.type == (ushort) ThingType.Sun)
		 * planetType = planetTypes[Mathf.Abs(thing.seed % 2) + 4]; //suns! else
		 * planetType = planetTypes[Mathf.Abs(thing.seed % 4)]; //planets! Rect
		 * planetUV = planetType.planetSprite.UV; uvs[vertexOffset + 0] = new
		 * Vector2(planetUV.xMin, planetUV.yMax); uvs[vertexOffset + 1] = new
		 * Vector2(planetUV.xMax, planetUV.yMax); uvs[vertexOffset + 2] = new
		 * Vector2(planetUV.xMax, planetUV.yMin); uvs[vertexOffset + 3] = new
		 * Vector2(planetUV.xMin, planetUV.yMin); triangles[triangleOffset + 0]
		 * = vertexOffset + 0; triangles[triangleOffset + 1] = vertexOffset + 1;
		 * triangles[triangleOffset + 2] = vertexOffset + 2;
		 * triangles[triangleOffset + 3] = vertexOffset + 2;
		 * triangles[triangleOffset + 4] = vertexOffset + 3;
		 * triangles[triangleOffset + 5] = vertexOffset + 0; triangleOffset +=
		 * 6; vertexOffset += 4; } mesh1.vertices = vertices; mesh2.vertices =
		 * vertices; mesh1.triangles = triangles; mesh1.uv = uvs; mesh1.bounds =
		 * new Bounds(Vector3.zero, new Vector3(ushort.MaxValue * 2,
		 * ushort.MaxValue * 2, 0.0f)); mesh2.triangles = triangles; mesh2.uv =
		 * uvs; mesh2.bounds = new Bounds(Vector3.zero, new
		 * Vector3(ushort.MaxValue * 2, ushort.MaxValue * 2, 0.0f));
		 * mesh1.Optimize(); mesh2.Optimize();
		 * DataPools.poolInt.ReturnArray(triangles);
		 * DataPools.poolVector2.ReturnArray(uvs); } if ((frameCount % 2) == 0)
		 * { mesh1.vertices = vertices; meshFilter.sharedMesh = mesh1; } else {
		 * mesh2.vertices = vertices; meshFilter.sharedMesh = mesh2; }
		 * DataPools.poolVector3.ReturnArray(vertices); frameCount++;
		 */
	}

	public final void updateLayers(final float deltaTime) {

		for (int i = 0; i < layers.length; i++) {
			layers[i].act(deltaTime);
		}

		for (int i = 0; i < layers.length; i++) {
			layers[i].draw();
		}
	}

	@Override
	public void dispose() {

		for (int i = 0; i < layers.length; i++) {
			layers[i].dispose();
		}
	}

	public final void resize(final int width, final int height) {

		for (int i = 0; i < layers.length; i++) {
			layers[i].setViewport(width, height);
		}
	}
}
