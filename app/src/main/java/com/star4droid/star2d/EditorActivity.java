package com.star4droid.star2d;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.star4droid.star2d.Activities.FilesManagerActivity;
import com.star4droid.star2d.Adapters.AddPopup;
import com.star4droid.star2d.Adapters.ColourSelector;
import com.star4droid.star2d.Adapters.EditorField;
import com.star4droid.star2d.Adapters.MissingFileDialog;
import com.star4droid.star2d.Adapters.Properties;
import com.star4droid.star2d.Adapters.SPNote;
import com.star4droid.star2d.CodeEditor.MyIndexer;
import com.star4droid.star2d.Fragments.*;
import com.star4droid.star2d.Fragments.BodiesFragment;
import com.star4droid.star2d.Helpers.CodeGenerator;
import com.star4droid.star2d.Helpers.CompileThread;
import com.star4droid.star2d.Helpers.EngineSettings;
import com.star4droid.star2d.Helpers.FileUtil;
import com.star4droid.star2d.Helpers.JointsHelper;
import com.star4droid.star2d.Helpers.Project;
import com.star4droid.star2d.Helpers.PropertySet;
import com.star4droid.star2d.Helpers.SwipeHelper;
import com.star4droid.star2d.Items.*;
import com.star4droid.star2d.Items.Editor;

import com.star4droid.star2d.evo.R;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class EditorActivity extends AppCompatActivity {
	ImageView play,grid,move,rotate,scale,rotateScreen,openFiles,center_camera,
	addBody,deleteBody,save,deleteScene,undo,redo,sceneColor,lock,copyScene,renameScene;
	Spinner scenesSpinner,bodiesSpinner;
	Editor editor;
	LinearLayout sceneLinear,propsLinear,right_linear;
	Project project;
	Properties properties;
	ArrayList<String> scenesList= new ArrayList<>();
	ViewPager2 right_viewPager;
	public void onCreate(Bundle bundle){
		super.onCreate(bundle);
		EngineSettings.init(this);
		JointsHelper.init(this);
		Utils.setLanguage(this);
		setContentView(R.layout.editor);
		// Hide system UI
		Utils.hideSystemUi(getWindow());
		HashMap<String,Object> map;
		
		try {
			EditorField.spinnerMap = new Gson().fromJson(Utils.readAssetFile("spinners.json",this), new TypeToken<HashMap<String, Object>>(){}.getType());
		} catch(Exception ex){
			Utils.showMessage(this,"spinner map init error : "+ex.toString());
			return;
		}
		
		project = new Project(getIntent().getStringExtra("project"));
		init();
		editor.setProject(project);
		editor.setScene("scene1");
		editor.loadFromPath();
		indexFiles();
		refreshBodies();
		new Timer().schedule(new TimerTask(){
			@Override
			public void run() {
				new Handler(Looper.getMainLooper()).post(new Runnable(){
					@Override
					public void run() {
						editor.updateChilds();
					}
				});
			}
		},200);
		setupSwitchModeButton(move,Editor.TOUCHMODE.MOVE);
		setupSwitchModeButton(scale,Editor.TOUCHMODE.SCALE);
		setupSwitchModeButton(grid,Editor.TOUCHMODE.GRID);
		setupSwitchModeButton(rotate,Editor.TOUCHMODE.ROTATE);
		updateScenes();
		addBody.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View arg0) {
				AddPopup.showFor(EditorActivity.this,addBody,editor);
			}
		});
		
		findViewById(R.id.back).setOnClickListener(b->{
			finish();
		});
		
		deleteScene.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v){
				AlertDialog.Builder builder= new AlertDialog.Builder(EditorActivity.this);
				builder.setTitle("Delete");
				builder.setPositiveButton("Delete",new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						project.deleteScene(editor.getScene());
						editor.setScene("scene1");
						editor.loadFromPath();
						editor.updateChilds();
						updateScenes();
						refreshBodies();
					}
				});
				builder.setNegativeButton("Cancel",new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						
					}
				});
				builder.show();
			}
		});
		
		undo.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v){
				if(editor.canUndo()) editor.undo();
			}
		});
		
		redo.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View arg0) {
				if(editor.canRedo()) editor.redo();
			}
		});
		
		openFiles.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View arg0) {
				Intent i= new Intent();
				i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				i.setClass(EditorActivity.this,FilesManagerActivity.class);
				i.putExtra("path",project.getPath());
				startActivity(i);
			}
		});
		
		scenesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
				deleteScene.setVisibility(scenesList.get(position).toLowerCase().equals("scene1")?View.GONE:View.VISIBLE);
				renameScene.setVisibility(scenesList.get(position).toLowerCase().equals("scene1")?View.GONE:View.VISIBLE);
				if(!(editor.getScene().toUpperCase().equals(scenesList.get(position).toUpperCase()))) {
				if(scenesList.get(position).equals(ADD_SCENE)){
					editScene("add");
					for(int x=0;x<scenesList.size();x++){
						if(scenesList.get(x).toLowerCase().equals(editor.getScene().toLowerCase())){
							scenesSpinner.setSelection(x);
							break;
						}
					}
				} else {
				if(!scenesList.get(position).toLowerCase().equals(editor.getScene().toLowerCase())){
					editor.setScene(scenesList.get(position));
					editor.loadFromPath();
					CodeGenerator.generateFor(editor,code->{
						FileUtil.writeFile(project.getCodesPath(editor.getScene()),code);
					});
					refreshBodies();
				}
				}
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
		
		deleteBody.setOnClickListener(v->{
				project.deleteBody(PropertySet.getPropertySet(editor.getSelectedView()).get("name").toString(),editor.getScene());
				editor.removeView(editor.getSelectedView());
				editor.selectView(null);
				refreshBodies();
		});
		
		save.setOnClickListener(v->{
				save.setScaleX(0);
				save.setScaleY(0);
				save.animate().scaleX(1).scaleY(1).start();
				project.save(editor);
		});
		
		sceneLinear.setOnClickListener(v->{
				scenesSpinner.performClick();
		});
		
		sceneColor.setOnClickListener(v->{
				ColourSelector.show(editor,"sceneColor");
		});
		
		copyScene.setOnClickListener(v->{
			editScene("copy");
		});
		
		renameScene.setOnClickListener(v->{
			editScene("rename");
		});
		
		editor.setEditorListener(new Editor.EditorListener(){
			@Override
			public void onUpdateUndoRedo() {
				updateUndoRedo();
			}
			
			@Override
			public void onBodySelected() {
				refreshBodies();
				bodiesFragment.update();
				if(editor.getSelectedView()!=null){
					String isLock=PropertySet.getPropertySet(editor.getSelectedView()).getString("lock");
					lock.setImageDrawable(getDrawable(isLock.equals("true")?R.drawable.lock:R.drawable.unlock));
				}
			}
		});
		
		lock.setOnClickListener(view->{
			if(editor.getSelectedView()!=null){
				PropertySet<String,Object> propertySet = PropertySet.getPropertySet(editor.getSelectedView());
				String isLock = propertySet.getString("lock").equals("true")?"false":"true";
				lock.setImageDrawable(getDrawable(isLock.equals("true")?R.drawable.lock:R.drawable.unlock));
				propertySet.put("lock",isLock);
			}
		});
		
		center_camera.setOnClickListener(view->{
			editor.centerCamera();
		});
		
		rotateScreen.setOnClickListener(view->{
			PropertySet<String,Object> sceneCg=editor.getConfig();
			String changeTo= editor.getOrienation()==Editor.ORIENATION.LANDSCAPE?"":"landscape";
			sceneCg.put("or",changeTo);
			editor.setOrienation(changeTo.equals("")?Editor.ORIENATION.PORTRAIT:Editor.ORIENATION.LANDSCAPE);
			FileUtil.writeFile(project.getConfig(editor.getScene()),sceneCg.toString());
		});
		
		play.setOnClickListener(view-> {
				if(!FileUtil.isExistFile(FileUtil.getPackageDataDir(EditorActivity.this)+"/bin/cp.jar")){
					MissingFileDialog.showFor(EditorActivity.this,MissingFileDialog.CP);
					return;
				}
				final AlertDialog[] dialog= {Utils.showMessage(EditorActivity.this,"generating java...")};
				dialog[0].setCancelable(false);
				CodeGenerator.generateFor(editor,new CodeGenerator.GenerateListener(){
					@Override
					public void onGenerate(String code) {
						FileUtil.writeFile(project.getCodesPath(editor.getScene()),code);
						CompileThread compile = new CompileThread(EditorActivity.this,project.getPath()+"/java/",false);
						compile.setOnChangeStatus(new CompileThread.OnStatusChanged(){
							@Override
							public void onStatus(String s) {
								dialog[0].dismiss();
								dialog[0]=Utils.updateMessage(dialog[0],s,false);
							}

							@Override
							public void onEnd(String message) {
								//delete the generated code ?
								//FileUtil.deleteFile(project.getCodesPath(editor.getScene()));
							}

							@Override
							public void onError(String error) {
								dialog[0].dismiss();
								dialog[0]=Utils.updateMessage(dialog[0],error,true);
							}

							@Override
							public void onSuccess(String message) {
								dialog[0]=Utils.updateMessage(dialog[0],message,true);
								FileUtil.writeFile(project.getDex(editor.getScene()),"");
								FileUtil.moveFile(project.getPath()+"/java/classes.dex",project.getDex(editor.getScene()));
								Intent i= new Intent();
								if(editor.getOrienation()==Editor.ORIENATION.PORTRAIT) {
									i.setClass(EditorActivity.this,PortraitPlayer.class);
								} else i.setClass(EditorActivity.this,LandscapePlayer.class);
								i.putExtra("path",project.getPath());
								i.putExtra("scene",editor.getScene());
								startActivity(i);
								//dialog.dismiss();
							}
							
						});
						compile.start();
					}
				});
				
		});
		findViewById(R.id.right_swipe).setOnClickListener(view->{});
		bodiesFragment = new BodiesFragment(editor);
		SwipeHelper.useViewToSwipe(findViewById(R.id.right_swipe),right_linear,SwipeHelper.SwipeType.RIGHT_LEFT,1,Integer.MAX_VALUE);
		properties = new Properties(this);
		propsLinear.addView(properties.getView());
		properties.getViewPager().setAdapter(new FragmentAdapter(this,editor));
		right_viewPager.setAdapter(new RightFragmentAdapter(this,editor));
		//disable viewpager touch because we don't need it currently...
		right_viewPager.requestDisallowInterceptTouchEvent(true);
		SPNote.show(this);
	}
	
	public void updateUndoRedo(){
		undo.setImageTintList(ColorStateList.valueOf(editor.canUndo() ? getColor(R.color.sim_yellow):getColor(R.color.unselect_color)));
		redo.setImageTintList(ColorStateList.valueOf(editor.canRedo() ? getColor(R.color.sim_yellow):getColor(R.color.unselect_color)));
	}
	
	public void selectMode(View v){
		grid.setBackground(getDrawable(R.drawable.imgs_style));
		move.setBackground(getDrawable(R.drawable.imgs_style));
		rotate.setBackground(getDrawable(R.drawable.imgs_style));
		scale.setBackground(getDrawable(R.drawable.imgs_style));
		v.setBackground(getDrawable(R.drawable.select_style));
	}
	
	public void refreshBodies(){
		bodiesList = editor.getBodiesList();
		bodiesSpinner.setOnItemSelectedListener(null);
		bodiesSpinner.setAdapter(getSpinnerAdapter(bodiesList,this,bodiesSpinner));
		if(editor.getSelectedView()!=null){
			bodiesSpinner.setSelection(getCurrentBody());
		}
		bodiesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> _param1, View _param2, int position, long _param4) {
				boolean found=false;
				for(int x=0;x<editor.getChildCount();x++){
					if(!Utils.isEditorItem(editor.getChildAt(x))) continue;
					if(PropertySet.getPropertySet(editor.getChildAt(x)).getString("name").equals(bodiesList.get(position))){
						editor.selectView(editor.getChildAt(x));
						found = true;
						break;
					}
				}
				//if(!found) refreshBodies();//Toast.makeText(EditorActivity.this,"not found",Toast.LENGTH_SHORT).show();
				deleteBody.setVisibility(View.VISIBLE);
			}
			
			@Override
			public void onNothingSelected(AdapterView<?> _param1) {
				deleteBody.setVisibility(View.INVISIBLE);
				
			}
		});
		if(bodiesFragment!=null) bodiesFragment.update();
	}
	
	String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890_";
	
	public void editScene(String action){
		View dialog_cv = getLayoutInflater().inflate(R.layout.create_dialog,null);
		final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		final EditText name = dialog_cv.findViewById(R.id.name);
		((TextView)dialog_cv.findViewById(R.id.title)).setText(action.equals("add")?"Add Scene":(action.equals("copy")?"Copy Scene":"Rename Scene"));
		((TextView)dialog_cv.findViewById(R.id.add)).setText("Add");
		name.setHint("Scene Name...");
		if(!action.equals("add")) name.setText(editor.getScene());
		alertDialog.setView(dialog_cv);
		dialog_cv.findViewById(R.id.add).setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View arg0) {
				String path = project.getScenesPath()+name.getText().toString();
				if(name.getText().toString().equals("")) return;
				for(char c:name.getText().toString().toCharArray()){
					//only possible chars...to prevent writing strange chars :|
					if(!(chars.contains(""+c))) {
						Utils.showMessage(EditorActivity.this,"Not Allowed char : "+c);
						return;
					}
				}
				if(FileUtil.isExistFile(path)) {
					Utils.showMessage(EditorActivity.this,"There is scene with the same name..!!");
					return;
				}
				FileUtil.writeFile(path,"");
				if(action.equals("add")){
					
				} else if(action.equals("rename")){
					project.renameScene(editor.getScene(),name.getText().toString());
				} else if(action.equals("copy")){
					project.copyScene(editor.getScene(),name.getText().toString());
				}
				if(FileUtil.isExistFile(path)){
				editor.setScene(name.getText().toString());
				editor.loadFromPath();
				updateScenes();
				} else {
					Utils.showMessage(EditorActivity.this,"Failed to create the path..");
					return;
				}
				alertDialog.dismiss();
			}
		});
		alertDialog.show();
	}
	
	int getCurrentBody(){
		ArrayList<String> arr= editor.getBodiesList();
		for(int x=0;x<arr.size();x++){
			if(!PropertySet.getPropertySet(editor.getSelectedView()).containsKey("name")) continue;
			if(PropertySet.getPropertySet(editor.getSelectedView()).get("name").toString().equals(arr.get(x))){
				return x;
			}
		}
		return 0;
	}
	
	public String ADD_SCENE="Add Scene";
	public void updateScenes(){
		scenesList.clear();
		ArrayList<String> temp= new ArrayList<>();
		FileUtil.listDir(project.getScenesPath(),temp);
		for(String s:temp) scenesList.add(Uri.parse(s).getLastPathSegment());
		if(scenesList.size()==0) scenesList.add("scene1");
		scenesList.add(ADD_SCENE);
		String prev = editor.getScene();
		scenesSpinner.setAdapter(getSpinnerAdapter(scenesList,this,null));
		for(int x=0;x<scenesList.size();x++){
			if(prev.toLowerCase().equals(scenesList.get(x).toLowerCase())){
				scenesSpinner.setSelection(x);
				break;
			}
		}
	}
	
	ArrayList<String> bodiesList;
	public static ArrayAdapter getSpinnerAdapter(ArrayList<String> arrayList,Context context,final Spinner spinner){
		return new ArrayAdapter<String>(context,android.R.layout.simple_spinner_item,arrayList){
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				// Get the default view from the ArrayAdapter
				View view = super.getView(position, convertView, parent);
				
				// Cast the view to a TextView
				TextView textView = (TextView) view;
				textView.setPadding(8,12,8,12);
				// Set the text color
				textView.setTextColor(getContext().getColor(R.color.text_color));
				//textView.setBackgroundColor(getContext().getColor(R.color.button_background));
				/*
				if(Build.VERSION.SDK_INT<30)
				textView.setBackgroundDrawable(getContext().getDrawable(R.drawable.section_field));
				else textView.setBackground(getContext().getDrawable(R.drawable.section_field));
				*/
				textView.setTextSize(12);
				return view;
			}
			
			@Override
			public View getDropDownView(final int position, View convertView, ViewGroup parent) {
				// Get the default drop-down view from the ArrayAdapter
				View view = super.getDropDownView(position, convertView, parent);
				
				// Cast the view to a TextView
				TextView textView = (TextView) view;
				textView.setPadding(8,12,8,12);//left ,top, right, bottom
				
				// Set the text color
				textView.setTextColor(getContext().getColor(R.color.text_color));
				textView.setBackgroundColor(getContext().getColor(R.color.button_background));
				/*
				if(Build.VERSION.SDK_INT<30)
					textView.setBackgroundDrawable(getContext().getDrawable(R.drawable.section_field));
				else textView.setBackground(getContext().getDrawable(R.drawable.section_field));
				*/
				textView.setTextSize(12);
							
				return view;
			}
		};
	}
	
	public void init(){
		addBody = findViewById(R.id.addBody);
		play = findViewById(R.id.play);
		grid = findViewById(R.id.grid);
		move = findViewById(R.id.move);
		scale = findViewById(R.id.scale);
		rotate = findViewById(R.id.rotate);
		bodiesSpinner = findViewById(R.id.bodiesSpinner);
		editor = findViewById(R.id.editor);
		scenesSpinner = findViewById(R.id.scenesSpinner);
		deleteBody = findViewById(R.id.deleteBody);
		save = findViewById(R.id.save);
		deleteScene = findViewById(R.id.deleteScene);
		sceneLinear = findViewById(R.id.sceneLinear);
		propsLinear = findViewById(R.id.propsLinear);
		undo = findViewById(R.id.undo);
		redo = findViewById(R.id.redo);
		rotateScreen = findViewById(R.id.rotateScreen);
		sceneColor = findViewById(R.id.sceneColor);
		openFiles = findViewById(R.id.files_manager);
		lock = findViewById(R.id.lock);
		center_camera = findViewById(R.id.center_camera);
		renameScene = findViewById(R.id.renameScene);
		copyScene = findViewById(R.id.copyScene);
		right_viewPager = findViewById(R.id.right_vp);
		right_linear = findViewById(R.id.right_linear);
	}
	
	public void setupSwitchModeButton(View v,final Editor.TOUCHMODE touchmode){
		v.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View view) {
				if(editor.getChildCount()>0) {
				selectMode(view);
				editor.setTouchMode(touchmode);
				} else {
					selectMode(grid);
					editor.setTouchMode(Editor.TOUCHMODE.GRID);
				}
			}
		});
	}
	
	BodiesFragment bodiesFragment;
	private class RightFragmentAdapter extends FragmentStateAdapter {
		Editor editor;
		public RightFragmentAdapter(AppCompatActivity activity,Editor ed){
			super(activity);
			editor=ed;
		}
		@Override
		public Fragment createFragment(int position) {
			switch(position){
				case 0:
				return bodiesFragment;
				
			}
			return null;
		}
		
		@Override
		public int getItemCount() {
			return 1;
		}
	}
	
	private class FragmentAdapter extends FragmentStateAdapter {
		Editor editor;
		public FragmentAdapter(AppCompatActivity activity,Editor ed){
			super(activity);
			editor=ed;
		}
		@Override
		public Fragment createFragment(int position) {
			switch(position){
				case 0:
					return new SectionsListFragment(properties.getViewPager());
				case 1:
					return new PropertiesFragment(editor);
				case 2:
					return new EventsFragment(editor);
				case 3:
					return new JointsFragment(editor);
			}
			return null;
		}

		@Override
		public int getItemCount() {
			return 4;
		}

	}
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if(hasFocus)
		{
			Utils.hideSystemUi(getWindow().getDecorView());
		}
		else {
			try {
				Object wmgInstance = Class.forName("android.view.WindowManagerGlobal").getMethod("getInstance").invoke(null);
				Field viewsField = Class.forName("android.view.WindowManagerGlobal").getDeclaredField("mViews");
				
				viewsField.setAccessible(true);
				ArrayList<View> views = (ArrayList<View>) viewsField.get(wmgInstance);
				for (View view: views) {
					Utils.hideSystemUi(view);
					if(Build.VERSION.SDK_INT<30)
					view.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
						@Override
						public void onSystemUiVisibilityChange(int visibility) {
							Utils.hideSystemUi(view);
						}
					});
					else view.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener(){
						@Override
						public WindowInsets onApplyWindowInsets(View v, WindowInsets insets) {
						    Utils.hideSystemUi(v);
							return insets;
						}
						
					});
				}
				} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
	}
	
	public void indexFiles(){
		if(!EngineSettings.get().getBoolean("AutoComp",false)) return;
		findViewById(R.id.progress).setVisibility(View.VISIBLE);
		new Thread(){
			public void run(){
				Looper.prepare();
				editor.setIndexer(new MyIndexer().indexFiles(editor));
				new Handler(Looper.getMainLooper()).post(()->{
					findViewById(R.id.progress).setVisibility(View.GONE);
				});
			}
		}.start();
	}
}