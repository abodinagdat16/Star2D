package com.star4droid.star2d;

import android.Manifest;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import com.google.android.material.color.MaterialColors;
import com.google.android.material.elevation.SurfaceColors;
import com.google.android.material.navigation.NavigationView;
import com.star4droid.star2d.Activities.SettingsActivity;
import com.star4droid.star2d.Adapters.ExportDialog;
import com.star4droid.star2d.Helpers.EngineSettings;
import com.star4droid.star2d.Helpers.FileUtil;
import com.star4droid.star2d.Utils;
import com.star4droid.star2d.evo.R;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ProjectsActivity extends AppCompatActivity {
	boolean goToSettings=false;
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
		Utils.setLanguage(this);
        setContentView(R.layout.projects);
		initVars();
		checkPerms();
		if(checkPerms(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.ACCESS_MEDIA_LOCATION))
			init();
		findViewById(R.id.settings).setOnClickListener(v->{
			goToSettings=true;
			Intent intent=new Intent();
			intent.setClass(this,SettingsActivity.class);
			startActivity(intent);
		});
		projectsPath = FileUtil.getPackageDataDir(this)+"/projects/";
		FileUtil.listDir(projectsPath,projectsList);
		listView1.setAdapter(new ProjectsListAdapter(this,projectsList));
		filePicker = registerForActivityResult(new ActivityResultContracts.OpenMultipleDocuments(),new ActivityResultCallback<List<Uri>>(){
			@Override
			public void onActivityResult(List<Uri> uriList) {
				String err="";
				for(Uri uri:uriList){
					try {
						if(uri!=null) restoreProject(getContentResolver().openInputStream(uri));
						else err = err+"file : "+FileUtil.convertUriToFilePath(ProjectsActivity.this,uri)+" , Null Uri \n";
						} catch(Exception ex){
						err = err+"file : "+FileUtil.convertUriToFilePath(ProjectsActivity.this,uri)+" , error : "+ex.toString()+"\n";
					}
				}
				if(!err.equals("")) Utils.showMessage(ProjectsActivity.this,err);
			}
		});
        View decorView = getWindow().getDecorView();

        if (Build.VERSION.SDK_INT < 30) {
            int uiOptions =
                    View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            decorView.setSystemUiVisibility(uiOptions);
        } else {
            WindowInsetsControllerCompat windowInsetsController =
                    new WindowInsetsControllerCompat(getWindow(), decorView);
            windowInsetsController.hide(WindowInsetsCompat.Type.systemBars());
            windowInsetsController.setSystemBarsBehavior(
                    WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
        }
        
        navigation.getLayoutParams().width = Utils.getScreenWidth(this) / 5;
        navigation.setNavigationItemSelectedListener(
                menuItem -> {
                menuItem.setChecked(true);
                   Menu menu = navigation.getMenu();
        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            if (item != menuItem && item.isChecked()) {
                item.setChecked(false);
            }
        }
        
                    
                    return true;
                });
                }
	ListView listView1;
    View addProject, restore;
    ActivityResultLauncher filePicker;
    String projectsPath;
    int FILE_PICKER_CODE = 1117;
    NavigationView navigation;
    ArrayList<String> projectsList = new ArrayList<>();
    Intent fp =
            new Intent(Intent.ACTION_GET_CONTENT) {
                {
                    setType("*/*");
                    putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                }
            };

    public void initVars() {
        listView1 = findViewById(R.id.listview1);
        addProject = findViewById(R.id.addProject);
        restore = findViewById(R.id.restore);
        navigation = findViewById(R.id.navigation);
    }

    public void init() {

        restore.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        if (Build.VERSION.SDK_INT < 30)
                            startActivityForResult(fp, FILE_PICKER_CODE);
                        else filePicker.launch(new String[] {"*/*"});
                    }
                });

        addProject.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        createProject();
                    }
                });
    }

    public void createProject() {
        View dialog_cv = getLayoutInflater().inflate(R.layout.create_dialog, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        Utils.hideSystemUi(alertDialog.getWindow());
        final EditText name = dialog_cv.findViewById(R.id.name);
        alertDialog.setView(dialog_cv);
        dialog_cv
                .findViewById(R.id.add)
                .setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View arg0) {
                                FileUtil.makeDir(projectsPath + name.getText().toString());
                                refreshList();
                                alertDialog.dismiss();
                            }
                        });
        alertDialog.show();
    }

    public void refreshList() {
        projectsList.clear();
        FileUtil.listDir(projectsPath, projectsList);
        if (listView1.getAdapter() == null)
            listView1.setAdapter(new ProjectsListAdapter(this, projectsList));
        else ((ProjectsListAdapter) listView1.getAdapter()).notifyDataSetChanged();
    }
    
    public boolean checkPerms(final String... perms) {
        for (String i : perms) {
            if (ContextCompat.checkSelfPermission(this, i) == PackageManager.PERMISSION_DENIED) {
                final AlertDialog dialog = new AlertDialog.Builder(this).create();
                dialog.setTitle("Warning !");
                dialog.setMessage("Give the the necessary permissions...");
                dialog.setButton(
                        AlertDialog.BUTTON_POSITIVE,
                        "Ok",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                ActivityCompat.requestPermissions(
                                        ProjectsActivity.this, perms, 1000);
                                dialog.dismiss();
                            }
                        });
                dialog.setCancelable(false);
                dialog.show();
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1000) {
            init();
        }
    }

    @Override
    protected void onActivityResult(int code, int result, Intent data) {
        super.onActivityResult(code, result, data);
            if (result == RESULT_OK) {
                ArrayList<String> _filePath =
                        Utils.getArrayList(code, result, data, ProjectsActivity.this);
                /*if (data != null) {
                    if (data.getClipData() != null) {
                        for (int _index = 0; _index < data.getClipData().getItemCount(); _index++) {
                            ClipData.Item _item = data.getClipData().getItemAt(_index);
                            _filePath.add(
                                    FileUtil.convertUriToFilePath(
                                            getApplicationContext(), _item.getUri()));
                        }
                    } else {
                        _filePath.add(
                                FileUtil.convertUriToFilePath(
                                        getApplicationContext(), data.getData()));
                    }
                }*/
                // onFilePicked....
				if(code==ExportDialog.RECIEVE_ICON){
					//Utils.showMessage(this,_filePath.get(0));
					Utils.setImageFromFile(ExportDialog.icon,_filePath.get(0));
					ExportDialog.imgPath = _filePath.get(0);
					return;
				}
				if(code==FILE_PICKER_CODE){
                String err = "";
                for (String f : _filePath)
                    try {
                        restoreProject(
                                getContentResolver()
                                        .openInputStream(Uri.fromFile(new java.io.File(f))));
                    } catch (Exception e) {
                        err = err + "\n File : " + f + " - error : \n" + e.toString();
                    }
                if (!err.equals("")) Utils.showMessage(ProjectsActivity.this, err);
				}
            }
    }
public void restoreProject(InputStream inputStream) throws Exception {
        final AlertDialog dialog = new AlertDialog.Builder(ProjectsActivity.this).create();
        dialog.setView(new ProgressBar(ProjectsActivity.this));
        dialog.setCancelable(false);
        dialog.show();
        new Thread() {
            @Override
            public void run() {
                final StringBuilder err = new StringBuilder("");
                final String restoreP =
                        FileUtil.getPackageDataDir(ProjectsActivity.this) + "/projects/";
                try {
                    Utils.unzipf(inputStream, restoreP, "");
                } catch (Exception ex) {
                    err.append(ex.toString());
                }

                new Handler(Looper.getMainLooper())
                        .post(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        dialog.dismiss();
                                        AlertDialog dialog =
                                                new AlertDialog.Builder(ProjectsActivity.this)
                                                        .create();
                                        dialog.setCancelable(true);
                                        TextView text = new TextView(ProjectsActivity.this);
                                        text.setPadding(8, 8, 8, 8);
                                        // text.setBackgroundColor(Color.WHITE);
                                        text.setTextColor(Color.BLACK);
                                        text.setText(
                                                err.toString().equals("")
                                                        ? ("restored...")
                                                        : err.toString());
                                        dialog.setView(text);
                                        dialog.show();
                                        refreshList();
                                    }
                                });
            }
        }.start();
    }
	String settings="";
    @Override
    protected void onPause() {
        super.onPause();
		settings=EngineSettings.get().getString("lang","")+EngineSettings.get().getBoolean("night",false);
    }
	
	@Override
	protected void onResume() {
		super.onResume();
		if(goToSettings){
			String re=EngineSettings.get().getString("lang","")+EngineSettings.get().getBoolean("night",false);
			if(!re.equals(settings)){
				Utils.setLanguage(this);
				recreate();
				settings=re;
				goToSettings=false;
			}
		}
	}
}