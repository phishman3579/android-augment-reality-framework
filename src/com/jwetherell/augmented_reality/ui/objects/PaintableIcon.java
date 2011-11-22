package com.jwetherell.augmented_reality.ui.objects;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

/**
 * This class extends PaintableObject to draw an icon.
 * 
 * @author Justin Wetherell <phishman3579@gmail.com>
 */
public class PaintableIcon extends PaintableObject {
    private Bitmap bitmap=null;
    private int width = 0;
    private int height = 0;
    private Rect srcRect = new Rect();
    private Rect dstRect = new Rect();
    
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
    	
        this.bitmap = bitmap;
        this.width = width;
        this.height = height;
        srcRect.set(0, 0, bitmap.getWidth(), bitmap.getHeight());
        dstRect.set(0, 0, this.width, this.height);
    }

	/**
	 * {@inheritDoc}
	 */
	@Override
    public void paint(Canvas canvas) {
    	if (canvas==null || bitmap==null) throw new NullPointerException();

        paintBitmap(canvas, bitmap, srcRect, dstRect);
    }

	/**
	 * {@inheritDoc}
	 */
	@Override
    public float getWidth() {
        return width;
    }

	/**
	 * {@inheritDoc}
	 */
	@Override
    public float getHeight() {
        return height;
    }
}
