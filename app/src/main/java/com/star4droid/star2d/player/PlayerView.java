package com.star4droid.star2d.player;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.ContactFilter;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.World;
import com.google.gson.reflect.TypeToken;
import com.star4droid.star2d.Helpers.FileUtil;
import com.star4droid.star2d.Helpers.PropertySet;
import com.google.gson.Gson;
import com.star4droid.star2d.LandscapePlayer;
import com.star4droid.star2d.PortraitPlayer;
import com.star4droid.star2d.Utils;
import java.util.ArrayList;
import com.star4droid.star2d.Helpers.Project;
import com.star4droid.star2d.evo.R;
import java.util.HashMap;

public class PlayerView extends FrameLayout {
	public Project project;
	public World world;
	// scale : scale of the view
	// worldScale : scale of box2d world
	float scale=1,worldX=0,worldY=0,worldScale=0.1f,steps=3,
	lineX=0,camera_offsetX=0,camera_offsetY=0,ratioScale=1;
	long time=0;
	int FPS=60;
	boolean isPlaying=true;
	PlayerItem followX=null,followY=null;
	long lastFrameTime = 0,delta=0;
	HashMap<String,Object> collisionMap= new HashMap<>();
	ArrayList<HashMap<String,Object>> sounds= new ArrayList<>();
	PropertySet<String,Object> propertySet;
	TextView FPSText;
	HashMap<String,AnimationDrawable> spritesheets= new HashMap<>();
	SharedPreferences sharedPreferences;
	Paint paint = new Paint(){
		{
			setColor(Color.GREEN);
			setStrokeWidth(2);
			setStyle(Paint.Style.STROKE);
			setStrokeCap(Paint.Cap.ROUND);
			setStrokeJoin(Paint.Join.ROUND);
			setDither(true);
			setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
		}
	};
	Path path= new Path();
	
	public PlayerView(Context context,String path,String scene){
		super(context);
		sharedPreferences = context.getSharedPreferences("sh",Activity.MODE_PRIVATE);
		this.project = new Project(path);
		this.world = new World(new Vector2(0,9.8f),true);
		this.world.setContactFilter(new ContactFilter(){
			@Override
			public boolean shouldCollide(Fixture fixture1, Fixture fixture2) {
				try {
					PlayerItem body1 = (PlayerItem)fixture1.getUserData();
					PlayerItem body2 = (PlayerItem)fixture2.getUserData();
					boolean b1= body1.getProperties().getString("Collision").contains(body2.getParentName());
					boolean b2= body2.getProperties().getString("Collision").contains(body1.getParentName());
					return !(b1||b2);
				} catch(Exception ex){}
			    return false;
			}
			
		});
		this.world.setContactListener(new ContactListener(){
			@Override
			public void beginContact(Contact contact) {
				try {
					PlayerItem body1 = (PlayerItem)contact.getFixtureA().getUserData();
					PlayerItem body2 = (PlayerItem)contact.getFixtureB().getUserData();
					collisionMap.put(body1.getName()+","+body2.getName(),"true");
					collisionMap.put(body2.getName()+","+body1.getName(),"true");
					if(body1 instanceof BoxItem) ((BoxItem)(body1)).elementEvent.onCollisionBegin(body1,body2);
					if(body1 instanceof CircleItem) ((CircleItem)(body1)).elementEvent.onCollisionBegin(body1,body2);
					
					if(body2 instanceof BoxItem) ((BoxItem)(body2)).elementEvent.onCollisionBegin(body2,body1);
					if(body2 instanceof CircleItem) ((CircleItem)(body2)).elementEvent.onCollisionBegin(body2,body1);
					onCollisionBegin(body1,body2);
					
				} catch(Exception ex){
					//Utils.showMessage(getContext(),ex.toString());
					Log.e("star2dXXX",ex.toString());
				}
			}

			@Override
			public void endContact(Contact contact) {
				try {
					PlayerItem body1 = (PlayerItem)contact.getFixtureA().getUserData();
					PlayerItem body2 = (PlayerItem)contact.getFixtureB().getUserData();
					collisionMap.remove(body1.getName()+","+body2.getName());
					collisionMap.remove(body2.getName()+","+body1.getName());
					if(body1 instanceof BoxItem) ((BoxItem)(body1)).elementEvent.onCollisionEnd(body1,body2);
					if(body1 instanceof CircleItem) ((CircleItem)(body1)).elementEvent.onCollisionEnd(body1,body2);
					
					if(body2 instanceof BoxItem) ((BoxItem)(body2)).elementEvent.onCollisionEnd(body2,body1);
					if(body2 instanceof CircleItem) ((CircleItem)(body2)).elementEvent.onCollisionEnd(body2,body1);
					onCollisionEnd(body1,body2);
				} catch(Exception ex){
					Log.e("star2dXXX",ex.toString());
				}
			}

			@Override
			public void postSolve(Contact arg0, ContactImpulse arg1) {
			}

			@Override
			public void preSolve(Contact arg0, Manifold arg1) {
			}
		});
		loadScene(scene);
	}
	
	public PlayerView(Context context){
		super(context);
		setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.MATCH_PARENT));
	}
	
	public PlayerView(Context context,AttributeSet attributeSet){
		super(context,attributeSet);
	}
	
	public PlayerView(Context context,AttributeSet attributeSet,int i){
		super(context,attributeSet,i);
	}
	
	public static PlayerView getFromDex(String path,String scene,Context context){
		try {
			Project project = new Project(path);
			String optimizedDir = context.getDir("odex", Context.MODE_PRIVATE).getAbsolutePath();
			
			dalvik.system.DexClassLoader dcl = new dalvik.system.DexClassLoader(project.getDex(scene), optimizedDir, null, context.getClass().getClassLoader() );
			Class<?> playerClass = dcl.loadClass("com.star4droid.Game."+scene.toLowerCase());
			java.lang.reflect.Constructor<?> constructor = playerClass.getConstructor(Context.class,String.class,String.class);
			return ((PlayerView)(constructor.newInstance(context,path,scene)));
		} catch(Throwable e){
			Utils.showMessage(context,"Error : \n"+Log.getStackTraceString(e));
		}
		return null;
	}
	
	public void onPause(){
		for(HashMap<String,Object> hash:sounds){
			try {
				getMedia(hash).pause();
			} catch(Exception ex){}
		}
	}
	
	public void onResume(){
		for(HashMap<String,Object> hash:sounds){
			try {
				if(hash.get("playing").toString().equals("true"))
					getMedia(hash).start();
			} catch(Exception ex){}
		}
	}
	
	public void onCollisionBegin(PlayerItem body1,PlayerItem body2){
		
	}
	
	public void onCollisionEnd(PlayerItem body1,PlayerItem body2){
		
	}
	
	public boolean checkCollision(PlayerItem p1,PlayerItem p2){
		return (collisionMap.containsKey(p1.getName()+","+p2.getName())&&collisionMap.get(p1.getName()+","+p2.getName()).equals("true"));
	}
	
	public void setSpriteSheets(HashMap<String,AnimationDrawable> animations){
		spritesheets = animations;
	}
	
	public boolean isPlaying(){
		return isPlaying;
	}
	
	public float getDelta(){
		return (float)delta;
	}
	
	public void setAnimation(PlayerItem item,String animation){
		if(spritesheets.containsKey(animation)){
			if(item instanceof ImageView) {
				AnimationDrawable originalAnimation = spritesheets.get(animation);
				AnimationDrawable clonedAnimation = (android.graphics.drawable.AnimationDrawable)(originalAnimation.getConstantState().newDrawable().mutate());
				((ImageView)item).setImageDrawable(clonedAnimation);
				clonedAnimation.start();
			}
		} else {
			Utils.showMessage(getContext(),"Animation Not found : "+animation);
		}
	}
	
	public void createSound(String sound,final String id){
		for(HashMap<String,Object> hash:sounds){
			if(hash.get("id").toString().equals(id)){
				if(getMedia(hash).isPlaying()){
					getMedia(hash).pause();
				}
				releaseSound(getMedia(hash));
				hash.put(sound,MediaPlayer.create(getContext(),Uri.fromFile(new java.io.File(sound))));
				return;
			}
		}
		final HashMap<String,Object> hash = new HashMap<>();
		hash.put("id",id);
		hash.put("name",id);
		hash.put(sound,MediaPlayer.create(getContext(),Uri.fromFile(new java.io.File(sound))));
		hash.put("playing","false");
		getMedia(hash).setOnCompletionListener(new MediaPlayer.OnCompletionListener(){
			@Override
			public void onCompletion(MediaPlayer arg0) {
				hash.put("playing","false");
			}
		});
		sounds.add(hash);
	}
	
	public void releaseSound(String sound){
		for(HashMap<String,Object> hash:sounds)
			if(hash.get("name").toString().equals(sound)){
				releaseSound(getMedia(hash));
				hash.put("playing","false");
			}
	}
	
	public void pauseSound(String sound){
		for(HashMap<String,Object> hash:sounds)
		if(hash.get("name").toString().equals(sound)){
			getMedia(hash).pause();
			hash.put("playing","false");
		}
	}
	
	public float getViewGridX(){
		return 50;
	}
	
	public float getViewGridY(){
		return 50;
	}
	
	public float getZooming(){
		return scale/ratioScale;
	}
	
	public void startSound(String sound){
		for(HashMap<String,Object> hash:sounds)
		if(hash.get("name").toString().equals(sound)){
			getMedia(hash).start();
			hash.put("playing","true");
		}
	}
	
	public void loopSound(String sound,boolean b){
		for(HashMap<String,Object> hash:sounds)
		if(hash.get("name").toString().equals(sound))
		getMedia(hash).setLooping(b);
	}
	
	public void releaseSound(MediaPlayer player){
		try {
			player.release();
		} catch(Exception ex){}
	}
	
	public MediaPlayer getMedia(HashMap<String,Object> hash){
		return (MediaPlayer)(hash.get("media"));
	}
	
	public PlayerView load(String path,String scene){
		removeAllViewsInLayout();
		setWillNotDraw(false);
		project = new Project(path);
		propertySet = PropertySet.getFrom(FileUtil.readFile(project.getConfig(scene)));
		if(propertySet!=null) setBackgroundColor(propertySet.getColor("sceneColor"));
		world = new World(new Vector2(0,9.8f),true);
		String gs = FileUtil.readFile(project.getScenesPath()+scene);
		if(!gs.equals("")){
			ArrayList<PropertySet<String, Object>> propertySets = new Gson().fromJson(gs,
		new TypeToken<ArrayList<PropertySet<String, Object>>>() {
		}.getType());
			for(PropertySet<String,Object> propertySet:propertySets){
				switch(propertySet.getString("TYPE")) {
					case "BOX":
						addView(new BoxItem(getContext(),propertySet,this,null));
						break;
					case "CIRCLE":
						addView(new CircleItem(getContext(),propertySet,this,null));
						break;
					case "TEXT":
						addView(new TextItem(getContext(),propertySet,this,null));
						break;
					case "PROGRESS":
						addView(new ProgressItem(getContext(),propertySet,this,null));
						break;
					case "JOYSTICK":
						addView(new JoyStickItem(getContext(),propertySet,this,null));
				}
				
			}
		}
		invalidate();
		return this;
	}
	
	public void loadScene(String scene){
		try {
			propertySet = PropertySet.getFrom(FileUtil.readFile(project.getConfig(scene)));
			if(propertySet!=null) setBackgroundColor(propertySet.getColor("sceneColor"));
			float height = getResources().getDisplayMetrics().heightPixels,
			width = getResources().getDisplayMetrics().widthPixels;
			height = height/propertySet.getInt("logicHeight");
			width = width/propertySet.getInt("logicWidth");
			ratioScale = width<height?width:height;
			scale = ratioScale;
		} catch(Exception ex){}
	}
	
	public void Pause(){
		isPlaying=false;
	}
	
	public void Resume(){
		isPlaying=true;
	}
	
	public void setFPSText(TextView textView){
		FPSText = textView;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		long currentTime = System.nanoTime();
		if(time==0||Math.abs(time-System.currentTimeMillis())>=1000){
			time=System.currentTimeMillis();
			if(FPSText!=null){
				FPSText.setText("FPS : "+FPS);
			}
			FPS=0;
		}
		FPS++;
		try {
		if(isPlaying) for(int x=0;x<steps;x++)
						world.step(0.016666668f,8,3);
		} catch(Exception e){}
		if(followX!=null) setCameraX(followX,true);
		if(followY!=null) setCameraY(followY,true);
		for(int x=0;x<getChildCount();x++)
			if(getChildAt(x) instanceof PlayerItem)
				((PlayerItem)getChildAt(x)).update();
		path.reset();
		path.moveTo(lineX,2);
		path.lineTo(lineX+50,2);
		if(lineX>=getMeasuredWidth()) lineX=0;
			else lineX++;
		delta = currentTime - lastFrameTime;
		lastFrameTime = currentTime;
		invalidate();
	}
	
	public void openUrl(String url){
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse(url));
		getContext().startActivity(intent);
	}
	
	public void openScene(String scene){
		Intent intent = new Intent(Intent.ACTION_VIEW);
		String cfg=FileUtil.readFile(project.getConfig(scene));
		intent.putExtra("scene",scene);
		intent.putExtra("path",project.getPath());
		String or=(cfg.equals("")?"":Utils.getProperty(cfg).getString("or"));
		intent.setClass(getContext(),or.equals("")?PortraitPlayer.class:LandscapePlayer.class);
		getContext().startActivity(intent);
	}
	
	public void finish(){
		((Activity)getContext()).finish();
	}
	
	public void saveValue(String key,Object value){
		sharedPreferences.edit().putString(key,value.toString()).commit();
	}
	
	public String getValue(String key){
		return sharedPreferences.getString(key,"");
	}
	
	public void setGravity(float x,float y){
		world.setGravity(new Vector2(x,y));
	}
	
	public void setImage(PlayerItem item,String image){
		Utils.setImageFromFile((ImageView)item.getView(),project.getImagesPath()+image);
	}
	
	public void toast(Object message){
		Toast.makeText(getContext(),message.toString(),Toast.LENGTH_SHORT).show();
	}
	
	public void debug(Object debug){
		//ToDo : debug the errors..
	}
	
	public void write(String path,String string){
		FileUtil.writeFile(path,string);
	}
	
	public String read(String path){
		return FileUtil.readFile(path);
	}
	
	public void setZoom(float zoom){
		scale = ratioScale*zoom;
	}
	
	public void setCameraX(PlayerItem item,boolean follow){
		if(followX!=null) followX = null;
		float x = item.getBody().getPosition().x * (1/worldScale);
		float width=item.getView().getMeasuredWidth()/2;
		float w = getMeasuredWidth() / 2;
		float h = getMeasuredHeight() / 2;
		worldX = camera_offsetX + ((-1 * (x - width)) - ((-1*w) + width));
		if(follow) followX = item;
	}
	
	public void setCameraX(float x){
		worldX = x;
	}
	
	public void setCameraY(float y){
		worldY = y;
	}
	
	public void setCameraOffset(float x,float y){
		camera_offsetX = x;
		camera_offsetY = y;
	}
	
	public void setSteps(float s){
		steps = Math.max(s,1);
	}
	
	public void setCameraY(PlayerItem item,boolean follow){
			if(followY!=null) followY=null;
			float y = item.getBody().getPosition().y * (1/worldScale);
			float height=item.getView().getMeasuredHeight()/2;
			float w = getMeasuredWidth() / 2;
			float h = getMeasuredHeight() / 2;
			worldY = camera_offsetY + ((-1 * (y - height)) - ((h*-1) + height));
			if(follow) followX=item;
	}
	
	public void setCameraXY(PlayerItem item,boolean follow){
		setCameraX(item,follow);
		setCameraY(item,follow);
		if(follow){
			followX = item;
			followY = item;
		}
	}
	
	public void cameraFollowX(PlayerItem item){
		followX = item;
		setCameraX(item,true);
	}
	
	public float getCameraX(){
		return worldX;
	}
	
	public float getCameraY(){
		return worldY;
	}
	
	public void cameraFollowY(PlayerItem item){
		followY = item;
		setCameraY(item,true);
	}
	
	public void setCameraXY(float x,float y){
		stopFollowing();
		worldX = x;
		worldY = y;
	}
	
	public void stopFollowing(){
		followX = null;
		followY = null;
	}
	
	public void setCameraCenterX(float x){
		stopFollowing();
		worldX = x-getMeasuredWidth();
	}
	
	public void setCameraCenterY(float y){
		stopFollowing();
		worldY = y-getMeasuredHeight();
	}
	
	public void setCameraCenter(float x,float y){
		setCameraCenterX(x);
		setCameraCenterY(y);
	}
	
	public static ArrayList<StarTimer> timers =new ArrayList<>();
    class StarTimer {
        long time=0,targetTime=0;
        boolean repeat=false,ended=false;
        Runnable runn=null;
        public StarTimer(Runnable run,long target, boolean r){
        runn = run;
        targetTime = target;
        repeat = r;
        timers.add(this);
        }
        
        private boolean remove(){
        try { timers.remove(this); return false; } catch(Exception ex){}
        return true;
        }
        
        public void cancel(){
        runn=null;
        ended=true;
        remove();
        }
        
        public boolean step(long tm){
            if(runn==null){
            return remove();
            }
            time+=tm;
            if((time>=targetTime)&&(!ended)){
            time=0;
            new android.os.Handler(android.os.Looper.getMainLooper()).post(runn);
            if(!repeat) {
            ended=true;
            return remove();
            }
            }
            return true;
        }
    }
}