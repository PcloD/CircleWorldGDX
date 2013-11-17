package com.fdangelo.circleworld.universeview.tilemap;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.fdangelo.circleworld.universeengine.tilemap.ITilemapCircleListener;
import com.fdangelo.circleworld.universeengine.tilemap.TilemapCircle;

public class TilemapCircleView extends Actor implements ITilemapCircleListener
{
    private int lastHeight;
    private int lastWidth;
    
    private TilemapCircleViewBackgroundRenderer backgroundRenderer;

    private TilemapCircleViewRenderer[] renderers;

    private TilemapCircle tilemapCircle;
    
    public TilemapCircle getTilemapCircle()
    {
        return tilemapCircle;
    }
    
    public TilemapCircleView()
    {
    }
    
    public void Init(TilemapCircle tilemapCircle)
    {
        this.tilemapCircle = tilemapCircle;
        
        tilemapCircle.setListener(this);
        
        UpdatePosition();
        
        InitRenderers();
        
        UpdateMesh();
    }
 
    private void UpdateMesh()
    {
        for (int i = 0; i < renderers.length; i++)
            renderers[i].UpdateMesh();
    }

    private void InitRenderers()
    {
        int renderersAmount = MathUtils.clamp(MathUtils.ceil((tilemapCircle.getWidth() * tilemapCircle.getHeight()) / (32 * 32)), 1, 256);

        if (renderers == null || renderers.length != renderersAmount || lastWidth != tilemapCircle.getWidth() || lastHeight != tilemapCircle.getHeight())
        {
            //Destroy existing renderers
            if (renderers != null)
            	for (int i = 0; i < renderers.length; i++)
            		renderers[i].dispose();
            
            if (backgroundRenderer != null)
            	backgroundRenderer.dispose();
   
            //Add tile map circle renderers
            renderers = new TilemapCircleViewRenderer[renderersAmount];

            lastWidth = tilemapCircle.getWidth();
            lastHeight = tilemapCircle.getHeight();

            int sizeX = MathUtils.ceil((float) tilemapCircle.getWidth() / (float) renderers.length);

            int fromX = 0;
            int toX = sizeX;

            for (int i = 0; i < renderers.length; i++)
            {
            	renderers[i] = new TilemapCircleViewRenderer();
                renderers[i].Init(
                    this, 
                    fromX,
                    toX);

                fromX += sizeX;
                toX += sizeX;

                if (toX >= tilemapCircle.getWidth())
                    toX = tilemapCircle.getWidth();
                
                if (fromX > toX)
                    fromX = toX;
            }
            
            //Add background renderer
            backgroundRenderer = new TilemapCircleViewBackgroundRenderer();
        }
        
        backgroundRenderer.Init(this);

        for (int i = 0; i < renderers.length; i++)
            renderers[i].SetDirty();
    }

    private TilemapCircleViewRenderer GetRenderer(int tileX, int tileY)
    {
        if (renderers != null && renderers.length > 0)
        {
            int sizeX = MathUtils.ceil((float) tilemapCircle.getWidth() / (float) renderers.length);

            int rendererIndex = tileX / sizeX;

            return renderers[rendererIndex];
        }

        return null;
    }

    public void OnTilemapTileChanged (int tileX, int tileY)
    {
        GetRenderer(tileX, tileY).SetDirty();
    }
    
    public void OnTilemapParentChanged(float deltaTime)
    {
        UpdatePosition();
        
        UpdateMesh();
    }
    
    private void UpdatePosition()
    {
    	setPosition(tilemapCircle.getPositionX(), tilemapCircle.getPositionY());
    	setRotation(tilemapCircle.getRotation() * MathUtils.radiansToDegrees);
    }
    
    public void Recycle()
    {
        tilemapCircle = null;
    }
}
