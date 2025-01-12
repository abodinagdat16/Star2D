package com.star4droid.star2d.Adapters;

import android.content.Context;
import android.net.Uri;
import android.view.ViewGroup;
import android.view.View;

import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import androidx.appcompat.app.AlertDialog;
import com.star4droid.star2d.Helpers.FileUtil;
import com.star4droid.star2d.Items.Editor;
import com.star4droid.star2d.evo.R;
import com.star4droid.star2d.Utils;
import java.util.ArrayList;

public class ImagesSelectorAdapter extends BaseAdapter {
	ArrayList<String> imagesList;
	Context context;
	AlertDialog dialog;
	ArrayList<String> interals= new ArrayList<>();
	String currentPath="";
	public ImagesSelectorAdapter(Context ctx,ArrayList<String> arrayList,AlertDialog alertDialog){
		this.imagesList = arrayList;
		this.context = ctx;
		this.dialog = alertDialog;
	}
	
	public static void show(Context context,final onSelectImage onSelect,final Editor editor){
		AlertDialog dialog= new AlertDialog.Builder(context).create();
		GridView grid = new GridView(context);
		grid.setHorizontalSpacing(1);
		grid.setVerticalSpacing(1);
		grid.setNumColumns(4);
		grid.setAdapter(new ImagesSelectorAdapter(context,getImagesList(editor),dialog).setOnSelect(onSelect));
		dialog.setView(grid);
		Utils.hideSystemUi(dialog.getWindow());
		dialog.show();
	}
	
	public static ArrayList<String> getImagesList(Editor editor){
		ArrayList<String> filesList= new ArrayList<>();
		FileUtil.listDir(editor.getProject().getImagesPath(),filesList);
		return filesList;
	}
	
	@Override
	public int getCount() {
	    return imagesList.size();
	}

	@Override
	public Object getItem(int position) {
	    return imagesList.get(position);
	}

	@Override
	public long getItemId(int position) {
	    return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
	    ImageView view=null;
		try {
		view= (ImageView)convertView;
		} catch(Exception exception){}
		if(view==null){
			view = new ImageView(context);
			view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,250));
		}
		if(FileUtil.isFile(imagesList.get(position))) Utils.setImageFromFile(view,imagesList.get(position));
		else if(FileUtil.isDirectory(imagesList.get(position))){
			view.setImageDrawable(context.getDrawable(R.drawable.ic_filter_black));
		} else if(imagesList.get(position).equals("...")) view.setImageDrawable(context.getDrawable(R.drawable.right_black));
		view.setOnClickListener(new ImageView.OnClickListener(){
			@Override
			public void onClick(View arg0) {
				String path=imagesList.get(position);
				if(FileUtil.isDirectory(path)){
					interals.add(Uri.parse(path).getLastPathSegment());
					imagesList.clear();
					imagesList.add("...");
					FileUtil.listDir(path,imagesList);
					currentPath=path;
					notifyDataSetChanged();
					return;
				}
				if(path.equals("...")) {
					imagesList.clear();
					interals.remove(interals.size()-1);
					if(interals.size()!=0) interals.add("...");
					currentPath = Utils.removeLastFromPath(currentPath);
					FileUtil.listDir(currentPath,imagesList);
					notifyDataSetChanged();
					return;
				}
				String p="/";
				for(String in:interals) p+=in+Utils.seperator;
				if(onSelectImage!=null&&FileUtil.isFile(path)) onSelectImage.onSelect(currentPath+p+Uri.parse(path).getLastPathSegment(),dialog);
			}
		});
		return view;
	}
	
	onSelectImage onSelectImage;
	public ImagesSelectorAdapter setOnSelect(onSelectImage onselect){
		onSelectImage = onselect;
		return this;
	}
	
	public interface onSelectImage {
		public void onSelect(String path,AlertDialog dialog);
	}
}