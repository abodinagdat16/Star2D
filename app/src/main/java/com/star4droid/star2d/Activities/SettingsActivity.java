package com.star4droid.star2d.Activities;

import android.os.Bundle;
import android.util.Pair;
import android.widget.AdapterView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import androidx.appcompat.widget.AppCompatSpinner;
import com.google.android.material.snackbar.Snackbar;
import com.star4droid.star2d.Helpers.EngineSettings;
import com.star4droid.star2d.Utils;
import com.star4droid.star2d.evo.R;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {
	AppCompatSpinner spinner,compilerSp;
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		Utils.setLanguage(this);
		setContentView(R.layout.settings_layout);
		CheckBox AutoComp = findViewById(R.id.auto_completion),
		d8=findViewById(R.id.d8),night_mode=findViewById(R.id.night_mode);
		d8.setChecked(EngineSettings.get().getBoolean("d8",false));
		AutoComp.setChecked(EngineSettings.get().getBoolean("AutoComp",false));
		AutoComp.setOnCheckedChangeListener((compound,b)->{
			EngineSettings.set("AutoComp",compound.isChecked());
		});
		d8.setOnCheckedChangeListener((compound,b)->{
			EngineSettings.set("d8",compound.isChecked());
		});
		spinner=findViewById(R.id.spinner);
		compilerSp=findViewById(R.id.comp_sp);
		final Pair[] langs=new Pair[]{
			new Pair<>("en","US"),
			new Pair<>("ar","AE"),
			new Pair<>("ru","RU")
		};
		List<String> list = List.of(new String[]{"English","اللغه العربيه","Язык"});
		compilerSp.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,List.of(
			new String[]{"javac","ecj"}
		)));
		spinner.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,list));
		String lng=EngineSettings.get().getString("lang",getString(R.string.lang));
		for(int x=0;x<langs.length;x++)
			if(langs[x].first.toString().equals(lng)){
					spinner.setSelection(x);
					break;
				}
		spinner.setOnItemSelectedListener(new AppCompatSpinner.OnItemSelectedListener(){
			@Override
			public void onItemSelected(AdapterView<?> adapterView, View arg1, int pos, long arg3) {
				if(EngineSettings.get().getString("lang","").equals(langs[pos].first.toString())) return;
				EngineSettings.set("lang",langs[pos].first.toString());
				EngineSettings.set("local",langs[pos].second.toString());
				Utils.setLanguage(SettingsActivity.this);
				//Snackbar.make(adapterView,getString(R.string.restart_the_app),Snackbar.LENGTH_SHORT).show();
				recreate();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
			
		});
		if(EngineSettings.get().getString("compiler","javac").equals("ecj"))
			compilerSp.setSelection(1);
		compilerSp.setOnItemSelectedListener(new AppCompatSpinner.OnItemSelectedListener(){
			@Override
			public void onItemSelected(AdapterView<?> adapterView, View arg1, int pos, long arg3) {
				EngineSettings.set("compiler",pos==0?"javac":"ecj");
			}
			
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
			
		});
		night_mode.setChecked(EngineSettings.get().getBoolean("night",false));
		night_mode.setOnCheckedChangeListener((v,checked)->{
			EngineSettings.set("night",night_mode.isChecked());
			Utils.setLanguage(SettingsActivity.this);
			//Snackbar.make(night_mode,getString(R.string.restart_the_app),Snackbar.LENGTH_SHORT).show();
			recreate();
		});
	}
}