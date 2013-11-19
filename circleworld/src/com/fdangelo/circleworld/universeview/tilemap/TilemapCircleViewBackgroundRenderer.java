package com.fdangelo.circleworld.universeview.tilemap;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import com.fdangelo.circleworld.universeengine.tilemap.Planet;
import com.fdangelo.circleworld.universeengine.tilemap.PlanetType;
import com.fdangelo.circleworld.universeengine.tilemap.TilemapCircle;
import com.fdangelo.circleworld.universeengine.utils.DataPools;

public class TilemapCircleViewBackgroundRenderer implements Disposable
{
    //private Mesh mesh;
    //private MeshFilter meshFilter;
    //private MeshRenderer meshRenderer;
    
    //private TilemapCircleView tilemapCircleView;
    private TilemapCircle tilemapCircle;
    
    private Vector2[] circleNormals;
    private float[] circleHeights;
    
    private boolean firstTime = true;

    public TilemapCircleViewBackgroundRenderer()
    {
        //meshRenderer = gameObject.AddComponent<MeshRenderer>();
        //meshFilter = gameObject.AddComponent<MeshFilter>();
        //mesh = new Mesh();
    }
    
    public void Init(TilemapCircleView tilemapCircleView)
    {
        //this.tilemapCircleView = tilemapCircleView;
        this.tilemapCircle = tilemapCircleView.getTilemapCircle();
        this.circleNormals = tilemapCircle.getCircleNormals();
        this.circleHeights = tilemapCircle.getCircleHeights();
        
        //meshRenderer.sharedMaterial = this.tilemapCircleView.backgroundMaterial;
        
        InitMesh();
    }
    
    static private Color fromColor = new Color();
    static private Color toColor = new Color();

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
        
        
        int vertexCount = circleNormals.length + 1;
        int triangleCount = circleNormals.length * 3;
        
        Vector3[] vertices = DataPools.poolVector3.GetArray(vertexCount);
        Color[] colors = DataPools.poolColor.GetArray(vertexCount);
        //Vector2[] uvs = DataPools.poolVector2.GetArray(vertexCount);
        
        float heavenHeight = circleHeights[circleHeights.length - 1] + 
                             (circleHeights[circleHeights.length - 1] - circleHeights[circleHeights.length - 2]) * 5.0f; //Repeat the last tile height 5 times
        
        
        int vertexOffset = 1;
        vertices[0].set(0, 0, 10);
        colors[0].set(fromColor);
          
        for (int i = 0; i < circleNormals.length; i++)
        {
            vertices[vertexOffset].set(circleNormals[i].x, circleNormals[i].y, 0).scl(heavenHeight);
            vertices[vertexOffset].z = 10;
            colors[vertexOffset].set(toColor);
            
            //uvs[vertexOffset] = Vector2.zero;
                        
            vertexOffset++;
        }
  
        //mesh.vertices = vertices;
        //mesh.uv = uvs;
        //mesh.colors32 = colors;
        
        if (firstTime)
        {
            firstTime = false;
            
            int[] triangles = DataPools.poolInt.GetArray(triangleCount);
            
            int triangleOffset = 0;
            vertexOffset = 1;
            
            for (int i = 1; i < circleNormals.length; i++)
            {
                triangles[triangleOffset + 0] = 0;
                triangles[triangleOffset + 1] = vertexOffset - 1;
                triangles[triangleOffset + 2] = vertexOffset;
                
                triangleOffset += 3;
                vertexOffset ++;
            }
            
            triangles[triangleOffset + 0] = 0;
            triangles[triangleOffset + 1] = vertexOffset - 1;
            triangles[triangleOffset + 2] = 1;            
            
            //mesh.triangles = triangles;
            //mesh.Optimize();
            
            DataPools.poolInt.ReturnArray(triangles);
        }
        

        //meshFilter.sharedMesh = mesh;
        
        DataPools.poolColor.ReturnArray(colors);
        DataPools.poolVector3.ReturnArray(vertices);
        //DataPools.poolVector2.ReturnArray(uvs);
    }

	public void dispose() 
	{
		//TODO: Dispose
	}
}
