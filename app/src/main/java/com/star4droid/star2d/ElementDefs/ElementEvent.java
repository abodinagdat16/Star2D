package com.star4droid.star2d.ElementDefs;

import android.view.MotionEvent;
import android.view.View;
import com.star4droid.star2d.Helpers.PropertySet;
import com.star4droid.star2d.player.PlayerItem;

public interface ElementEvent {
	default public String getName(){
		try {
			return PropertySet.getPropertySet((View)this).get("name").toString();
		} catch(Exception ex){
			return "Null";
		}
	}
	public void onClick(PlayerItem current);
	public void onTouchStart(PlayerItem current,MotionEvent event);
	public void onTouchEnd(PlayerItem current,MotionEvent event);
	public void onBodyCreated(PlayerItem current);
	public void onBodyUpdate(PlayerItem current);
	public void onCollisionBegin(PlayerItem current,PlayerItem body2);
	public void onCollisionEnd(PlayerItem curreny,PlayerItem body2);
}