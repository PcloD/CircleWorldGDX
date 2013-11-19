package com.fdangelo.circleworld.universeview.tilemap;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import com.fdangelo.circleworld.universeengine.tilemap.TileSubtype;
import com.fdangelo.circleworld.universeengine.tilemap.TileType;
import com.fdangelo.circleworld.universeengine.tilemap.TileTypes;
import com.fdangelo.circleworld.universeengine.tilemap.TilemapCircle;
import com.fdangelo.circleworld.universeengine.utils.DataPools;

public class TilemapCircleViewRenderer implements Disposable
{
    private boolean dirty;

    private int fromX;
    private int toX;
    //private TilemapCircleView tilemapCircleView;
    private TilemapCircle tilemapCircle;
    
    private Vector2[] circleNormals;
    private float[] circleHeights;

    private boolean firstTime;
    
    //private Mesh mesh;
    //private MeshFilter meshFilter;
    //private MeshRenderer meshRenderer;
    
    static private TileType[] tileTypes;
    
    public TilemapCircleViewRenderer()
    {
        //meshRenderer = gameObject.AddComponent<MeshRenderer>();
        //meshFilter = gameObject.AddComponent<MeshFilter>();
        //mesh = new Mesh();
        
        if (tileTypes == null)
            tileTypes = TileTypes.GetTileTypes();
    }
    
    public void Init(TilemapCircleView tilemapCircleView, int fromX, int toX)
    {
        dirty = true;
        firstTime = true;

        //this.tilemapCircleView = tilemapCircleView;
        this.tilemapCircle = tilemapCircleView.getTilemapCircle();
        this.fromX = fromX;
        this.toX = toX;
        this.circleNormals = tilemapCircle.getCircleNormals();
        this.circleHeights = tilemapCircle.getCircleHeights();
        
        //meshRenderer.sharedMaterial = tilemapCircleView.material;
    }

    public void SetDirty()
    {
        dirty = true;
    }
    
    static private Vector2 p1 = new Vector2();
    static private Vector2 p2 = new Vector2();
    static private Vector2 p3 = new Vector2();
    static private Vector2 p4 = new Vector2();

    public void UpdateMesh()
    {
        if (!dirty)
            return;

        dirty = false;

        int vertexOffset = 0;
        int triangleOffset = 0;

        int height = tilemapCircle.getHeight();
        int width = tilemapCircle.getWidth();
        
        int vertexCount = (toX - fromX) * tilemapCircle.getHeight() * 4;
        int triangleCount = (toX - fromX) * tilemapCircle.getHeight() * 6;
        
        Vector3[] vertices = DataPools.poolVector3.GetArray(vertexCount);
        Color[] colors = DataPools.poolColor.GetArray(vertexCount);
        Vector2[] uvs = DataPools.poolVector2.GetArray(vertexCount);

        for (int y = 0; y < height; y++)
        {
            float upRadius = circleHeights[y + 1];
            float downRadius = circleHeights[y];

            for (int x = fromX; x < toX; x++)
            {
                byte tileId = tilemapCircle.GetTile(x, y);

                if (tileId == 0) //skip empty tiles
                {
                	p1.set(0, 0);
                	p2.set(0, 0);
                	p3.set(0, 0);
                	p4.set(0, 0);
                }
                else
                {
                    p1.set(circleNormals[x]).scl(upRadius);
                    p2.set(circleNormals[(x + 1) % width]).scl(upRadius);
                    p3.set(circleNormals[(x + 1) % width]).scl(downRadius);
                    p4.set(circleNormals[x]).scl(downRadius);
                }
                
                TileType tileType = tileTypes[tileId];

                vertices[vertexOffset + 0].set(p1.x, p1.y, 0);
                vertices[vertexOffset + 1].set(p2.x, p2.y, 0);
                vertices[vertexOffset + 2].set(p3.x, p3.y, 0);
                vertices[vertexOffset + 3].set(p4.x, p4.y, 0);
    
                //if (tilemapCircleView.debugColor)
                //{
                //    colors[vertexOffset + 0] = Color.red;
                //    colors[vertexOffset + 1] = Color.green;
                //    colors[vertexOffset + 2] = Color.blue;
                //    colors[vertexOffset + 3] = Color.cyan;
                //}
                //else
                //{
                    colors[vertexOffset + 0].set(Color.WHITE);
                    colors[vertexOffset + 1].set(Color.WHITE);
                    colors[vertexOffset + 2].set(Color.WHITE);
                    colors[vertexOffset + 3].set(Color.WHITE);
                //}
                
                TileSubtype subtype;
                
                if (y == height - 1 || tilemapCircle.GetTile(x, y + 1) == 0)
                    subtype = tileType.top;
                else
                    subtype = tileType.center;

                uvs[vertexOffset + 0].x = subtype.uvFromX;
                uvs[vertexOffset + 0].y = subtype.uvToY;
                
                uvs[vertexOffset + 1].x = subtype.uvToX;
                uvs[vertexOffset + 1].y = subtype.uvToY;
                
                uvs[vertexOffset + 2].x = subtype.uvToX;
                uvs[vertexOffset + 2].y = subtype.uvFromY;
                
                uvs[vertexOffset + 3].x = subtype.uvFromX;
                uvs[vertexOffset + 3].y = subtype.uvFromY;

                vertexOffset += 4;
                triangleOffset += 6;
            }
        }

        //mesh.vertices = vertices;
        //mesh.uv = uvs;
        //mesh.colors32 = colors;

        if (firstTime)
        {
            int[] triangles = DataPools.poolInt.GetArray(triangleCount);
        
            int size = height * (toX - fromX);
            
            vertexOffset = 0;
            triangleOffset = 0;
            
            for (int i = 0; i < size; i++)
            {
                triangles[triangleOffset + 0] = vertexOffset + 0;
                triangles[triangleOffset + 1] = vertexOffset + 1;
                triangles[triangleOffset + 2] = vertexOffset + 2;

                triangles[triangleOffset + 3] = vertexOffset + 2;
                triangles[triangleOffset + 4] = vertexOffset + 3;
                triangles[triangleOffset + 5] = vertexOffset + 0;
                
                triangleOffset += 6;
                vertexOffset += 4;
            }
            
            //mesh.triangles = triangles;
            
            DataPools.poolInt.ReturnArray(triangles);
        }

        //meshFilter.sharedMesh = mesh;
        
        DataPools.poolColor.ReturnArray(colors);
        DataPools.poolVector3.ReturnArray(vertices);
        DataPools.poolVector2.ReturnArray(uvs);

        firstTime = false;
    }

	public void dispose() 
	{
		//TODO: Dispose
	}
}
