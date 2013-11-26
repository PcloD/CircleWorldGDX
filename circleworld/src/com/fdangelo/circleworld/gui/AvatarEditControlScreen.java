package com.fdangelo.circleworld.gui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.esotericsoftware.tablelayout.Value;
import com.fdangelo.circleworld.gui.core.Screen;
import com.fdangelo.circleworld.universeview.objects.AvatarInputEditTool;
import com.fdangelo.circleworld.universeview.objects.AvatarInputMode;
import com.fdangelo.circleworld.universeview.objects.AvatarViewInput;

public class AvatarEditControlScreen extends Screen {
	
	private Label tooltip;
	
	@Override
	protected void initScreen() {
		
		Table bottom = new Table();
		
		Button toolNone = new TextButton("None", getDefaultSkin());
		Button toolAddTiles = new TextButton("Add Tiles", getDefaultSkin());
		Button toolRemoveTiles = new TextButton("Remove Tiles", getDefaultSkin());
		Button toolMoveCamera = new TextButton("Move Camera", getDefaultSkin());
		
		toolNone.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				onToolNoneClicked();
			}
		});
		
		toolAddTiles.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				onToolAddTilesClicked();
			}
		});
		
		toolRemoveTiles.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				onToolRemoveTilesClicked();
			}
		});
		
		toolMoveCamera.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				onToolMoveCameraClicked();
			}
		});
				
		bottom.add(toolNone).width(Value.percentWidth(0.25f)).height(Value.percentHeight(1));
		bottom.add(toolAddTiles).width(Value.percentWidth(0.25f)).height(Value.percentHeight(1));
		bottom.add(toolRemoveTiles).width(Value.percentWidth(0.25f)).height(Value.percentHeight(1));
		bottom.add(toolMoveCamera).width(Value.percentWidth(0.25f)).height(Value.percentHeight(1));
		
		tooltip = new Label("Select a tool", getDefaultSkin());
		
		getScreenTable().add(tooltip).expandY().bottom();
		getScreenTable().row();
		getScreenTable().add(bottom).width(Value.percentWidth(1.0f)).height(Value.percentHeight(0.25f)).bottom();
	}
	
	public final void setTool(AvatarInputEditTool tool) {
		AvatarViewInput.mode = AvatarInputMode.Edit;
		AvatarViewInput.editTool = tool;
		
		switch(tool) {
			case None:
				tooltip.setText("Select a tool");
				break;
			case Add:
				tooltip.setText("[Add Tiles] -> Tap to add tiles");
				break;
			case Remove:
				tooltip.setText("[Remove Tiles] -> Tap to remove tiles");
				break;
			case MoveCamera:
				tooltip.setText("[Move Camera] -> Pan to move camera");
				break;
		}
	}
	
	private final void onToolNoneClicked() {
		setTool(AvatarInputEditTool.None);
	}
	
	private final void onToolAddTilesClicked() {
		setTool(AvatarInputEditTool.Add);
	}

	private final void onToolRemoveTilesClicked() {
		setTool(AvatarInputEditTool.Remove);
	}

	private final void onToolMoveCameraClicked() {
		setTool(AvatarInputEditTool.MoveCamera);
	}
}
