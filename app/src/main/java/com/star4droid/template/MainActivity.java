package com.star4droid.template;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.star4droid.template.Items.StageImp;

public class MainActivity extends AndroidApplication {
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		if (Build.VERSION.SDK_INT >= 23) {
			if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
			||checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
				requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1000);
			}
		}
		AndroidApplicationConfiguration configuration= new AndroidApplicationConfiguration();
		initialize(StageImp.getFromDex((getIntent().hasExtra("path")&&(!getIntent().getStringExtra("path").equals("")))?getIntent().getStringExtra("path"):(new java.io.File(getFilesDir()+"/game/").listFiles()[0]+""),
		(getIntent().hasExtra("scene")&&(!getIntent().getStringExtra("scene").equals("")))?getIntent().getStringExtra("scene"):"scene1",null,null),configuration);
	}
}