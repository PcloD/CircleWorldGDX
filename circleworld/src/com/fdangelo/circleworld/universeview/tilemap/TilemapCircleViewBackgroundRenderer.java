package com.fdangelo.circleworld.universeview.tilemap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.GL11;
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

public class TilemapCircleViewBackgroundRenderer implements Disposable
{
    private TilemapCircle tilemapCircle;
    
    private Vector2[] circleNormals;
    private float[] circleHeights;
    
    private Mesh mesh;
    
    static private ShaderProgram shader;
    static private int shaderWorldViewLocation;
    

    public TilemapCircleViewBackgroundRenderer()
    {
    	if (shader == null)
    	{
    		String vertexShader = 
    				"attribute vec4 a_position;\n" + 
                    "attribute vec4 a_color;\n" +
                    "uniform mat4 u_projTrans;\n" +
                    "varying vec4 v_color;" + 
                    "void main()                  \n" + 
                    "{                            \n" + 
                    "   v_color = a_color; \n" + 
                    "   gl_Position =  u_projTrans * a_position;  \n"+ 
                    "}";
    		
			String fragmentShader = 
					"#ifdef GL_ES\n" +
					"precision mediump float;\n" + 
					"#endif\n" + 
					"varying vec4 v_color;\n" + 
					"void main()                                  \n" + 
					"{                                            \n" + 
					"  gl_FragColor = v_color;\n" +
					"}";
			
			shader = new ShaderProgram(vertexShader, fragmentShader);
			if (!shader.isCompiled()) throw new IllegalArgumentException("Error compiling shader: " + shader.getLog());
			
			shaderWorldViewLocation = shader.getUniformLocation("u_projTrans");
    	}
	}
    
    public void Init(TilemapCircleView tilemapCircleView)
    {
        //this.tilemapCircleView = tilemapCircleView;
        tilemapCircle = tilemapCircleView.getTilemapCircle();
        circleNormals = tilemapCircle.getCircleNormals();
        circleHeights = tilemapCircle.getCircleHeights();
        
        InitMesh();
    }
    
    static private Color fromColor = new Color();
    static private Color toColor = new Color();
    
    static private MeshBuilder meshBuilder = new MeshBuilder();
    static private Vector3 vertexPos = new Vector3();
    static private Color vertexColor = new Color();
    
    private void InitMesh()
    {
        fromColor.set(Color.BLUE);
        toColor.set(Color.CYAN);
        toColor.a = 0;
        
        if (tilemapCircle instanceof Planet)
        {
            PlanetType planetType = ((Planet) tilemapCircle).getPlanetType();
            
            fromColor.set(planetType.backColorFrom);
            toColor.set(planetType.backColorTo);
        }
        
        if (mesh != null)
        {
        	mesh.dispose();
        	mesh = null;
        }
        
        int totalTriangles = circleNormals.length;
        int totalIndices = totalTriangles * 3;
        int totalVertices = circleNormals.length + 1;
        
        meshBuilder.begin(Usage.Position | Usage.Color);
        meshBuilder.ensureCapacity(totalVertices, totalIndices);
        
        float heavenHeight = circleHeights[circleHeights.length - 1] + 
                             (circleHeights[circleHeights.length - 1] - circleHeights[circleHeights.length - 2]) * 5.0f; //Repeat the last tile height 5 times
        
        
        vertexPos.set(0, 0, -10);
        vertexColor.set(fromColor);
        meshBuilder.vertex(vertexPos, null, vertexColor, null);
          
        vertexColor.set(toColor);
        for (int i = 0; i < circleNormals.length; i++)
        {
            vertexPos.set(circleNormals[i].x * heavenHeight, circleNormals[i].y * heavenHeight, -10);
            meshBuilder.vertex(vertexPos, null, vertexColor, null);
        }
  
        short vertexOffset = 1;
        
        for (int i = 1; i < circleNormals.length; i++)
        {
        	meshBuilder.triangle((short) 0, (short) (vertexOffset - 1), vertexOffset);
            vertexOffset ++;
        }
        
        meshBuilder.triangle((short) 0, (short) (vertexOffset - 1), (short) 1);
        
        mesh = meshBuilder.end();
    }

	public void dispose() 
	{
		if (mesh != null)
		{
			mesh.dispose();
			mesh = null;
		}
	}
	
	public void draw(Matrix4 matrix) 
	{
		Gdx.gl.glDepthMask(false);
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		shader.begin();
		shader.setUniformMatrix(shaderWorldViewLocation, matrix);
		mesh.render(shader, GL10.GL_TRIANGLES);
		shader.end();
	}
}
