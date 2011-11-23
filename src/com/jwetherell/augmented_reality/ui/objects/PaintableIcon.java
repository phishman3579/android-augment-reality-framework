package com.jwetherell.augmented_reality.ui.objects;

import android.graphics.Bitmap;
import android.graphics.Canvas;


/**
 * This class extends PaintableObject to draw an icon.
 * 
 * @author Justin Wetherell <phishman3579@gmail.com>
 */
public class PaintableIcon extends PaintableObject {
    private Bitmap bitmap=null;

    public PaintableIcon(Bitmap bitmap, int width, int height) {
    	set(bitmap,width,height);
    }

    /**
     * Set the bitmap. This should be used instead of creating new objects.
     * @param bitmap Bitmap that should be rendered.
     * @throws NullPointerException if Bitmap is NULL.
     */
    public void set(Bitmap bitmap, int width, int height) {
    	if (bitmap==null) throw new NullPointerException();
    	
        this.bitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
    }

	/**
	 * {@inheritDoc}
	 */
	@Override
    public void paint(Canvas canvas) {
    	if (canvas==null || bitmap==null) throw new NullPointerException();

        paintBitmap(canvas, bitmap, -(bitmap.getWidth()/2), -(bitmap.getHeight()/2));
    }

	/**
	 * {@inheritDoc}
	 */
	@Override
    public float getWidth() {
        return bitmap.getWidth();
    }

	/**
	 * {@inheritDoc}
	 */
	@Override
    public float getHeight() {
        return bitmap.getHeight();
    }
}
