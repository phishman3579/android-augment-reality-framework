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

    public PaintableIcon(Bitmap bitmap) {
    	set(bitmap);
    }

    public void set(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    @Override
    public void paint(Canvas canvas) {
    	assert(canvas!=null && bitmap!=null);
    	
        paintBitmap(canvas, bitmap, 0, 0);
    }
    
    @Override
    public float getWidth() {
        return bitmap.getWidth();
    }

    @Override
    public float getHeight() {
        return bitmap.getHeight();
    }
}
