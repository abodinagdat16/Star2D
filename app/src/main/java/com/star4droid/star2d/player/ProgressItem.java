package com.star4droid.star2d.player;

import android.content.Context;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import com.star4droid.star2d.ElementDefs.ElementEvent;
import com.star4droid.star2d.Helpers.PropertySet;
import com.badlogic.gdx.physics.box2d.Body;
import android.view.View;
import com.star4droid.star2d.Helpers.StarProgressBar;

public class ProgressItem extends StarProgressBar implements PlayerItem {
	PropertySet<String,Object> propertySet;
	PlayerView playerView;
	ElementEvent elementEvent;
	public ProgressItem(Context context,PropertySet<String,Object> ps,PlayerView pv,ElementEvent event){
		super(context);
		propertySet=ps;
		playerView=pv;
		elementEvent = event;
		float width = propertySet.getFloat("width"),
		height = propertySet.getFloat("height"),
		x = propertySet.getFloat("x"),
		y = propertySet.getFloat("y");
		setX(x);
		setY(y);
		setZ(propertySet.getFloat("z"));
		setRotation(propertySet.getFloat("rotation"));
		setVisibility(propertySet.getString("Visible").equals("true")?View.VISIBLE:View.GONE);
		setLayoutParams(new FrameLayout.LayoutParams((int)width,(int)height));
		setBackColor(propertySet.getColor("Background Color"));
		setProgressColor(propertySet.getColor("Progress Color"));
		setMax(propertySet.getInt("Max"));
		setProgress(propertySet.getInt("Progress"));
		if(elementEvent!=null) setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View arg0) {
				if(elementEvent!=null) elementEvent.onClick(ProgressItem.this);
			}
		});
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
	    return null;
	}
	
	@Override
	public View getClone(String newName) {
		PropertySet<String,Object> set = new PropertySet<>();
		set.putAll(propertySet);
		set.put("old",getParentName());
		set.put("name",newName);
		return new ProgressItem(getContext(),set,playerView,elementEvent);
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
}