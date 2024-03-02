package com.star4droid.star2d.Items;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.star4droid.star2d.Adapters.EditorField;
import com.star4droid.star2d.Adapters.Section;
import com.star4droid.star2d.CodeEditor.MyIndexer;
import com.star4droid.star2d.Helpers.FileUtil;
import com.star4droid.star2d.Helpers.Project;
import com.star4droid.star2d.Helpers.PropertySet;
import com.star4droid.star2d.Helpers.ScaleGesture;
import com.star4droid.star2d.Utils;
import java.util.Comparator;
import java.util.Collections;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

public class Editor extends FrameLayout {
	private float scale = 0.5f, sizeX = 50, sizeY = 50,logicalWidth=1600,logicalHeight=720,ratioScale=1;
	boolean SHOW_GRIDS = true, GRID_MOVEMENT = false, AUTO_SAVE = true;
	Paint paint = new Paint(), gridPaint;
	ScaleGestureDetector detector;
	private String scene = "scene1";
	private View selectedView, selector;
	Path path = new Path(), boxPath = new Path();
	PropertySet<String,Object> editorConfig;
	Project project;
	LinearLayout propertiesLinear;
	PointPicker picker;
	private MyIndexer myIndexer;
	private static Editor currentEditor;
	GradientDrawable selectorDrawable = new GradientDrawable() { public GradientDrawable getIns(int a, int b, int c, int d) { this.setCornerRadius(a); this.setStroke(b, c); this.setColor(d); return this; } }.getIns(0, 3, Color.RED, Color.TRANSPARENT);
	/*
	PropertySet.onChangeListener onChangeListener = new PropertySet.onChangeListener(){
		@Override
		public void onChange(String s, Object object) {
		//if(AUTO_SAVE) project.save(Editor.this);
		}
	};
	*/

	public Editor(Context context) {
		super(context);
		init();
	}

	public Editor(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);
		init();
	}

	public Editor(Context context, AttributeSet attributeSet, int n) {
		super(context, attributeSet, n);
		init();
	}
	
	@Override
	public void onConfigurationChanged(Configuration _configuration) {
		super.onConfigurationChanged(_configuration);
		ScreenView = new ScreenSize(this);
		updateChilds();
		updateSelector();
		
	}
	//initialize the view
	public void init() {
		setBackgroundColor(Color.TRANSPARENT);
		paint.setStrokeCap(Paint.Cap.SQUARE);
		paint.setColor(Color.RED);
		paint.setStrokeWidth(2.0f);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeCap(Paint.Cap.ROUND);
		paint.setStrokeJoin(Paint.Join.ROUND);
		paint.setDither(true);
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SCREEN));
		gridPaint = new Paint(paint);
		gridPaint.setAlpha(75);
		gridPaint.setStrokeWidth(0.5f);
		gridPaint.setColor(Color.DKGRAY);
		detector = new ScaleGestureDetector(this.getContext(), new ScaleGesture().setEditor(this));
		selector = new LinearLayout(getContext());
		Utils.setCornerRadiusWithStroke(selector, 0, 3, Color.RED, Color.TRANSPARENT);
		selector.setLayoutParams(new ViewGroup.LayoutParams(1, 1));
		selector.setVisibility(View.GONE);
		selector.setZ(999999);
		selector.setTranslationZ(99999);
		addView(selector);
		picker = new PointPicker(getContext(),this){
			public void onDone(float x,float y){
				removeView(picker.getPointPicker());
				onPickListener.onPick(x,y);
			}
		};
		currentEditor = this;
	}

	public void setIndexer(MyIndexer indexer){
		myIndexer = indexer;
	}

	public void setToCurrentEditor(){
		currentEditor = this;
	}
	
	public MyIndexer getIndexer(){
		return myIndexer;
	}
	
	public static Editor getCurrentEditor(){
		return currentEditor;
	}
	//to detect when new item added
	@Override
	public void addView(View v) {
		super.addView(v);
		PropertySet<String,Object> ps = PropertySet.getPropertySet(v);
		if(ps==null||(!ps.containsKey("name"))) return;
		selector.setVisibility(View.VISIBLE);
		if (selectedView == null)
			selectView(v);
	}
	
	public void disableTouchExcept(View v){
		int index = indexOfChild(v);
		for(int x=0;x<getChildCount();x++){
			if(x==index) continue;
			getChildAt(x).setEnabled(false);
		}
	}
	
	public void enableTouch(){
		for(int x=0;x<getChildCount();x++) 
			getChildAt(x).setEnabled(true);
	}
	
	public void setProperties(LinearLayout linearLayout){
		propertiesLinear = linearLayout;
	}
	
	public enum ORIENATION {
		LANDSCAPE,PORTRAIT
	}
	
	public String readEvent(String event){
		return FileUtil.readFile(getEventPath(event)+".java")/*+FileUtil.readFile(getEventPath(event)+".code")*/;
	}
	
	public String readEvent(String event,String body){
		String result= FileUtil.readFile(getEventPath(event,body)+".java")+FileUtil.readFile(getEventPath(event,body)+".code");
		//Log.e("eeeee","empty "+getEventPath(event,body));
		return result;
	}
	
	public String getEventPath(String event){
		return project.getEventPath(scene,"",event);
	}
	
	public String getEventPath(String event,String body){
		return project.getEventPath(scene,body,event);
	}
	
	public ORIENATION getOrienation(){
		return getScreenView().getOrienation();
	}
	
	public void setOrienation(ORIENATION or){
		getScreenView().setOrienation(or);
		updateChilds();
		updateSelector();
	}
	
	//get current scene name / useless comment  :/
	public String getScene() {
		return scene;
	}

	public Editor setScene(String s) {
		scene = s;
		return this;
	}
	
	public void centerCamera(){
		ValueAnimator va = ValueAnimator.ofFloat(1,0);
		final float mx=moveX,my=moveY;
		va.addUpdateListener(v->{
			float a = (float)v.getAnimatedValue();
			moveX = mx*a;
			moveY = my*a;
			Editor.this.invalidate();
			updateChilds();
			updateSelector();
		});
		va.start();
	}
	
	String lastConfig="nothing...";
	private void updateConfig(){
		//ToDo : fix returning Null...
		editorConfig=null;
		String cfg=FileUtil.readFile(project.getConfig(scene));
		if(cfg.equals(lastConfig)&&editorConfig!=null) return;
		lastConfig = cfg;
		if(!cfg.equals("")){
			editorConfig = Utils.getProperty(cfg);
			setBackgroundColor(editorConfig.getColor("sceneColor"));
		}
		if(editorConfig==null) {
			lastConfig="nothing...";
			FileUtil.writeFile(project.getConfig(scene),Utils.readAssetFile("scene.json",getContext()));
			cfg=FileUtil.readFile(project.getConfig(scene));
			editorConfig = Utils.getProperty(cfg);
			setBackgroundColor(editorConfig.getColor("sceneColor"));
			if(FileUtil.readFile(project.getConfig(scene)).equals("")) {
			Utils.showMessage(getContext(),"Creating scene File Error!!\n"+project.getConfig(scene));
			}
			//updateConfig();
			return;
		}
		
		if(editorConfig!=null){
			if(!editorConfig.containsKey("logicHeight")){
				editorConfig.put("logicWidth",Utils.getScreenWidth((Activity)getContext()));
				editorConfig.put("logicHeight",getContext().getResources().getDisplayMetrics().heightPixels);
				saveConfig();
			}
		}
		//Utils.showMessage(getContext(),"config is not null.."+editorConfig.getString("or"));
	}
	
	public PropertySet<String,Object> getConfig(){
		updateConfig();
		return editorConfig;
	}
	
	int tries=0;
	public void setSceneColor(String s){
		while(editorConfig==null&&tries<99999){
		 updateConfig();
		 tries++;
		 }
		if(editorConfig!=null){
			String prev = editorConfig.containsKey("sceneColor")?editorConfig.getString("sceneColor"):"";
			try {
			editorConfig.put("sceneColor",s);
			setBackgroundColor(editorConfig.getColor("sceneColor"));
			FileUtil.writeFile(project.getConfig(scene),editorConfig.toString());
			} catch(Exception ex){
				Utils.showMessage(getContext(),"Error : "+ex.toString());
				editorConfig.put("sceneColor",prev);
			}
		} else Utils.showMessage(getContext(),"Null Config.!!");
	}

	public void saveConfig(){
		if(editorConfig!=null)
			FileUtil.writeFile(project.getConfig(scene),editorConfig.toString());
	}

	public void setProject(Project p) {
		project = p;
	}

	public void setScale(float sc) {
		scale = sc;
		invalidate();
		updateChilds();
		updateSelector();
	}

	public void selectView(View v) {
		if(picker.getPointPicker().getParent()!=null) return;
		if (Utils.isEditorItem(v)) {
			selectedView = v;
			updateProperties();
			updateSelector();
			if (editorListener != null)
				editorListener.onBodySelected();
		}
	}

	//update the selector ...
	public void updateSelector() {
		if (selectedView == null)
			selector.setVisibility(GONE);
		else if (selectedView.getParent() == null){
			selectedView = null;
			selector.setVisibility(GONE);
			} else {
			if(selector.getParent()==null) addView(selector);
			PropertySet<String,Object> propertySet = PropertySet.getPropertySet(selectedView);
			selectedView.setVisibility(VISIBLE);
			selector.setVisibility(VISIBLE);
			int height = selectedView.getMeasuredHeight();
			int width = selectedView.getMeasuredWidth();
			boolean bool = propertySet == null ? false : propertySet.containsKey("ColliderX");
			if (bool) {
				String HKey = propertySet.containsKey("Collider Height")?"Collider Height":"Collider Radius";
				String WKey = propertySet.containsKey("Collider Width")?"Collider Width":"Collider Radius";
				//Utils.setCornerRadiusWithStroke(selector, (selectedView instanceof CircleBody)?1080:0, 3, Color.RED, Color.TRANSPARENT);
				selectorDrawable.setCornerRadius((selectedView instanceof CircleBody)?1080:0);
				selector.setBackground(selectorDrawable);
				float xm = propertySet.getFloat("ColliderX") * ratioScale * scale;
				float ym = propertySet.getFloat("ColliderY") * ratioScale * scale;
				float ah = propertySet.getFloat(HKey) * scale * ratioScale * (HKey.contains("Radius")?2:1);
				float aw = propertySet.getFloat(WKey) * scale  * ratioScale * (HKey.contains("Radius")?2:1);
				float x = (width - aw) / 2f;
				float y = (height - ah) / 2f;
				selector.getLayoutParams().height = (int) ah;
				selector.getLayoutParams().width = (int) aw;
				selector.setX(x + xm + selectedView.getX());
				selector.setY(y + ym + selectedView.getY());
			} else {
				selector.getLayoutParams().height = height;
				selector.getLayoutParams().width = width;
				selector.setX(selectedView.getX());
				selector.setY(selectedView.getY());
			}
			selector.setLayoutParams(selector.getLayoutParams());
			selector.setRotation(selectedView.getRotation());
		}
	}

	public void setGridSize(float x, float y) {
		sizeX = x;
		sizeY = y;
		invalidate();
	}

	public boolean isAutoSave() {
		return AUTO_SAVE;
	}

	public void updateProperties() {
		if (propertiesLinear == null)
			return;
		for (int x = 0; x < propertiesLinear.getChildCount(); x++) {
			View v = propertiesLinear.getChildAt(x);
			if (v.getTag() == null)
				continue;
			if (v.getTag() instanceof Section) {
				((Section) (v.getTag())).update(PropertySet.getPropertySet(selectedView));
			}
		}
	}

	ScreenSize ScreenView;

	public ScreenSize getScreenView() {
		if (ScreenView != null)
			return ScreenView;
		ScreenView = new ScreenSize(this);
		return ScreenView;
	}

	public void setLogicalWH(float width,float height){
		logicalWidth = width;
		logicalHeight = height;
		getConfig().put("logicWidth",width);
		editorConfig.put("logicHeight",height);
		saveConfig();
	}

	public View getSelectedView() {
		return selectedView;
	}

	public void showGrids(boolean b) {
		SHOW_GRIDS = b;
		invalidate();
	}

	public void setRatioScale(float rs){
		ratioScale = rs;
	}

	public float getRatioScale(){
		return ratioScale;
	}

	public float getEditorScale() {
		return scale*ratioScale;
	}

	private float moveX = 0, moveY = 0;

	public float getMoveX() {
		return moveX;
	}

	public float getMoveY() {
		return moveY;
	}

	public ArrayList<String> getBodiesList() {
		ArrayList<String> bodies = new ArrayList<>();
		List<Pair<Float, String>> zValues = new ArrayList<>();
		
		for (int x = 0; x < getChildCount(); x++) {
			View v = getChildAt(x);
			if (PropertySet.getPropertySet(v) == null)
			continue;
			if (PropertySet.getPropertySet(v).containsKey("name")) {
				float z = v.getZ();
				String name = PropertySet.getPropertySet(v).getString("name");
				zValues.add(new Pair<>(z, name));
			}
		}
		
		// Sort the zValues list based on the z values
		Collections.sort(zValues, new Comparator<Pair<Float, String>>() {
			@Override
			public int compare(Pair<Float, String> pair1, Pair<Float, String> pair2) {
				return Float.compare(pair1.first, pair2.first);
			}
		});
		
		// Add the names to the bodies list in the sorted order
		for (Pair<Float, String> pair : zValues) {
			bodies.add(pair.second);
		}
		
		return bodies;
	}

	public void updateChilds() {
		for (int x = 0; x < getChildCount(); x++)
			if (Utils.isEditorItem(getChildAt(x)))
				Utils.update(getChildAt(x));
	}
		
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if(ScreenView==null) ScreenView = new ScreenSize(this);
		if(getMeasuredWidth()>ScreenView.width) ScreenView.width=getMeasuredWidth();
		float bx = ((getMoveX() - (getScreenView().getMeasuredWidth() / 2.0f)) * scale
				+ (getScreenView().getMeasuredWidth() / 2f));
		float by = ((getMoveY() - (getScreenView().getMeasuredHeight() / 2.0f)) * scale
				+ (getScreenView().getMeasuredHeight() / 2f));
		if (selectedView == null && getChildCount() > 0)
			selectedView = getChildAt(0);

		if (SHOW_GRIDS) {
			path.reset();
			float xt = bx;
			while (xt < getMeasuredWidth()) {
				path.moveTo(xt, 0);
				path.lineTo(xt, getMeasuredHeight());
				xt += sizeX * scale;
			}
			if (bx > 0) {
				xt = bx;
				while (xt > 0) {
					path.moveTo(xt, 0);
					path.lineTo(xt, getMeasuredHeight());
					xt -= sizeX * scale;
				}
			}
			float yt = by;
			while (yt < this.getMeasuredHeight()) {
				path.moveTo(0, yt);
				path.lineTo(getMeasuredWidth(), yt);
				yt += sizeY * scale;
			}
			if (by > 0) {
				yt = by;
				while (yt > 0) {
					path.moveTo(0, yt);
					path.lineTo(getMeasuredWidth(), yt);
					yt -= sizeY * scale;
				}
			}

		}
		boxPath.reset();

		boxPath.moveTo(bx, by);
		float hg = getScreenView().getMeasuredHeight() * scale;
		float wd = getScreenView().getMeasuredWidth() * scale;
		boxPath.lineTo(bx + wd, by);
		boxPath.lineTo(bx + wd, by + hg);
		boxPath.lineTo(bx, by + hg);
		boxPath.lineTo(bx, by);
		if (SHOW_GRIDS)
			canvas.drawPath(path, gridPaint);
		canvas.drawPath(boxPath, paint);
	}

	//Useless
	public static float[] rotate(double angle, float dx, float dy) {
		double ep = 1e-10;
		double x = dx * Math.cos(angle) - dy * Math.sin(angle);
		double y = dx * Math.sin(angle) + dy * Math.cos(angle);
		//because of some reasons, i used this way..., if you want more details
		//try see the result without it like cos(90) , it's wrong result
		//search why that ?
		return new float[] { Math.abs(x) < ep ? 0 : (float) x, Math.abs(y) < ep ? 0 : (float) y };
	}

	private TOUCHMODE touch_mode = TOUCHMODE.GRID;

	public void setTouchMode(TOUCHMODE mode) {
		if (selectedView == null) {
			touch_mode = TOUCHMODE.GRID;
			return;
		}
		touch_mode = mode;
	}

	public enum TOUCHMODE {
		MOVE, SCALE, GRID, ROTATE
	}

	float startX = 0, startY = 0, DeltaScale = 1.3f, sx = 0, sy = 0, startAngle = 0;
	boolean two = false,lockedItem=false;
	PropertySet<String, Object> propertySet;

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();
		detector.onTouchEvent(event);

		if (event.getPointerCount() >= 2) {
			two = true;
			return true;
		}
		if (two) {
			startX = event.getRawX();
			startY = event.getRawY();
			two = false;
		}

		switch (action) {
		case MotionEvent.ACTION_DOWN:
			startX = event.getRawX();
			startY = event.getRawY();
			sx = startX;
			sy = startY;
			propertySet = PropertySet.getPropertySet(selectedView); 
			lockedItem = propertySet.getString("lock").equals("true")||(picker.getPointPicker().getParent()!=null);
			if(selectedView!=null){
			float centerX = selectedView.getX() + selectedView.getMeasuredWidth() / 2.0f,
					centerY = selectedView.getY() + selectedView.getMeasuredHeight() / 2.0f;
			double angle = Math.atan2(event.getY() - centerY, event.getX() - centerX);
			startAngle = (float) Math.toDegrees(angle) - 90;
			}
			//propertySet.setOnChangeListener(onChangeListener);
			if (selectedView == null)
				touch_mode = TOUCHMODE.GRID;
			break;
		case MotionEvent.ACTION_MOVE:
			float dx = event.getRawX() - startX, dy = event.getRawY() - startY;
			if (touch_mode == TOUCHMODE.MOVE&&!lockedItem) {
				if (GRID_MOVEMENT) {
					if (!(sizeX * scale <= Math.abs(dx)))
						dx = 0;
					else
						startX = event.getRawX();
					if (!(sizeY * scale <= Math.abs(dy)))
						dy = 0;
					else
						startY = event.getRawY();
				}
				propertySet.put("x", propertySet.getFloat("x") + (dx * DeltaScale * (1 / scale)));
				propertySet.put("y", propertySet.getFloat("y") + (dy * DeltaScale * (1 / scale)));
				propertySet.updateMatrixToCurrent();
			} else if (touch_mode == TOUCHMODE.SCALE&&!lockedItem) {
				if (propertySet.containsKey("width")) {
					//float[] rotation = rotate(Math.toRadians(selectedView.getRotation()),dx,dy);
					//dx = rotation[0];
					//dy = rotation[1];
					float x = propertySet.getFloat("width") + (dx * DeltaScale * (1 / scale));
					float y = propertySet.getFloat("height") + (dy * DeltaScale * (1 / scale));

					float cx = propertySet.getFloat("Collider Width") + (dx * DeltaScale * (1 / scale));
					float cy = propertySet.getFloat("Collider Height") + (dy * DeltaScale * (1 / scale));
					propertySet.put("width", Math.max(x, 1f));
					propertySet.put("height", Math.max(y, 1f));
					if(propertySet.containsKey("Collider Width")) propertySet.put("Collider Width", Math.max(1f,cx));
					if(propertySet.containsKey("Collider Height")) propertySet.put("Collider Height", Math.max(1f,cy));
				} else if(propertySet.containsKey("radius")){
					float rad = propertySet.getFloat("radius") + (dx * DeltaScale * (1 / scale));
					float cRad = propertySet.getFloat("Collider Radius") + (dx * DeltaScale * (1 / scale));
					propertySet.put("radius", Math.max(rad, 1f));
					propertySet.put("Collider Radius", Math.max(cRad,1f));
				}
			} else if (touch_mode == TOUCHMODE.ROTATE&&!lockedItem) {
				float centerX = selectedView.getX() + selectedView.getMeasuredWidth() / 2.0f,
						centerY = selectedView.getY() + selectedView.getMeasuredHeight() / 2.0f;
				double angle = Math.atan2(event.getY() - centerY, event.getX() - centerX);
				angle = Math.toDegrees(angle) - 90;
				float currentAngle = propertySet.containsKey("rotation") ? propertySet.getFloat("rotation") : 0;
				double deltaRotation = angle - startAngle; 
				startAngle = (float)angle;
				angle = currentAngle + deltaRotation;
				while (angle > 360)
					angle -= 360;
				while (angle < 0)
					angle += 360;
				propertySet.put("rotation", angle);
				propertySet.updateMatrixToCurrent();
			} else if (touch_mode == TOUCHMODE.GRID||lockedItem) {
				moveX += dx * DeltaScale * (1 / scale);
				moveY += dy * DeltaScale * (1 / scale);
			}
			break;
		case MotionEvent.ACTION_UP:
			if (AUTO_SAVE && project != null)
				project.save(this);
			float x = sy - event.getRawX(), y = sy - event.getRawY();
			float dist = (float) Math.sqrt(x * x + y * y);
			if (dist <= 500) {
				//click(event.getX(),event.getY());
			}
			break;
		}
		if (!((touch_mode == TOUCHMODE.MOVE) && GRID_MOVEMENT)) {
			startX = event.getRawX();
			startY = event.getRawY();
		}
		invalidate();
		updateChilds();
		updateSelector();
		updateProperties();
		return true;
	}

	//public HashMap<String,String> Names = new HashMap<>();
	public String getName(View s) {
		ArrayList<String> array = getBodiesList();
		String name = "Item";
		if (s instanceof EditorItem)
			name = ((EditorItem)s).getTypeName();
		
		int x = 1;
		while (array.contains(name + x))
			x++;
		return name + x;
	}

	//it should be "previous" but lazy to edit it :|
	String currentState = "";

	public String getSaveState() {
		ArrayList<PropertySet<String, Object>> items = new ArrayList<>();
		for (int x = 0; x < getChildCount(); x++) {
			if (!Utils.isEditorItem(getChildAt(x)))
				continue;
			items.add(PropertySet.getPropertySet(getChildAt(x)));
		}
		String save = new Gson().toJson(items);
		if (getChildCount() == 0)
			save = "";
		//String last = (undoList.size()>0)?undoList.elementAt(undoList.size()-1):"";
		if (!save.equals(currentState)) {
			undoList.push(currentState);
			currentState = save;
			redoList.clear();
			//Log.e("error_of_star2d","prev :\n"+(undoList.elementAt(undoList.size()-1)+"\n prev : \n"+save));
		}
		if (editorListener != null)
			editorListener.onUpdateUndoRedo();
		return save;
	}

	public Project getProject() {
		return project;
	}

	Stack<String> undoList = new Stack<>(), redoList = new Stack<>();

	public boolean canUndo() {
		return (undoList.size() > 0);
	}

	public boolean canRedo() {
		return (redoList.size() > 0);
	}

	public void undo() {
		if (canUndo()) {
			String currentItem = selectedView!=null?PropertySet.getPropertySet(getSelectedView()).getString("name"):"";
			String state = currentState;// getSaveState();
			String element = undoList.pop();
			load(element);
			redoList.push(state);
			if(currentItem.equals("")) return;
			for(int x=0;x<getChildCount();x++){
				if(Utils.isEditorItem(getChildAt(x)))
					if(PropertySet.getPropertySet(getChildAt(x)).getString("name").equals(currentItem)){
						selectView(getChildAt(x));
						return;
					}
			}
		}
		if (editorListener != null)
			editorListener.onUpdateUndoRedo();
	}

	public void redo() {
		if (canRedo()) {
			String currentItem = selectedView!=null?PropertySet.getPropertySet(getSelectedView()).getString("name"):"";
			String state = currentState;
			String element = redoList.pop();
			load(element);
			undoList.push(state);
			if(currentItem.equals("")) return;
			for(int x=0;x<getChildCount();x++){
				if(Utils.isEditorItem(getChildAt(x)))
					if(PropertySet.getPropertySet(getChildAt(x)).getString("name").equals(currentItem)){
						selectView(getChildAt(x));
						return;
					}
			}
		}
		if (editorListener != null)
			editorListener.onUpdateUndoRedo();
	}

	public void load(String s) {
		try {
			removeAllViewsInLayout();
			currentState = s;
			if (s.equals(""))
				return;
			selectedView = null;
			HashMap<String,PropertySet> propsMap= new HashMap<>();
			ArrayList<PropertySet<String, Object>> propertySets = new Gson().fromJson(s,
					new TypeToken<ArrayList<PropertySet<String, Object>>>() {
					}.getType());
			for (PropertySet<String, Object> propertySet : propertySets) {
				EditorItem item = null;
				switch(propertySet.getString("TYPE")){
					case "BOX":
					item = new BoxBody(getContext());
					break;
					case "CIRCLE":
					item = new CircleBody(getContext());
					break;
					case "TEXT":
					item = new TextItem(getContext());
					break;
					case "PROGRESS":
					item = new ProgressItem(getContext());
					break;
					case "JOYSTICK":
					item = new JoyStickItem(getContext());
					break;
				}
				
					if (item == null)
					continue;
				propsMap.put(propertySet.getString("name"),propertySet);
				addView((View) item);
				item.setProperties(propertySet);
				if (selectedView == null)
					selectView((View) item);
			}
			updateSelector();
			updateProperties();
			for(int x=0;x<getChildCount();x++){
				if(Utils.isEditorItem(getChildAt(x))){
					PropertySet set1= PropertySet.getPropertySet(getChildAt(x));
					if(!set1.getString("parent").equals("")){
						if(propsMap.containsKey(set1.getString("parent"))){
							set1.setParent(propsMap.get(set1.getString("parent")));
						} else set1.put("parent","");
					}
				}
			}
			
			for(int x=0;x<getChildCount();x++){
				if(Utils.isEditorItem(getChildAt(x))){
					EditorItem item=(EditorItem)getChildAt(x);
					item.getPropertySet().updateMatrixToCurrent(false);
					//item.getPropertySet().put("tileX",item.getPropertySet().getFloat("tileX"));
					//item.getPropertySet().put("tileY",item.getPropertySet().getFloat("tileY"));
					//item.setProperties(item.getPropertySet());
				}
			}
			propsMap.clear();
			if (editorListener != null) {
				editorListener.onUpdateUndoRedo();
				if (selectedView != null)
					editorListener.onBodySelected();
			}
			
		} catch(Exception ex){
			Utils.showMessage(getContext(),"Loading Error : "+Log.getStackTraceString(ex));
		}
	}

	public void loadFromPath() {
		undoList.clear();
		redoList.clear();
		String string = FileUtil.readFile(project.getScenesPath() + scene);
		updateConfig();
		if(ScreenView!=null) ScreenView.update();
			else ScreenView = new ScreenSize(this);
		
		if(editorConfig!=null){
		if(editorConfig.containsKey("sceneColor")) setBackgroundColor(editorConfig.getColor("sceneColor"));
			String or=(editorConfig.containsKey("or"))?editorConfig.getString("or"):"";
			//((Activity)getContext()).setRequestedOrientation(or.contains("land")?ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE:ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			//Utils.showMessage(getContext(),"orienation : "+or);
			getScreenView().setOrienation((!or.equals(""))?ORIENATION.LANDSCAPE:ORIENATION.PORTRAIT);
		}
		load(string);
	}

	public void setEditorListener(EditorListener listener) {
		editorListener = listener;
	}

	class ScreenSize {
		public float height, width;
		Editor editor;
		ORIENATION orienation=ORIENATION.PORTRAIT;

		public ScreenSize(Editor ed) {
			editor = ed;
			update();
		}
		
		public void update(){
			PropertySet<String,Object> ps=editor.getConfig();
			height = editor.getResources().getDisplayMetrics().heightPixels;
			width = editor.getResources().getDisplayMetrics().widthPixels;
			/*
			if(editor.getMeasuredWidth()>width) width=editor.getMeasuredWidth();
			float rh = height/ps.getInt("logicHeight"),
			rw = width/ps.getInt("logicWidth");
			float scale = (rw<rh)?rw:rh;
			editor.setRatioScale(scale);
			height = height*scale;
			width = width*scale;
			*/
		}

		public float getMeasuredHeight() {
			return (orienation==ORIENATION.PORTRAIT)?width:height;
		}

		public float getMeasuredWidth() {
			return (orienation==ORIENATION.PORTRAIT)?height:width;
		}
		
		public void setOrienation(ORIENATION or){
			orienation=or;
		}
		
		public ORIENATION getOrienation(){
			return orienation;
		}
	}

	EditorListener editorListener;
	OnPickListener onPickListener;
	
	public void setOnPick(OnPickListener pick){
		if(pick!=null&&picker.getPointPicker().getParent()==null) addView(picker.getPointPicker());
			else return;
		onPickListener = pick;
		picker.getPointPicker().setX(getMeasuredWidth()/2f);
		picker.getPointPicker().setY(getMeasuredHeight()/2f);
	}

	public interface EditorListener {
		public void onUpdateUndoRedo();
		public void onBodySelected();
	}
	
	public interface OnPickListener {
		public void onPick(float x,float y);
	}
}