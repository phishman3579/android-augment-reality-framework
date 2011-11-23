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

    public IconMarker(String name, double latitude, double longitude, double altitude, int color, Bitmap bitmap) {
        super(name, latitude, longitude, altitude, color);
        this.bitmap = bitmap;
    }

	/**
	 * {@inheritDoc}
	 */
    @Override
    public void drawIcon(Canvas canvas) {
    	if (canvas==null || bitmap==null) throw new NullPointerException();
    	
        if (gpsSymbol==null) gpsSymbol = new PaintableIcon(bitmap,96,96);
    	
        symbolXyzRelativeToCameraView.get(symbolArray);
        if (symbolContainer==null) 
            symbolContainer = new PaintablePosition(gpsSymbol, symbolArray[0], symbolArray[1], 0, 1);
        else 
            symbolContainer.set(gpsSymbol, symbolArray[0], symbolArray[1], 0, 1);
        symbolContainer.paint(canvas);
    }
}
