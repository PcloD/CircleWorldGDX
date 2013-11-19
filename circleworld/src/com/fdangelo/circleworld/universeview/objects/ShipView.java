package com.fdangelo.circleworld.universeview.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.ShortArray;
import com.fdangelo.circleworld.GameLogic;
import com.fdangelo.circleworld.GameLogicState;
import com.fdangelo.circleworld.universeview.UniverseView;

public class ShipView extends UniverseObjectView
{
	private ShipViewInput input;
	private TextureRegion sprite;
	
	public ShipView()
	{
		input = new ShipViewInput(this);
		sprite = GameLogic.Instace.assetManager.get("atlas/ships.atlas", TextureAtlas.class).findRegion("Battleship");
	}
	
	@Override
	public void act(float delta)
	{
		super.act(delta);
		input.Update(delta);
	}
	
	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		batch.draw(sprite, getX(), getY(), getOriginX(), getOriginY(), getWidth(), getHeight(), getScaleX(), getScaleY(), -getRotation() + 90.0f); //, getUniverseObject().getSizeX(), getUniverseObject().getSizeY());
	}
	
	/*
    public override void OnDrawGizmos ()
    {
        float sizeY = 1.0f;
        float sizeX = 1.0f;
        
        Gizmos.color = Color.red;
        Gizmos.DrawLine(transform.position, transform.position + transform.up * sizeY);
        Gizmos.color = Color.blue;
        Gizmos.DrawLine(transform.position - transform.right * sizeX * 0.5f, transform.position + transform.right * sizeX * 0.5f);
    }
    */
    
    private ShortArray closeThings;
    
    @Override
    public void OnUniverseObjectUpdated (float deltaTime)
    {
        super.OnUniverseObjectUpdated (deltaTime);
        
        if (GameLogic.Instace.getState() == GameLogicState.PlayingShip)
        {
            closeThings = universeView.getUniverse().FindClosestRenderedThings(universeObject.getPositionX(), universeObject.getPositionY(), 100.0f, closeThings);
            
            for (int i = 0; i < closeThings.size && i < UniverseView.MaxActivePlanetViews; i++)
                universeView.GetPlanetView(closeThings.get(i));
        }
    }
}
