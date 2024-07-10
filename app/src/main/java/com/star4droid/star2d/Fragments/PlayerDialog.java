package com.star4droid.star2d.Fragments;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidFragmentApplication;
import com.star4droid.star2d.Helpers.SwipeHelper;
import com.star4droid.star2d.Items.Editor;
import com.star4droid.star2d.evo.R;
import com.star4droid.template.Items.StageImp;


public class PlayerDialog extends AndroidFragmentApplication {
	String path,scene;
	StageImp stage;
	public PlayerDialog(){}
	public PlayerDialog(String path,String scene){
		this.path = path;
		this.scene = scene;
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
		if(stage==null) return;
		//stage.onPause();
		//stage.dispose();
		Editor.getCurrentEditor().linkTo(null);
	}
	@Override
	public View onCreateView(LayoutInflater arg0, ViewGroup arg1, Bundle arg2) {
		if(path!=null&&scene!=null){
			stage = StageImp.getFromDex(path,scene,null,null);
			Editor.getCurrentEditor().linkTo(stage);
		}
		return (stage==null)?null:initializeForView(stage);
	}
	
	@Override
	public void onSaveInstanceState(Bundle bundle) {
		super.onSaveInstanceState(bundle);
		if(bundle==null) return;
		bundle.putString("path",path);
		bundle.putString("scene",scene);
	}
	
	@Override
	public void onViewStateRestored(Bundle bundle) {
		super.onViewStateRestored(bundle);
		if(bundle==null) return;
		path = bundle.getString("path");
		scene = bundle.getString("scene");
	}
	
	public static class FragmentAdapter extends FragmentStateAdapter {
		String path,scene;
		public FragmentAdapter(AppCompatActivity ctx,String path,String scene){
			super(ctx);
			this.path = path;
			this.scene = scene;
		}
		@Override
        public Fragment createFragment(int position) {
            
            return new PlayerDialog(path,scene);
        }

        @Override
        public int getItemCount() {
            return 1;
        }
	}
	
	public static AlertDialog showFor(AppCompatActivity activity,String path,String scene){
		AlertDialog dialog = new AlertDialog.Builder(activity).create();
		View view = activity.getLayoutInflater().inflate(R.layout.player_dialog,null);
		dialog.setView(view);
		dialog.getWindow().setDimAmount(0);
		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS|WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
		SwipeHelper.dragDialogBy(view.findViewById(R.id.drag),dialog);
		SwipeHelper.setWH(dialog,ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
		ViewPager2 pager2 = view.findViewById(R.id.viewPager);
		int width = activity.getResources().getDisplayMetrics().heightPixels;
		int height = activity.getResources().getDisplayMetrics().widthPixels;
		pager2.setLayoutParams(new LinearLayout.LayoutParams((int)(height*0.5f),(int)(width*0.5f)));
		//view.findViewById(R.id.frame).setLayoutParams(new LinearLayout.LayoutParams((int)(height*0.5f),(int)(width*0.5f)));
		pager2.setAdapter(new FragmentAdapter(activity,path,scene));
		view.findViewById(R.id.close).setOnClickListener(v->{
			Gdx.app.exit();
			if(dialog.isShowing()) dialog.dismiss();
		});
		view.findViewById(R.id.play).setOnClickListener(v->{
			if(getStage().isPlaying())
			    getStage().Pause();
			else getStage().Resume();
			((android.widget.ImageView)v).setImageResource(getStage().isPlaying()?R.drawable.ic_pause_black:R.drawable.ic_play_arrow_black);
		});
		
		//SwipeHelper.scaleDialogBy(view.findViewById(R.id.scaler),dialog);
		SwipeHelper.scaleViewBy(view.findViewById(R.id.scaler),(dx,dy)->{
			try {
				getStage().getViewport().update(pager2.getMeasuredWidth(),pager2.getMeasuredHeight());
				//getStage().resize(pager2.getMeasuredWidth(),pager2.getMeasuredHeight());
			} catch(Exception e){}
		},pager2);
		dialog.show();
		return dialog;
	}
	
	private static StageImp getStage(){
	    return Editor.getCurrentEditor().getLink().getStage();
	}
}