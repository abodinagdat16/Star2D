package com.star4droid.star2d.Helpers;

import box2dLight.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.star4droid.star2d.Items.Editor;
import com.star4droid.template.Items.StageImp;
import com.star4droid.template.Utils.PlayerItem;

public class EditorLink {
	Editor editor;
	StageImp stage;
	public EditorLink(Editor editor,StageImp stage){
		this.editor = editor;
		this.stage = stage;
	}
	
	public StageImp getStage(){
		return stage;
	}
	
	public void positionChange(PropertySet<String,Object> propertySet){
		find(propertySet.getString("name"),item->{
			float x = propertySet.getFloat("x"),
					y = propertySet.getFloat("y");
					y = stage.getViewport().getWorldHeight()-item.getActor().getHeight()-y;
			if(item.getBody()!=null){
					float offX = propertySet.getFloat("ColliderX")*-1,
					offY = propertySet.getFloat("ColliderY")*-1;
					item.getBody().setTransform(
					new Vector2(
						(offX+x+(item.getActor().getWidth()*0.5f)),
						(offY+y+(item.getActor().getHeight()*0.5f))),
						(float)Math.toRadians(-propertySet.getFloat("rotation"))
					);
			} else item.getActor().setPosition(x,y);
		},light->{
			float dist = propertySet.getFloat("Distance"),
					x = propertySet.getFloat("x"),y = propertySet.getFloat("y");
			if(light instanceof PointLight){
				light.setPosition(x+dist*0.5f,stage.getViewport().getWorldHeight() - dist - y + dist*0.5f);
			} else if(light instanceof ConeLight) light.setPosition(x,stage.getViewport().getWorldHeight() - dist - y + dist*0.5f);
			light.setDirection(-propertySet.getFloat("rotation"));
		});
	}
	
	public void sizeChanged(PropertySet<String,Object> propertySet){
		find(propertySet.getString("name"),item->{
			if(item.getBody()!=null){
				Shape shape = item.getBody().getFixtureList().get(0).getShape();
				if(shape instanceof PolygonShape){
					((PolygonShape)shape).setAsBox(propertySet.getFloat("Collider Width")*0.5f,
													propertySet.getFloat("Collider Height")*0.5f);
					item.getActor().setSize(propertySet.getFloat("width"),propertySet.getFloat("height"));
				} else {
					shape.setRadius(propertySet.getFloat("Collider Radius")*0.5f);
					item.getActor().setSize(propertySet.getFloat("radius"),propertySet.getFloat("radius"));
				}
				item.getProperties().putAll(propertySet);
				item.update();
			} else {
				boolean isCircle = propertySet.containsKey("radius");
				item.getActor().setSize(propertySet.getFloat(isCircle?"radius":"width"),propertySet.getFloat(isCircle?"radius":"height"));
			}
		},null);
	}
	
	public void update(PropertySet<String,Object> propertySet){
		find(propertySet.getString("name"),item->{
			updateBody(item,propertySet);
		},light->{
			updateLight(light,propertySet);
		});
	}
	
	public void updateLight(Light light,PropertySet<String,Object> propertySet){
		light.setColor(Color.valueOf(propertySet.getString("color")));
		if(light instanceof ConeLight){
			((ConeLight)light).setConeDegree(propertySet.getFloat("Cone Degree"));
		}
		light.setDistance(propertySet.getFloat("Distance"));
		light.setActive(propertySet.getString("Active").equals("true"));
		light.setXray(propertySet.getString("X Ray").equals("true"));
		light.setStaticLight(propertySet.getString("Static Light").equals("true"));
		light.setSoftnessLength(propertySet.getFloat("Softness Length"));
		light.setSoft(propertySet.getString("Soft").equals("true"));
		String attachTo = propertySet.getString("attach To");
		PlayerItem item=attachTo.equals("")?null:stage.findItem(attachTo);
		if(light instanceof PositionalLight)
		    ((PositionalLight)light).attachToBody(item==null?null:item.getBody(),propertySet.getFloat("Offset X"),propertySet.getFloat("Offset Y"));
		else light.attachToBody(item==null?null:item.getBody());
	}
	
	public void updateBody(PlayerItem item,PropertySet<String,Object> propertySet){
		item.getProperties().clear();
		item.getProperties().putAll(propertySet);
		item.getProperties().put("do update","true");
		item.update();
	}
	
	private void find(String name,BodyCase bodyCase,LightCase lightCase){
		PlayerItem item = bodyCase!=null?stage.findItem(name):null;
		Light light = lightCase!=null?stage.findLight(name):null;
		if(item!=null)
			bodyCase.OnBodyCase(item);
		if(light!=null)
			lightCase.OnLightCase(light);
	}
	
	interface BodyCase {
		public void OnBodyCase(PlayerItem item);
	}
	interface LightCase {
		public void OnLightCase(Light light);
	}
}