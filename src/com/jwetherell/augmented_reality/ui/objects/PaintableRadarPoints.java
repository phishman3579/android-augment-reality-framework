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
	private PaintablePoint paintablePoint = null;
	private PaintablePosition pointContainer = null;
	
	@Override
    public void paint(Canvas canvas) {
		if (canvas==null) return;
		
        /** Radius is in KM. */
        float range = ARData.getRadius() * 1000;

        //Draw the markers in the circle
        float scale = range / Radar.RADIUS;
        for (Marker pm : ARData.getMarkers()) {
            float x = pm.getLocationVector().x / scale;
            float y = pm.getLocationVector().z / scale;
            if ((x*x+y*y)<(Radar.RADIUS*Radar.RADIUS)) {
                if (paintablePoint==null) paintablePoint = new PaintablePoint(pm.getColor(),true);
                else paintablePoint.set(pm.getColor(),true);
                
                if (pointContainer==null) pointContainer = new PaintablePosition( 	paintablePoint, 
                                                                          			(x+Radar.RADIUS-1), 
                                                                          			(y+Radar.RADIUS-1), 
                                                                          			0, 
                                                                          			1);
                else pointContainer.set(paintablePoint, 
              							(x+Radar.RADIUS-1), 
              							(y+Radar.RADIUS-1), 
              							0, 
              							1);
                
                pointContainer.paint(canvas);
            }
        }
    }

    @Override
    public float getWidth() {
        return Radar.RADIUS * 2;
    }

    @Override
    public float getHeight() {
        return Radar.RADIUS * 2;
    }
}
