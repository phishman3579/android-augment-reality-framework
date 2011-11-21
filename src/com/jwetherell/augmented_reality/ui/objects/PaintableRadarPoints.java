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
    private final float[] locationArray = new float[3];
	private PaintablePoint paintablePoint = null;
	private PaintablePosition pointContainer = null;

	/**
	 * {@inheritDoc}
	 */
	@Override
    public void paint(Canvas canvas) {
		if (canvas==null) throw new NullPointerException();

		//Draw the markers in the circle
		float range = ARData.getRadius() * 1000;
		float scale = range / Radar.RADIUS;
		for (Marker pm : ARData.getMarkers()) {
		    pm.getLocation().get(locationArray);
		    float x = locationArray[0] / scale;
		    float y = locationArray[2] / scale;
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

	/**
	 * {@inheritDoc}
	 */
	@Override
    public float getWidth() {
        return Radar.RADIUS * 2;
    }

	/**
	 * {@inheritDoc}
	 */
	@Override
    public float getHeight() {
        return Radar.RADIUS * 2;
    }
}
