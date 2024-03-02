package com.star4droid.star2d.player;
import android.view.View;
import android.widget.FrameLayout;
import com.badlogic.gdx.physics.box2d.Body;
import com.star4droid.star2d.Helpers.PropertySet;

public interface PlayerItem {
	public void update();
	public View getView();
	public Body getBody();
	default public FrameLayout getFrame(){
		return (FrameLayout)getView().getParent();
	}
	default public String getName(){
		return getProperties().getString("name");
	};
	default public String getParentName(){
		return getProperties().containsKey("old")?getProperties().getString("old"):getName();
	}
	default public PlayerItem getChild(String child){
		ChildsHolder holder= getChildsHolder().getChild(child);
		if(holder!=null) return holder.getPlayerItem();
		return null;
	}
	default public void setScript(ItemScript script){
		getProperties().setScript(script);
	}
	default public <T extends ItemScript> T getScript(){
		return (T)getProperties().getScript();
	}
	public ChildsHolder getChildsHolder();
	default public void setParent(PlayerItem item){
		getChildsHolder().setParent(item.getChildsHolder());
	}
	default public void addChild(PlayerItem item){
		getChildsHolder().addChild(item);
	}
	public PropertySet<String,Object> getProperties();
	public View getClone(String newName);
	default public void setItemText(String text){};
	default public void setProgress(int progress){};
	default public int getProgress(){return 0;}
	default public void setMax(int max){}
	default public float getJoyStickX(){return 0;}
	default public float getJoyStickY(){return 0;}
	default public double getAngle(){return 0;}
	default public double getPower(){return 0;}
	default public double getAngleDegrees(){return 0;}
	default public void destroy(){}
	default public float getBodyX(){return getBody().getPosition().x;}
	default public float getBodyY(){return getBody().getPosition().y;}
	default public float getViewX(){return getView().getX();}
	default public float getViewY(){return getView().getY();}
	default public float distToPoint(float x,float y){
		float xx= x-this.getBody().getPosition().x;
		float yy= y-this.getBody().getPosition().y;
		return (float)Math.sqrt(xx*xx+yy*yy);
	}
	default public float distTo(PlayerItem item){
		return this.distToPoint(item.getBody().getPosition().x,item.getBody().getPosition().y);
	}
	default public float getViewCenterX(){
		return (getView().getX()+(getView().getMeasuredWidth()/2));
	}
	
	default public float getViewCenterY(){
		return (getView().getY()+(getView().getMeasuredHeight()/2));
	}
	
}