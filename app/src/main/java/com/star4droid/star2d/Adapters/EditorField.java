package com.star4droid.star2d.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PointF;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatCheckBox;
import com.star4droid.star2d.EditorActivity;
import com.star4droid.star2d.Helpers.CheckBoxUtils;
import com.star4droid.star2d.Helpers.FileUtil;
import com.star4droid.star2d.Helpers.PropertySet;
import com.star4droid.star2d.Items.Editor;
import com.star4droid.star2d.Items.EditorItem;
import com.star4droid.star2d.evo.R;
import com.star4droid.star2d.Utils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class EditorField implements EditorValue {
	View view;//ScrollParent;
	TextView name,value;
	AppCompatCheckBox checkBox;
	ImageView imageView;
	Spinner spinner;
	ViewParent parent;
	public static String allowedChars="ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890_";
	String type,imgPath="Null",spinnerPrev="Null";
	private static String drag_control_fields=null,disableOnPlayFields=null;
	Editor editor;
	public static HashMap<String,Object> spinnerMap;
	public EditorField(final Context ctx,Editor mEditor,String nm,String mType){
		type = mType;
		editor = mEditor;
		if(drag_control_fields==null)
			drag_control_fields = Utils.readAssetFile("dragControl.json",ctx);
		if(disableOnPlayFields==null)
			disableOnPlayFields = Utils.readAssetFile("playOffProps.txt",ctx);
		if(type.equals("body")){
			view = LayoutInflater.from(ctx).inflate(R.layout.float_value,null);
			name = view.findViewById(R.id.name);
			value = view.findViewById(R.id.value);
			name.setText(nm);
			if(nm.equals("Collision")||nm.equals("attach To")) value.setText("Tap to select");
			value.setOnClickListener(v->{
				if(isDisabled()) return;
				final AlertDialog alert = new AlertDialog.Builder(ctx).create();
				
				View vv = LayoutInflater.from(ctx).inflate(R.layout.joint_dialog,null);
				final LinearLayout linear = vv.findViewById(R.id.linear);
				((View)(vv.findViewById(R.id.name).getParent())).setVisibility(View.GONE);
				vv.findViewById(R.id.cancel).setOnClickListener(cn->{
					alert.dismiss();
				});
				final TextView btn = vv.findViewById(R.id.add);
				btn.setText(ctx.getString(R.string.edit));
				String vl = PropertySet.getPropertySet(editor.getSelectedView()).getString(getName());
				for(String str:editor.getBodiesList()){
					final AppCompatCheckBox checkBox = new AppCompatCheckBox(ctx);
					checkBox.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));
					checkBox.setText(str);
					checkBox.setChecked(vl.equals(str)||vl.contains("("+str+")"));
					if(!nm.equals("Collision")){
					//in Collision only user can select many objects....
						checkBox.setOnClickListener(vi->{
							boolean check=checkBox.isChecked();
							for(int x=0;x<linear.getChildCount();x++)
								if(linear.getChildAt(x) instanceof AppCompatCheckBox)
									(((AppCompatCheckBox)linear.getChildAt(x))).setChecked(false);
							checkBox.setChecked((nm.equals("parent")||nm.equals("attach To"))?check:true);
							
						});
					}
					linear.addView(checkBox);
				}
				btn.setOnClickListener(b->{
					String result="";
					for(int x=0;x<linear.getChildCount();x++){
						if(!(linear.getChildAt(x) instanceof AppCompatCheckBox)) continue;
						AppCompatCheckBox check = ((AppCompatCheckBox)linear.getChildAt(x));
						if(check.isChecked()){
							result+="("+check.getText().toString()+")";
						}
					}
					if(!getName().equals("Collision")) result = result.replace("(","").replace(")","");
					if((getName().equals("Script"))&&result.equals("")){
						Utils.showMessage(ctx,"Can\'t be empty!");
						return;
					}
					
					alert.dismiss();
					if(getName().equals("parent")){
						if(result.equals(""))
							PropertySet.getPropertySet(editor.getSelectedView()).setParent(null);
						if(!result.equals(""))
						for(int x=0;x<editor.getChildCount();x++){
							if(Utils.isEditorItem(editor.getChildAt(x))){
								PropertySet set=PropertySet.getPropertySet(editor.getChildAt(x));
								if(set.getString("name").equals(result)){
									if(!PropertySet.getPropertySet(editor.getSelectedView()).setParent(set)){
										Utils.showMessage(ctx,"Can\'t be set as parent!");
										return;
									}
									editor.selectView(editor.getSelectedView());
									break;
								}
							}
						}
					}
					PropertySet.getPropertySet(editor.getSelectedView()).put(getName(),result);
					editor.updateProperties();
					if(editor.getLink()!=null)
					    editor.getLink().update(PropertySet.getPropertySet(editor.getSelectedView()));
				});
				alert.setView(vv);
				Utils.hideSystemUi(alert.getWindow());
				alert.show();
			});
			
		}
		if(type.equals("color")){
			view = LayoutInflater.from(ctx).inflate(R.layout.color_field,null);
			name = view.findViewById(R.id.name);
			value = view.findViewById(R.id.value);
			name.setText(nm);
			value.setOnClickListener(v->{
					if(isDisabled()) return;
					ColourSelector.show(editor,getName());
			});
		}
		else if(type.equals("float")){
			view = LayoutInflater.from(ctx).inflate(R.layout.float_value,null);
			name = view.findViewById(R.id.name);
			value = view.findViewById(R.id.value);
			final View control = view.findViewById(R.id.control);
			name.setText(nm);
			control.setVisibility(drag_control_fields.contains(","+getName()+",")?View.VISIBLE:View.INVISIBLE);
			if(drag_control_fields.contains(","+getName()+",")) control.setOnTouchListener(new View.OnTouchListener(){
				private PointF pf= new PointF(),sf=new PointF();
				@Override
				public boolean onTouch(View v, MotionEvent event){
					int ev = event.getAction();
					if(isDisabled()) return false;
					switch (ev) {
						case MotionEvent.ACTION_DOWN:
						parent = view.getParent();
						int x=0;
						while(parent!=null&&x<=10){
							parent.requestDisallowInterceptTouchEvent(true);
							parent=parent.getParent();
							x++;
						}
						//if(ScrollParent==null) ScrollParent = editor.getProperties().getView().findViewById(R.id.propertiesScroll);
						pf.set(event.getX(), event.getY());
						sf.set(event.getX(), event.getY());
						
						break;
						case MotionEvent.ACTION_UP:
						//ScrollParent.setOnTouchListener(null);
						parent = view.getParent();
						x=0;
						while(parent!=null&&x<=10){
							parent.requestDisallowInterceptTouchEvent(true);
							parent=parent.getParent();
							x++;
						}
						if(editor.getLink()!=null){
							editor.getLink().sizeChanged(PropertySet.getPropertySet(editor.getSelectedView()));
							editor.getLink().positionChange(PropertySet.getPropertySet(editor.getSelectedView()));
						}
						break;
						case MotionEvent.ACTION_CANCEL:
						//ScrollParent.setOnTouchListener(null);
						
						break;
						case MotionEvent.ACTION_MOVE:
						
						float dx=event.getX()-pf.x;
						float dy=event.getY()-pf.y;//Uesless
						float increase = (float)(dx*0.05*1.3f*(1/editor.getEditorScale()));
						PropertySet<String,Object> propertySet = PropertySet.getPropertySet(editor.getSelectedView());
						propertySet.put(getName(),propertySet.getFloat(getName())+increase);
						if(getName().equals("height")) propertySet.put("Collider Height",propertySet.getFloat("Collider Height")+increase);
						if(getName().equals("width")) propertySet.put("Collider Width",propertySet.getFloat("Collider Width")+increase);
						if(getName().equals("x")||getName().equals("y")||getName().equals("rotation"))
							propertySet.updateMatrixToCurrent();
						editor.updateProperties();
						editor.updateSelector();
						Utils.update(editor.getSelectedView());
						break;
					}
					pf.set(event.getX(),event.getY());
					return true;
				}
			});
			
			value.setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View v){
					if(isDisabled()) return;
					NumbersDialog.show(ctx,PropertySet.getPropertySet(editor.getSelectedView()),new NumbersDialog.OnDone(){
						public boolean onGet(String s){
							try {
							float f= Utils.getFloat(s);
							PropertySet<String,Object> propertySet=PropertySet.getPropertySet(editor.getSelectedView());
							propertySet.put(getName(),f);
							if(getName().equals("radius")&&propertySet.containsKey("Collider Radius")) propertySet.put("Collider Radius",f);
							if(getName().equals("width")&&propertySet.containsKey("Collider Width")) propertySet.put("Collider Width",f);
							if(getName().equals("height")&&propertySet.containsKey("Collider Height")) propertySet.put("Collider Height",f);
							editor.updateProperties();
							editor.getSaveState();
							if(getName().equals("x")||getName().equals("y")||getName().equals("rotation"))
								propertySet.updateMatrixToCurrent();
							if(getName().equals("tileX")||getName().equals("tileY"))
							((EditorItem)(editor.getSelectedView())).setProperties(propertySet);
							else Utils.update(editor.getSelectedView());
							if(editor.getLink()!=null)
								editor.getLink().update(propertySet);
							} catch(Exception ex){}
							return true;
						}
					},getName());
				}
			});
			} else if(type.equals("image")){
				view = LayoutInflater.from(ctx).inflate(R.layout.float_value,null);
				//imageView = view.findViewById(R.id.image);
				name = view.findViewById(R.id.name);
				value = view.findViewById(R.id.value);
				name.setText(nm);
				value.setOnClickListener(new View.OnClickListener(){
					@Override
					public void onClick(View v){
						if(isDisabled()) return;
						ImagesSelectorAdapter.show(v.getContext(),new ImagesSelectorAdapter.onSelectImage(){
							@Override
							public void onSelect(String path, AlertDialog dialog) {
								PropertySet.getPropertySet(editor.getSelectedView()).put(getName(),"/"+Uri.parse(path).getLastPathSegment());
								editor.updateProperties();
								editor.getSaveState();
								dialog.dismiss();
								if(editor.getLink()!=null)
									editor.getLink().update(PropertySet.getPropertySet(editor.getSelectedView()));
							}
						},editor);
					}
				});
			} else if(type.equals("boolean")){
				view = LayoutInflater.from(ctx).inflate(R.layout.checkbox_value,null);
				checkBox = view.findViewById(R.id.checkbox);
				name = view.findViewById(R.id.name);
				name.setText(nm);
				checkBox.setText(nm);
				checkBox.setOnClickListener(v->{
					PropertySet.getPropertySet(editor.getSelectedView()).put(getName(),checkBox.isChecked()?"true":"false");
					if(getName().equals("Visible")){
						if(editor.getSelectedView()!=null) Utils.update(editor.getSelectedView());
					}
					if(editor.getLink()!=null)
						editor.getLink().update(PropertySet.getPropertySet(editor.getSelectedView()));
				});
				
				} else if(type.equals("spinner")){
					view = LayoutInflater.from(ctx).inflate(R.layout.spinner_value,null);
					spinner = view.findViewById(R.id.spinner);
					name = view.findViewById(R.id.name);
					name.setText(nm);
				} else if(type.equals("string")){
					//lazy to create new layout
					view = LayoutInflater.from(ctx).inflate(R.layout.float_value,null);
					name = view.findViewById(R.id.name);
					value = view.findViewById(R.id.value);
					name.setText(nm);
					value.setOnClickListener(new View.OnClickListener(){
						@Override
						public void onClick(View arg0) {
							if(isDisabled()) return;
							View dialog_cv = LayoutInflater.from(ctx).inflate(R.layout.create_dialog,null);
							final AlertDialog alertDialog = new AlertDialog.Builder(ctx).create();
							final EditText nam = dialog_cv.findViewById(R.id.name);
							TextView add = dialog_cv.findViewById(R.id.add);
							TextView title = dialog_cv.findViewById(R.id.title);
							nam.setHint("Enter Value");
							title.setText("Edit "+getName());
							add.setText("Edit");
							nam.setText(PropertySet.getPropertySet(editor.getSelectedView()).getString(getName()));
							Utils.hideSystemUi(alertDialog.getWindow());
							alertDialog.setView(dialog_cv);
							add.setOnClickListener(new View.OnClickListener(){
								@Override
								public void onClick(View arg0) {
									if(getName().equals("name")) {
										if(editor.getBodiesList().contains(nam.getText().toString())) {
											Utils.showMessage(ctx,"Name already used by anthor element..!!");
											return;
										}
									}
									if(!getName().equals("Text"))
									for(char c:nam.getText().toString().toCharArray()){
										if(!allowedChars.contains(String.valueOf(c))){
											Utils.showMessage(ctx,"use A-Z a-z or _ , Not Allowed Char : "+c);
											return;
										}
									}
									PropertySet<String,Object> ps = PropertySet.getPropertySet(editor.getSelectedView());
									//change the script when name changed...
									if(ps.getString("Script").equals(ps.getString("name"))&&!ps.getString("name").equals(nam.getText().toString())){
										ps.put("Script",nam.getText().toString());
										FileUtil.moveFile(editor.getProject().getBodyScriptPath(ps.getString("name"),editor.getScene()),
															editor.getProject().getBodyScriptPath(nam.getText().toString(),editor.getScene()));
									}
									ps.put(getName(),nam.getText().toString());
									value.setText(nam.getText().toString());
									Utils.update(editor.getSelectedView());
									alertDialog.dismiss();
									editor.getSaveState();
									if(editor.getLink()!=null)
										editor.getLink().update(PropertySet.getPropertySet(editor.getSelectedView()));
								}
							});
							alertDialog.show();
						}
					});
					
					
				}
		view.setTag(this);
		/*
		if(value!=null) value.setTextSize(8);
		if(name!=null) name.setTextSize(8);
		if(checkBox!=null) name.setTextSize(8);
		*/
	}
	
	public View getView(){
		return view;
	}
	
	@Override
	public void setValue(String string){
		value.setText(string);
	}
	
	public String getName(){
		return name.getText().toString();
	}
	
	@Override
	public boolean update(PropertySet p) {
		try {
			return refresh(p);
		} catch(Exception e){
			return false;
		}
	}
	
	private boolean refresh(PropertySet p){
		int bool = p==null ? View.GONE:(p.containsKey(getName())?View.VISIBLE:View.GONE);
		if(p==null) return false;
		view.setVisibility(bool);
		if(p.containsKey(name.getText().toString())){
			if(type.equals("float"))
			value.setText(p.getString(name.getText().toString()));
			if(type.equals("image")){
				if(!imgPath.equals(p.getString(getName()))){
					try {
						value.setText(p.getString(getName()).replace(Utils.seperator,"/"));
				 		//Utils.setImageFromFile(imageView,editor.getProject().getImagesPath()+p.getString(getName()));
				 } catch (Exception ex){
					 value.setText("Error..!!\n"+ex.toString());
				 }
				 if(editor.getSelectedView() instanceof EditorItem) ((EditorItem)(editor.getSelectedView())).setProperties(PropertySet.getPropertySet(editor.getSelectedView()));
				 }
				 imgPath = p.getString(name.getText().toString());
				 }
			if(type.equals("boolean")) {
				checkBox.setEnabled(!isDisabled());
				checkBox.setChecked(p.getString(name.getText().toString()).equals("true"));
				//CheckBoxUtils.setCheckMarkDrawable(checkBox.getContext(),checkBox,android.R.drawable.checkbox_on_background,android.R.drawable.checkbox_off_background,checkBox.getMeasuredHeight());
				//CheckBoxUtils.resizeCheckBox(checkBox,checkBox.getMeasuredHeight());
			} else if(type.equals("spinner")){
				if(spinnerPrev.equals(p.getString(getName()))) return bool==View.VISIBLE;
				spinner.setOnItemSelectedListener(null);
				String[] list = spinnerMap.get(getName()).toString().split(",");
				final ArrayList<String> arrayList = new ArrayList<String>(Arrays.asList(list));
				spinner.setAdapter(EditorActivity.getSpinnerAdapter(arrayList,view.getContext(),null));
				String value = p.getString(getName());
				for(int x=0;x<arrayList.size();x++){
					if(arrayList.get(x).equals(value)) {
						spinner.setSelection(x);
						break;
					}
				}
				spinner.setEnabled(!isDisabled());
				spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1, int pos, long arg3) {
						PropertySet.getPropertySet(editor.getSelectedView()).put(getName(),arrayList.get(pos));
						if(getName().equals("Light Type")||getName().equals("Shape")) ((EditorItem)(editor.getSelectedView())).setProperties(
						PropertySet.getPropertySet(editor.getSelectedView())
						);
						editor.getSaveState();
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
					}
					
				});
				spinnerPrev = p.getString(getName());
			} else if(type.equals("string")){
				value.setText(p.getString(getName()));
			} else if(type.equals("color")){
				value.setBackgroundColor(Color.parseColor(p.getString(getName())));
			} else if(type.equals("body")&&(!getName().equals("Collision"))){
				value.setText(p.getString(getName()));
			}
		} else view.setVisibility(View.GONE);
		return bool==View.VISIBLE;
	}
	
	public String getItemName(){
		return PropertySet.getPropertySet(editor.getSelectedView()).getString("name");
	}
	
	public boolean isDisabled(){
		return editor.getLink()!=null&&disableOnPlayFields.contains(","+getName()+",");
	}
	
	public boolean isMultiSelect(){
	    return getName().equals("Collision");
	}
}