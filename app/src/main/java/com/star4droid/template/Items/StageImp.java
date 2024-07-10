package com.star4droid.template.Items;

import box2dLight.Light;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Music;
//import com.star4droid.star2d.evo.star2dApp;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.ContactFilter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.star4droid.star2d.Helpers.Project;
import com.star4droid.template.LoadingStage;
import com.star4droid.template.Utils.*;
import java.util.ArrayList;
import java.util.HashMap;
import box2dLight.RayHandler;

public class StageImp extends ApplicationAdapter {
	World world = new World(new Vector2(0,-9.8f),true);
	boolean playing=true, debugBox2d = false;
	Stage UiStage,GameStage;
	int steps=6,backgroundColor=0xFFFFFF;
    Box2DDebugRenderer debugRenderer;
	Color backgroundColorGdx= Color.WHITE;
	Project project;
	PropertySet<String,Object> propertySet;
	ArrayList<StageImp> previousStages= new ArrayList<>();
	HashMap<String,Object> collisionMap= new HashMap<>();
	//Image background;
	StagePair stagePair;//useless...
	ProjectAssetLoader assetLoader;
	LoadingStage loadingStage;
	Preferences preferences;
	PlayerItem followX,followY;
	float[] cameraOffset = new float[]{0,0};
	FPSCalc fPSCalc=new FPSCalc();
	StageImp currentStage;
	private RayHandler rayHandler;
	Viewport viewport;
	boolean loadComplete=false,onCreateCalled=false;
	SpriteSheetLoader spriteSheetLoader;
	ArrayList<LightInfo> lights= new ArrayList<>();
	
	public StageImp(){
		viewport = new FitViewport(720,1560);
	}
	
	public StageImp(Viewport port){
		viewport = port;
	}
	
	public StageImp(ProjectAssetLoader loader){
		this(new FitViewport(720,1560),loader);
	}
	
	public StageImp(Viewport port,ProjectAssetLoader projectAssetLoader){
		super();
		assetLoader = projectAssetLoader;
		viewport = port;
	}
	
	public void init(Viewport viewport){
	    float height = propertySet==null?0:propertySet.getInt("logicHeight"),
			width = propertySet==null?0:propertySet.getInt("logicWidth");
		if(height==0) height=1560;
		if(width==0) width=720;
		float ratio = width/height;
		
		//viewport = new FitViewport(10,10/ratio);
		if(viewport==null) viewport = new FitViewport(width,height);
		    else viewport.setWorldSize(width,height);
		
		debugRenderer = new Box2DDebugRenderer();
		
		Gdx.input.setCatchKey(4,true);//back key
		UiStage = new Stage(viewport);
		GameStage = new Stage(viewport){
			@Override
			public boolean keyDown(int key){
				if(key == 4){
					(currentStage==null?StageImp.this:currentStage).finish();
					return true;
				}
				return false;
			}
		};
		
		loadingStage = new LoadingStage();
		stagePair = new StagePair(UiStage,GameStage);
		preferences = Gdx.app.getPreferences("Game-Prefs");
		if(project==null)
		    project = new Project(Gdx.files.getExternalStoragePath());
		initDone = true;
		if(spriteSheetLoader!=null&&assetLoader!=null&&!isMain()){
		    onCreate();
		    onCreateCalled = true;
		}
		
		rayHandler = new RayHandler(world);
		rayHandler.setAmbientLight(0.1f, 0.1f, 0.1f, 1f);
		setupLight();
		
	}
	
	public RayHandler getRayHandler(){
	    return rayHandler;
	}
	
	public void addLight(String name,Light light){
		lights.add(new LightInfo(name,light));
	}
	
	public Light findLight(String name){
		for(LightInfo lightInfo:lights)
			if(lightInfo.name.equals(name))
				return lightInfo.light;
		return null;
	}
	
	private void drawDebug(){
	    debugRenderer.render(world, GameStage.getCamera().combined);
	}
	
	public StageImp setSpriteSheetLoader(SpriteSheetLoader spriteSheetLoader){
		this.spriteSheetLoader = spriteSheetLoader;
		return this;
	}
	
	public SpriteSheetLoader getSpriteSheetLoader(){
		return spriteSheetLoader;
	}
	
	private boolean initDone=false;
	public boolean initComplete(){
	    return initDone;
	}
	
	public PlayerItem findItem(String name){
	    //String names="";
		try {
			for(Actor actor:GameStage.getActors()){
				if(actor==null) continue;
				String actorName=(actor.getName()==null)?"":actor.getName();
				//names+=", "+actorName;
				if(actorName.equals(name)) {
					if(actor instanceof PlayerItem)
						return (PlayerItem)actor;
				}
			}
			for(Actor actor:UiStage.getActors()){
				//if(actor!=null&&(actor instanceof PlayerItem)&&actor.getName()!=null&&actor.getName().equals(name))
					//return (PlayerItem)actor;
				if(actor==null) continue;
				String actorName=(actor.getName()==null)?"":actor.getName();
				//names+=", "+actorName;
				if(actorName.equals(name)) {
					if(actor instanceof PlayerItem)
						return (PlayerItem)actor;
				}
			}
		} catch(Exception ex){
		    //throw new RuntimeException(ex);
		}
		//throw new RuntimeException("All :\n"+names);
		return null;
	}
	
	@Override
	public final void create() {
		super.create();
		init(viewport);
		InputMultiplexer multiplexer = new InputMultiplexer();
		multiplexer.addProcessor(UiStage);
		multiplexer.addProcessor(GameStage);
		Gdx.input.setInputProcessor(multiplexer);
		
		if(assetLoader==null||spriteSheetLoader==null){
			assetLoader = new ProjectAssetLoader(project);
			assetLoader.setAssetsLoadListener(()->{
				spriteSheetLoader.start();
				//loadComplete = true;
				//onCreate();
			});
			spriteSheetLoader = new SpriteSheetLoader(assetLoader,project,(errorHappend,message)->{
				loadComplete = true;
			});
			assetLoader.finishLoading();
		} else {
			loadComplete = true;
			onCreateCalled = true;
			onCreate();
		}
		
	}
	
	public void onCreate(){
		
	}
	
	public void setupLight(){
		if(!(rayHandler==null||propertySet==null)){
			rayHandler.setBlur(propertySet.getString("Enable Blur").toString().equals("true"));
			rayHandler.setBlurNum(propertySet.getInt("Blur Number"));
			rayHandler.setCulling(propertySet.getString("Enable Culling").toString().equals("true"));
			RayHandler.setGammaCorrection(propertySet.getString("Gamma Correction").toString().equals("true"));
			rayHandler.setShadows(propertySet.getString("Enable Shadows").toString().equals("true"));
			RayHandler.useDiffuseLight(propertySet.getString("Use Diffuse Light").toString().equals("true"));
			try {
			    if(!propertySet.getString("Ambient Light").equals(""))
			        rayHandler.setAmbientLight(Color.valueOf(propertySet.getString("Ambient Light")));
			} catch(Exception ex){}
		}
	}
	
	@Override
	public final void resume() {
		super.resume();
		if(!loadComplete) return;
		if(currentStage==null)
			onResume();
		else currentStage.onResume();
	}

	@Override
	public final void pause() {
		super.pause();
		if(!loadComplete) return;
		if(currentStage==null)
			onPause();
				else currentStage.pause();
	}
	
	public void Pause(){
		playing = false;
	}
	
	public void Resume(){
		playing = true;
	}
    
    public boolean onCreateCalled(){
        return onCreateCalled;
    }
    
	@Override
	public final void render() {
		super.render();
		// PlayerItem item= findItem("Box1");
		// if(item!=null){
		    // GameStage.getCamera().position.x = item.getActorX();
		    // GameStage.getCamera().position.y = item.getActorY();
		// }
		fPSCalc.update();
		Color bg = (currentStage==null)?backgroundColorGdx:currentStage.getBackgroundColor();
		Gdx.gl.glClearColor(bg.r,bg.g,bg.b,bg.a);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		if(loadComplete&&(!onCreateCalled)){
			onCreate();
			onCreateCalled = true;
		}
		
		if(!loadComplete){
			loadingStage.setProgress(assetLoader.getProgress()*75);
			loadingStage.act();
			loadingStage.draw();
		} else if(currentStage==null){
			act();
			draw();
			onDraw();
		} else {
		    if(!currentStage.initComplete())
		        currentStage.init(null);
			currentStage.act();
			currentStage.draw();
			//if(!currentStage.draw())
			    //throw new RuntimeException("game not drawn for unknown reason...");
			currentStage.onDraw();
		}
	}
	
	public void onDraw(){
		
	}

	public StagePair getPair(){
		return stagePair;
	}
	
	public Color getBackgroundColor(){
		return backgroundColorGdx;
	}
	
	public StageImp setBackgroundColor(String hex){
		backgroundColorGdx = Color.valueOf(hex);
		backgroundColor = Color.argb8888(backgroundColorGdx.a,backgroundColorGdx.r,backgroundColorGdx.g,backgroundColorGdx.b);
		return this;
	}
	
	public StageImp setBackgroundColor(int color){
		//background.setColor(new Color(color));
		backgroundColor = color;
		backgroundColorGdx = new Color(color);
		return this;
	}
	
	public StageImp setProject(Project p){
		project = p;
		if(assetLoader==null&&p!=null) assetLoader = new ProjectAssetLoader(p);
			else if(p!=null&&assetLoader!=null) assetLoader.setProject(p);
		return this;
	}
	
	public float getWidth(){
		return UiStage.getWidth();
	}
	
	public float getHeight(){
		return UiStage.getHeight();
	}
	
	public World getWorld(){
		return world;
	}
	
	public StageImp setSteps(int x){
		steps = Math.max(x,1);
		return this;
	}
	
	public void cameraFollowX(PlayerItem playerItem){
		followX = playerItem;
	}
	
	public void cameraFollowY(PlayerItem playerItem){
		followY = playerItem;
	}
	
	public void openUrl(String url){
		Gdx.net.openURI(url);
	}
	
	public void finish(){
		if(finishFunc!=null){
			finishFunc.onFinish(this);
			return;
		}
		if(currentStage==null)
			Gdx.app.exit();
				else {
					if(previousStages.contains(this))
						previousStages.remove(this);
				}
	}
	
	public boolean setImage(PlayerItem playerItem,String image){
	    String imgPath = (project.getImagesPath()+image).replace("//","/");
		if(assetLoader.contains(imgPath)){
			playerItem.setImage(assetLoader.get(imgPath,Texture.class));
			return true;
		}
		return false;
	}
	
	public ProjectAssetLoader getAssets(){
		return assetLoader;
	}
	
	public StageImp setAssetsLoader(ProjectAssetLoader loader){
		assetLoader = loader;
		return this;
	}
	
	public void setGravity(float x,float y){
		world.setGravity(new Vector2(x,y));
	}
	
	public Stage getGameStage(){
		return GameStage;
	}
	
	public Stage getUiStage(){
		return UiStage;
	}
	
	public StageImp setPropertySet(PropertySet<String,Object> set){
		propertySet = set;
		if(propertySet!=null) {
			setBackgroundColor(propertySet.getString("sceneColor"));
			float height = propertySet.getInt("logicHeight"),
			width = propertySet.getInt("logicWidth");
			if(viewport!=null) viewport.setWorldSize(width,height);
		    //background.setSize(width,height);
		    setupLight();
		}
		return this;
	}
	
	public float toStageY(float worldY){
		if(UiStage==null) return worldY;
		return UiStage.getViewport().getWorldHeight()-worldY;
	}
	
	public Viewport getViewport(){
		return (GameStage==null)?null:GameStage.getViewport();
	}
	
	public Camera getCamera(){
		return GameStage.getCamera();
	}
	
	public boolean isPlaying(){
		return playing;
	}
	
	public boolean checkCollision(PlayerItem p1,PlayerItem p2){
		return (collisionMap.containsKey(p1.getName()+","+p2.getName())&&collisionMap.get(p1.getName()+","+p2.getName()).equals("true"));
	}
	
	public float getDelta(){
		return Gdx.graphics.getDeltaTime();
	}
	
	private void setupCollision(){
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
					
					if(body1.getScript()!=null) body1.getScript().collisionBegin(body2);
						else body1.getElementEvents().onCollisionBegin(body1,body2);
					
					if(body2.getScript()!=null) body2.getScript().collisionBegin(body1);
						else body1.getElementEvents().onCollisionBegin(body2,body1);
					
					onCollisionBegin(body1,body2);
					
				} catch(Exception ex){
					//Utils.showMessage(getContext(),ex.toString());
					//Utils.Log("star2dXXX",ex.toString());
				}
			}

			@Override
			public void endContact(Contact contact) {
				try {
					PlayerItem body1 = (PlayerItem)contact.getFixtureA().getUserData();
					PlayerItem body2 = (PlayerItem)contact.getFixtureB().getUserData();
					collisionMap.remove(body1.getName()+","+body2.getName());
					collisionMap.remove(body2.getName()+","+body1.getName());
					
					if(body1.getScript()!=null) body1.getScript().collisionBegin(body2);
						else body1.getElementEvents().onCollisionEnd(body1,body2);
					
					if(body2.getScript()!=null) body2.getScript().collisionBegin(body1);
						else body1.getElementEvents().onCollisionEnd(body2,body1);
					
					onCollisionEnd(body1,body2);
				} catch(Exception ex){
					//Utils.Log("star2dXXX",ex.toString());
				}
			}

			@Override
			public void postSolve(Contact arg0, ContactImpulse arg1) {
			}

			@Override
			public void preSolve(Contact arg0, Manifold arg1) {
			}
		});
	}
	
	public void onCollisionBegin(PlayerItem body1,PlayerItem body2){
		
	}
	
	public void onCollisionEnd(PlayerItem body1,PlayerItem body2){
		
	}
	
	public static StageImp getFromDex(String path,String scene,ProjectAssetLoader projectAssetLoader,SpriteSheetLoader spriteSheetLoader){
		try {
			Project project = new Project(path);
			PropertySet<String,Object> set = PropertySet.getFrom(Utils.readFile(project.getConfig(scene)));
			String optimizedDir = null;// star2dApp.getContext().getDir("odex", android.content.Context.MODE_PRIVATE).getAbsolutePath();
			
			dalvik.system.DexClassLoader dcl = new dalvik.system.DexClassLoader(project.getDex(), optimizedDir, null, StageImp.class.getClassLoader() );
            
			Class<?> playerClass = dcl.loadClass("com.star4droid.Game."+scene.toLowerCase());
			java.lang.reflect.Constructor<?> constructor = playerClass.getConstructor();
			return ((StageImp)constructor.newInstance()).setAssetsLoader(projectAssetLoader).setProject(project).setSpriteSheetLoader(spriteSheetLoader).setPropertySet(set);
		} catch(Throwable e){
		String err = "path : "+path+"\n scene : "
			+scene+"\n error : "+e.toString()+"\n"
			+"cause : "+e.getCause()+"\n"+
			Utils.getStackTraceString(e);
		//Utils.Log(err);
		//throw new RuntimeException(err);
		}
		return null;
	}
	
	private Music getMusic(String sound){
		return assetLoader.get((project.get("sounds")+sound).replace("//","/"),Music.class);
	}
	
	public void loopSound(String sound,boolean loop){
		getMusic(sound).setLooping(loop);
	}
	
	public void releaseSound(String sound){
		Music music = getMusic(sound);
		if(music.isPlaying())
			music.stop();
	}
	
	public void pauseSound(String sound){
		if(getMusic(sound).isPlaying())
			getMusic(sound).pause();
	}
	
	public void setAnimation(PlayerItem playerItem,String anim){
		
	}
	
	public void createSound(String sound,String id){}
	
	public void startSound(String sound){
		getMusic(sound).play();
	}
	
	public void openScene(String scene){
		if(openSceneFunc!=null){
			openSceneFunc.openScene(scene,this);
			return;
		}
		StageImp newStage = getFromDex(project.getPath(),scene,assetLoader,spriteSheetLoader);
		newStage.setOpenSceneFunc((sc,stageImp)->{
				if(currentStage==stageImp)
					previousStages.add(stageImp);
				currentStage = getFromDex(project.getPath(),sc,assetLoader,spriteSheetLoader).setOpenSceneFunc(newStage.openSceneFunc).setFinishFunc(newStage.finishFunc);
			}).setFinishFunc((st)->{
				if(previousStages.contains(st))
					previousStages.remove(st);
				if(previousStages.size()==0)
					Gdx.app.exit();
				else currentStage = previousStages.get(previousStages.size()-1);
				if(currentStage.isMain()) currentStage = null;
			});
		if(currentStage==null){
			previousStages.add(this);
		} else {
			previousStages.add(currentStage);
		}
		currentStage = newStage;
		
	}
	
	public void setZoom(float zoom){
		((OrthographicCamera)getCamera()).zoom = 1/zoom;
	}
	
	public float getZooming(){
		return 1/((OrthographicCamera)getCamera()).zoom;
	}
	
	public static String read(String file){
		return Gdx.files.absolute(file).readString();
	}
	
	public static void write(String file,String content){
		Gdx.files.absolute(file).writeString(content,false);
	}
	
	public void setCameraX(float x){
		getCamera().position.x = x;
	}
	
	public void setCameraXY(float x,float y){
		getCamera().position.x = x;
		getCamera().position.y = y;
	}
	
	public void setCameraXY(PlayerItem playerItem){
		getCamera().position.set(playerItem.getActorX()+playerItem.getActor().getWidth()*0.5f,playerItem.getActorY()+playerItem.getActor().getHeight()*0.5f,0);
	}
	
	public void setCameraY(float y){
		getCamera().position.y = y;
	}
	
	public void setCameraX(PlayerItem playerItem){
		getCamera().position.x = playerItem.getActorX()+playerItem.getActor().getWidth()*0.5f;
	}
	
	public void setCameraY(PlayerItem playerItem){
		getCamera().position.y = playerItem.getActorY()+playerItem.getActor().getHeight()*0.5f;
	}
	
	public boolean draw() {
		if(preferences==null) return false;
		if(followX!=null&&followY!=null){
			getCamera().position.set(followX.getActorX()+followX.getActor().getWidth()*0.5f+cameraOffset[0],followY.getActorY()+followY.getActor().getHeight()*0.5f+cameraOffset[1],0);
		} else if(followX!=null){
			getCamera().position.x = followX.getActorX()+followX.getActor().getWidth()*0.5f+cameraOffset[0];
		} else if(followY!=null){
			getCamera().position.y = followY.getActorY()+followY.getActor().getWidth()*0.5f+cameraOffset[1];
		}
		GameStage.draw();
		UiStage.draw();
		if(debugBox2d)
		    drawDebug();
		if(playing) for(int x=0;x<steps;x++)
						world.step(getDelta(),8,3);
		rayHandler.setCombinedMatrix(GameStage.getViewport().getCamera().combined,0,0,1,1);
		rayHandler.updateAndRender();
		return true;
	}
	
	public void setCameraCenter(float x,float y){
		getCamera().position.set(x,y,0);
	}
	
	public void setValue(String name,String value){
		preferences.putString(name,value);
	}
	
	public String getValue(String name){
		return preferences.getString(name,"");
	}
	
	public void setCameraCenterX(float x){
		getCamera().position.x = x;
	}
	
	public void setCameraCenterY(float y){
		getCamera().position.y = y;
	}
	
	public void setCameraOffset(float x,float y){
		cameraOffset[0] = x;
		cameraOffset[1] = y;
	}
	
	public float getCameraX(){
		return getCamera().position.x;
	}
	
	public float getCameraY(){
		return getCamera().position.y;
	}
	
	public void act() {
		if(preferences==null) return;
		UiStage.act();
		GameStage.act();
	}
	
	public void addActor(Actor actor) {
		if(actor instanceof PlayerItem&&((PlayerItem)actor).getProperties()!=null&&((PlayerItem)actor).getProperties().containsKey("type")&&((PlayerItem)actor).getProperties().getString("type").equals("UI"))
			UiStage.addActor(actor);
		else GameStage.addActor(actor);
	}
	
	public void dispose(){
		UiStage.dispose();
		GameStage.dispose();
		//assetLoader.dispose();
	}
	
    public void onPause(){
        
    }

    public void onResume(){
        
    }
	
	public boolean isMain(){
		return openSceneFunc==null;
	}
	
	FinishFunc finishFunc;
	OpenSceneFunc openSceneFunc;
	public StageImp setFinishFunc(FinishFunc func){
		finishFunc = func;
		return this;
	}
	
	public StageImp setOpenSceneFunc(OpenSceneFunc func){
		openSceneFunc = func;
		return this;
	}
	
	public interface FinishFunc {
		public void onFinish(StageImp stageImp);
	}
	
	public interface OpenSceneFunc {
		public void openScene(String scene,StageImp stageImp);
	}
	
	public class StagePair {
		public Stage UiStage,GameStage;
		public StagePair(Stage ui,Stage game){
			UiStage = ui;
			GameStage = game;
		}
	}
	
	public class LightInfo {
		Light light;
		String name;
		public LightInfo(String name,Light light){
			this.light = light;
			this.name = name;
		}
	}
}