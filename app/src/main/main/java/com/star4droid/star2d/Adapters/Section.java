package com.star4droid.star2d.Adapters;

import android.animation.ValueAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.star4droid.star2d.Helpers.PropertySet;
import com.star4droid.star2d.evo.R;

public class Section {
	
	ViewGroup linear,parent;
	View view;
	public Section(String name,ViewGroup p){
		view = LayoutInflater.from(p.getContext()).inflate(R.layout.section,null);
		//view.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));
		View linear1= view.findViewById(R.id.linear1);
		final View show = view.findViewById(R.id.show);
		linear = view.findViewById(R.id.sec1);
		TextView text = view.findViewById(R.id.text);
		text.setText(name);
		this.parent = p;
		linear.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,1));
		linear1.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v){
				if(show.getRotation()==0){
					show.setRotation(180);
					expandView(linear);
				} else {
					show.setRotation(0);
					ValueAnimator va = ValueAnimator.ofInt(linear.getMeasuredHeight(),1);
					va.setDuration(300);
					va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(){
						@Override
						public void onAnimationUpdate(ValueAnimator va) {
						int v = (int)va.getAnimatedValue();
						linear.getLayoutParams().height = v;
						linear.setLayoutParams(linear.getLayoutParams());
						}
					});
					va.start();
				}
			}
		});
	}
	
	public void add(View v){
		linear.addView(v);
	}
	
	public Section setup(){
		view.setTag(this);
		/*if(parent.getChildCount()>0) */
		parent.addView(view);
		return this;
	}
	
	public void update(PropertySet propertySet){
		int vis = 0;
		for(int x=0;x<linear.getChildCount();x++){
			View v=linear.getChildAt(x);
			if(v.getTag()==null) continue;
			if(v.getTag() instanceof EditorField){
				boolean b=((EditorField)(v.getTag())).update(propertySet);
				if(b) vis++;
			}
			}
			view.setVisibility(vis>0?View.VISIBLE:View.GONE);
	}
	
	public static void expandView(final View view) {
		view.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		final int targetHeight = view.getMeasuredHeight();
		
		view.getLayoutParams().height = 0;
		view.setVisibility(View.VISIBLE);
		
		android.view.animation.Animation animation = new android.view.animation.Animation() {
			@Override
			protected void applyTransformation(float interpolatedTime, android.view.animation.Transformation transformation) {
				view.getLayoutParams().height = (int)(targetHeight * interpolatedTime);
				if((int)(targetHeight*interpolatedTime)==targetHeight) view.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
				view.requestLayout();
			}
			
			@Override
			public boolean willChangeBounds() {
				return true;
			}
		};
		
		animation.setDuration((int) (targetHeight / view.getContext().getResources().getDisplayMetrics().density));
		view.startAnimation(animation);
		}
}