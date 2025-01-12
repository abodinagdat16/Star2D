package com.star4droid.star2d.Helpers;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class ColorWheelView extends View {
	private Paint colorWheelPaint,CirclePaint;
	private int selectedColor,alpha=255;
	private float lastX=0,lastY=0;
	Bitmap bitmap;
	WheelEventListener wheelEvent;
	
	// Constructor
	public ColorWheelView(Context context) {
		super(context);
		init();
	}
	
	public ColorWheelView(Context context,AttributeSet attributeSet){
		super(context,attributeSet);
		init();
	}
	
	public ColorWheelView(Context context,AttributeSet attributeSet,int i){
		super(context,attributeSet,i);
		init();
	}
	
	public void setAlphaValue(int value){
		if(value>=0||value<=255)
			alpha=value;
		bitmap = createColorWheelBitmap();
	}
	
	// Initialize the View
	private void init() {
		colorWheelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		colorWheelPaint.setStyle(Paint.Style.FILL);
		colorWheelPaint.setStrokeWidth(1f);
		selectedColor = Color.RED;
		CirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		CirclePaint.setStyle(Paint.Style.STROKE);
		CirclePaint.setStrokeWidth(1f);
		CirclePaint.setColor(Color.WHITE);
	}
	
	// Set the selected color
	public void setSelectedColor(int color) {
		selectedColor = color;
		invalidate(); // Refresh the View
	}
	
	// Get the selected color
	public int getSelectedColor() {
		return selectedColor;
	}
	
	public static String getColorHex(int color) {
		String hex = String.format("#%06X", (0xFFFFFF & color));
		return hex;
	}
	
	public String getSelectedHex(){
		return getColorHex(selectedColor);
	}
	
	@Override
	protected void onSizeChanged(int x,int y,int z,int w){
		super.onSizeChanged(x,y,z,w);
		bitmap=null;
		invalidate();
	}
	
	// Override the onDraw method to draw the Color Wheel
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if(bitmap==null) bitmap=createColorWheelBitmap();
		canvas.drawBitmap(bitmap,0,0,null);
		canvas.drawCircle(lastX,lastY,25,CirclePaint);
	}
	
	private Bitmap createColorWheelBitmap() {
		if(bitmap!=null) bitmap.recycle();
		int width = getWidth();
		int height = getHeight();
		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		
		float centerX = width / 2f;
		float centerY = height / 2f;
		float radius = Math.min(centerX, centerY) - 20;
		
		// Draw the color wheel
		for (float angle = 0; angle < 360; angle ++) {
			int color=Color.HSVToColor(new float[]{angle, 1f, 1f});
			
			/*
			int a = alpha; //Color.alpha(color);
			int r = Color.red(color);
			int g = Color.green(color);
			int b = Color.blue(color);
			*/
			
			colorWheelPaint.setColor(color);
			canvas.drawArc(centerX - radius, centerY - radius, centerX + radius, centerY + radius,
			angle, 1, true, colorWheelPaint);
		}
		
		return bitmap;
	}
	
	// Override the onTouchEvent method to handle touch events
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();
		float centerX = getWidth() / 2f;
		float centerY = getHeight()/ 2f;
		float radius = Math.min(centerX, centerY) - 20;
		
		// Calculate the distance from the touch point to the center of the color wheel
		float distance = (float) Math.sqrt(Math.pow(x - centerX, 2) + Math.pow(y - centerY, 2));
		
		// If the touch point is within the color wheel, update the selected color
		if (distance <= radius) {
			// Calculate the angle of the selected point
			float angle = (float) Math.toDegrees(Math.atan2(y - centerY, x - centerX));
			if (angle < 0) {
				angle += 360;
			}
			
			int color=Color.HSVToColor(new float[]{angle, 1f, 1f});
			// Set the selected color based on the angle
			
			/*
			int a = alpha; //Color.alpha(color);
			int r = Color.red(color);
			int g = Color.green(color);
			int b = Color.blue(color);
			*/
			
			setSelectedColor(color);
			lastX = x;
			lastY = y;
			// Call the onColorSelected method
			if(wheelEvent!=null) wheelEvent.onColorChanged(selectedColor);
			if(event.getAction()==MotionEvent.ACTION_UP&&wheelEvent!=null) wheelEvent.onColorSelected(selectedColor);
			
			return true;
		}
		
		return super.onTouchEvent(event);
	}
	
	public ColorWheelView setWheelEventListener(WheelEventListener wheelEventListener){
		wheelEvent = wheelEventListener;
		return this;
	}
	
	public interface WheelEventListener {
		public void onColorChanged(int color);
		public void onColorSelected(int color);
	}
	
}