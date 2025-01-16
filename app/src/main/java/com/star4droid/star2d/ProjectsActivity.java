package com.star4droid.star2d;

import static android.os.Build.VERSION.SDK_INT;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.ClipData;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.navigation.NavigationView;
import com.star4droid.star2d.Activities.SettingsActivity;
import com.star4droid.star2d.Adapters.ExportDialog;
import com.star4droid.star2d.Helpers.EngineSettings;
import com.star4droid.star2d.Helpers.FileUtil;
import com.star4droid.star2d.evo.R;

import java.io.InputStream;
import java.util.ArrayList;

public class ProjectsActivity extends AppCompatActivity {
    boolean goToSettings = false;
    ListView listView1;
    View addProject, restore;
    ActivityResultLauncher<String[]> filePicker;
    String projectsPath;
    int FILE_PICKER_CODE = 1117;
    NavigationView navigation;
    ArrayList<String> projectsList = new ArrayList<>();
    Intent fp = new Intent(Intent.ACTION_GET_CONTENT) {
        {
            setType("*/*");
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        }
    };
    String settings = "";

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Utils.setLanguage(this);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.projects);

        initVars();

        if (checkPerms(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE/*, Manifest.permission.ACCESS_MEDIA_LOCATION*/))
            init();

        findViewById(R.id.settings).setOnClickListener(v -> {
            goToSettings = true;
            Intent intent = new Intent();
            intent.setClass(this, SettingsActivity.class);
            startActivity(intent);
        });
		
		projectsPath = FileUtil.getPackageDataDir(this) + "/projects/";
        
        FileUtil.listDir(projectsPath, projectsList);
        projectsList.clear();
        
		for(String folder: projectsList)
		    Utils.moveFolder(folder, getFilesDir() +"/projects/" + new java.io.File(folder).getName());
		
        projectsPath = getFilesDir()+"/projects/"; //FileUtil.getPackageDataDir(this) + "/projects/";

        FileUtil.listDir(projectsPath, projectsList);
		
        listView1.setAdapter(new ProjectsListAdapter(this, projectsList));

        filePicker = registerForActivityResult(new ActivityResultContracts.OpenMultipleDocuments(), uriList -> {
            String err = "";
            for (Uri uri : uriList) {
                try {
                    if (uri != null) restoreProject(getContentResolver().openInputStream(uri));
                    else
                        err = err + "file : " + FileUtil.convertUriToFilePath(ProjectsActivity.this, uri) + " , Null Uri \n";
                } catch (Exception ex) {
                    err = err + "file : " + FileUtil.convertUriToFilePath(ProjectsActivity.this, uri) + " , error : " + ex + "\n";
                }
            }
            if (!err.equals("")) Utils.showMessage(ProjectsActivity.this, err);
        });

        navigation.getLayoutParams().width = Utils.getScreenWidth(this) / 5;

        navigation.setNavigationItemSelectedListener(menuItem -> {
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

    public void initVars() {
        listView1 = findViewById(R.id.listview1);
        addProject = findViewById(R.id.addProject);
        restore = findViewById(R.id.restore);
        navigation = findViewById(R.id.navigation);
    }

    public void init() {
        try {
            Utils.extractAssetFile(this, "cp.jar", FileUtil.getPackageDataDir(this) + "/bin/cp.jar");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


        restore.setOnClickListener(
                arg0 -> {
                    if (SDK_INT < 30)
                        startActivityForResult(fp, FILE_PICKER_CODE);
                    else filePicker.launch(new String[]{"*/*"});
                });

        addProject.setOnClickListener(
                arg0 -> createProject());
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
                        arg0 -> {
                            FileUtil.makeDir(projectsPath + name.getText().toString());
                            refreshList();
                            alertDialog.dismiss();
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
        if (SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                try {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                    intent.addCategory("android.intent.category.DEFAULT");
                    intent.setData(Uri.parse(String.format("package:%s", getApplicationContext().getPackageName())));
                    startActivity(intent);
                } catch (Exception e) {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                    startActivity(intent);
                }
            }

        } else {
            for (String i : perms) {
                if (ContextCompat.checkSelfPermission(this, i) == PackageManager.PERMISSION_DENIED) {
                    final AlertDialog dialog = new AlertDialog.Builder(this).create();
                    dialog.setTitle("Warning !");
                    dialog.setMessage("Give the the necessary permissions...");
                    dialog.setButton(
                            AlertDialog.BUTTON_POSITIVE,
                            "Ok",
                            (arg0, arg1) -> {
                                ActivityCompat.requestPermissions(
                                        ProjectsActivity.this, perms, 1000);
                                dialog.dismiss();
                            });
                    dialog.setCancelable(false);
                    dialog.show();
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
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
                if (data != null) {
                    if (data.getClipData() != null) {
                        for (int _index = 0; _index < data.getClipData().getItemCount(); _index++) {
                            ClipData.Item _item = data.getClipData().getItemAt(_index);
                            if (code == ExportDialog.RECIEVE_ICON) {
                                ExportDialog.imgPath = FileUtil.convertUriToFilePath(getApplicationContext(), _item.getUri());
                                return;
                            } else if (code == FILE_PICKER_CODE) {
                                try {
                                    restoreProject(getContentResolver().openInputStream(_item.getUri()));
                                } catch(Exception ex){}
                            }
                            /*_filePath.add(
                                    FileUtil.convertUriToFilePath(
                                            getApplicationContext(), _item.getUri()));*/
                        }
                    } else {
                        if (code == ExportDialog.RECIEVE_ICON) {
                                ExportDialog.imgPath = FileUtil.convertUriToFilePath(getApplicationContext(), data.getData());
                                return;
                        } else if (code == FILE_PICKER_CODE) {
                            try {
                                restoreProject(getContentResolver().openInputStream(data.getData()));
                            } catch(Exception ex){}
                        }
                        /*_filePath.add(
                                FileUtil.convertUriToFilePath(
                                        getApplicationContext(), data.getData()));*/
                                        
                    }
                }
            // onFilePicked....
           /* if (code == ExportDialog.RECIEVE_ICON) {
                //Utils.showMessage(this,_filePath.get(0));
                Utils.setImageFromFile(ExportDialog.icon, _filePath.get(0));
                ExportDialog.imgPath = _filePath.get(0);
                return;
            }
            
            if (code == FILE_PICKER_CODE) {
                String err = "";
                for (String f : _filePath)
                    try {
                        restoreProject(
                                getContentResolver()
                                        .openInputStream(Uri.fromFile(new java.io.File(f))));
                    } catch (Exception e) {
                        err = err + "\n File : " + f + " - error : \n" + e;
                    }
                if (!err.equals("")) Utils.showMessage(ProjectsActivity.this, err);
            }*/
        }
    }

    public void restoreProject(InputStream inputStream) {
        final AlertDialog dialog = new AlertDialog.Builder(ProjectsActivity.this).create();
        dialog.setView(new ProgressBar(ProjectsActivity.this));
        dialog.setCancelable(false);
        dialog.show();
        new Thread() {
            @Override
            public void run() {
                final StringBuilder err = new StringBuilder();
                final String restoreP =
                        projectsPath;
                try {
                    Utils.unzipf(inputStream, restoreP, "");
                } catch (Exception ex) {
                    err.append(ex);
                }

                new Handler(Looper.getMainLooper())
                        .post(
                                () -> {
                                    dialog.dismiss();
                                    AlertDialog dialog1 =
                                            new AlertDialog.Builder(ProjectsActivity.this)
                                                    .create();
                                    dialog1.setCancelable(true);
                                    TextView text = new TextView(ProjectsActivity.this);
                                    text.setPadding(8, 8, 8, 8);
                                    // text.setBackgroundColor(Color.WHITE);
                                    text.setTextColor(Color.BLACK);
                                    text.setText(
                                            err.toString().equals("")
                                                    ? ("restored...")
                                                    : err.toString());
                                    dialog1.setView(text);
                                    dialog1.show();
                                    refreshList();
                                });
            }
        }.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        settings = EngineSettings.get().getString("lang", "") + EngineSettings.get().getBoolean("night", false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (goToSettings) {
            String re = EngineSettings.get().getString("lang", "") + EngineSettings.get().getBoolean("night", false);
            if (!re.equals(settings)) {
                Utils.setLanguage(this);
                recreate();
                settings = re;
                goToSettings = false;
            }
        }
    }
}
