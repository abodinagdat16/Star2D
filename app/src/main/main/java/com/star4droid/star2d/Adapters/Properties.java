package com.star4droid.star2d.Adapters;

import android.animation.ValueAnimator;
import android.content.Context;
import android.view.View;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import androidx.core.widget.NestedScrollView;
import androidx.viewpager2.widget.ViewPager2;
import com.star4droid.star2d.Helpers.SwipeHelper;
import com.star4droid.star2d.evo.R;
import android.view.MotionEvent;

public class Properties {
	View view;
	LinearLayout propertiesLinear;
	ViewPager2 viewPager;
	public Properties(Context ctx){
		 view = LayoutInflater.from(ctx).inflate(R.layout.properties,null);
		//setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.MATCH_PARENT));
		view.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.MATCH_PARENT));
		//setBackgroundColor(0xFF313131);
		final View swipe = view.findViewById(R.id.properties_swip_image);
		propertiesLinear = view.findViewById(R.id.propertiesLinear);
		viewPager = view.findViewById(R.id.viewPager);
		view.findViewById(R.id.back).setOnClickListener(view-> {
				viewPager.setCurrentItem(0);
		});
		final NestedScrollView scrollView=view.findViewById(R.id.propertiesScroll);
		viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback(){
			@Override
			public void onPageSelected(int position) {
				super.onPageSelected(position);
				view.findViewById(R.id.backLin).setVisibility(position>0?View.VISIBLE:View.GONE);
				scrollView.smoothScrollTo(0,0);
			}
		});
		propertiesLinear.setLayoutParams(new LinearLayout.LayoutParams(1,ViewGroup.LayoutParams.WRAP_CONTENT));
		swipe.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v){
				
			}
		});
		SwipeHelper.useViewToSwipe(swipe,propertiesLinear,SwipeHelper.SwipeType.LEFT_RIGHT,1,Integer.MAX_VALUE);
		
	}
	
	public LinearLayout getPropertiesLinear(){
		return propertiesLinear;
	}
	
	public ViewPager2 getViewPager(){
		return viewPager;
	}
	
	public View getView(){
		return view;
	}
}