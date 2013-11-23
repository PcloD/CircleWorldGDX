package com.fdangelo.circleworld;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.StringBuilder;
import com.fdangelo.circleworld.universeengine.utils.UEProfiler;

public class MyGdxGame implements ApplicationListener 
{
	private GameLogic gamelogic;
	private StringBuilder sb;
	private Stage guistage;
	private Label performance;
	private Skin skin;
	
	@Override
	public void create() 
	{		
		gamelogic = new GameLogic();
		
		initGUI();
	}
	
	private void initGUI() 
	{
		guistage = new Stage();
		Gdx.input.setInputProcessor(guistage);
		
		skin = new Skin(Gdx.files.internal("data/uiskin.json"));
		
		Table rootTable = new Table();
		rootTable.setFillParent(true);
		guistage.addActor(rootTable);
		
		rootTable.left();
		rootTable.top();
		
		performance = new Label("", skin);
		
		rootTable.add(performance);
		
		final TextButton switchButton = new TextButton("BOARD SHIP", skin);
				
		switchButton.addListener(new ChangeListener() {
			public void changed (ChangeEvent event, Actor actor) {
					
					switch(gamelogic.getState())
					{
						case PlayingAvatar:
							switchButton.setText("LEAVE SHIP");
							GameLogic.Instace.PlayerBoardShip();
							break;
							
						case PlayingShip:
				            int clickedThingIndex = gamelogic.universeView.getUniverse().FindClosestRenderedThing(gamelogic.universeView.shipView.getUniverseObject().getPositionX(), gamelogic.universeView.shipView.getUniverseObject().getPositionY(), 30.0f);
				            if (clickedThingIndex >= 0)
				            {
								switchButton.setText("BOARD SHIP");
				                GameLogic.Instace.PlayerLeaveShip(gamelogic.universeView.getUniverse().GetPlanet((short) clickedThingIndex));
				            }
							break;
							
						default:
							//Nothing
							break;
					}
			}
		});
			
		rootTable.add(switchButton).right().expandX().width(200).height(100);
		
		sb = new StringBuilder();
	}

	@Override
	public void dispose() {
		gamelogic.dispose();
		guistage.dispose();
	}

	@Override
	public void render() 
	{		
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		gamelogic.UpdateAndRender(Gdx.graphics.getDeltaTime());
				
		guistage.act();
		guistage.draw();
		
		updatePerformance();
	}
	
	private void updatePerformance()
	{
		//int totalRenderCalls = guistage.getSpriteBatch().renderCalls + gamelogic.getStage().getSpriteBatch().renderCalls;
		int totalRenderCalls = -1;
		
		sb.length = 0;
		sb.append("FPS: ")
		  .append(Gdx.graphics.getFramesPerSecond())
		  .append(" Used Memory: ")
		  .append(Gdx.app.getJavaHeap() / 1024)
		  .append("kb Used Native: ")
		  .append(Gdx.app.getNativeHeap() / 1024)
		  .append("kb Render Calls: ")
		  .append(totalRenderCalls);
		
		performance.setText(sb);
		
		UEProfiler.Update();
		UEProfiler.Clear();
	}

	@Override
	public void resize(int width, int height) 
	{
		guistage.setViewport(width, height);
		gamelogic.resize(width, height);
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}
}
