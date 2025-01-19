package com.star4droid.star2d.Items;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.RequestListener;
import android.widget.LinearLayout.LayoutParams;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.star4droid.star2d.Helpers.FileUtil;
import com.star4droid.star2d.Helpers.JoyStick;
import com.star4droid.star2d.Helpers.PropertySet;
import com.star4droid.star2d.evo.R;
import com.star4droid.star2d.Utils;

public class JoyStickItem extends JoyStick implements EditorItem {
	Editor editor;
	PropertySet<String, Object> propertySet;
	float sx = 0.0f;
	float sy = 0.0f;
	
	public JoyStickItem(Context context) {
		super(context);
		stopped=true;
		setLayoutParams(new LayoutParams(50, 50));
		setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				
			}
		});
	}
	
	public void setProperties(PropertySet<String, Object> properties) {
		propertySet = properties;
		PropertySet<String,Object> temp = PropertySet.getDefualt(this,"joystick.json");
		for(String s:temp.keySet()){
			if(!propertySet.containsKey(s)){
				propertySet.put(s,temp.get(s));
			}
		}
		update();
		if(!propertySet.getString("Button Image").equals("")) {
			java.io.File file = new java.io.File(editor.getProject().getImagesPath()+propertySet.getString("Button Background").replace(Utils.seperator,"/"));
			Uri imageUri = Uri.fromFile(file);
			Glide.with(getContext()).asBitmap()
			.override(com.bumptech.glide.request.target.Target.SIZE_ORIGINAL, com.bumptech.glide.request.target.Target.SIZE_ORIGINAL)
			.priority(com.bumptech.glide.Priority.HIGH)
			.load(imageUri)
			.into(new com.bumptech.glide.request.target.CustomTarget<Bitmap>() {
				@Override
				public void onLoadFailed(Drawable arg0) {
					JoyStickItem.this.setButtonDrawable(null);
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
		
		if(!propertySet.getString("Pad Image").equals("")) {
			java.io.File file = new java.io.File(editor.getProject().getImagesPath()+propertySet.getString("Pad Image").replace(Utils.seperator,"/"));
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
		
	}
	
	public JoyStickItem setDefault() {
		propertySet = PropertySet.getDefualt(this,"joystick.json");
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
			//setRotation(this.propertySet.getFloat("rotation"));
			
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
			if (dist<= 40f) {
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
		return "Joystick";
	}
}