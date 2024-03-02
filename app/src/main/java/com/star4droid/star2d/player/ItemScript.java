package com.star4droid.star2d.player;
import android.view.MotionEvent;
import android.view.View;
import com.badlogic.gdx.physics.box2d.Body;

public class ItemScript {
	private PlayerItem playerItem;
	public final Body body;
	public final View view;
	public ItemScript(PlayerItem item){
		playerItem = item;
		body = item.getBody();
		view = item.getView();
	}
	
	public void setItem(PlayerItem item){
		playerItem=item;
	}
	
	public void onClick(){
		
	}
	public void onTouchStart(MotionEvent event){
		
	}
	public void onTouchEnd(MotionEvent event){
		
	}
	public void onBodyCreated(){
		
	}
	public void onBodyUpdate(){
		
	}
	public void onCollisionBegin(PlayerItem other){
		
	}
	public void onCollisionEnd(PlayerItem other){
		
	}
	
	public final PlayerItem getPlayerItem(){
		return playerItem;
	}
}