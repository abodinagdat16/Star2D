package com.star4droid.star2d.Items;

import android.content.Context;
import android.graphics.PointF;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import com.star4droid.star2d.evo.R;
import android.view.View;
import android.widget.LinearLayout;

public class PointPicker {
	View view;
	public PointPicker(Context context,Editor editor){
		view = LayoutInflater.from(context).inflate(R.layout.editor_point_picker,null);
		view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));
		view.setOnTouchListener(new View.OnTouchListener() {
			PointF DownPT = new PointF();
			PointF StartPT = new PointF();
			PointF mv = new PointF();
			@Override public boolean onTouch(View _view, MotionEvent event) {
				switch (event.getAction()) {
					case MotionEvent.ACTION_MOVE:
					mv.set(event.getX() - DownPT.x, event.getY() - DownPT.y);
					_view.setX(StartPT.x+mv.x-100);
					_view.setY(StartPT.y+mv.y-100);
					StartPT = new PointF(_view.getX(), _view.getY());
					break; //xenon
					case MotionEvent.ACTION_DOWN : DownPT.x = event.getX();
					DownPT.y = event.getY();
					StartPT = new PointF(_view.getX(), _view.getY());
					break;
					case MotionEvent.ACTION_UP : break;
					default : break;
				} return true;
		}});
		view.findViewById(R.id.pick).setOnClickListener(v->{
			float editorScale = editor.getEditorScale();
			float measuredWidth = editor.getScreenView().getMeasuredWidth() / 2.0f;
			float measuredHeight = editor.getScreenView().getMeasuredHeight() / 2.0f;
			float moveX = editor.getMoveX();
			float moveY = editor.getMoveY();
			float x = (((view.getX() - measuredWidth) / editorScale) + measuredWidth) - moveX;
			float y = (((view.getY() - measuredHeight) / editorScale) + measuredHeight) - moveY;
			onDone(x*0.1f,y*0.1f);
		});
	}
	public View getPointPicker(){
		view.setZ(999999);
		return view;
	}
	
	public void onDone(float x,float y){
		
	}
}