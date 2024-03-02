package com.star4droid.star2d.Adapters;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import com.star4droid.star2d.EditorActivity;
import com.star4droid.star2d.Items.*;
import com.star4droid.star2d.evo.R;
import com.star4droid.star2d.Utils;

public class AddPopup {
	
	public static float getLastZ(Editor editor){
		float z=0;
		for(int x=0;x<editor.getChildCount();x++){
			if(Utils.isEditorItem(editor.getChildAt(x))){
				z = Math.max(editor.getChildAt(x).getZ(),z);
			}
		}
		z++;
		return z;
	}
	
	public static void showFor(final EditorActivity activity,final View view,final Editor editor){
		View popupV = LayoutInflater.from(activity).inflate(R.layout.add_popup,null);
		final PopupWindow popup = new PopupWindow(popupV,ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT,true);
		popup.setAnimationStyle(android.R.style.Animation_Dialog);
		popup.showAsDropDown(view, 0,0);
		popup.setBackgroundDrawable(new BitmapDrawable());
		LinearLayout linear = ((LinearLayout)(popupV.findViewById(R.id.box).getParent()));
		for (int i = 0; i < linear.getChildCount(); i++) {
			View v = linear.getChildAt(i);
			v.setBackground(new GradientDrawable() { public GradientDrawable getIns(int a, int b, int c, int d) { this.setCornerRadius(a); this.setStroke(b, c); this.setColor(d); return this; } }.getIns(8, 0, 0xFFFFB300, 0xFF515151));
		}
		linear.setBackground(new GradientDrawable() { public GradientDrawable getIns(int a, int b, int c, int d) { this.setCornerRadius(a); this.setStroke(b, c); this.setColor(d); return this; } }.getIns(0, 2, 0xFFFFB300, 0xFF222222));
		
		popupV.findViewById(R.id.box).setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View view){
				BoxBody box = new BoxBody(activity);
				editor.addView(box);
				box.setDefault();
				box.getPropertySet().put("z",getLastZ(editor));
				editor.selectView(box);
				activity.refreshBodies();
				box.update();
				popup.dismiss();
			}
		});
		popupV.findViewById(R.id.circle).setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View arg0) {
				CircleBody circleBody = new CircleBody(activity);
				editor.addView(circleBody);
				circleBody.setDefault();
				circleBody.getPropertySet().put("z",getLastZ(editor));
				editor.selectView(circleBody);
				activity.refreshBodies();
				circleBody.update();
				popup.dismiss();
			}
		});
		popupV.findViewById(R.id.text).setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View arg0) {
				TextItem textItem = new TextItem(activity);
				editor.addView(textItem);
				textItem.setDefault();
				textItem.getPropertySet().put("z",getLastZ(editor));
				editor.selectView(textItem);
				activity.refreshBodies();
				textItem.update();
				popup.dismiss();
			}
		});
		popupV.findViewById(R.id.progress).setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View arg0) {
				ProgressItem progressItem = new ProgressItem(activity);
				editor.addView(progressItem);
				progressItem.setDefault();
				progressItem.getPropertySet().put("z",getLastZ(editor));
				editor.selectView(progressItem);
				activity.refreshBodies();
				progressItem.update();
				popup.dismiss();
			}
		});
		popupV.findViewById(R.id.joystick).setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View arg0) {
				JoyStickItem joyStickItem = new JoyStickItem(activity);
				editor.addView(joyStickItem);
				joyStickItem.setDefault();
				joyStickItem.getPropertySet().put("z",getLastZ(editor));
				editor.selectView(joyStickItem);
				activity.refreshBodies();
				joyStickItem.update();
				popup.dismiss();
			}
		});
	}
}