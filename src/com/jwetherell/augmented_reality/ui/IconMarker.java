package com.jwetherell.augmented_reality.ui;

import com.jwetherell.augmented_reality.ui.objects.PaintableIcon;
import com.jwetherell.augmented_reality.ui.objects.PaintablePosition;
import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * This class extends Marker and draws an icon instead of a circle for it's visual representation.
 * 
 * @author Justin Wetherell <phishman3579@gmail.com>
 */
public class IconMarker extends Marker {
    private static final float[] symbolArray = new float[3];
    private Bitmap bitmap = null;
    private PaintableIcon icon = null;

    public IconMarker(String name, double latitude, double longitude, double altitude, int color, Bitmap bitmap) {
        super(name, latitude, longitude, altitude, color);
        this.bitmap = bitmap;
        icon = new PaintableIcon(bitmap,48,48);
    }

	/**
	 * {@inheritDoc}
	 */
    @Override
    public void drawIcon(Canvas canvas) {
    	if (canvas==null || bitmap==null) throw new NullPointerException();
    	
        float maxHeight = Math.round(canvas.getHeight() / 10f) + 1;
        
        symbolXyzRelativeToCameraView.get(symbolArray);
        if (symbolContainer==null) 
            symbolContainer = new PaintablePosition(icon, (symbolArray[0] - maxHeight/1.5f), (symbolArray[1] - maxHeight/1.5f), 0, 2);
        else 
            symbolContainer.set(icon, (symbolArray[0] - maxHeight/1.5f), (symbolArray[1] - maxHeight/1.5f), 0, 2);
        symbolContainer.paint(canvas);
    }
}
