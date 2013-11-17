package com.fdangelo.circleworld.universeview.objects;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.fdangelo.circleworld.universeengine.objects.IUniverseObjectListener;
import com.fdangelo.circleworld.universeengine.objects.UniverseObject;
import com.fdangelo.circleworld.universeengine.tilemap.TilemapCircle;
import com.fdangelo.circleworld.universeview.UniverseView;
import com.fdangelo.circleworld.universeview.tilemap.TilemapCircleView;

public class UniverseObjectView extends Actor implements IUniverseObjectListener
{
    protected UniverseObject universeObject;
    protected UniverseView universeView;
    protected TilemapCircleView parentView;

    protected boolean visible = true;
    
    public UniverseObject getUniverseObject()
    {
        return universeObject;
    }
    
    public TilemapCircleView getParentView()
    {
        return parentView;
    }
    
    public UniverseView getUniverseView()
    {
        return universeView;
    }
    
    public UniverseObjectView()
    {
    }
    
    public void Init(UniverseObject universeObject, UniverseView universeView)
    {
        this.universeView = universeView;
        this.universeObject = universeObject;
        
        universeObject.Listener = this;
        
        parentView = universeView.GetPlanetView(universeObject.parent);
        
        UpdatePosition();
    }

    public void OnUniverseObjectUpdated(float deltaTime)
    {
        UpdatePosition();
    }
    
    public void OnParentChanged(TilemapCircle parent)
    {
        parentView = universeView.GetPlanetView(universeObject.parent);
        
        UpdatePosition();
    }

    protected void UpdatePosition()
    {
        if (universeObject.Visible)
        {
            if (!visible)
            {
                visible = true;
                go.SetActive(true);
            }

            trans.localPosition = universeObject.Position;
            trans.localScale = Vector3.one * universeObject.Scale;
            trans.localRotation = Quaternion.AngleAxis(-universeObject.Rotation * Mathf.Rad2Deg, Vector3.forward);
        }
        else
        {
            if (visible)
            {
                visible = false;
                go.SetActive(false);
            }
        }
    }
    
    /*
    public virtual void OnDrawGizmosSelected()
    {
        OnDrawGizmos();
    }

    public virtual void OnDrawGizmos()
    {
        if (universeObject != null)
        {
            Gizmos.color = Color.red;
            Gizmos.DrawLine(transform.position, transform.position + transform.up * universeObject.Size.y);
            Gizmos.color = Color.blue;
            Gizmos.DrawLine(transform.position + transform.up * universeObject.Size.y * 0.5f, transform.position + transform.up * universeObject.Size.y * 0.5f + transform.right * universeObject.Size.x * 0.5f);
        }
    }
    */
}

