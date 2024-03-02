package com.star4droid.star2d.Helpers;

import android.net.Uri;
import com.star4droid.star2d.Items.Editor;
import java.util.ArrayList;

public class Project {
	private String path;
	public Project(String p){
		path = p;
	}
	public String getPath(){
		return path;
	}
	
	public String get(String name){
		return path+"/"+name+"/";
	}
	
	public String getBodyScriptPath(String body,String scene){
		return path+"/java/com/star4droid/Game/Scripts/"+scene+"/"+body+"Script.java";
	}
	
	public String getCodesPath(String scene){
		return path+"/java/com/star4droid/Game/"+scene.toLowerCase()+".java";
	}
	
	public void renameScene(String scene,String newScene){
		ArrayList<String> arrayList = getSceneList(newScene);
		int x=0;
		for(String file:getSceneList(scene)){
			FileUtil.moveFile(file,arrayList.get(x));
			x++;
		}
	}
	
	public String getScriptsPath(String scene){
		return path+"/scripts/"+scene+"/";
	}
	
	public void copyScene(String scene,String newScene){
		ArrayList<String> arrayList = getSceneList(newScene);
		int x=0;
		for(String file:getSceneList(scene)){
			FileUtil.copyFile(file,arrayList.get(x));
			x++;
		}
	}
	
	public ArrayList<String> getSceneList(String scene){
		ArrayList<String> arrayList = new ArrayList<>();
		arrayList.add(getScenesPath()+scene);
		arrayList.add(getConfig(scene));
		arrayList.add(path+"/Events/"+scene);
		//arrayList.add(getDex(scene));
		arrayList.add(getJoints(scene));
		arrayList.add(getCodesPath(scene));
		arrayList.add(getScriptsPath(scene));
		return arrayList;
	}
	
	ArrayList<String> importantList=new ArrayList<>();
	public ArrayList<String> getImportantList(){
		if(importantList.size()>0) return importantList;
		importantList = new ArrayList<>();
		importantList.add(get("anims").toLowerCase());
		importantList.add(get("dex").toLowerCase());
		importantList.add(get("configs").toLowerCase());
		importantList.add(get("files").toLowerCase());
		importantList.add(get("scenes").toLowerCase());
		return importantList;
	}
	
	public void deleteScene(String scene){
	    //ArrayList<String> arrayList = getSceneList(newScene);
		for(String file:getSceneList(scene)){
			FileUtil.deleteFile(file);
		}
	}
	
	public void deleteBody(String scene,String body){
		FileUtil.deleteFile(path+"/Events/"+scene+"/private/"+body);
	}
	
	public String getEventPath(String scene,String body,String event){
		if(body.equals("")) return path+"/Events/"+scene+"/public/"+event;
		return path+"/Events/"+scene+"/private/"+body+"/"+event;
	}
	
	public String getConfig(String scene){
		return path+"/configs/"+scene+".json";
	}
	
	public String getJoints(String scene){
		return path+"/joints/"+scene+"/";
	}
	
	public String getDex(String scene){
		return path+"/dex/scenes.dex";//+scene+".dex";
	}
	
	public String getName(){
		return Uri.parse(path).getLastPathSegment();
	}
	
	public void save(Editor editor){
		FileUtil.writeFile(path+"/scenes/"+editor.getScene(),editor.getSaveState());
	}
	
	public String getScenesPath(){
		return path+"/scenes/";
	}
	
	public String getImagesPath(){
		return path+"/images/";
	}
}