package com.star4droid.star2d.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import androidx.appcompat.app.AlertDialog;
import com.star4droid.star2d.Helpers.ColorWheelView;
import com.star4droid.star2d.Helpers.PropertySet;
import com.star4droid.star2d.Items.Editor;
import com.star4droid.star2d.Items.EditorItem;
import com.star4droid.star2d.evo.R;
import com.star4droid.star2d.Utils;

public class ColourSelector {
	public static void show(final Editor editor,final String key){
		Context context= editor.getContext();
		View view = LayoutInflater.from(context).inflate(R.layout.color_value_editor,null);
		final AlertDialog dialog = new AlertDialog.Builder(context).create();
		dialog.setView(view);
		final EditText resultText = view.findViewById(R.id.resultText);
		final View result = view.findViewById(R.id.result);
		final SeekBar seek = view.findViewById(R.id.seek);
		
		view.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v){
				dialog.dismiss();
			}
		});
		
		resultText.addTextChangedListener(new TextWatcher(){
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				try {
					result.setBackgroundColor(Color.parseColor(resultText.getText().toString()));
				} catch(Exception e){}
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		//resultText.setText(key.equals("sceneColor")?editor...);
		final ColorWheelView wheel = view.findViewById(R.id.wheel);
		wheel.setWheelEventListener(new ColorWheelView.WheelEventListener(){
			@Override
			public void onColorChanged(int color) {
				//result.setBackgroundColor(color);
				resultText.setText(wheel.getSelectedHex());
			}

			@Override
			public void onColorSelected(int color) {
				
			}
		});
		
		seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
			@Override
			public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
			}
			
			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
				wheel.setAlphaValue(seek.getProgress());
			}
		});
		
		view.findViewById(R.id.select).setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v){
				if(key.equals("sceneColor")){
					editor.setSceneColor(resultText.getText().toString());
					dialog.dismiss();
					return;
				}
				PropertySet<String,Object> p = PropertySet.getPropertySet(editor.getSelectedView());
				p.put(key,resultText.getText().toString());
				((EditorItem)(editor.getSelectedView())).setProperties(p);
				editor.updateProperties();
				editor.getSaveState();
				dialog.dismiss();
			}
		});
		Utils.hideSystemUi(dialog.getWindow());
		dialog.show();
	}
}