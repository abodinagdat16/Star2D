package com.star4droid.star2d;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import com.star4droid.star2d.Helpers.EngineSettings;
import com.star4droid.star2d.evo.R;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		Utils.setLanguage(this);
        setContentView(R.layout.activity_main);
		EngineSettings.init(this);
		new Timer().schedule(new TimerTask(){
			@Override
			public void run() {
				Intent i = new Intent();
				i.setClass(MainActivity.this,ProjectsActivity.class);
				startActivity(i);
				finish();
			}
		},3000);
		 
    }
}