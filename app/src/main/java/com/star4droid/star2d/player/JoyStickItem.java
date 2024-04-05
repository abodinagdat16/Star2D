package com.star4droid.star2d.player;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import androidx.core.widget.TextViewCompat;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.star4droid.star2d.ElementDefs.ElementEvent;
import com.star4droid.star2d.Helpers.JoyStick;
import com.star4droid.star2d.Helpers.PropertySet;
import com.badlogic.gdx.physics.box2d.Body;

public class JoyStickItem extends JoyStick implements PlayerItem {
	PropertySet<String,Object> propertySet;
	PlayerView playerView;
	public ElementEvent elementEvent;
	public JoyStickItem(Context context,PropertySet<String,Object> ps,PlayerView pv,ElementEvent event){
		super(context);
		propertySet = ps;
		playerView = pv;
		elementEvent = event;
		float width = propertySet.getFloat("width"),
		height = propertySet.getFloat("height"),
		x = propertySet.getFloat("x"),
		y = propertySet.getFloat("y");
		setX(x);
		setY(y);
		setZ(propertySet.getFloat("z"));
//       getLayoutParams().height = (int)height;
//			getLayoutParams().width = (int)width;
//			setLayoutParams(getLayoutParams());
		setRotation(propertySet.getFloat("rotation"));
		setVisibility(propertySet.getString("Visible").equals("true")?View.VISIBLE:View.GONE);
		setLayoutParams(new FrameLayout.LayoutParams((int)width,(int)height));
		if(propertySet.getString("Button Image").equals("")) {
			java.io.File file = new java.io.File(propertySet.getString("Button Background"));
			Uri imageUri = Uri.fromFile(file);
			Glide.with(getContext()).asBitmap()
			.override(com.bumptech.glide.request.target.Target.SIZE_ORIGINAL, com.bumptech.glide.request.target.Target.SIZE_ORIGINAL)
			.priority(com.bumptech.glide.Priority.HIGH)
			.load(imageUri)
			.into(new com.bumptech.glide.request.target.CustomTarget<Bitmap>() {
				@Override
				public void onLoadFailed(Drawable arg0) {
					JoyStickItem.this.setButtonDrawable(null);
					if(elementEvent!=null) setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View arg0) {
				if(elementEvent!=null) elementEvent.onClick(JoyStickItem.this);
			}
		});
				}
				
				@Override
				public void onResourceReady(Bitmap bitmap, Transition<? super Bitmap> arg1) {
					JoyStickItem.this.setButtonDrawable(bitmap);
				}
				
				@Override
				public void onLoadCleared(Drawable arg0) {
				}
				
			});
		} else setButtonDrawable(null);
		
		if(propertySet.getString("Pad Image").equals("")) {
			java.io.File file = new java.io.File(propertySet.getString("Pad Image"));
			Uri imageUri = Uri.fromFile(file);
			Glide.with(getContext()).asBitmap()
			.override(com.bumptech.glide.request.target.Target.SIZE_ORIGINAL, com.bumptech.glide.request.target.Target.SIZE_ORIGINAL)
			.priority(com.bumptech.glide.Priority.HIGH)
			.load(imageUri)
			.into(new com.bumptech.glide.request.target.CustomTarget<Bitmap>() {
				@Override
				public void onLoadFailed(Drawable arg0) {
					JoyStickItem.this.setPadBackground(null);
				}
				
				@Override
				public void onResourceReady(Bitmap bitmap, Transition<? super Bitmap> arg1) {
					JoyStickItem.this.setPadBackground(bitmap);
				}
				
				@Override
				public void onLoadCleared(Drawable arg0) {
				}
				
			});
		} else setPadBackground(null);
		elementEvent.onBodyCreated(this);
	}
	
	@Override
	public void update() {
	    elementEvent.onBodyUpdate(this);
	}
	
	@Override
	public View getView() {
		return this;
	}
	
	@Override
	public Body getBody() {
		return null;
	}
	
	@Override
	public PropertySet<String, Object> getProperties() {
		return propertySet;
	}
	
	@Override
	public View getClone(String newName) {
		PropertySet<String,Object> set = new PropertySet<>();
		set.putAll(propertySet);
		set.put("old",getParentName());
		set.put("name",newName);
		return new JoyStickItem(getContext(),set,playerView,elementEvent);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(elementEvent!=null){
			switch(event.getAction()){
				case MotionEvent.ACTION_DOWN:
					elementEvent.onTouchStart(this,event);
					break;
				case MotionEvent.ACTION_UP:
					elementEvent.onTouchEnd(this,event);
					break;
				
			}
		}
		super.onTouchEvent(event);
		return true;
    }
	
	ChildsHolder childsHolder= new ChildsHolder(this);
	@Override
	public ChildsHolder getChildsHolder() {
		if(childsHolder!=null)
		return childsHolder;
		else {
			childsHolder = new ChildsHolder(this);
			return childsHolder;
		}
	}
	
	public float getJoyStickX(){
		return (float)(Math.cos(getAngle())*-1);
	}
	
	public float getJoyStickY(){
		return (float)(Math.sin(getAngle())*-1);
	}
	
}