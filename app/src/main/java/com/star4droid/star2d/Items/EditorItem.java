package com.star4droid.star2d.Items;

import android.graphics.Point;
import com.star4droid.star2d.Helpers.PropertySet;

public interface EditorItem {
	Point repeat= new Point(1,1);
	public PropertySet<String,Object> getPropertySet();
	public void update();
	public void setProperties(PropertySet<String,Object> propertySet);
	public String getTypeName();
}