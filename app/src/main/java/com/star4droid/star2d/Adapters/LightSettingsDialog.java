package com.star4droid.star2d.Adapters;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import androidx.appcompat.app.AlertDialog;
import com.star4droid.star2d.Helpers.FileUtil;
import com.star4droid.star2d.evo.R;
import com.star4droid.star2d.Helpers.Project;
import com.star4droid.star2d.Helpers.PropertySet;

public class LightSettingsDialog {
	public static void showFor(Context context,Project project,String scene){
		String config=FileUtil.readFile(project.getConfig(scene));
		PropertySet<String,Object> propertySet = config.equals("")?new PropertySet<>():PropertySet.getFrom(config);
		LayoutInflater inflater = LayoutInflater.from(context);
		AlertDialog dialog = new AlertDialog.Builder(context).create();
		View cv=LayoutInflater.from(context).inflate(R.layout.light_settings,null);
		dialog.setView(cv);
		LinearLayout linear = cv.findViewById(R.id.linear);
		if(!(config.equals("")||propertySet.isEmpty()))
		for(int x=0;x<linear.getChildCount();x++){
			View view = linear.getChildAt(x);
			if(view instanceof CheckBox){
				String name=((CheckBox)view).getText().toString();
				if(propertySet.containsKey(name))
					((CheckBox)view).setChecked(propertySet.getString(name).equals("true"));
			} else if(view instanceof EditText){
				String name = ((EditText)view).getHint().toString();
				if(propertySet.containsKey(name))
					((EditText)view).setText(propertySet.getString(name));
			}
		}
		if(propertySet.containsKey(context.getString(R.string.ambient_light)))
			cv.findViewById(R.id.ambient).setBackgroundColor(Color.parseColor(propertySet.getString(context.getString(R.string.ambient_light))));
		cv.findViewById(R.id.save).setOnClickListener(v->{
			//PropertySet<String,Object> result= new PropertySet<>();
			for(int x=0;x<linear.getChildCount();x++){
				View view = linear.getChildAt(x);
				if(view instanceof CheckBox){
					String name=((CheckBox)view).getText().toString();
					propertySet.put(name,((CheckBox)view).isChecked()?"true":"false");
				} else if(view instanceof EditText){
					String name = ((EditText)view).getHint().toString();
					propertySet.put(name,((EditText)view).getText().toString());
				}
			}
			FileUtil.writeFile(project.getConfig(scene),propertySet.toString());
			dialog.dismiss();
		});
		cv.findViewById(R.id.ambient).setOnClickListener(v->{
			ColourSelector.show(context,(hex)->{
				propertySet.put(context.getString(R.string.ambient_light),hex);
				v.setBackgroundColor(Color.parseColor(hex));
				return true;
			});
		});
		dialog.show();
	}
}