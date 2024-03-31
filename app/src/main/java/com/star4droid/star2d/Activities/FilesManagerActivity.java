package com.star4droid.star2d.Activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.star4droid.star2d.Helpers.FileUtil;
import com.star4droid.star2d.Helpers.UriUtils;
import com.star4droid.star2d.Utils;
import com.star4droid.star2d.evo.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class FilesManagerActivity extends AppCompatActivity {

    public final int REQ_CD_FP_IMGS = 101;
    public final int REQ_CD_FP_FILES = 102;
    public final int REQ_CD_FP_SOUNDS = 103;
    private final ArrayList<HashMap<String, Object>> lm = new ArrayList<>();
    private final ArrayList<String> interalPath = new ArrayList<>();
    private final Intent fp_imgs = new Intent(Intent.ACTION_GET_CONTENT);
    private final Intent fp_files = new Intent(Intent.ACTION_GET_CONTENT);
    private final Intent fp_sounds = new Intent(Intent.ACTION_GET_CONTENT);
    private final Intent intent = new Intent();
    ActivityResultLauncher<String[]> files_picker, sounds_picker;
    ActivityResultLauncher<PickVisualMediaRequest> pickMedia;
    private String path = "";
    private String types_x = "";
    private FrameLayout linear7;
    private ImageView add;
    private LinearLayout linear1;
    private LinearLayout linear2;
    private LinearLayout linear5;
    private ImageView select_files;
    private LinearLayout linear3;
    private ImageView select_images;
    private LinearLayout linear4;
    private ImageView select_anims;
    private LinearLayout linear8;
    private ImageView select_sounds;
    private LinearLayout filesLin;
    private LinearLayout empty;
    private ListView listview1;
    private TextView textview1;
    private TextView textview2;
    private AlertDialog.Builder dll;
    private MediaPlayer mp;

    @Override
    protected void onCreate(Bundle _savedInstanceState) {
        super.onCreate(_savedInstanceState);
        Utils.setLanguage(this);
        setContentView(R.layout.files_manager);
        initialize();

        pickMedia =
                registerForActivityResult(new ActivityResultContracts.PickMultipleVisualMedia(), uriList -> {
                    if (uriList == null) return;
                    StringBuilder p = new StringBuilder();
                    for (String s : interalPath) p.append(s).append("/");
                    for (Uri uri : uriList) {
                        String s = FileUtil.convertUriToFilePath(FilesManagerActivity.this, uri);
                        String to = path.concat("/".concat(types_x.concat("/").concat(p.toString()))).concat(Uri.parse(s).getLastPathSegment());
                        UriUtils.copyUriToUri(FilesManagerActivity.this, uri, Uri.fromFile(new File(to)));
                        Log.d("Da", "Copying Data");
                    }
                    _refresh();
                });


        files_picker = registerForActivityResult(new ActivityResultContracts.OpenMultipleDocuments(), uriList -> {
            if (uriList == null) return;
            String p = "";
            for (String s : interalPath) p = p + s + "/";
            for (Uri uri : uriList) {
                String s = FileUtil.convertUriToFilePath(FilesManagerActivity.this, uri);
                String to = path.concat("/".concat(types_x.concat("/").concat(p))).concat(Uri.parse(s).getLastPathSegment());
                UriUtils.copyUriToUri(FilesManagerActivity.this, uri, Uri.fromFile(new File(to)));
            }
            _refresh();
        });

        sounds_picker = registerForActivityResult(new ActivityResultContracts.OpenMultipleDocuments(), new ActivityResultCallback<List<Uri>>() {
            @Override
            public void onActivityResult(List<Uri> uriList) {
                if (uriList == null) return;
                String p = "";
                for (String s : interalPath) p = p + s + "/";
                for (Uri uri : uriList) {
                    String s = FileUtil.convertUriToFilePath(FilesManagerActivity.this, uri);
                    String to = path.concat("/".concat(types_x.concat("/").concat(p))).concat(Uri.parse(s).getLastPathSegment());
                    UriUtils.copyUriToUri(FilesManagerActivity.this, uri, Uri.fromFile(new File(to)));
                }
                _refresh();
            }
        });

        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
                    || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1000);
            } else {
                initializeLogic();
            }
        } else {
            initializeLogic();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1000) {
            initializeLogic();
        }
    }

    private void initialize() {
        linear7 = findViewById(R.id.linear7);
        add = findViewById(R.id.add);
        linear1 = findViewById(R.id.linear1);
        linear2 = findViewById(R.id.linear2);
        linear5 = findViewById(R.id.linear5);
        select_files = findViewById(R.id.select_files);
        linear3 = findViewById(R.id.linear3);
        select_images = findViewById(R.id.select_images);
        linear4 = findViewById(R.id.linear4);
        select_anims = findViewById(R.id.select_anims);
        linear8 = findViewById(R.id.linear8);
        select_sounds = findViewById(R.id.select_sounds);
        filesLin = findViewById(R.id.filesLin);
        empty = findViewById(R.id.empty);
        listview1 = findViewById(R.id.listview1);
        textview1 = findViewById(R.id.textview1);
        textview2 = findViewById(R.id.textview2);
        fp_imgs.setType("image/*");
        fp_imgs.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        fp_files.setType("*/*");
        fp_files.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        fp_sounds.setType("audio/*");
        fp_sounds.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        dll = new AlertDialog.Builder(this);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View _view) {
                if (types_x.contains("anims") || types_x.contains("images")) {
                    final AlertDialog cd = new AlertDialog.Builder(FilesManagerActivity.this).create();
                    LayoutInflater cdLI = getLayoutInflater();
                    View cdCV = cdLI.inflate(R.layout.create_dialog, null);
                    cd.setView(cdCV);
                    final TextView title = (TextView)
                            cdCV.findViewById(R.id.title);
                    final EditText name = (EditText)
                            cdCV.findViewById(R.id.name);
                    final Button add = (Button)
                            cdCV.findViewById(R.id.add);
                    final TextView imp = (TextView)
                            cdCV.findViewById(R.id.imp);
                    cd.show();
                    title.setText(types_x.contains("images") ? "Add Folder" : "Add Animation");
                    name.setText("");
                    name.setHint(types_x.contains("images") ? "Folder Name..." : "Animation Name...");
                    imp.setVisibility(View.VISIBLE);
                    if (types_x.contains("images")) {
                        imp.setText("import images");
                    }
                    add.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View _view) {
                            if (types_x.contains("anims")) {
                                FileUtil.writeFile(path.concat("/".concat(types_x.concat("/"))).concat(name.getText().toString()), "");
                            } else {
                                FileUtil.makeDir(path.concat("/".concat(types_x.concat("/"))).concat(name.getText().toString()));
                            }
                            _refresh();
                            cd.dismiss();
                        }
                    });
                    imp.setOnClickListener(_view1 -> {
                        if (types_x.contains("images")) {
                            if (Build.VERSION.SDK_INT >= 30)
                                pickMedia.launch(new PickVisualMediaRequest.Builder()
                                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                                        .build());

                            else startActivityForResult(fp_imgs, REQ_CD_FP_IMGS);
                        } else {
                            if (Build.VERSION.SDK_INT >= 30)
                                files_picker.launch(new String[]{"*/*"});
                            else startActivityForResult(fp_files, REQ_CD_FP_FILES);
                        }
                        cd.dismiss();
                    });
                } else {
                    if (types_x.contains("files")) {
                        if (Build.VERSION.SDK_INT >= 30) files_picker.launch(new String[]{"*/*"});
                        else startActivityForResult(fp_files, REQ_CD_FP_FILES);
                    } else {
                        if (types_x.contains("sounds")) {
                            if (Build.VERSION.SDK_INT >= 30)
                                sounds_picker.launch(new String[]{"audio/*"});
                            else startActivityForResult(fp_sounds, REQ_CD_FP_SOUNDS);
                        }
                    }
                }
            }
        });

        select_files.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View _view) {
                types_x = "files";
                _refresh();
            }
        });

        select_images.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View _view) {
                types_x = "images";
                interalPath.clear();
                _refresh();
            }
        });

        select_anims.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View _view) {
                types_x = "anims";
                _refresh();
            }
        });

        select_sounds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View _view) {
                types_x = "sounds";
                _refresh();
            }
        });
    }

    private void initializeLogic() {
        linear2.setBackground(new GradientDrawable() {
            public GradientDrawable getIns(int a, int b, int c, int d) {
                this.setCornerRadius(a);
                this.setStroke(b, c);
                this.setColor(d);
                return this;
            }
        }.getIns(25, 1, 0xFFFFB300, 0xFF3C3C3C));
        add.setBackground(new GradientDrawable() {
            public GradientDrawable getIns(int a, int b) {
                this.setCornerRadius(a);
                this.setColor(b);
                return this;
            }
        }.getIns(25, 0xFF0078FE));
        path = getIntent().getStringExtra("path");
    }

    @Override
    protected void onActivityResult(int _requestCode, int _resultCode, Intent _data) {
        super.onActivityResult(_requestCode, _resultCode, _data);

        switch (_requestCode) {
            case REQ_CD_FP_IMGS:
            case REQ_CD_FP_FILES:
            case REQ_CD_FP_SOUNDS:
                if (_resultCode == AppCompatActivity.RESULT_OK) {
                    ArrayList<String> _filePath = Utils.getArrayList(_requestCode, _resultCode, _data, FilesManagerActivity.this);
                    String p = "";
                    for (String s : interalPath) p = p + "/" + s;
                    for (String s : _filePath) {
                        FileUtil.copyFile(s, path.concat("/".concat(types_x.concat("/").concat(p))).concat(Uri.parse(s).getLastPathSegment()));
                    }
                    _refresh();
                } else {

                }
                break;
			/*
			case REQ_CD_FP_FILES:
			if (_resultCode == AppCompatActivity.RESULT_OK) {
				ArrayList<String> _filePath = Utils.getArrayList(_requestCode,_resultCode,_data,FilesManagerActivity.this);
				String p="";
				for(String s:interalPath) p = p+"/"+s;
				for(String s:_filePath){
					FileUtil.copyFile(s, path.concat("/".concat(types_x.concat("/"))).concat(p).concat(Uri.parse(s).getLastPathSegment()));
				}
				_refresh();
			}

			break;

			case REQ_CD_FP_SOUNDS:
			if (_resultCode == AppCompatActivity.RESULT_OK) {
				ArrayList<String> _filePath = Utils.getArrayList(_requestCode,_resultCode,_data,FilesManagerActivity.this);
				String p="";
				for(String s:interalPath) p = p+"/"+s;
				for(String s:_filePath){
					FileUtil.copyFile(s, path.concat("/".concat(types_x.concat("/"))).concat(p).concat(Uri.parse(s).getLastPathSegment()));
				}
				_refresh();
			}
			else {

			}
			break;
			*/
            default:
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        _refresh();
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            mp.pause();
            mp.release();
        } catch (Exception e) {

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        try {
            mp.pause();
            mp.release();
        } catch (Exception e) {

        }
        finish();
    }

    public void _refresh() {
        if (lm.size() == 0) {
            listview1.setAdapter(new Listview1Adapter(lm));
        }
        lm.clear();
        ArrayList<String> arr = new ArrayList<>();
        if (types_x.equals("")) types_x = "images";
        String p = "";
        if (types_x.equals("images")) {
            for (String s : interalPath) p = p + "/" + s;
        }
        FileUtil.listDir(path.concat("/".concat(types_x.concat(p))), arr);
        FileUtil.makeDir(path + "/" + types_x + p);
        Collections.sort(arr);
        if ((interalPath.size() > 0) && types_x.contains("images")) {
            {
                HashMap<String, Object> _item = new HashMap<>();
                _item.put("file", "...");
                lm.add(_item);
            }

        }
        for (String s : arr) {
            {
                HashMap<String, Object> _item = new HashMap<>();
                _item.put("file", s);
                lm.add(_item);
            }

        }
        if (0 == lm.size()) {
            filesLin.setVisibility(View.GONE);
        } else {
            filesLin.setVisibility(View.VISIBLE);
        }
        if (types_x.contains("images")) {
            android.graphics.drawable.GradientDrawable id = new android.graphics.drawable.GradientDrawable(android.graphics.drawable.GradientDrawable.Orientation.RIGHT_LEFT, new int[]{0xFFFF9C08, 0xFFEF544F});
            id.setCornerRadii(new float[]{(float) (0), (float) (0), (float) (0), (float) (0), (float) (0), (float) (0), (float) (0), (float) (0)}); //LeftTop, //RightTop, //RightBottom, //LeftBottom,
            select_images.setBackground(id);
        } else {
            select_images.setBackgroundColor(Color.TRANSPARENT);
        }
        if (types_x.contains("anims")) {
            android.graphics.drawable.GradientDrawable id = new android.graphics.drawable.GradientDrawable(android.graphics.drawable.GradientDrawable.Orientation.RIGHT_LEFT, new int[]{0xFFFF9C08, 0xFFEF544F});
            id.setCornerRadii(new float[]{(float) (0), (float) (0), (float) (0), (float) (0), (float) (0), (float) (0), (float) (0), (float) (0)}); //LeftTop, //RightTop, //RightBottom, //LeftBottom,
            select_anims.setBackground(id);
        } else {
            select_anims.setBackgroundColor(Color.TRANSPARENT);
        }
        if (types_x.contains("files")) {
            android.graphics.drawable.GradientDrawable id = new android.graphics.drawable.GradientDrawable(android.graphics.drawable.GradientDrawable.Orientation.RIGHT_LEFT, new int[]{0xFFFF9C08, 0xFFEF544F});
            id.setCornerRadii(new float[]{(float) (25), (float) (25), (float) (25), (float) (25), (float) (0), (float) (0), (float) (0), (float) (0)}); //LeftTop, //RightTop, //RightBottom, //LeftBottom,
            select_files.setBackground(id);
        } else {
            select_files.setBackgroundColor(Color.TRANSPARENT);
        }
        if (types_x.contains("sounds")) {
            android.graphics.drawable.GradientDrawable id = new android.graphics.drawable.GradientDrawable(android.graphics.drawable.GradientDrawable.Orientation.RIGHT_LEFT, new int[]{0xFFFF9C08, 0xFFEF544F});
            id.setCornerRadii(new float[]{(float) (0), (float) (0), (float) (0), (float) (0), (float) (25), (float) (25), (float) (25), (float) (25)}); //LeftTop, //RightTop, //RightBottom, //LeftBottom,
            select_sounds.setBackground(id);
        } else {
            select_sounds.setBackgroundColor(Color.TRANSPARENT);
        }
        ((BaseAdapter) listview1.getAdapter()).notifyDataSetChanged();
    }


    public String _remove_last_from_path(final String _path) {
        String st = "";
        ArrayList<String> lsts = new ArrayList<>();
        try {
            String[] lines = _path.concat("/").split("/");
            Collections.addAll(lsts, lines);
        } catch (Exception e8727) {
        }
        lsts.remove(lsts.size() - 1);
        for (String hh : lsts) {
            if (FileUtil.isDirectory(hh)) {
                if (st == "") {
                    st = hh;
                } else {
                    st = st + "/" + hh;
                }
            } else {
                if (st == "") {
                    st = hh;
                } else {
                    st = st + "/" + hh;
                }
            }
        }
        return (st);
    }


    public void _setImageFromFile(final ImageView _img, final String _path) {
        java.io.File file = new java.io.File(_path);
        Uri imageUri = Uri.fromFile(file);

        Glide.with(getApplicationContext()).asBitmap().load(imageUri).into(_img);
        /**/
		/*

		class th extends Thread {
			@Override
			public void run(){
				final Bitmap bit = FileUtil.decodeSampleBitmapFromPath(_path, 1024, 1024);
				new Handler(Looper.getMainLooper()).post(new Runnable() {
					@Override
					public void run() {
						_img.setImageBitmap(bit);
					}
				});
			}
		}
		new th().start();
		*/
    }

    public String _path(final String _path, final String _txt1, final String _txt2) {
        long d = (new java.io.File(_path)).lastModified();
        java.text.SimpleDateFormat sdate = new java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault());
        java.text.SimpleDateFormat stime = new java.text.SimpleDateFormat("hh:mm aa", java.util.Locale.getDefault());
        java.util.Date date = new java.util.Date(d);
        return (_txt1.concat(stime.format(date).concat(_txt2.concat(sdate.format(date)))));
    }

    public class Listview1Adapter extends BaseAdapter {

        ArrayList<HashMap<String, Object>> _data;

        public Listview1Adapter(ArrayList<HashMap<String, Object>> _arr) {
            _data = _arr;
        }

        @Override
        public int getCount() {
            return _data.size();
        }

        @Override
        public HashMap<String, Object> getItem(int _index) {
            return _data.get(_index);
        }

        @Override
        public long getItemId(int _index) {
            return _index;
        }

        @Override
        public View getView(final int _position, View _v, ViewGroup _container) {
            LayoutInflater _inflater = getLayoutInflater();
            View _view = _v;
            if (_view == null) {
                _view = _inflater.inflate(R.layout.list_csv, null);
            }

            final LinearLayout linear1 = _view.findViewById(R.id.linear1);
            final ImageView imageview1 = _view.findViewById(R.id.imageview1);
            final TextView name = _view.findViewById(R.id.name);
            final LinearLayout delLin = _view.findViewById(R.id.delLin);
            final LinearLayout renameLin = _view.findViewById(R.id.backupLin);
            final LinearLayout export = _view.findViewById(R.id.export);
            final ImageView imageview2 = _view.findViewById(R.id.imageview2);
            final ImageView imageview3 = _view.findViewById(R.id.imageview3);
            final ImageView imageview4 = _view.findViewById(R.id.imageview4);

            linear1.setBackground(new GradientDrawable() {
                public GradientDrawable getIns(int a, int b, int c, int d) {
                    this.setCornerRadius(a);
                    this.setStroke(b, c);
                    this.setColor(d);
                    return this;
                }
            }.getIns(15, 0, 0xFFFFB300, 0xFF222222));
            if (types_x.contains("anim")) {
                export.setVisibility(View.VISIBLE);
                imageview4.setImageResource(R.drawable.backup);
            } else {
                export.setVisibility(View.GONE);
            }
            renameLin.setVisibility(_data.get(_position).get("file").toString().equals("...") ? View.GONE : View.VISIBLE);
            delLin.setBackground(new GradientDrawable() {
                public GradientDrawable getIns(int a, int b, int c, int d) {
                    this.setCornerRadius(a);
                    this.setStroke(b, c);
                    this.setColor(d);
                    return this;
                }
            }.getIns(15, 0, 0xFFFFB300, 0xFF515151));
            renameLin.setBackground(new GradientDrawable() {
                public GradientDrawable getIns(int a, int b, int c, int d) {
                    this.setCornerRadius(a);
                    this.setStroke(b, c);
                    this.setColor(d);
                    return this;
                }
            }.getIns(15, 0, 0xFFFFB300, 0xFF515151));
            export.setBackground(new GradientDrawable() {
                public GradientDrawable getIns(int a, int b, int c, int d) {
                    this.setCornerRadius(a);
                    this.setStroke(b, c);
                    this.setColor(d);
                    return this;
                }
            }.getIns(15, 0, 0xFFFFB300, 0xFF515151));
            delLin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View _view) {
                    AlertDialog.Builder dll = new AlertDialog.Builder(FilesManagerActivity.this);
                    dll.setTitle("Delete ");
                    dll.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface _dialog, int _which) {
                            FileUtil.deleteFile(_data.get(_position).get("file").toString());
                            _refresh();
                        }
                    });
                    dll.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface _dialog, int _which) {

                        }
                    });
                    dll.create().show();
                }
            });
            renameLin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View _view) {
                    final AlertDialog cd = new AlertDialog.Builder(FilesManagerActivity.this).create();
                    LayoutInflater cdLI = getLayoutInflater();
                    View cdCV = cdLI.inflate(R.layout.create_dialog, null);
                    cd.setView(cdCV);
                    final TextView nameT = name;
                    final TextView title = (TextView)
                            cdCV.findViewById(R.id.title);
                    final EditText name = (EditText)
                            cdCV.findViewById(R.id.name);
                    final Button add = (Button)
                            cdCV.findViewById(R.id.add);

                    cd.show();
                    add.setText("Rename");
                    title.setText("Edit Name");
                    name.setText(Uri.parse(_data.get(_position).get("file").toString()).getLastPathSegment());
                    name.setHint("File Name...");
                    add.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View _view) {
                            if ((!(Uri.parse(_data.get(_position).get("file").toString()).getLastPathSegment().equalsIgnoreCase(name.getText().toString())))) {
                                FileUtil.moveFile(_data.get(_position).get("file").toString(), _remove_last_from_path(_data.get(_position).get("file").toString()).concat("/".concat(name.getText().toString())));
                                _data.get(_position).put("file", _remove_last_from_path(_data.get(_position).get("file").toString()).concat("/".concat(name.getText().toString())));
                                if (types_x.contains("images")) {
									/*
									for(HashMap<String, Object> map : Utils.tempList){
										if (map.containsKey("image")) {
											String interal = map.containsKey("image_interal") ? map.get("image_interal").toString() : "";
											String p="";
											for(String s:interalPath) p = p+"/"+s;
											if (p.equals(interal)) {
												try {
													if (Uri.parse(map.get("image").toString()).getLastPathSegment().toLowerCase().equals(Uri.parse(_data.get(_position).get("file").toString()).getLastPathSegment().toLowerCase())) {
														map.put("image", _remove_last_from_path(_data.get(_position).get("file").toString()).concat("/".concat(name.getText().toString())));
													}
													} catch (Exception e) {

												}
											}
										}
									}
									*/
                                }
                            }
                            nameT.setText(name.getText().toString());
                            cd.dismiss();
                        }
                    });
                }
            });
            imageview3.setImageResource(R.drawable.edit_icon);
            name.setText(Uri.parse(_data.get(_position).get("file").toString()).getLastPathSegment());
            if (types_x.contains("images")) {
                if (_data.get(_position).get("file").toString().equals("...")) {
                    imageview1.setImageResource(R.drawable.right_icon);
                    delLin.setVisibility(View.GONE);
                } else {
                    if (FileUtil.isDirectory(_data.get(_position).get("file").toString())) {
                        imageview1.setImageResource(R.drawable.ic_folder_white);
                    } else {
                        _setImageFromFile(imageview1, _data.get(_position).get("file").toString());
                    }
                    delLin.setVisibility(View.VISIBLE);
                }
                imageview1.setVisibility(View.VISIBLE);
            } else {
                /**/
				/*
				if (types_x.contains("sounds")) {

				}
				else {
					if (types_x.contains("anims")) {

					}
					else {

					}
				}
				*/
                delLin.setVisibility(View.VISIBLE);
            }
            linear1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View _view) {
                    if (types_x.contains("anim")) {
                        intent.setClass(getApplicationContext(), AnimationActivity.class);
                        intent.putExtra("path", _data.get(_position).get("file").toString());
                        intent.putExtra("imgs", path.concat("/images/"));
                        startActivity(intent);
                    } else {
                        if (types_x.contains("sounds")) {
                            try {
                                if (mp.isPlaying()) {
                                    mp.pause();
                                    try {
                                        mp.release();
                                    } catch (Exception e) {

                                    }
                                } else {
                                    mp = MediaPlayer.create(getApplicationContext(), Uri.fromFile(new java.io.File(_data.get(_position).get("file").toString())));
                                    mp.start();
                                }
                            } catch (Exception e) {
                                mp = MediaPlayer.create(getApplicationContext(), Uri.fromFile(new java.io.File(_data.get(_position).get("file").toString())));
                                mp.start();
                            }
                        }
                    }
                    if (types_x.contains("images")) {
                        if (_data.get(_position).get("file").toString().equals("...")) {
                            interalPath.remove((interalPath.size() - 1));
                            _refresh();
                        } else {
                            if (FileUtil.isDirectory(_data.get(_position).get("file").toString())) {
                                interalPath.add(Uri.parse(_data.get(_position).get("file").toString()).getLastPathSegment());
                                _refresh();
                            } else {

                            }
                        }
                    }
                }
            });
            export.setOnClickListener(_view1 -> {
                if (types_x.contains("anim")) {
                    FileUtil.writeFile(FileUtil.getExternalStorageDir().concat("/Star2D/".concat(Uri.parse(_data.get(_position).get("file").toString()).getLastPathSegment())), FileUtil.readFile(_data.get(_position).get("file").toString()));
                    dll.setTitle("Alert");
                    dll.setMessage("Exported successfully to : ".concat(FileUtil.getExternalStorageDir().concat("/Star2D/".concat(Uri.parse(_data.get(_position).get("file").toString()).getLastPathSegment()))));
                    dll.setPositiveButton("OK", (_dialog, _which) -> {

                    });
                    dll.create().show();
                }
            });
            if (!types_x.contains("images")) {
                imageview1.setVisibility(View.GONE);
            }

            return _view;
        }
    }
}