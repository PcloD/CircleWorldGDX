package com.fdangelo.circleworld.universeview.tilemap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.utils.MeshBuilder;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import com.fdangelo.circleworld.universeengine.tilemap.TileSubtype;
import com.fdangelo.circleworld.universeengine.tilemap.TileType;
import com.fdangelo.circleworld.universeengine.tilemap.TileTypes;
import com.fdangelo.circleworld.universeengine.tilemap.TilemapCircle;

public class TilemapCircleViewRenderer implements Disposable {
	private boolean dirty;

	private int fromX;
	private int toX;
	private TilemapCircle tilemapCircle;

	private Vector2[] circleNormals;
	private float[] circleHeights;

	private Mesh mesh;

	static private TileType[] tileTypes;
	static private ShaderProgram shader;
	static private int shaderWorldViewLocation;
	static private int shaderTextureLocation;

	private float halfTexelWidth;
	private float halfTexelHeight;

	public TilemapCircleViewRenderer() {
		if (tileTypes == null) {
			tileTypes = TileTypes.getTileTypes();
		}

		if (shader == null) {
			final String vertexShader = "attribute vec4 a_position;\n" + "attribute vec4 a_color;\n" + "attribute vec2 a_texCoord0;\n"
					+ "uniform mat4 u_projTrans;\n" + "varying vec4 v_color;" + "varying vec2 v_texCoords;" + "void main()                  \n"
					+ "{                            \n" + "   v_color = a_color; \n" + "   v_texCoords = a_texCoord0; \n"
					+ "   gl_Position =  u_projTrans * a_position;  \n" + "}";

			final String fragmentShader = "#ifdef GL_ES\n" + "#define LOWP lowp\n" + "precision mediump float;\n" + "#else\n" + "#define LOWP \n" + "#endif\n"
					+ "varying LOWP vec4 v_color;\n" + "varying vec2 v_texCoords;\n" + "uniform sampler2D u_texture;\n"
					+ "void main()                                  \n" + "{                                            \n"
					+ "  gl_FragColor = v_color * texture2D(u_texture, v_texCoords);\n" + "}";

			shader = new ShaderProgram(vertexShader, fragmentShader);

			if (!shader.isCompiled()) {
				throw new IllegalArgumentException("Error compiling shader: " + shader.getLog());
			}

			shaderWorldViewLocation = shader.getUniformLocation("u_projTrans");
			shaderTextureLocation = shader.getUniformLocation("u_texture");
		}
	}

	public final void init(final TilemapCircleView tilemapCircleView, final int fromX, final int toX) {
		dirty = true;

		tilemapCircle = tilemapCircleView.getTilemapCircle();
		this.fromX = fromX;
		this.toX = toX;
		circleNormals = tilemapCircle.getCircleNormals();
		circleHeights = tilemapCircle.getCircleHeights();

		halfTexelWidth = 0.5f * (1.0f / tilemapCircleView.getTilesetTexture().getWidth());
		halfTexelHeight = 0.5f * (1.0f / tilemapCircleView.getTilesetTexture().getHeight());
	}

	public void setDirty() {
		dirty = true;
	}

	public boolean isDirty() {
		return dirty;
	}

	static private Vector2 p1 = new Vector2();
	static private Vector2 p2 = new Vector2();
	static private Vector2 p3 = new Vector2();
	static private Vector2 p4 = new Vector2();

	static private Vector3 tmpv3 = new Vector3();
	static private Vector2 tmpv2 = new Vector2();

	static private MeshBuilder meshBuilder;

	public final void updateMesh() {
		if (!dirty) {
			return;
		}

		dirty = false;

		if (mesh != null) {
			mesh.dispose();
			mesh = null;
		}

		int vertexOffset = 0;

		final int height = tilemapCircle.getHeight();
		final int width = tilemapCircle.getWidth();

		if (meshBuilder == null) {
			meshBuilder = new MeshBuilder();
			meshBuilder.begin(Usage.Position | Usage.Color | Usage.TextureCoordinates);
			meshBuilder.ensureCapacity(8192, 16384);
		} else {
			meshBuilder.begin(Usage.Position | Usage.Color | Usage.TextureCoordinates);
		}

		for (int y = 0; y < height; y++) {
			final float upRadius = circleHeights[y + 1];
			final float downRadius = circleHeights[y];

			for (int x = fromX; x < toX; x++) {
				final byte tileId = tilemapCircle.getTile(x, y);

				if (tileId == 0) // skip empty tiles
				{
					p1.set(0, 0);
					p2.set(0, 0);
					p3.set(0, 0);
					p4.set(0, 0);
				} else {
					p1.set(circleNormals[x]).scl(upRadius);
					p2.set(circleNormals[(x + 1) % width]).scl(upRadius);
					p3.set(circleNormals[(x + 1) % width]).scl(downRadius);
					p4.set(circleNormals[x]).scl(downRadius);
				}

				final TileType tileType = tileTypes[tileId];
				TileSubtype subtype;

				if (y == height - 1 || tilemapCircle.getTile(x, y + 1) == 0) {
					subtype = tileType.top;
				} else {
					subtype = tileType.center;
				}

				meshBuilder.vertex(tmpv3.set(p1.x, p1.y, 0), null, Color.WHITE, tmpv2.set(subtype.uvFromX + halfTexelWidth, subtype.uvFromY + halfTexelHeight));

				meshBuilder.vertex(tmpv3.set(p2.x, p2.y, 0), null, Color.WHITE, tmpv2.set(subtype.uvToX - halfTexelWidth, subtype.uvFromY + halfTexelHeight));

				meshBuilder.vertex(tmpv3.set(p3.x, p3.y, 0), null, Color.WHITE, tmpv2.set(subtype.uvToX - halfTexelWidth, subtype.uvToY - halfTexelHeight));

				meshBuilder.vertex(tmpv3.set(p4.x, p4.y, 0), null, Color.WHITE, tmpv2.set(subtype.uvFromX + halfTexelWidth, subtype.uvToY - halfTexelHeight));
			}
		}

		final int size = height * (toX - fromX);

		vertexOffset = 0;

		for (int i = 0; i < size; i++) {
			meshBuilder.triangle((short) (vertexOffset + 0), (short) (vertexOffset + 1), (short) (vertexOffset + 2));

			meshBuilder.triangle((short) (vertexOffset + 2), (short) (vertexOffset + 3), (short) (vertexOffset + 0));

			vertexOffset += 4;
		}

		mesh = meshBuilder.end();
	}

	@Override
	public void dispose() {
		if (mesh != null) {
			mesh.dispose();
			mesh = null;
		}
	}

	static public void beginDraw(final Matrix4 matrix, final Texture texture) {
		Gdx.gl.glDepthMask(false);
		Gdx.gl.glEnable(GL10.GL_BLEND);
		Gdx.gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		texture.bind();
		shader.begin();
		shader.setUniformi(shaderTextureLocation, 0);
		shader.setUniformMatrix(shaderWorldViewLocation, matrix);
	}

	public void draw() {
		if (mesh != null) {
			mesh.render(shader, GL10.GL_TRIANGLES);
		}
	}

	static public void endDraw() {
		shader.end();
	}
}
