package com.fdangelo.circleworld.universeview.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Application.ApplicationType;
import com.fdangelo.circleworld.GameLogic;
import com.fdangelo.circleworld.GameLogicState;
import com.fdangelo.circleworld.universeengine.objects.Ship;
import com.fdangelo.circleworld.universeengine.objects.ShipInput;
import com.fdangelo.circleworld.universeview.UniverseViewCamera;

public class ShipViewInput
{
    private ShipView shipView;
    
    static public ShipInputMode mode = ShipInputMode.Move;
    
    //private GUIStyle centeredLabelStyle;
    //private GUIStyle centeredBoxStyle;
    
    public ShipViewInput(ShipView shipView)
    {
    	this.shipView = shipView;
    }
    
    public void Update()
    {
        if (GameLogic.Instace.getState() != GameLogicState.PlayingShip)
            return;
        
        switch(mode)
        {
            case Move:
                UpdateMove();
                UniverseViewCamera.Instance.UpdateZoomInput();
                break;
        }
    }
        
    private void UpdateMove()
    {
        ShipInput shipInput = ((Ship) shipView.getUniverseObject()).input;
        
        if (Gdx.app.getType() == ApplicationType.Android || Gdx.app.getType() == ApplicationType.iOS)
        {
            int touchCount = 0;
            int touch1x = -1, touch1y = -1;
            int touch2x = -1, touch2y = -1;
            
            if (Gdx.input.isTouched(0))
            {
            	touch1x = Gdx.input.getX(0);
            	touch1y = Gdx.input.getY(0);
            	touchCount++;
            }
            
            if (Gdx.input.isTouched(1))
            {
            	touch1x = Gdx.input.getX(1);
            	touch1y = Gdx.input.getY(1);
            	touchCount++;
            }
            
            float screenWidth = Gdx.graphics.getWidth();
            float screenHeight = Gdx.graphics.getHeight();
            

            if (touchCount >= 1)
            {
                if (touch1x > screenWidth / 2.0f && touch1y < screenHeight * 0.25f ||
                    touchCount > 1 && touch2x > screenWidth / 2.0f  && touch2y < screenHeight * 0.25f)
                {
                	shipInput.moveDirection = 1.0f;
                }

                if (touch1x < screenWidth / 4.0f  && touch1y < screenHeight * 0.25f ||
                    touchCount > 1 && touch2x < screenWidth / 4.0f && touch2y < screenHeight * 0.25f)
                {
                	shipInput.rotateDirection = -1.0f;
                }
                else if (touch1x < screenWidth / 2.0f  && touch1y < screenHeight * 0.25f ||
                         touchCount > 1 && touch2x < screenWidth / 2.0f && touch2y < screenHeight * 0.25f)
                {
                	shipInput.rotateDirection = 1.0f;
                }
            }
        }
        else
        {
        	if (Gdx.input.isKeyPressed(Input.Keys.A))
        		shipInput.rotateDirection = -1;
        	else if (Gdx.input.isKeyPressed(Input.Keys.D))
        		shipInput.rotateDirection = 1;
        	else
        		shipInput.rotateDirection = 0;
        	
        	if (Gdx.input.isKeyPressed(Input.Keys.W))
        		shipInput.moveDirection = 1;
        	else
        		shipInput.moveDirection = 0;
        }
    }
    
    /*
    
#if ENABLE_ONGUI
    public void OnGUI()
    {
        if (GameLogic.Instace.State != GameLogicState.PlayingShip)
            return;
        
        if (centeredLabelStyle == null)
        {
            centeredLabelStyle = new GUIStyle(GUI.skin.label);
            centeredLabelStyle.alignment = TextAnchor.MiddleCenter;
            
            centeredBoxStyle = new GUIStyle(GUI.skin.box);
            centeredBoxStyle.alignment = TextAnchor.MiddleCenter;
        }
        
        InputAreas.ResetInputAreas();
        
        switch(mode)
        {
            case ShipInputMode.Move:
                DrawMoveGUI();
                break;
        }
    }
    
    private void DrawMoveGUI()
    {
        //Draw movement keys
        if (Application.platform == RuntimePlatform.Android || Application.platform == RuntimePlatform.IPhonePlayer)
        {
            InputAreas.AddInputArea(new Rect(0, Screen.height - Screen.height * 0.25f, Screen.width, Screen.height * 0.25f));
            
            GUI.Button(new Rect(0, Screen.height - Screen.height * 0.25f, Screen.width / 4, Screen.height * 0.25f), "Rotate Left");
            
            GUI.Button(new Rect(Screen.width / 4.0f, Screen.height - Screen.height * 0.25f, Screen.width / 4, Screen.height * 0.25f), "Rotate Right");
            
            GUI.Button(new Rect(Screen.width / 2.0f, Screen.height - Screen.height * 0.25f, Screen.width / 2, Screen.height * 0.25f), "Move Forward");
        }
        
        //Draw switch to avatar button
        InputAreas.AddInputArea(new Rect(Screen.width - (Screen.width / 8) * 3.0f, 0, Screen.width / 8, Screen.height / 8));
        if (GUI.Button(new Rect(Screen.width - (Screen.width / 8) * 3.0f, 0, Screen.width / 8, Screen.height / 8), "LEAVE SHIP"))
        {
            int clickedThingIndex = shipView.UniverseView.Universe.FindClosestRenderedThing(shipView.UniverseObject.Position, 30.0f);
            if (clickedThingIndex >= 0)
                GameLogic.Instace.PlayerLeaveShip(shipView.UniverseView.Universe.GetPlanet((ushort) clickedThingIndex));
        }
    }
#endif
     */

}


