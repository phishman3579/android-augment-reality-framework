package com.jwetherell.augmented_reality.ui.objects;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class PaintableIcon extends PaintableObject {
    private Bitmap bitmap=null;

    public PaintableIcon(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
    
    @Override
    public float getWidth() {
        return bitmap.getWidth();
    }

    @Override
    public float getHeight() {
        return bitmap.getHeight();
    }

    @Override
    public void paint(Canvas canvas) {
        paintBitmap(canvas, bitmap, 0, 0);
    }
}
