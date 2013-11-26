package com.fdangelo.circleworld.gui;

import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.esotericsoftware.tablelayout.Value;
import com.fdangelo.circleworld.gui.core.Screen;

public class AvatarMoveControlScreen extends Screen {
	
	@Override
	protected void initScreen() {
		
		Table halfLeft = new Table();
		Table halfRight = new Table();
		
		Button walkLeftButton = new TextButton("Left", getDefaultSkin());
		Button walkRightButton = new TextButton("Right", getDefaultSkin());
		Button jumpButton = new TextButton("Jump", getDefaultSkin());
		
		halfLeft.add(walkLeftButton).width(Value.percentWidth(0.5f)).height(Value.percentHeight(1));
		halfLeft.add(walkRightButton).width(Value.percentWidth(0.5f)).height(Value.percentHeight(1));
		
		halfRight.add(jumpButton).width(Value.percentWidth(1)).height(Value.percentHeight(1));
		
		getScreenTable().add(halfLeft).width(Value.percentWidth(0.5f)).height(Value.percentHeight(0.25f)).expandY().bottom();
		getScreenTable().add(halfRight).width(Value.percentWidth(0.5f)).height(Value.percentHeight(0.25f)).expandY().bottom();
	}
}
