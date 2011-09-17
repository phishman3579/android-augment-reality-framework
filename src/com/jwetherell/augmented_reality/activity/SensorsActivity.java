package com.jwetherell.augmented_reality.activity;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

import com.jwetherell.augmented_reality.common.Matrix;
import com.jwetherell.augmented_reality.data.ARData;

import android.app.Activity;
import android.content.Context;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;


/**
 * This class extends Activity and processes sensor data and location data.
 * 
 * @author Justin Wetherell <phishman3579@gmail.com>
 */
public class SensorsActivity extends Activity implements SensorEventListener, LocationListener {
    private static final Logger logger = Logger.getLogger(SensorsActivity.class.getSimpleName());    
    private static final AtomicBoolean computing = new AtomicBoolean(false); 

    private static final int MIN_TIME = 30*1000;
    private static final int MIN_DISTANCE = 10;

    private static final float RTmp[] = new float[9]; //Temporary rotation matrix in Android format
    private static final float Rot[] = new float[9]; //Final rotation matrix in Android format
    private static final float grav[] = new float[3]; //Gravity (a.k.a accelerometer data)
    private static final float mag[] = new float[3]; //Magnetic 

    private static int rHistIdx = 0;
    private static final Matrix tempR = new Matrix();
    private static final Matrix finalR = new Matrix();
    private static final Matrix smoothR = new Matrix();
    private static final Matrix histR[] = new Matrix[10];
    private static final Matrix m1 = new Matrix();
    private static final Matrix m2 = new Matrix();
    private static final Matrix m3 = new Matrix();
    private static final Matrix mageticNorthCompensation = new Matrix();

    private static SensorManager sensorMgr = null;
    private static List<Sensor> sensors = null;
    private static Sensor sensorGrav = null;
    private static Sensor sensorMag = null;
    private static LocationManager locationMgr = null;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        
        double angleX = Math.toRadians(-90);
        double angleY = Math.toRadians(-90);
        
        //Counter-clockwise rotation at -90 degrees around the x-axis
        // [ 1, 0,   0    ]
        // [ 0, cos, -sin ]
        // [ 0, sin, cos  ]
        m1.set( 1f, 
                0f, 
                0f, 
                0f, 
                (float) Math.cos(angleX), 
                (float) -Math.sin(angleX), 
                0f, 
                (float) Math.sin(angleX), 
                (float) Math.cos(angleX));

        //Counter-clockwise rotation at -90 degrees around the x-axis
        // [ 1, 0,   0    ]
        // [ 0, cos, -sin ]
        // [ 0, sin, cos  ]
        m2.set( 1f, 
                0f, 
                0f, 
                0f, 
                (float) Math.cos(angleX), 
                (float) -Math.sin(angleX), 
                0f, 
                (float) Math.sin(angleX), 
                (float) Math.cos(angleX));
        
        //Counter-clockwise rotation at -90 degrees around the y-axis
        // [ cos,  0, sin ]
        // [ 0,    1, 0   ]
        // [ -sin, 0, cos ]
        m3.set( (float) Math.cos(angleY), 
                0f, 
                (float) Math.sin(angleY),
                0f, 
                1f, 
                0f, 
                (float) -Math.sin(angleY), 
                0f, (float) Math.cos(angleY));

        //Identity matrix
        // [ 1, 0, 0 ]
        // [ 0, 1, 0 ]
        // [ 0, 0, 1 ]
        mageticNorthCompensation.toIdentity();

        //Historic matrices to "smooth" the data
        for (int i = 0; i < histR.length; i++) {
            histR[i] = new Matrix();
        }
        
        try {
            sensorMgr = (SensorManager) getSystemService(SENSOR_SERVICE);

            sensors = sensorMgr.getSensorList(Sensor.TYPE_ACCELEROMETER);
            if (sensors.size() > 0) sensorGrav = sensors.get(0);

            sensors = sensorMgr.getSensorList(Sensor.TYPE_MAGNETIC_FIELD);
            if (sensors.size() > 0) sensorMag = sensors.get(0);

            sensorMgr.registerListener(this, sensorGrav, SensorManager.SENSOR_DELAY_NORMAL);
            sensorMgr.registerListener(this, sensorMag, SensorManager.SENSOR_DELAY_NORMAL);

            locationMgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, this);

            try {
                /*defaulting to our place*/
                Location hardFix = new Location("ATL");
                hardFix.setLatitude(39.931261);
                hardFix.setLongitude(-75.051267);
                hardFix.setAltitude(1);

                try {
                    Location gps=locationMgr.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    Location network=locationMgr.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if(gps!=null)
                        onLocationChanged(gps);
                    else if (network!=null)
                        onLocationChanged(network);
                    else
                        onLocationChanged(hardFix);
                } catch (Exception ex2) {
                    onLocationChanged(hardFix);
                }

                GeomagneticField gmf = new GeomagneticField((float) ARData.getCurrentLocation().getLatitude(), 
                                                            (float) ARData.getCurrentLocation().getLongitude(),
                                                            (float) ARData.getCurrentLocation().getAltitude(), 
                                                            System.currentTimeMillis());
                angleY = Math.toRadians(-gmf.getDeclination());
                
                //Counter-clockwise rotation at negative declination around the y-axis
                //note: declination of the horizontal component of the magnetic field
                //      from true north, in degrees (i.e. positive means the magnetic 
                //      field is rotated east that much from true north). 
                //note2: declination is the difference between true north and magnetic north
                // [ cos,  0, sin ]
                // [ 0,    1, 0   ]
                // [ -sin, 0, cos ]
                mageticNorthCompensation.set( (float) Math.cos(angleY), 
                        0f, 
                        (float) Math.sin(angleY), 
                        0f, 
                        1f, 
                        0f, 
                        (float) -Math.sin(angleY), 
                        0f, 
                        (float) Math.cos(angleY));

            } catch (Exception ex) {
                logger.info("Exception: "+ex);
            }
        } catch (Exception ex1) {
            try {
                if (sensorMgr != null) {
                    sensorMgr.unregisterListener(this, sensorGrav);
                    sensorMgr.unregisterListener(this, sensorMag);
                    sensorMgr = null;
                }
                if (locationMgr != null) {
                    locationMgr.removeUpdates(this);
                    locationMgr = null;
                }
            } catch (Exception ex2) {
                logger.info("Exception: "+ex2);
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        try {
            try {
                sensorMgr.unregisterListener(this, sensorGrav);
            } catch (Exception ex) {
                logger.info("Exception: "+ex);
            }
            try {
                sensorMgr.unregisterListener(this, sensorMag);
            } catch (Exception ex) {
                logger.info("Exception: "+ex);
            }
            sensorMgr = null;

            try {
                locationMgr.removeUpdates(this);
            } catch (Exception ex) {
                logger.info("Exception: "+ex);
            }
            locationMgr = null;
        } catch (Exception ex) {
            logger.info("Exception: "+ex);
        }
    }
    
    @Override
    public void onSensorChanged(SensorEvent evt) {
    	if (!computing.compareAndSet(false, true)) return;
    	
        if (evt.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            grav[0] = evt.values[0];
            grav[1] = evt.values[1];
            grav[2] = evt.values[2];
        } else if (evt.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            mag[0] = evt.values[0];
            mag[1] = evt.values[1];
            mag[2] = evt.values[2];
        }

        //Get rotation and inclination matrices given the gravity and geomagnetic matrices
        SensorManager.getRotationMatrix(RTmp, null, grav, mag);

        //Translate the rotation matrices from X and -Z (landscape)
        SensorManager.remapCoordinateSystem(RTmp, SensorManager.AXIS_X, SensorManager.AXIS_MINUS_Z, Rot);

        //Convert from float[9] to Matrix
        tempR.set(Rot[0], Rot[1], Rot[2], Rot[3], Rot[4], Rot[5], Rot[6], Rot[7], Rot[8]);
 
        //Identity matrix
        // [ 1, 0, 0 ]
        // [ 0, 1, 0 ]
        // [ 0, 0, 1 ]
        finalR.toIdentity();

        //Multiply by the counter-clockwise rotation at the negative declination around the y-axis
        finalR.prod(mageticNorthCompensation);

        //Multiply by the counter-clockwise rotation at -90 degrees around the x-axis
        finalR.prod(m1);

        //Multiply by the translated rotation matrix
        finalR.prod(tempR);

        //Multiply by the counter-clockwise rotation at -90 degrees around the y-axis
        finalR.prod(m3);

        //Multiply by the counter-clockwise rotation at -90 degrees around the x-axis
        finalR.prod(m2);

        finalR.invert(); 

        //Start to smooth the data (catch a boundary case)
        histR[rHistIdx].set(finalR);
        rHistIdx++;
        if (rHistIdx >= histR.length) rHistIdx = 0;

        //Zero out the smoothed rotation matrix
        smoothR.set(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f);
        
        //Add the historic data
        for (int i = 0; i < histR.length; i++) {
            smoothR.add(histR[i]);
        }
        //Smooth the historic data
        smoothR.mult(1 / (float) histR.length);

        //Set the rotation matrix (used to translate all object from lat/lon to x/y/z)
        ARData.setRotationMatrix(smoothR);
        
        computing.set(false);
    }

    @Override
    public void onProviderDisabled(String provider) {
        //Ignore
    }

    @Override
    public void onProviderEnabled(String provider) {
        //Ignore
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        //Ignore
    }

    @Override
    public void onLocationChanged(Location location) {
        ARData.setCurrentLocation(location);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        if(sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD && accuracy==SensorManager.SENSOR_STATUS_UNRELIABLE) {
            logger.info("Compass data unreliable");
        }
    }
}
