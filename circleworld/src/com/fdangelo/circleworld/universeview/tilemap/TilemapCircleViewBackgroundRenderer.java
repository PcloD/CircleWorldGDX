package com.fdangelo.circleworld.universeview.tilemap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.utils.MeshBuilder;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import com.fdangelo.circleworld.universeengine.tilemap.Planet;
import com.fdangelo.circleworld.universeengine.tilemap.PlanetType;
import com.fdangelo.circleworld.universeengine.tilemap.TilemapCircle;

public class TilemapCircleViewBackgroundRenderer implements Disposable {
	private TilemapCircle tilemapCircle;

	private Vector2[] circleNormals;
	private float[] circleHeights;

	private Mesh mesh;

	static private ShaderProgram shader;
	static private int shaderWorldViewLocation;

	public TilemapCircleViewBackgroundRenderer() {
		if (shader == null) {
			final String vertexShader = "attribute vec4 a_position;\n" + "attribute vec4 a_color;\n" + "uniform mat4 u_projTrans;\n" + "varying vec4 v_color;"
					+ "void main()                  \n" + "{                            \n" + "   v_color = a_color; \n"
					+ "   gl_Position =  u_projTrans * a_position;  \n" + "}";

			final String fragmentShader = "#ifdef GL_ES\n" + "precision mediump float;\n" + "#endif\n" + "varying vec4 v_color;\n"
					+ "void main()                                  \n" + "{                                            \n" + "  gl_FragColor = v_color;\n"
					+ "}";

			shader = new ShaderProgram(vertexShader, fragmentShader);
			if (!shader.isCompiled()) {
				throw new IllegalArgumentException("Error compiling shader: " + shader.getLog());
			}

			shaderWorldViewLocation = shader.getUniformLocation("u_projTrans");
		}
	}

	public final void init(final TilemapCircleView tilemapCircleView) {
		// this.tilemapCircleView = tilemapCircleView;
		tilemapCircle = tilemapCircleView.getTilemapCircle();
		circleNormals = tilemapCircle.getCircleNormals();
		circleHeights = tilemapCircle.getCircleHeights();

		initMesh();
	}

	static private Color fromColor = new Color();
	static private Color toColor = new Color();

	static private MeshBuilder meshBuilder;
	static private Vector3 vertexPos = new Vector3();
	static private Color vertexColor = new Color();

	private final void initMesh() {
		fromColor.set(Color.BLUE);
		toColor.set(Color.CYAN);
		toColor.a = 0;

		if (tilemapCircle instanceof Planet) {
			final PlanetType planetType = ((Planet) tilemapCircle).getPlanetType();

			fromColor.set(planetType.backColorFrom);
			toColor.set(planetType.backColorTo);
		}

		if (mesh != null) {
			mesh.dispose();
			mesh = null;
		}

		final int totalTriangles = circleNormals.length;
		final int totalIndices = totalTriangles * 3;
		final int totalVertices = circleNormals.length + 1;

		if (meshBuilder == null) {
			meshBuilder = new MeshBuilder();
			meshBuilder.begin(Usage.Position | Usage.Color);
			meshBuilder.ensureCapacity(totalVertices + 1, totalIndices + 1);
		} else {
			meshBuilder.begin(Usage.Position | Usage.Color);
		}

		final float heavenHeight = circleHeights[circleHeights.length - 1]
				+ (circleHeights[circleHeights.length - 1] - circleHeights[circleHeights.length - 2]) * 5.0f; // Repeat
																												// the
																												// last
																												// tile
																												// height
																												// 5
																												// times

		vertexPos.set(0, 0, -10);
		vertexColor.set(fromColor);
		meshBuilder.vertex(vertexPos, null, vertexColor, null);

		vertexColor.set(toColor);
		for (int i = 0; i < circleNormals.length; i++) {
			vertexPos.set(circleNormals[i].x * heavenHeight, circleNormals[i].y * heavenHeight, -10);
			meshBuilder.vertex(vertexPos, null, vertexColor, null);
		}

		short vertexOffset = 1;

		for (int i = 1; i < circleNormals.length; i++) {
			meshBuilder.triangle((short) 0, (short) (vertexOffset - 1), vertexOffset);
			vertexOffset++;
		}

		meshBuilder.triangle((short) 0, (short) (vertexOffset - 1), (short) 1);

		mesh = meshBuilder.end();
	}

	@Override
	public void dispose() {
		if (mesh != null) {
			mesh.dispose();
			mesh = null;
		}
	}

	public final void draw(final Matrix4 matrix) {
		Gdx.gl.glDepthMask(false);
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		shader.begin();
		shader.setUniformMatrix(shaderWorldViewLocation, matrix);
		mesh.render(shader, GL10.GL_TRIANGLES);
		shader.end();
	}
}
