package com.star4droid.star2d.Items;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout.LayoutParams;
import com.star4droid.star2d.Helpers.FileUtil;
import com.star4droid.star2d.Helpers.PropertySet;
import com.star4droid.star2d.evo.R;
import com.star4droid.star2d.Utils;
import java.util.ArrayList;

public class LightItem extends ImageView implements EditorItem {
	Editor editor;
	PropertySet<String, Object> propertySet;
	float sx = 0.0f;
	String Type = "point";
	float sy = 0.0f;
	Path path = new Path();
	Paint paint = new Paint(){
	    {
	        setStrokeCap(Cap.SQUARE);
    		setColor(Color.YELLOW);
    		setStrokeWidth(3.5f);
    		setStyle(Style.STROKE);
    		setStrokeCap(Cap.ROUND);
    		setStrokeJoin(Join.ROUND);
    		setDither(true);
    		setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SCREEN));
	    }
	};
	
	public LightItem(Context context) {
		super(context);
		setLayoutParams(new LayoutParams(250, 250));
		setImageResource(R.drawable.light);
		//setScaleType(ScaleType.FIT_XY);
		setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				
			}
		});
	}
	
	public void setProperties(PropertySet<String, Object> properties) {
		propertySet = properties;
		if(propertySet.getString("Light Type").equals("directional"))
		    setImageResource(R.drawable.arrow);
		PropertySet<String,Object> temp = PropertySet.getDefualt(this, "Lights/"+propertySet.getString("Light Type")+".json");
		for(String key:temp.keySet()){
		    if(key.equals("Script")||key.equals("parent")) continue;
			if(!propertySet.containsKey(key)){
				propertySet.put(key,temp.get(key));
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
	}
	
	public LightItem setDefault(String type) {
		propertySet = PropertySet.getDefualt(this,"Lights/"+type+".json");
		if(propertySet==null) Log.e(Utils.error_tag,"Null PropertySet");
		propertySet.remove("Script");
		propertySet.remove("parent");
		if(propertySet.getString("Light Type").equals("directional"))
		    setImageResource(R.drawable.arrow);
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
			int h = (int)propertySet.getFloat("Distance");
			int w = (int)this.propertySet.getFloat("Distance");
			if(h==0) h = 200;
			if(w==0) w = 200;
			float moveX = this.editor.getMoveX();
			float moveY = this.editor.getMoveY();
			float x = this.propertySet.getFloat("x");
			float y = this.propertySet.getFloat("y");
			
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
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if(propertySet==null||!propertySet.containsKey("Light Type")) return;
		String type=propertySet.getString("Light Type");
		if(type.equals("")) return;
		path.reset();
		float width= getMeasuredWidth(), height= getMeasuredHeight();
		if(type.equals("point")){
		    path.addCircle(width*0.5f,height*0.5f,Math.min(width, height)*0.5f,Path.Direction.CW);
		} else if(type.equals("cone")){
		    //cone light ... draw triangle ▶️ 
		    path.moveTo(2, height*0.5f);// •
		    path.lineTo(width-2,0); // => /
		    path.lineTo(width-2,height-2); // => |
		    path.lineTo(2, height*0.5f); // => \
		} else {
		    //directional light ...draw arrow (ToDo : idk)
		    // path.moveTo(2, height*(1f/3f));
		    // path.lineTo(width*(2f/3f),height*(1f/3f));
		    // path.lineTo(width*(2f/3f),height*0.5f);
		    // path.lineTo(width*(2f/3f), height - 2);
		    // path.lineTo(width*(2f/3f), height*(2f/3f));
		    // path.lineTo(2,height*(2f/3f));
		    // path.lineTo(2, height*(1f/3f));
		}
		if(!type.equals("directional"))
		    canvas.drawPath(path,paint);
	}
	
	@Override
	public String getTypeName(){
		return "PointLight";
	}
}