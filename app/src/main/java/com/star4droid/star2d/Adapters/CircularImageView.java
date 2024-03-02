package com.star4droid.star2d.Adapters;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.content.Context;
import com.star4droid.star2d.evo.R;

public class CircularImageView extends ImageView {
	
	public CircularImageView(Context context,AttributeSet attributeSet){
		super(context,attributeSet);
	}
	public CircularImageView(Context ctx){
		super(ctx);
		setImageResource(R.drawable.icon);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		
		Drawable drawable = getDrawable();
		
		if (drawable == null) {
			return;
		}
		
		if (getWidth() == 0 || getHeight() == 0) {
			return;
		}
		Bitmap b = ((BitmapDrawable) drawable).getBitmap();
		Bitmap bitmap = b.copy(Bitmap.Config.ARGB_8888, true);
		
		int w = getWidth(), h = getHeight();
		
		Bitmap roundBitmap = getRoundedCroppedBitmap(bitmap, w);
		canvas.drawBitmap(roundBitmap, 0, 0, null);
		
	}
	 
	public Bitmap getRoundedCroppedBitmap(Bitmap bitmap, int radius) {
		Bitmap finalBitmap;
		if (bitmap.getWidth() != radius || bitmap.getHeight() != radius)
		finalBitmap = Bitmap.createScaledBitmap(bitmap, radius, radius,
		false);
		else
		finalBitmap = bitmap;
		Bitmap output = Bitmap.createBitmap(finalBitmap.getWidth(),
		finalBitmap.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, finalBitmap.getWidth(),
		finalBitmap.getHeight());
		
		paint.setAntiAlias(true);
		paint.setFilterBitmap(true);
		paint.setDither(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(Color.parseColor("#BAB399"));
		canvas.drawCircle(finalBitmap.getWidth() / 2 + 0.7f,
		finalBitmap.getHeight() / 2 + 0.7f,
		finalBitmap.getWidth() / 2 + 0.1f, paint);
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		canvas.drawBitmap(finalBitmap, rect, rect, paint);
		
		return output;
	}
	
}