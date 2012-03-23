package com.jwetherell.augmented_reality.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicBoolean;

import android.content.Context;
import android.graphics.Canvas;
import android.view.View;

import com.jwetherell.augmented_reality.data.ARData;
import com.jwetherell.augmented_reality.ui.Marker;
import com.jwetherell.augmented_reality.ui.Radar;


/**
 * This class extends the View class and is designed draw the zoom bar, radar circle, and markers on the View.
 * 
 * @author Justin Wetherell <phishman3579@gmail.com>
 */
public class AugmentedView extends View {
    private static final AtomicBoolean drawing = new AtomicBoolean(false);

    private static final Radar radar = new Radar();
    private static final float[] locationArray = new float[3];
    private static final List<Marker> cache = new ArrayList<Marker>(); 
    private static final TreeSet<Marker> updated = new TreeSet<Marker>();
    private static final int COLLISION_ADJUSTMENT = 100;

    public AugmentedView(Context context) {
        super(context);
    }

	/**
	 * {@inheritDoc}
	 */
	@Override
    protected void onDraw(Canvas canvas) {
    	if (canvas==null) return;

        if (drawing.compareAndSet(false, true)) { 
	        //Get all the markers
	        List<Marker> collection = ARData.getMarkers();

	        //Prune all the markers that are out of the radar's radius (speeds up drawing and collision detection)
            cache.clear();
            for (Marker m : collection) {
                m.update(canvas, 0, 0);
                if (m.isOnRadar()) cache.add(m);
	        }
            collection = cache;

	        if (AugmentedReality.useCollisionDetection) adjustForCollisions(canvas,collection);

	        //Draw AR markers in reverse order since the last drawn should be the closest
	        ListIterator<Marker> iter = collection.listIterator(collection.size());
	        while (iter.hasPrevious()) {
	            Marker marker = iter.previous();
	            marker.draw(canvas);
	        }

	        //Radar circle and radar markers
	        if (AugmentedReality.showRadar) radar.draw(canvas);
	        drawing.set(false);
        }
    }

	private static void adjustForCollisions(Canvas canvas, List<Marker> collection) {
	    updated.clear();
	    
        //Update the AR markers for collisions
        for (Marker marker1 : collection) {
            if (updated.contains(marker1) || !marker1.isInView()) continue;

            int collisions = 1;
            for (Marker marker2 : collection) {
                if (marker1.equals(marker2) || updated.contains(marker2) || !marker2.isInView()) continue;

                if (marker1.isMarkerOnMarker(marker2)) {
                    marker2.getLocation().get(locationArray);
                    float y = locationArray[1];
                    float h = collisions*COLLISION_ADJUSTMENT;
                    locationArray[1] = y+h;
                    marker2.getLocation().set(locationArray);
                    marker2.update(canvas, 0, 0);
                    collisions++;
                    updated.add(marker2);
                }
            }
            updated.add(marker1);
        }
	}
}
