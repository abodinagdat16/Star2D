package com.star4droid.star2d.ElementDefs;
import com.star4droid.star2d.Helpers.PropertySet;
import java.lang.reflect.Field;
import com.star4droid.star2d.player.PlayerView;
import com.star4droid.star2d.player.CircleItem;

public class CircleDef {
	PropertySet<String,Object> propertySet= new PropertySet<>();
	public static final String TYPE="CIRCLE";
	public ElementEvent elementEvents;
	public String image="",type="DYNAMIC",name="", Collision="";
	public boolean Active=true,Bullet=false,isSensor=false,Fixed_Rotation=false,Visible=true;
	public float ColliderX=0,x=0,y=0,z=0,rotation=0,ColliderY=0,Gravity_Scale=1,tileX=1,tileY=1, Collider_Radius=Integer.MAX_VALUE,
	density=1,friction=0,restitution=0.5f,radius=50;
	
	public CircleDef(){
		propertySet.put("Collision","");
	}
	
	public CircleItem build(PlayerView player){
		if(name.equals("")) throw new RuntimeException("set name to the item..!!");
		propertySet = new PropertySet<>();
		for(Field field:getClass().getFields()){
			try {
				field.setAccessible(true);
				propertySet.put(field.getName().replace("_",""),field.get(this));
				} catch(Throwable ex){
				
			}
		}
		if(Collider_Radius==Integer.MAX_VALUE) propertySet.put("Collider Radius",radius);
		return new CircleItem(player.getContext(),propertySet,player,elementEvents);
	}
}