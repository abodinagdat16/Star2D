package com.star4droid.star2d.Adapters;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.star4droid.star2d.EditorActivity;
import com.star4droid.star2d.Items.*;
import android.widget.PopupMenu;
import android.view.MenuItem;
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
    	PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
    	//popupMenu.setForceShowIcon(true);
        popupMenu.getMenu().add(0, 0,0,activity.getString(R.string.box_body)).setIcon(R.drawable.box);
        popupMenu.getMenu().add(0,1,1,activity.getString(R.string.circle_body)).setIcon(R.drawable.circle);
        popupMenu.getMenu().add(0,2,2,activity.getString(R.string.text_item)).setIcon(R.drawable.txt_icon);
        popupMenu.getMenu().add(0,3,3,activity.getString(R.string.joystick_item)).setIcon(R.drawable.joystick);
        popupMenu.getMenu().add(0,4,4,activity.getString(R.string.progress_item)).setIcon(R.drawable.progress);
        popupMenu.getMenu().add(0,5,5,activity.getString(R.string.custom_body)).setIcon(R.drawable.progress);
    
        popupMenu.setOnMenuItemClickListener(
                new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if(item.getItemId()==0){
                            BoxBody box = new BoxBody(activity);
            				editor.addView(box);
            				box.setDefault();
            				box.getPropertySet().put("z",getLastZ(editor));
            				editor.selectView(box);
            				activity.refreshBodies();
            				box.update();
                        } else if(item.getItemId()==1){
                            CircleBody circleBody = new CircleBody(activity);
            				editor.addView(circleBody);
            				circleBody.setDefault();
            				circleBody.getPropertySet().put("z",getLastZ(editor));
            				editor.selectView(circleBody);
            				activity.refreshBodies();
            				circleBody.update();
                        } else if(item.getItemId()==2){
                            TextItem textItem = new TextItem(activity);
            				editor.addView(textItem);
            				textItem.setDefault();
            				textItem.getPropertySet().put("z",getLastZ(editor));
            				editor.selectView(textItem);
            				activity.refreshBodies();
            				textItem.update();
                        } else if(item.getItemId()==3){
                            JoyStickItem joyStickItem = new JoyStickItem(activity);
            				editor.addView(joyStickItem);
            				joyStickItem.setDefault();
            				joyStickItem.getPropertySet().put("z",getLastZ(editor));
            				editor.selectView(joyStickItem);
            				activity.refreshBodies();
            				joyStickItem.update();
                        } else if(item.getItemId()==4){
                            ProgressItem progressItem = new ProgressItem(activity);
            				editor.addView(progressItem);
            				progressItem.setDefault();
            				progressItem.getPropertySet().put("z",getLastZ(editor));
            				editor.selectView(progressItem);
            				activity.refreshBodies();
            				progressItem.update();
                        } else if(item.getItemId() == 5){
                            CustomBody custom = new CustomBody(activity);
            				editor.addView(custom);
            				custom.setDefault();
            				custom.getPropertySet().put("z",getLastZ(editor));
            				editor.selectView(custom);
            				activity.refreshBodies();
            				custom.update();
                        }
                        return true;
                    }
                });
        popupMenu.show();
	}
}