package com.fdangelo.circleworld.universeview;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.fdangelo.circleworld.GameLogic;
import com.fdangelo.circleworld.GameLogicState;
import com.fdangelo.circleworld.universeview.objects.AvatarInputMode;
import com.fdangelo.circleworld.universeview.objects.AvatarViewInput;
import com.fdangelo.circleworld.universeview.objects.ShipInputMode;
import com.fdangelo.circleworld.universeview.objects.ShipViewInput;
import com.fdangelo.circleworld.utils.Mathf;

public class UniverseViewCamera
{
	static public UniverseViewCamera Instance;
    
    private static final float CAMERA_Z = -10;
    private static final float SMOOTH_TIME = 0.5f;
    private static final float ZOOM_SMOOTH_TIME = 0.15f;
    
    public float cameraDistance = 10;
    public float zoomSpeed = 10;
    public float scale = 1.0f;
    
    public float minCameraDistance = 4;
    public float maxCameraDistance = 36000;

    private OrthographicCamera cam;
    private boolean moving;

    private Vector3 movingFromInputPosition;
    private int moveTouchFingerId;

    private boolean zooming;
    private int zoomingTouchFinger1Id;
    private int zoomingTouchFinger2Id;
    private Vector3 zoomingTouchFinger1FromPosition = new Vector3();
    private Vector3 zoomingTouchFinger2FromPosition = new Vector3();
    
    private float zoomingCameraDistanceDelta;
    private float zoomingCameraDistanceDeltaVelocity;
    
    private Actor followingObject;
    private boolean followRotation;
    private boolean followScale;
    
    private Vector2 followingObjectPositionDelta = new Vector2();
    //private Vector2 followingObjectPositionDeltaVelocity = new Vector2();
    
    private float followingObjectScaleDelta;
    //private float followingObjectScaleDeltaVelocity;
    
    private float followingObjectCameraDistanceDelta;
    //private float followingObjectCameraDistanceDeltaVelocity;
    
    private float followingObjectRotationDelta;
    
    private float followObjectSmoothTime;
    
    static private Vector2 tmpv2 = new Vector2();
    static private Vector3 tmpv3 = new Vector3();
    
    public Actor getFollowingObject()
    {
        return followingObject; 
    }
    
    public Camera getCamera()
    {
    	return cam;
    }
    
    public UniverseViewCamera(Stage stage)
    {
        Instance = this;
        
        cam = (OrthographicCamera) stage.getCamera();
        
        //trans.position = new Vector3(0, 0, CAMERA_Z);
    }
    
    public void Update(float deltaTime)
    {
        switch(GameLogic.Instace.getState())
        {
            case PlayingAvatar:
            case PlayingShip:
                UpdatePosition(deltaTime);
                UpdateZoomInput();
                UpdateZoom();
                break;
                
            case Travelling:
                UpdatePositionSmooth(deltaTime);
                UpdateZoom();
                break;
        }
    }
 
    private void UpdatePosition(float deltaTime)
    {
        followObjectSmoothTime = 0;
        
        if (followingObject != null)
        {
            if (GameLogic.Instace.getState() == GameLogicState.PlayingAvatar && AvatarViewInput.mode == AvatarInputMode.Move ||
                GameLogic.Instace.getState() == GameLogicState.PlayingShip && ShipViewInput.mode == ShipInputMode.Move)
            {
                followingObjectPositionDelta.set(Mathf.lerp(followingObjectPositionDelta, Vector2.Zero, deltaTime * (1.0f / SMOOTH_TIME)));
                followingObjectScaleDelta = Mathf.lerp(followingObjectScaleDelta, 0, deltaTime * (1.0f / SMOOTH_TIME));
                followingObjectRotationDelta = Mathf.lerp(followingObjectRotationDelta, 0, deltaTime * (1.0f / SMOOTH_TIME));
                followingObjectCameraDistanceDelta = Mathf.lerp(followingObjectCameraDistanceDelta, 0, deltaTime * (1.0f / SMOOTH_TIME));
            }
            
            float newPositionX, newPositionY;
            
            if (GameLogic.Instace.getState() == GameLogicState.PlayingAvatar && AvatarViewInput.mode == AvatarInputMode.Edit)
            {
                //newPosition = followingObject.position + trans.up * followingObjectPositionDelta.y + trans.right * followingObjectPositionDelta.x;
            	
            	newPositionX = followingObject.getX();
            	newPositionY = followingObject.getY();
            	
            	tmpv3.set(cam.up).scl(followingObjectPositionDelta.y);
            	newPositionX += tmpv3.x;
            	newPositionX += tmpv3.y;
            	
            	//Calculate "right" direction by making the cross product between the camera's foward and up vectors
            	tmpv3.set(cam.up).crs(cam.direction).scl(followingObjectPositionDelta.x);
            	newPositionX += tmpv3.x;
            	newPositionX += tmpv3.y;
            }
            else
            {
                newPositionX = followingObject.getX() + followingObjectPositionDelta.x;
                newPositionY = followingObject.getY() + followingObjectPositionDelta.y;
            }
            
            float newRotation;
            
            if (followRotation)
                newRotation = followingObject.getRotation();
            else
                newRotation = 0;
            
            float newScale;
            
            if (followScale)
                newScale = followingObject.getScaleX();
            else
                newScale = 1.0f;
            
            cam.position.x = newPositionX;
            cam.position.y = newPositionY;
            
            //trans.rotation = newRotation * followingObjectRotationDelta;
            
            scale = newScale + followingObjectScaleDelta;
        }
    }
    
    //Called by GameLogic
    private boolean UpdatePositionSmooth(float deltaTime)
    {
        followObjectSmoothTime += deltaTime;
        
        //followingObjectPositionDelta = Vector3.SmoothDamp(followingObjectPositionDelta, Vector2.zero, ref followingObjectPositionDeltaVelocity, SMOOTH_TIME);
        
        if (followingObject != null)
        {
        	followingObjectPositionDelta.set(Mathf.lerp(followingObjectPositionDelta, Vector2.Zero, followObjectSmoothTime / 1.0f));
            
            Vector3 newPosition = followingObject.position + trans.up * followingObjectPositionDelta.y + trans.right * followingObjectPositionDelta.x;
            newPosition.z = CAMERA_Z;
            
            Quaternion newRotation = followingObject.rotation;
            float newScale = followingObject.lossyScale.x;
            
            trans.position = Vector3.Lerp(trans.position, newPosition, followObjectSmoothTime / 1.0f);
            trans.rotation = Quaternion.Lerp(trans.rotation, newRotation, followObjectSmoothTime / 1.0f);
            scale = Mathf.Lerp(scale, newScale, followObjectSmoothTime / 1.0f);
            
            return followObjectSmoothTime > 1.0f;
        }
        else
        {
            return true;
        }
    }
    
    public void UpdateZoomInput()
    {
        if (Application.platform == RuntimePlatform.Android ||
            Application.platform == RuntimePlatform.IPhonePlayer)
        {
            if (Input.touchCount == 2)
            {
                Touch touch1 = Input.GetTouch(0);
                Touch touch2 = Input.GetTouch(1);

                if (!zooming || touch1.fingerId != zoomingTouchFinger1Id || touch2.fingerId != zoomingTouchFinger2Id)
                {
                    zooming = true;
                    zoomingTouchFinger1Id = touch1.fingerId;
                    zoomingTouchFinger2Id = touch2.fingerId;

                    zoomingTouchFinger1FromPosition = touch1.position;
                    zoomingTouchFinger2FromPosition = touch2.position;
                }
            }
            else
            {
                zooming = false;
            }

            if (zooming)
            {
                Vector3 finger1ToPosition = Input.GetTouch(0).position;
                Vector3 finger2ToPosition = Input.GetTouch(1).position;

                float deltaFrom = (zoomingTouchFinger1FromPosition - zoomingTouchFinger2FromPosition).magnitude;
                float deltaTo = (finger1ToPosition - finger2ToPosition).magnitude;

                float zoom = (deltaTo - deltaFrom) / Mathf.Sqrt(Screen.width * Screen.width + Screen.height * Screen.height);
                
                zoomingCameraDistanceDelta -= (cameraDistance + zoomingCameraDistanceDelta) * zoom * 4;

                zoomingTouchFinger1FromPosition = finger1ToPosition;
                zoomingTouchFinger2FromPosition = finger2ToPosition;
            }
        }
        else
        {
            //Use mouse
            float zoom = Input.GetAxis("Mouse ScrollWheel");
            zoomingCameraDistanceDelta -= (cameraDistance + zoomingCameraDistanceDelta) * zoom * zoomSpeed * Time.deltaTime;
        }
    }
    
    private void UpdateZoom()
    {
        float oldDelta = zoomingCameraDistanceDelta;
        zoomingCameraDistanceDelta = Mathf.SmoothDamp(zoomingCameraDistanceDelta, 0.0f, ref zoomingCameraDistanceDeltaVelocity, ZOOM_SMOOTH_TIME);
        cameraDistance += (oldDelta - zoomingCameraDistanceDelta);
        
        cameraDistance = Mathf.Clamp(cameraDistance, minCameraDistance, maxCameraDistance);  

        cam.orthographicSize = (cameraDistance + followingObjectCameraDistanceDelta) * scale;
    }
    
    public void UpdateMove()
    {
        Vector3 movingToInputPosition = movingFromInputPosition;

        if (Application.platform == RuntimePlatform.Android ||
            Application.platform == RuntimePlatform.IPhonePlayer)
        {
            if (Input.touchCount == 1)
            {
                Touch touch = Input.GetTouch(0);

                if (!moving || moveTouchFingerId != touch.fingerId)
                {
                    moveTouchFingerId = touch.fingerId;
                    moving = true;

                    movingFromInputPosition = touch.position;
                }
            }
            else
            {
                moving = false;
            }

            if (moving)
                movingToInputPosition = Input.GetTouch(0).position;
        }
        else
        {
            //Use mouse
            if (Input.GetMouseButtonDown(0))
            {
                moving = true;
                movingFromInputPosition = Input.mousePosition;
            }
            else if (Input.GetMouseButtonUp(0))
            {
                moving = false;
            }

            if (moving)
                movingToInputPosition = Input.mousePosition;
        }

        if (moving)
        {
            if (movingFromInputPosition != movingToInputPosition)
            {
                Vector3 movingFromWorldPosition = cam.ScreenToWorldPoint(movingFromInputPosition);
                Vector3 movingToWorldPosition = cam.ScreenToWorldPoint(movingToInputPosition);
                
                Vector3 delta = movingToWorldPosition - movingFromWorldPosition;
                
                if (followingObject)
                {
                    float deltaX = Vector3.Dot(delta, trans.right);
                    float deltaY = Vector3.Dot(delta, trans.up);
                    
                    followingObjectPositionDelta -= new Vector2(deltaX, deltaY);
                    
                    Vector3 newPosition = followingObject.position + trans.up * followingObjectPositionDelta.y + trans.right * followingObjectPositionDelta.x;
                    newPosition.z = CAMERA_Z;
                    
                    trans.position = newPosition;
                }

                movingFromInputPosition = movingToInputPosition;
                movingFromWorldPosition = cam.ScreenToWorldPoint(movingFromInputPosition);
            }
        }
    }
    

    private boolean travelInput;
    private Vector2 travelInputStartPosition;
    
    public void UpdateClickOnPlanetToTravel(UniverseView universeView)
    {
    	boolean clickTravel = false;
        Vector2 clickPosition = Vector2.zero;
        
        if (Application.platform == RuntimePlatform.Android || Application.platform == RuntimePlatform.IPhonePlayer)
        {
            if (!travelInput)
            {
                if (Input.touchCount == 1 && Input.GetTouch(0).phase == TouchPhase.Began && !InputAreas.IsInputArea(Input.GetTouch(0).position))
                {
                    travelInput = true;
                    travelInputStartPosition = Input.GetTouch(0).position;
                }
                else
                {
                    travelInput = false;
                }
            }
            else
            {
                if (Input.touchCount == 1)
                {
                    if (Input.GetTouch(0).phase == TouchPhase.Ended)
                    {
                        if ((travelInputStartPosition - Input.GetTouch(0).position).magnitude < 10)
                        {
                            clickTravel = true;
                            clickPosition = Input.GetTouch(0).position;
                        }
                        
                        travelInput = false;
                    }
                }
                else
                {
                    travelInput = false;
                }
            }
        }
        else
        {
            if (!travelInput)
            {
                if (Input.GetMouseButtonDown(0) && !InputAreas.IsInputArea(Input.mousePosition))
                {
                    travelInput = true;
                    travelInputStartPosition = Input.mousePosition;
                }
                else
                {
                    travelInput = false;
                }
            }
            else
            {
                if (Input.GetMouseButtonUp(0))
                {
                    if ((travelInputStartPosition - (Vector2) Input.mousePosition).magnitude < 10)
                    {
                        clickTravel = true;
                        clickPosition = Input.mousePosition;
                    }
                    
                    travelInput = false;
                }
                else if (!Input.GetMouseButton(0))
                {
                    travelInput = false;
                }
            }            
        }
        
        if (clickTravel)
        {
            Vector2 worldPos = Camera.main.ScreenToWorldPoint(clickPosition);
            Vector2 worldPosTolerance = Camera.main.ScreenToWorldPoint(clickPosition + Vector2.right * (Screen.dpi > 0 ? Screen.dpi : 72) / 2.54f); //1 cm tolerance
            
            int clickedThingIndex = universeView.Universe.FindClosestRenderedThing(worldPos, (worldPos - worldPosTolerance).magnitude);
               
            if (clickedThingIndex >= 0)
            {
                PlanetView targetPlanetView = universeView.GetPlanetView((ushort) clickedThingIndex);
                if (universeView.avatarView.UniverseObject.parent != targetPlanetView.TilemapCircle)
                    GameLogic.Instace.TravelToPlanet(targetPlanetView);
            }
        }
    }
    
    public void FollowObject(Actor toFollow, int followCameraParameters, boolean smoothTransition)
    {
        followRotation = (followCameraParameters & FollowCameraParameters.FollowRotation) != 0;
        followScale = (followCameraParameters & FollowCameraParameters.FollowScale) != 0;
        
        if (followingObject != toFollow)
        {
            if (smoothTransition && followingObject != null && toFollow != null)
            {
                followingObjectPositionDelta.set(cam.position.x - toFollow.getX(), cam.position.y - toFollow.getY());
                
                if (followScale)
                {
                    followingObjectScaleDelta = scale - toFollow.getScaleX();
                    
                    followingObjectCameraDistanceDelta = cameraDistance - cameraDistance * (scale / toFollow.getScaleX());
                    cameraDistance = cameraDistance * (scale / toFollow.getScaleX());
                }
                else
                {
                    followingObjectScaleDelta = scale - 1.0f;
                    
                    followingObjectCameraDistanceDelta = cameraDistance - cameraDistance * (scale / 1.0f);
                    cameraDistance = cameraDistance * (scale / 1.0f);
                }
                
                //if (followRotation)
                //    followingObjectRotationDelta = trans.rotation * Quaternion.Inverse(toFollow.rotation);
                //else
                //    followingObjectRotationDelta = trans.rotation * Quaternion.Inverse(Quaternion.identity);
            }
            
            followingObject = toFollow; 
        }
    }
}
