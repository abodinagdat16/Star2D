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
import android.widget.Toast;
import com.star4droid.star2d.Helpers.FileUtil;
import com.star4droid.star2d.Helpers.PropertySet;
import com.star4droid.star2d.evo.R;
import com.star4droid.star2d.Utils;

public class BoxBody extends ImageView implements EditorItem {
	Editor editor;
	PropertySet<String, Object> propertySet;
	float sx = 0.0f;
	float sy = 0.0f;
	
	public BoxBody(Context context) {
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
		PropertySet<String,Object> temp = PropertySet.getDefualt(this,"box.json");
		for(String s:temp.keySet()){
			if(!propertySet.containsKey(s)){
				propertySet.put(s,temp.get(s));
			}
		}
		update();
		
		if (!this.propertySet.getString("image").equals("")) {
            Toast.makeText(getContext(),"yes",2000).show();
			String img = this.editor.getProject().getImagesPath() + this.propertySet.getString("image").replace(Utils.seperator,"/");
			if (FileUtil.isExistFile(img)) {
				repeat.x = this.propertySet.getInt("tileX");
				repeat.y = this.propertySet.getInt("tileY");
				Utils.setImageFromFile(this, img, repeat, null, null);
			}
		}
        
	}
	
	public BoxBody setDefault() {
		propertySet = PropertySet.getDefualt(this,"box.json");
		if(propertySet==null) Log.e(Utils.error_tag,"Null PropertySet");
		return this;
	}
	
	public PropertySet<String, Object> getPropertySet() {
		return this.propertySet;
	}
	
	public void update() {
		if (this.editor == null) {
			this.editor = (Editor) getParent();
		}
		
		if (propertySet != null) {
			int h = (int) propertySet.getFloat("height");
			int w = (int) this.propertySet.getFloat("width");
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
			setRotation(this.propertySet.getFloat("rotation"));
		}
	}
	
	public void debug() {
		this.propertySet.getFloat("height");
		this.propertySet.getFloat("width");
		float moveX = this.editor.getMoveX();
		float moveY = this.editor.getMoveY();
		float f = this.propertySet.getFloat("x");
		float f2 = this.propertySet.getFloat("y");
		float editorScale = this.editor.getEditorScale();
		float measuredWidth = (this.editor.getScreenView().getMeasuredWidth()) / 2.0f;
		float measuredHeight = (this.editor.getScreenView().getMeasuredHeight()) / 2.0f;
		String str = "(";
		String str2 = "+";
		String str3 = "-";
		String str4 = ")*";
		String str5 = "MoveResult";
		Log.e(str5, str + moveX + str2 + f + str3 + measuredWidth + str4 + editorScale + str2 + measuredWidth);
		Log.e(str5, "resX : " + (((moveX + f) - measuredWidth) * editorScale) + measuredWidth);
		Log.e(str5, str + moveY + str2 + f2 + str3 + measuredHeight + str4 + editorScale + str2 + measuredHeight);
		Log.e(str5, "resY : " + (((moveY + f2) - measuredHeight) * editorScale) + measuredHeight);
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
		return "Box";
	}
}