package com.star4droid.star2d.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.ImageView;
import android.view.Window;
import android.os.Build;
import com.star4droid.star2d.Helpers.PropertySet;
import com.star4droid.star2d.Items.Editor;
import com.star4droid.star2d.evo.R;
import com.star4droid.star2d.Utils;

public class NumbersDialog {
	public static void show(Context ctx,PropertySet propertySet,OnDone ondone,String key){
		final AlertDialog cd = new AlertDialog.Builder(ctx).create();
		LayoutInflater cdLI = LayoutInflater.from(ctx);
		View cdCV = cdLI.inflate(R.layout.floating_input, null);
		cd.setView(cdCV);
		final LinearLayout linear = (LinearLayout)
		cdCV.findViewById(R.id.linear);
		final TextView text = (TextView)
		cdCV.findViewById(R.id.text);
		final ImageView clear = (ImageView)
		cdCV.findViewById(R.id.clear);
		final ImageView hide = (ImageView)
		cdCV.findViewById(R.id.hide);
		final ImageView del = (ImageView)
		cdCV.findViewById(R.id.del);
		final ImageView done = (ImageView)
		cdCV.findViewById(R.id.done);
		Window window = cd.getWindow(); window.setGravity(Gravity.LEFT | Gravity.CENTER);
		Utils.hideSystemUi(window);
		window.setDimAmount(0);
		//cd.getWindow().getDecorView().setSystemUiVisibility(flags);
		try {
			if (Build.VERSION.SDK_INT >= 23) {
				//cd.requestWindowFeature(Window.FEATURE_NO_TITLE);
				//View decorView = cd.getWindow().getDecorView();
				// Hide the status bar.
				//int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
				//decorView.setSystemUiVisibility(uiOptions);
				cd.getWindow().setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(Color.TRANSPARENT));
			}
			} catch (Exception e) {
			
		}
		cd.show();
		text.setText(propertySet.containsKey(key) ? propertySet.getString(key) : "0.0");
		hide.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View _view){
				cd.dismiss();
			}
		});
		clear.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View _view){
				text.setText("");
			}
		});
		done.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View _view){
				if(ondone.onGet(text.getText().toString())) cd.dismiss();
			}
		});
		del.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View _view){
				if (text.getText().toString().length() > 0) {
					text.setText(text.getText().toString().substring(0, text.getText().toString().length() - 1));
				}
			}
		});
		for (int i = 0; i < linear.getChildCount(); i++) {
			View vi = linear.getChildAt(i);
			if (i > 0) {
				for (int x = 0; x < ((ViewGroup)(vi)).getChildCount(); x++) {
					View vx = ((ViewGroup)(vi)).getChildAt(x);
					try {
						if ("-1234567890.".contains(((TextView) vx).getText().toString())) {
							vx.setOnClickListener(new View.OnClickListener(){
								@Override
								public void onClick(View _view){
									text.setText(text.getText().toString().concat(((TextView) _view).getText().toString()));
								}
							});
						}
						} catch (Exception e) {
						
					}
				}
			}
		}
	}
	
	public interface OnDone {
		public boolean onGet(String s);
	}
}