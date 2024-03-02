package com.star4droid.star2d.Fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import androidx.fragment.app.Fragment;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.star4droid.star2d.Adapters.EditorField;
import com.star4droid.star2d.Adapters.Section;
import com.star4droid.star2d.evo.R;
import com.star4droid.star2d.Items.Editor;
import com.star4droid.star2d.Utils;
import java.util.HashMap;

public class PropertiesFragment extends Fragment {
	Editor editor;
	LinearLayout view;
	public PropertiesFragment(){}
		
	public PropertiesFragment(Editor e){
		//super();
		editor=e;
	}
	
	@Override
	public void onViewCreated(View arg0, Bundle arg1) {
		super.onViewCreated(arg0, arg1);
	}
	
	@Override
	public void onStart() {
		super.onStart();
		wrap();
	}
	
	@Override
	public void onAttach(Context arg0) {
		super.onAttach(arg0);
		wrap();
	}
	
	public void wrap(){
		try {
			if(view!=null) view.setMinimumHeight(getContext().getResources().getDisplayMetrics().heightPixels*5);
		} catch(Exception ex){}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup arg1, Bundle arg2) {
		view = new LinearLayout(getContext());
        if(editor==null) return view;
		//view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT));
		//view.setBackgroundColor(Color.BLACK);
		view.setMinimumHeight(9999);
		view.setOrientation(LinearLayout.VERTICAL);
		//load properties
		String keys=",";
		HashMap<String,Object> map=null;
		editor.setProperties(view);
		try {
			map = new Gson().fromJson(Utils.readAssetFile("types.json",getContext()), new TypeToken<HashMap<String, Object>>(){}.getType());
			} catch(Exception ex){
			Utils.showMessage(getContext(),"Map init error : "+ex.toString());
			return view;
		}
		
		HashMap<String,Object> propertiesMap = new HashMap<>();
		for(String s:map.keySet()){
			for(String str:map.get(s).toString().split(",")){
				if(str.equals("")) continue;
				propertiesMap.put(str,s);
			}
		}
		
		try {
			map = new Gson().fromJson(Utils.readAssetFile("map.json",getContext()), new TypeToken<HashMap<String, Object>>(){}.getType());
			} catch(Exception ex){
			Utils.showMessage(getContext(),"editor map error : "+ex.toString());
			return view;
		}
		String details="";
		for(String s:map.keySet()){
			keys+=s+":\n";
			Section section = new Section(s,view);
			for(String str:map.get(s).toString().split(",")){
				keys+=str+",";
				if(str.equals("")) continue;
				try {
					if(propertiesMap.get(str).equals("static")) continue;
					EditorField editorField = new EditorField(getContext(),editor,str,propertiesMap.get(str).toString());
					section.add(editorField.getView());
					//Log.e(Utils.error_tag,"√ key : "+str);
					} catch(Exception ex){
					Log.e(Utils.error_tag,"key : "+str+"\n"+ex.toString());
				}
			}
			section.setup();
			
		}
		wrap();
		editor.updateProperties();
		//Utils.showMessage(getContext(),"Childs : "+view.getChildCount());
		return view;
	}
}