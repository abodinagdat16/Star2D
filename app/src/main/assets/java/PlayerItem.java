package com.star4droid.star2d.player;
import android.view.View;
import android.widget.FrameLayout;
import com.badlogic.gdx.physics.box2d.Body;
import com.star4droid.star2d.Helpers.PropertySet;

public interface PlayerItem {
	public void update();
	public View getView();
	public Body getBody();
	public FrameLayout getFrame();
	public String getName();
	public String getParentName();
	public PlayerItem getChild(String child);
	public void setScript(ItemScript script);
	public <T extends ItemScript> T getScript();
	public ChildsHolder getChildsHolder();
	public void setParent(PlayerItem item);
	public void addChild(PlayerItem item);
	public PropertySet<String,Object> getProperties();
	public View getClone(String newName);
	public void setItemText(String text);
	public void setProgress(int progress);
	public int getProgress();
	public void setMax(int max);
	public float getJoyStickX();
	public float getJoyStickY();
	public double getAngle();
	public double getPower();
	public double getAngleDegrees();
	public void destroy();
	public float getBodyX();
	public float getBodyY();
	public float getViewX();
	public float getViewY();
	public float distToPoint(float x,float y);
	public float distTo(PlayerItem item);
	public float getViewCenterX();
	public float getViewCenterY();
	
}