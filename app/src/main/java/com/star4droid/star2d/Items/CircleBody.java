package com.star4droid.star2d.Items;

import android.content.Context;
import android.graphics.Point;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout.LayoutParams;
import com.star4droid.star2d.Adapters.CircularImageView;
import com.star4droid.star2d.Helpers.FileUtil;
import com.star4droid.star2d.Helpers.PropertySet;
import com.star4droid.star2d.evo.R;
import com.star4droid.star2d.Utils;
import java.util.ArrayList;

public class CircleBody extends CircularImageView implements EditorItem {
	Editor editor;
	PropertySet<String, Object> propertySet;
	float sx = 0.0f;
	float sy = 0.0f;
	
	public CircleBody(Context context) {
		super(context);
		setLayoutParams(new LayoutParams(50, 50));
		setImageResource(R.drawable.icon);
		setScaleType(ScaleType.FIT_XY);
		setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				
			}
		});
	}
	
	public void setProperties(PropertySet<String, Object> properties) {
		propertySet = properties;
		
		if(!propertySet.getString("Shape").equals("Circle")){
		    if(editor!=null){
		        try {
		            editor.removeView(this);
		            EditorItem newBody = propertySet.getString("Shape").equals("Box") ? new BoxBody(editor.getContext()) : new CustomBody(getContext());
            				editor.addView((View)newBody);
            				newBody.setProperties(properties);
            				editor.selectView((View)newBody);
            				newBody.update();
		        } catch(Exception e){}
		    }
		    return;
        }
		
		PropertySet<String,Object> temp = PropertySet.getDefualt(this,"circle.json");
		for(String s:temp.keySet()){
			if(!propertySet.containsKey(s)){
				propertySet.put(s,temp.get(s));
			}
		}
		
	   ArrayList<String> toDel = new ArrayList<>();
	   for(String key:propertySet.keySet()){
			if(!temp.containsKey(key)){
				toDel.add(key);
			}
		}
		for(String key:toDel)
	        propertySet.remove(key);
		
		update();
		
		if (!this.propertySet.getString("image").equals("")) {
			String img = this.editor.getProject().getImagesPath() + this.propertySet.getString("image");
			if (FileUtil.isExistFile(img)) {
				repeat.x = this.propertySet.getInt("tileX");
				repeat.y = this.propertySet.getInt("tileY");
				Utils.setImageFromFile(this, img, repeat, null, null);
			}
		}
	}
	
	public CircleBody setDefault() {
		propertySet = PropertySet.getDefualt(this,"circle.json");
		if(propertySet==null) Log.e(Utils.error_tag,"Null PropertySet");
		return this;
	}
	
	public PropertySet<String, Object> getPropertySet() {
		return this.propertySet;
	}
	
	@Override
	public void update() {
		if (this.editor == null) {
			this.editor = (Editor) getParent();
		}
		
		if (propertySet != null) {
			int h = (int)(propertySet.getFloat("radius")*2);
			int w = (int)(this.propertySet.getFloat("radius")*2);
			float moveX = this.editor.getMoveX();
			float moveY = this.editor.getMoveY();
			float x = this.propertySet.getFloat("x");
			float y = this.propertySet.getFloat("y");
			setVisibility(propertySet.getString("Visible").equals("true")?VISIBLE:GONE);
			float editorScale = this.editor.getEditorScale();
			float measuredWidth = this.editor.getScreenView().getMeasuredWidth() / 2.0f;
			float measuredHeight = this.editor.getScreenView().getMeasuredHeight() / 2.0f;
			setX((((moveX + x) - measuredWidth) * editorScale) + measuredWidth);
			setY((((moveY + y) - measuredHeight) * editorScale) + measuredHeight);
			setZ(this.propertySet.getFloat("z"));
			getLayoutParams().height = Math.max((int)(h * editorScale), 1);
			getLayoutParams().width = Math.max((int)(w * editorScale), 1);
			setLayoutParams(getLayoutParams());
			//Log.e("star2d_Out","x : "+getX()+", y : "+getY()+", w : "+w+", h : "+h);
			setRotation(this.propertySet.getFloat("rotation"));
		}
	}
	
	public boolean onTouchEvent(MotionEvent motionEvent) {
	    if(!isEnabled()) return false;
		if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
			this.sx = motionEvent.getRawX();
			this.sy = motionEvent.getRawY();
			editor.disableTouchExcept(this);
		}
		if (motionEvent.getAction() == MotionEvent.ACTION_UP || motionEvent.getAction()==MotionEvent.ACTION_CANCEL) {
			float X = this.sx - motionEvent.getRawX();
			float Y = this.sy - motionEvent.getRawY();
			double dist = Math.sqrt((X*X)+(Y*Y));
			if (dist<= 80.0f) {
				editor.selectView(this);
			}
			editor.enableTouch();
		}
		//MotionEvent event = MotionEvent.obtain(android.os.SystemClock.uptimeMillis(),android.os.SystemClock.uptimeMillis(),motionEvent.getAction(),getX()+motionEvent.getX(),getY()+motionEvent.getY(),0);
		editor.onTouchEvent(motionEvent);
		//this.editor.dispatchTouchEvent(motionEvent);
		return true;
	}
	
	
	@Override
	public String getTypeName(){
		return "Circle";
	}
}