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
	
    private static String zoomLevel = null;
    private static int zoomProgress = 0;
    private static float radius = 20;
    private static Location currentLocation = null;
    private static Matrix rotationMatrix = null;
    private static Map<String,Marker> markerList = new ConcurrentHashMap<String,Marker>();

    public static void setZoomLevel(String zoomLevel) {
    	if (zoomLevel==null) return;
    	
        ARData.zoomLevel = zoomLevel;
    }
    public static String getZoomLevel() {
        return zoomLevel;
    }
    
    public static void setZoomProgress(int zoomProgress) {
        ARData.zoomProgress = zoomProgress;
    }
    public static int getZoomProgress() {
        return zoomProgress;
    }
    
    public static void setRadius(float radius) {
        ARData.radius = radius;
    }
    public static float getRadius() {
        return radius;
    }
    
    public static void setCurrentLocation(Location currentLocation) {
        ARData.currentLocation = currentLocation;
        onLocationChanged(currentLocation);
    }
    public static Location getCurrentLocation() {
        return currentLocation;
    }
    
    public static void setRotationMatrix(Matrix rotationMatrix) {
        ARData.rotationMatrix = rotationMatrix;
    }
    public static Matrix getRotationMatrix() {
        return rotationMatrix;
    }

    //DataHandler
    public static void addMarkers(List<Marker> markers) {
    	if (markers==null) return;
    	
    	logger.info("Marker before: "+markerList.size());
        for(Marker ma : markers) {
            if (!markerList.containsKey(ma)) {
            	ma.calcRelativePosition(ARData.getCurrentLocation());
            	markerList.put(ma.getName(),ma);
            }
        }
        logger.info("Marker count: "+markerList.size());
    }
        
    public static void onLocationChanged(Location location) {
    	for(Marker ma: markerList.values()) {
            ma.calcRelativePosition(location);
        }
    }

    public static Collection<Marker> getMarkers() {
        return markerList.values();
    }
}
