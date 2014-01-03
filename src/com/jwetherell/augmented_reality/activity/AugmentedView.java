package com.jwetherell.augmented_reality.activity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import android.content.Context;
import android.graphics.Canvas;
import android.util.Log;
import android.view.View;

import com.jwetherell.augmented_reality.data.ARData;
import com.jwetherell.augmented_reality.ui.Marker;
import com.jwetherell.augmented_reality.ui.Radar;

/**
 * This class extends the View class and is designed draw the zoom bar, radar
 * circle, and markers on the View.
 * 
 * @author Justin Wetherell <phishman3579@gmail.com>
 */
public class AugmentedView extends View {

    private static final String TAG = "AugmentedView";
    private static final AtomicBoolean drawing = new AtomicBoolean(false);
    private static final Radar radar = new Radar();
    private static final float[] locationArray = new float[3];
    private static final List<Marker> cache = new ArrayList<Marker>();
    private static final Set<Marker> updated = new HashSet<Marker>();

    public AugmentedView(Context context) {
        super(context);
        Log.v(TAG, "portrait              = "+AugmentedReality.ui_portrait);
        Log.v(TAG, "useCollisionDetection = "+AugmentedReality.useCollisionDetection);
        Log.v(TAG, "useSmoothing          = "+AugmentedReality.useDataSmoothing);
        Log.v(TAG, "showRadar             = "+AugmentedReality.showRadar);
        Log.v(TAG, "showZoomBar           = "+AugmentedReality.showZoomBar);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onDraw(Canvas canvas) {
        if (canvas == null) return;

        if (drawing.compareAndSet(false, true)) {
        	//Log.v(TAG, "DIRTY flag found, re-populating the cache.");

            // Get all the markers
            List<Marker> collection = ARData.getMarkers();

            // Prune all the markers that are out of the radar's radius (speeds
            // up drawing and collision detection)
            cache.clear();
            for (Marker m : collection) {
                m.update(canvas, 0, 0);
                if (m.isOnRadar() && m.isInView()) cache.add(m);
            }
            collection = cache;

            if (AugmentedReality.useCollisionDetection) 
                adjustForCollisions(canvas, collection);

            // Draw AR markers in reverse order since the last drawn should be
            // the closest
            ListIterator<Marker> iter = collection.listIterator(collection.size());
            while (iter.hasPrevious()) {
                Marker marker = iter.previous();
                marker.draw(canvas);
            }

            // Radar circle and radar markers
            if (AugmentedReality.showRadar) 
                radar.draw(canvas);
            drawing.set(false);
        }
    }

    private static void adjustForCollisions(Canvas canvas, List<Marker> collection) {
        updated.clear();

        // Update the AR markers for collisions
        for (int i=0; i<collection.size(); i++) {
            Marker marker1 = collection.get(i);
            if (!marker1.isInView()) {
                updated.add(marker1);
                continue;
            }
            if (updated.contains(marker1))
                continue;

            int collisions = 1;
            for (int j=i+1; j<collection.size(); j++) {
                Marker marker2 = collection.get(j);
                if (!marker2.isInView()) {
                    updated.add(marker2);
                    continue;
                }
                if (updated.contains(marker2))
                    continue;

                float width = marker1.getWidth();
                float height = marker1.getHeight();
                float max = Math.max(width, height);

                if (marker1.isMarkerOnMarker(marker2)) {
                    marker2.getLocation().get(locationArray);
                    float y = locationArray[1];
                    float h = collisions * max;
                    locationArray[1] = y + h;
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
