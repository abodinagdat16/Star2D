package com.star4droid.star2d.ElementDefs;

import com.star4droid.star2d.Helpers.PropertySet;
import com.star4droid.star2d.player.TextItem;
import com.star4droid.star2d.player.PlayerView;
import java.lang.reflect.Field;

public class TextDef {
	public ElementEvent elementEvents;
	PropertySet<String,Object> propertySet= new PropertySet<>();
	public static final String TYPE="TEXT";
	public String name="",Text="TextItem",Text_Color="#FFFFFF";
	public boolean Visible=true;
	public float x=0,y=0,z=0,width=50,height=50,rotation=0;
	
	public TextDef(){
		
	}
	
	public TextItem build(PlayerView player){
		if(name.equals("")) throw new RuntimeException("set name to the item..!!");
		propertySet = new PropertySet<>();
		for(Field field:getClass().getFields()){
			try {
				field.setAccessible(true);
				propertySet.put(field.getName().replace("_"," "),field.get(this));
				} catch(Throwable ex){
				
			}
		}
		return new TextItem(player.getContext(),propertySet,player,elementEvents);
	}
}