package com.star4droid.star2d.Helpers;

import android.animation.ValueAnimator;
import android.view.MotionEvent;
import android.view.View;

public class SwipeHelper {
	public static void useViewToSwipe(final View swipe,final View target,final SwipeType type,final int minValue,final int maxValue){
		swipe.setOnTouchListener(new View.OnTouchListener(){
			float initialTouchX=0;
			float initialTouchY=0;
			int initialX=0;
			int initialY;
			float startX=0,startY=0;
			public boolean onTouch(View view, MotionEvent motionEvent) {
				int n = motionEvent.getAction();
				if(n==MotionEvent.ACTION_DOWN){
					startX= motionEvent.getRawX();
					startY= motionEvent.getRawY();
				}
				
				if(n==MotionEvent.ACTION_UP){
					float x=startX-motionEvent.getRawX(), y=startY-motionEvent.getRawY();
					float dist = (float)Math.sqrt(x*x+y*y);
					if(dist<=50) {
						float ex = motionEvent.getX(),ey= motionEvent.getY(),vh = view.getMeasuredHeight(),vw = view.getMeasuredWidth();
						boolean bx = ((ex>=0)&&(ex<=vw));
						boolean by = ((ey>=0)&&(ey<=vh));
						if(!(bx&&by)) return false;
						final boolean left_right = type==SwipeType.LEFT_RIGHT||type==SwipeType.RIGHT_LEFT;
						int sw = (left_right)?target.getMeasuredWidth():target.getMeasuredHeight(),ew=(left_right)?view.getContext().getResources().getDisplayMetrics().widthPixels/4:view.getContext().getResources().getDisplayMetrics().heightPixels/3;
						if(left_right&&ew<=target.getMeasuredWidth()){
							ew=1;
						} else if(ew<=target.getMeasuredHeight()&&(!left_right)){
							ew=1;
						}
						ValueAnimator va = ValueAnimator.ofInt(sw,ew);
						va.setDuration(300);
						va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(){
							@Override
							public void onAnimationUpdate(ValueAnimator valueAnimator) {
								if(left_right){
								target.getLayoutParams().width = (int)valueAnimator.getAnimatedValue();
								} else target.getLayoutParams().height = (int)valueAnimator.getAnimatedValue();
								target.setLayoutParams(target.getLayoutParams());
							}
						});
						va.start();
					}
				}
				if (n != 0) {
					if (n != 2) {
						return true;
					}
					if(type==SwipeType.LEFT_RIGHT||type==SwipeType.RIGHT_LEFT){
						float div=(type==SwipeType.LEFT_RIGHT)?1:(-1);
						int xx = this.initialX + (int)((motionEvent.getRawX() - this.initialTouchX)*div);
						if(xx<minValue) xx=minValue;
						if(xx>maxValue) xx=maxValue;
						target.getLayoutParams().width = xx;
						target.setLayoutParams(target.getLayoutParams());
						} else {
						float div=(type==SwipeType.TOP_DOWN)?1:(-1);
						int yy = this.initialY + (int)((motionEvent.getRawY() - this.initialTouchY)*div);
						if(yy<minValue) yy=minValue;
						if(yy>maxValue) yy=maxValue;
						target.getLayoutParams().height = yy;
						target.setLayoutParams(target.getLayoutParams());
					}
					
					return true;
				}
				this.initialX = target.getMeasuredWidth();//start x
				this.initialY = target.getMeasuredHeight();
				this.initialTouchX = motionEvent.getRawX();
				this.initialTouchY = motionEvent.getRawY();
				return false;
			}
		});
	}
	
	public enum SwipeType {
		LEFT_RIGHT,RIGHT_LEFT,
		TOP_DOWN,DOWN_TOP
	}
}