package com.jwetherell.augmented_reality.data;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import com.jwetherell.augmented_reality.common.Matrix;
import com.jwetherell.augmented_reality.ui.Marker;

import android.location.Location;

/**
 * Abstract class which should be used to set global data.
 * 
 * @author Justin Wetherell <phishman3579@gmail.com>
 */
public abstract class ARData {
	private static final Logger logger = Logger.getLogger(ARData.class.getSimpleName());
    private static final Map<String,Marker> markerList = new ConcurrentHashMap<String,Marker>();

    /*defaulting to our place*/
    public static Location hardFix = new Location("ATL");
    static {
        hardFix.setLatitude(39.931261);
        hardFix.setLongitude(-75.051267);
        hardFix.setAltitude(1);
    }
    
    private static String zoomLevel = null;
    private static int zoomProgress = 0;
    private static float radius = 20;
    private static Location currentLocation = hardFix;
    private static Matrix rotationMatrix = null;
    

    /**
     * Set the zoom level.
     * @param zoomLevel String representing the zoom level.
     */
    public static void setZoomLevel(String zoomLevel) {
    	if (zoomLevel==null) throw new NullPointerException();
    	
        ARData.zoomLevel = zoomLevel;
    }
    
    /**
     * Get the zoom level.
     * @return String representing the zoom level.
     */
    public static String getZoomLevel() {
        return zoomLevel;
    }
    
    /**
     * Set the zoom progress.
     * @param zoomProgress int representing the zoom progress.
     */
    public static void setZoomProgress(int zoomProgress) {
        ARData.zoomProgress = zoomProgress;
    }
    
    /**
     * Get the zoom progress.
     * @return int representing the zoom progress.
     */
    public static int getZoomProgress() {
        return zoomProgress;
    }
    
    /**
     * Set the radius of the radar screen.
     * @param radius float representing the radar screen.
     */
    public static void setRadius(float radius) {
        ARData.radius = radius;
    }
    
    /**
     * Get the radius of the radar screen.
     * @return float representing the radar screen.
     */
    public static float getRadius() {
        return radius;
    }
    
    /**
     * Set the current location.
     * @param currentLocation Location to set.
     * @throws NullPointerException if Location param is NULL.
     */
    public static void setCurrentLocation(Location currentLocation) {
    	if (currentLocation==null) throw new NullPointerException();
    	
        ARData.currentLocation = currentLocation;
        onLocationChanged(currentLocation);
    }
    
    private static void onLocationChanged(Location location) {
    	for(Marker ma: markerList.values()) {
            ma.calcRelativePosition(location);
        }
    }
    
    /**
     * Get the current Location.
     * @return Location representing the current location.
     */
    public static Location getCurrentLocation() {
        return currentLocation;
    }
    
    /**
     * Set the rotation matrix.
     * @param rotationMatrix Matrix to use for rotation.
     */
    public static void setRotationMatrix(Matrix rotationMatrix) {
        ARData.rotationMatrix = rotationMatrix;
    }
    
    /**
     * Get the rotation matrix.
     * @return Matrix representing the rotation matrix.
     */
    public static Matrix getRotationMatrix() {
        return rotationMatrix;
    }

    /**
     * Add a List of Markers to our Collection.
     * @param markers List of Markers to add.
     */
    public static void addMarkers(List<Marker> markers) {
    	if (markers==null) throw new NullPointerException();
    	
    	logger.info("Marker before: "+markerList.size());
        for(Marker ma : markers) {
            if (!markerList.containsKey(ma)) {
            	ma.calcRelativePosition(ARData.getCurrentLocation());
            	markerList.put(ma.getName(),ma);
            }
        }
        logger.info("Marker count: "+markerList.size());
    }

    /**
     * Get the Markers collection.
     * @return Collection of Markers.
     */
    public static Collection<Marker> getMarkers() {
        return markerList.values();
    }
}
