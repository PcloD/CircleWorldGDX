package com.fdangelo.circleworld;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.StringBuilder;
import com.fdangelo.circleworld.universeengine.utils.UEProfiler;

public class MyGdxGame implements ApplicationListener 
{
	private GameLogic gamelogic;
	private StringBuilder sb;
	private Stage guistage;
	private Label performance;
	private Skin skin;
	private BitmapFont font;
	
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
		
		font = new BitmapFont();
		
		skin = new Skin();
        skin.add("default", new LabelStyle(font, new Color(Color.WHITE)));		
		
		Table table = new Table();
		table.setFillParent(true);
		guistage.addActor(table);		
		
		table.left();
		table.top();
		
		performance = new Label("", skin);
		table.add(performance);
		
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
		int totalRenderCalls = guistage.getSpriteBatch().renderCalls + gamelogic.getStage().getSpriteBatch().renderCalls;
		
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
		gamelogic.getStage().setViewport(width, height);
		guistage.setViewport(width, height);
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}
}
