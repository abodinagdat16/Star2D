package com.star4droid.star2d.Helpers;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.widget.CheckBox;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

public class CheckBoxUtils {
	
	public static void setCheckMarkDrawable(Context context, AppCompatCheckBox checkBox, int checkDrawableResId, int uncheckedDrawableResId, int sizeInDp) {
		// Get the check mark drawable for the checked state
		Drawable checkDrawable = ContextCompat.getDrawable(context, checkDrawableResId);
		checkDrawable = resizeDrawable(checkDrawable, sizeInDp);
		
		// Get the check mark drawable for the unchecked state
		Drawable uncheckedDrawable = ContextCompat.getDrawable(context, uncheckedDrawableResId);
		uncheckedDrawable = resizeDrawable(uncheckedDrawable, sizeInDp);
		
		// Apply the custom drawables to the check box
		checkBox.setButtonDrawable(createCheckDrawable(checkDrawable, uncheckedDrawable));
	}
	
	private static Drawable createCheckDrawable(Drawable checkDrawable, Drawable uncheckedDrawable) {
		Drawable wrappedCheckDrawable = DrawableCompat.wrap(checkDrawable);
		DrawableCompat.setTintList(wrappedCheckDrawable, null);
		
		Drawable wrappedUncheckedDrawable = DrawableCompat.wrap(uncheckedDrawable);
		DrawableCompat.setTintList(wrappedUncheckedDrawable, null);
		
		return new CustomCheckDrawable(wrappedCheckDrawable, wrappedUncheckedDrawable);
	}
	
	private static Drawable resizeDrawable(Drawable drawable, int sizeInDp) {
		int sizeInPixels = (int) (sizeInDp * Resources.getSystem().getDisplayMetrics().density);
		drawable.setBounds(0, 0, sizeInPixels, sizeInPixels);
		return drawable;
	}
	
	public static void resizeCheckBox(CheckBox checkBox,int size){
		Drawable drawable= checkBox.getButtonDrawable();
		drawable.setBounds(0,0,size,size);
		checkBox.setButtonDrawable(drawable);
	}
	
	private static class CustomCheckDrawable extends Drawable {
		private Drawable checkDrawable;
		private Drawable uncheckedDrawable;
		
		CustomCheckDrawable(Drawable checkDrawable, Drawable uncheckedDrawable) {
			this.checkDrawable = checkDrawable;
			this.uncheckedDrawable = uncheckedDrawable;
		}
		
		@Override
		public void draw(Canvas canvas) {
			AppCompatCheckBox checkBox = (AppCompatCheckBox) getCallback();
			if (checkBox.isChecked()) {
				checkDrawable.draw(canvas);
				} else {
				uncheckedDrawable.draw(canvas);
			}
		}

		@Override
		public void setAlpha(int alpha) {
			checkDrawable.setAlpha(alpha);
			uncheckedDrawable.setAlpha(alpha);
		}
		
		@Override
		public void setColorFilter(ColorFilter colorFilter) {
			checkDrawable.setColorFilter(colorFilter);
			uncheckedDrawable.setColorFilter(colorFilter);
		}
		
		@Override
		public int getOpacity() {
			return PixelFormat.TRANSLUCENT;
		}
		
		@Override
		public void setBounds(int left, int top, int right, int bottom) {
			super.setBounds(left, top, right, bottom);
			checkDrawable.setBounds(left, top, right, bottom);
			uncheckedDrawable.setBounds(left, top, right, bottom);
		}
	}
}
