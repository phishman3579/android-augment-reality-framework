package com.jwetherell.augmented_reality.ui.objects;

import com.jwetherell.augmented_reality.data.ARData;
import com.jwetherell.augmented_reality.ui.Marker;
import com.jwetherell.augmented_reality.ui.Radar;

import android.graphics.Canvas;

/**
 * This class extends PaintableObject to draw all the Markers at their appropriate locations.
 * 
 * @author Justin Wetherell <phishman3579@gmail.com>
 */
public class PaintableRadarPoints extends PaintableObject {

    public void paint(Canvas canvas) {
        /** Radius is in KM. */
        float range = ARData.getRadius() * 1000;

        //Draw the markers in the circle
        float scale = range / Radar.RADIUS;
        for (int i = 0; i < ARData.getMarkerCount(); i++) {
            Marker pm = ARData.getMarker(i);
            float x = pm.getLocationVector().x / scale;
            float y = pm.getLocationVector().z / scale;
            if ((x*x+y*y)<(Radar.RADIUS*Radar.RADIUS)) {
                PaintablePoint paintablePoint = new PaintablePoint(pm.getColor(),true);
                PaintablePosition pointContainer = new PaintablePosition( paintablePoint, 
                                                                          (x+Radar.RADIUS-1), 
                                                                          (y+Radar.RADIUS-1), 
                                                                          0, 
                                                                          1);
                pointContainer.paint(canvas);
            }
        }
    }

    /** Width on screen */
    public float getWidth() {
        return Radar.RADIUS * 2;
    }

    /** Height on screen */
    public float getHeight() {
        return Radar.RADIUS * 2;
    }
}
