package com.star4droid.star2d;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.core.view.WindowInsetsCompat;
import com.star4droid.star2d.Helpers.FileUtil;
import com.star4droid.star2d.Helpers.Project;
import com.star4droid.star2d.Loaders.SpriteSheetsLoader;
import com.star4droid.star2d.evo.R;
import com.star4droid.star2d.player.PlayerView;
import java.util.HashMap;

public class LandscapePlayer extends AppCompatActivity {
	PlayerView player;
	LinearLayout logo;
	FrameLayout frame;
	ImageView pause;
	TextView fpsText;
	boolean playing=true;
	SpriteSheetsLoader spriteSheetsLoader;
	Runnable addFrame = new Runnable(){
		@Override
		public void run() {
			frame.removeView(logo);
			frame.addView(player);
		}
	};
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		Utils.setLanguage(this);
		setContentView(R.layout.player);
		View decorView = getWindow().getDecorView();
		logo = findViewById(R.id.logo);
		
		if(Build.VERSION.SDK_INT<30){
			int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN
			| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
			| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
			decorView.setSystemUiVisibility(uiOptions);
			} else {
			WindowInsetsControllerCompat windowInsetsController = new WindowInsetsControllerCompat(getWindow(), decorView);
			windowInsetsController.hide(WindowInsetsCompat.Type.systemBars());
			windowInsetsController.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
		}
		
		frame= findViewById(R.id.frame);
		fpsText = findViewById(R.id.fpsText);
		
		String path =getIntent().getStringExtra("path")
		,scene = getIntent().getStringExtra("scene");
		player = PlayerView.getFromDex(path,scene,this);
		//TODO: improve it
        player.Pause();
		if(player!=null){
			spriteSheetsLoader = new SpriteSheetsLoader(this,new Project(path)){
				@Override
				public void onLoad(final Object... ob){
					fpsText.setText("Started");
					player.setFPSText(fpsText);
					player.setSpriteSheets((HashMap<String,AnimationDrawable>)ob[0]);
					new Handler(Looper.getMainLooper()).post(addFrame);
				}
			};
			spriteSheetsLoader.start();
			
		} else {
			frame.removeView(logo);
			TextView text = new TextView(this);
			text.setText("Dex Not Found..!!");
			text.setTextSize(20);
			frame.addView(text);
		}
		
		pause = findViewById(R.id.pause);
		pause.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View arg0) {
				if(player.isPlaying()){
					pause.setImageResource(R.drawable.play_yellow);
					player.Pause();
				} else{
					player.Resume();
					pause.setImageResource(R.drawable.pause_yellow);
				}
			}
		});
		pause.setOnLongClickListener(new View.OnLongClickListener(){
			@Override
			public boolean onLongClick(View arg0) {
				finish();
			    return false;
			}
		});
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		if(player!=null) player.onPause();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if(player!=null) player.onResume();
	}
}