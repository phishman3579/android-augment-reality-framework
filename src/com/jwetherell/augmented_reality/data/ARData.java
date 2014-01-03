package com.jwetherell.augmented_reality.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import android.location.Location;
import android.util.Log;

import com.jwetherell.augmented_reality.common.Matrix;
import com.jwetherell.augmented_reality.common.Orientation.ORIENTATION;
import com.jwetherell.augmented_reality.ui.Marker;

/**
 * Abstract class which should be used to set global data.
 * 
 * @author Justin Wetherell <phishman3579@gmail.com>
 */
public abstract class ARData {

    private static final String TAG = "ARData";
    private static final Map<String, Marker> markerList = new ConcurrentHashMap<String, Marker>();
    private static final List<Marker> cache = new CopyOnWriteArrayList<Marker>();
    private static final AtomicBoolean dirty = new AtomicBoolean(false);
    private static final float[] locationArray = new float[3];

    /* defaulting to our place */
    public static final Location hardFix = new Location("ATL");
    static {
        hardFix.setLatitude(39.931261);
        hardFix.setLongitude(-75.051267);
        hardFix.setAltitude(1);
    }

    private static final Object radiusLock = new Object();
    private static float radius = Float.valueOf(20);
    private static final Object zoomLevelLock = new Object();
    private static String zoomLevel = new String();
    private static final Object zoomProgressLock = new Object();
    private static int zoomProgress = 0;
    private static final Object currentLocationLock = new Object();
    private static Location currentLocation = hardFix;
    private static final Object rotationMatrixLock = new Object();
    private static Matrix rotationMatrix = new Matrix();
    private static final Object azimuthLock = new Object();
    private static float azimuth = 0;
    private static final Object rollLock = new Object();
    private static float roll = 0;
    private static final Object orientationLock = new Object();
    private static ORIENTATION orientation = ORIENTATION.UNKNOWN;
    private static final Object orientationAngleLock = new Object();
    private static int orientationAngle = 0;

    /**
     * Set the zoom level.
     * 
     * @param zoomLevel
     *            String representing the zoom level.
     */
    public static void setZoomLevel(String zoomLevel) {
        if (zoomLevel == null) throw new NullPointerException();

        synchronized (ARData.zoomLevelLock) {
            ARData.zoomLevel = zoomLevel;
        }
    }

    /**
     * Get the zoom level.
     * 
     * @return String representing the zoom level.
     */
    public static String getZoomLevel() {
        synchronized (ARData.zoomLevelLock) {
            return ARData.zoomLevel;
        }
    }

    /**
     * Set the zoom progress.
     * 
     * @param zoomProgress
     *            int representing the zoom progress.
     */
    public static void setZoomProgress(int zoomProgress) {
        synchronized (ARData.zoomProgressLock) {
            if (ARData.zoomProgress != zoomProgress) {
                ARData.zoomProgress = zoomProgress;
                if (dirty.compareAndSet(false, true)) {
                    Log.v(TAG, "Setting DIRTY flag!");
                    cache.clear();
                }
            }
        }
    }

    /**
     * Get the zoom progress.
     * 
     * @return int representing the zoom progress.
     */
    public static int getZoomProgress() {
        synchronized (ARData.zoomProgressLock) {
            return ARData.zoomProgress;
        }
    }

    /**
     * Set the radius of the radar screen.
     * 
     * @param radius
     *            float representing the radar screen.
     */
    public static void setRadius(float radius) {
        synchronized (ARData.radiusLock) {
            ARData.radius = radius;
        }
    }

    /**
     * Get the radius (in KM) of the radar screen.
     * 
     * @return float representing the radar screen.
     */
    public static float getRadius() {
        synchronized (ARData.radiusLock) {
            return ARData.radius;
        }
    }

    /**
     * Set the current location.
     * 
     * @param currentLocation
     *            Location to set.
     * @throws NullPointerException
     *             if Location param is NULL.
     */
    public static void setCurrentLocation(Location currentLocation) {
        if (currentLocation == null) throw new NullPointerException();

        Log.d(TAG, "current location. location=" + currentLocation.toString());
        synchronized (ARData.currentLocationLock) {
            ARData.currentLocation = currentLocation;
        }
        onLocationChanged(currentLocation);
    }

    private static void onLocationChanged(Location location) {
        Log.d(TAG, "New location, updating markers. location=" + location.toString());
        for (Marker ma : markerList.values()) {
            ma.calcRelativePosition(location);
        }

        if (dirty.compareAndSet(false, true)) {
            Log.v(TAG, "Setting DIRTY flag!");
            cache.clear();
        }
    }

    /**
     * Get the current Location.
     * 
     * @return Location representing the current location.
     */
    public static Location getCurrentLocation() {
        synchronized (ARData.currentLocationLock) {
            return ARData.currentLocation;
        }
    }

    /**
     * Set the rotation matrix.
     * 
     * @param rotationMatrix
     *            Matrix to use for rotation.
     */
    public static void setRotationMatrix(Matrix rotationMatrix) {
        synchronized (ARData.rotationMatrixLock) {
            ARData.rotationMatrix = rotationMatrix;
        }
    }

    /**
     * Get the rotation matrix.
     * 
     * @return Matrix representing the rotation matrix.
     */
    public static Matrix getRotationMatrix() {
        synchronized (ARData.rotationMatrixLock) {
            return ARData.rotationMatrix;
        }
    }

    /**
     * Add a List of Markers to our Collection.
     * 
     * @param markers
     *            List of Markers to add.
     */
    public static void addMarkers(Collection<Marker> markers) {
        if (markers == null) throw new NullPointerException();

        if (markers.size() <= 0) return;

        Log.d(TAG, "New markers, updating markers. new markers=" + markers.toString());
        for (Marker marker : markers) {
            if (!markerList.containsKey(marker.getName())) {
                marker.calcRelativePosition(getCurrentLocation());
                markerList.put(marker.getName(), marker);
            }
        }

        if (dirty.compareAndSet(false, true)) {
            Log.v(TAG, "Setting DIRTY flag!");
            cache.clear();
        }
    }

    /**
     * Get the Markers collection.
     * 
     * @return Collection of Markers.
     */
    public static List<Marker> getMarkers() {
        // If markers we added, zero out the altitude to recompute the collision
        // detection
        if (dirty.compareAndSet(true, false)) {
            Log.v(TAG, "DIRTY flag found, resetting all marker heights to zero.");
            for (Marker ma : markerList.values()) {
                ma.getLocation().get(locationArray);
                locationArray[1] = ma.getInitialY();
                ma.getLocation().set(locationArray);
            }

            Log.v(TAG, "Populating the cache.");
            List<Marker> copy = new ArrayList<Marker>(markerList.size());
            copy.addAll(markerList.values());
            Collections.sort(copy, comparator);
            // The cache should be sorted from closest to farthest marker.
            cache.clear();
            cache.addAll(copy);
        }
        return Collections.unmodifiableList(cache);
    }

    private static final Comparator<Marker> comparator = new Comparator<Marker>() {

        /**
         * {@inheritDoc}
         */
        @Override
        public int compare(Marker arg0, Marker arg1) {
            return Double.compare(arg0.getDistance(), arg1.getDistance());
        }
    };

    /**
     * Set the current Azimuth.
     * 
     * @param azimuth
     *            float representing the azimuth.
     */
    public static void setAzimuth(float azimuth) {
        synchronized (ARData.azimuthLock) {
            ARData.azimuth = azimuth;
        }
    }

    /**
     * Get the current Azimuth.
     * 
     * @return azimuth float representing the azimuth.
     */
    public static float getAzimuth() {
        synchronized (ARData.azimuthLock) {
            return ARData.azimuth;
        }
    }

    /**
     * Set the current Roll.
     * 
     * @param roll
     *            float representing the roll.
     */
    public static void setRoll(float roll) {
        synchronized (ARData.rollLock) {
            ARData.roll = roll;
        }
    }

    /**
     * Get the current Roll.
     * 
     * @return roll float representing the roll.
     */
    public static float getRoll() {
        synchronized (ARData.rollLock) {
            return ARData.roll;
        }
    }

    /**
     * Set the current orientation.
     * 
     * @param orientation
     *            ORIENTATION representing the orientation.
     */
    public static void setDeviceOrientation(ORIENTATION orientation) {
        synchronized (ARData.orientationLock) {
            ARData.orientation = orientation;
        }
    }

    /**
     * Get the current orientation.
     * 
     * @return orientation ORIENTATION representing the orientation.
     */
    public static ORIENTATION getDeviceOrientation() {
        synchronized (ARData.orientationLock) {
            return ARData.orientation;
        }
    }

    /**
     * Set the current orientation angle.
     * 
     * @param angle
     *            int representing the orientation angle.
     */
    public static void setDeviceOrientationAngle(int angle) {
        synchronized (ARData.orientationAngleLock) {
            ARData.orientationAngle = angle;
        }
    }

    /**
     * Get the current orientation angle.
     * 
     * @return angle int representing the orientation angle.
     */
    public static int getDeviceOrientationAngle() {
        synchronized (ARData.orientationAngleLock) {
            return ARData.orientationAngle;
        }
    }
}
