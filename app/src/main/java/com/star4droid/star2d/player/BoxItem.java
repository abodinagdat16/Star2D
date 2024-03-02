package com.star4droid.star2d.player;

import android.content.Context;
import android.graphics.Point;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.star4droid.star2d.ElementDefs.ElementEvent;
import com.star4droid.star2d.Helpers.PropertySet;
import com.badlogic.gdx.physics.box2d.Body;
import android.widget.ImageView;
import com.star4droid.star2d.Utils;

public class BoxItem extends ImageView implements PlayerItem {
	Body body;
	PropertySet<String,Object> propertySet;
	PlayerView player;
	public ElementEvent elementEvent;
	float scale=1,width,height;
	float[] offset = new float[]{0,0};//collider x,y
	public BoxItem(Context context,PropertySet<String,Object> props,PlayerView pv,ElementEvent event){
		super(context);
		setScaleType(ScaleType.FIT_XY);
		propertySet = props;
		player = pv;
		elementEvent = event;
		createBody();
		if(elementEvent!=null) setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View arg0) {
				if(elementEvent!=null) elementEvent.onClick(BoxItem.this);
			}
		});
	}
	
	private void createBody(){
		int rx = propertySet.getInt("tileX"),
			ry = propertySet.getInt("tileY");
		width = propertySet.getFloat("width");
		height = propertySet.getFloat("height");
		float x = propertySet.getFloat("x"),
		y = propertySet.getFloat("y");
		Utils.setImageFromFile(this,player.project.getImagesPath()+propertySet.getString("image"),new Point(rx,ry),null,null);
		setX(x);
		setY(y);
		setZ(propertySet.getFloat("z"));
		setRotation(propertySet.getFloat("rotation"));
		setVisibility(propertySet.getString("Visible").equals("true")?View.VISIBLE:View.GONE);
		setLayoutParams(new FrameLayout.LayoutParams((int)width,(int)height));
		if(!propertySet.getString("type").equals("UI")){
			String bt= String.valueOf(propertySet.getString("type").charAt(0)).toUpperCase();
			BodyDef.BodyType type = bt.equals("K")?BodyDef.BodyType.KinematicBody:(bt.equals("S")?BodyDef.BodyType.StaticBody:BodyDef.BodyType.DynamicBody);
			//creating the body
			BodyDef def = new BodyDef();
			def.type = type;
			offset[0] = propertySet.getFloat("ColliderX")*-1;
			offset[0] = propertySet.getFloat("ColliderY")*-1;
			def.position.set(0,0);
			body = player.world.createBody(def);
			//define its properties
			PolygonShape shape = new PolygonShape();
			shape.setAsBox(propertySet.getFloat("Collider Width")*player.worldScale*0.5f,propertySet.getFloat("Collider Height")*player.worldScale*0.5f);
			FixtureDef fx = new FixtureDef();
			fx.shape = shape;
			fx.friction=propertySet.getFloat("friction");
			fx.density=propertySet.getFloat("density");
			fx.restitution=propertySet.getFloat("restitution");
			fx.isSensor = propertySet.getString("isSensor").equals("true");
			body.createFixture(fx).setUserData(this);
			body.setFixedRotation(propertySet.getString("Fixed Rotation").equals("true"));
			body.setActive(propertySet.getString("Active").equals("true"));
			body.setBullet(propertySet.getString("Bullet").equals("true"));
			body.setGravityScale(propertySet.getFloat("Gravity Scale"));
			body.setTransform(new Vector2((offset[0]+x+(width/2))*player.worldScale,(offset[1]+y+(height/2))*player.worldScale),(float)Math.toRadians(propertySet.getFloat("rotation")));
			
		}
		if(elementEvent!=null) elementEvent.onBodyCreated(this);
	}
	
	@Override
	public void update(){
		if(body!=null){
			if(player.scale!=scale){
				scale = player.scale;
				setLayoutParams(new FrameLayout.LayoutParams((int)(width*scale),(int)(height*scale)));
			}
			float x= body.getPosition().x*(1/player.worldScale)+offset[0]-(getMeasuredWidth()*(1/player.scale)/2),
				  y= body.getPosition().y*(1/player.worldScale)+offset[1]-(getMeasuredHeight()*(1/player.scale)/2),
				  wd = player.getMeasuredWidth()*0.5f, hg = player.getMeasuredHeight()*0.5f;
			setX(((x-wd+player.worldX)*player.scale)+wd);
			setY(((y-hg+player.worldY)*player.scale)+hg);
			setRotation((float)Math.toDegrees(body.getAngle()));
			
		}
		if(elementEvent!=null) elementEvent.onBodyUpdate(this);
	}
	
	@Override
	public View getView() {
	    return this;
	}

	@Override
	public Body getBody() {
	    return body;
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
		return new BoxItem(getContext(),set,player,elementEvent);
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
	
	@Override
	public void destroy() {
		body.getWorld().destroyBody(body);
		player.removeView(this);
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