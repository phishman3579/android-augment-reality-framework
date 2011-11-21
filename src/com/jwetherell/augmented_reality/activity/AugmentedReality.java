package com.jwetherell.augmented_reality.activity;

import java.text.DecimalFormat;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import com.jwetherell.augmented_reality.camera.CameraSurface;
import com.jwetherell.augmented_reality.data.ARData;
import com.jwetherell.augmented_reality.ui.Marker;


/**
 * This class extends the SensorsActivity and is designed tie the AugmentedView and zoom bar together.
 * 
 * @author Justin Wetherell <phishman3579@gmail.com>
 */
public class AugmentedReality extends SensorsActivity implements OnTouchListener {
    private static final String TAG = "AugmentedReality";
    private static final DecimalFormat FORMAT = new DecimalFormat("#.##");
    
    private static WakeLock wakeLock = null;
    private static CameraSurface camScreen = null;    
    private static SeekBar myZoomBar = null;
    private static FrameLayout frameLayout = null;
    private static AugmentedView augmentedView = null;
    private static boolean useCollisionDetection = true;
    
	/**
	 * {@inheritDoc}
	 */
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        camScreen = new CameraSurface(this);
        setContentView(camScreen);
        
        myZoomBar = new SeekBar(this);
        myZoomBar.setMax(100);
        myZoomBar.setProgress(25);
        myZoomBar.setOnSeekBarChangeListener(myZoomBarOnSeekBarChangeListener);

        frameLayout = new FrameLayout(this);
        frameLayout.setMinimumWidth(3000);
        frameLayout.addView(myZoomBar);
        frameLayout.setPadding(10, 0, 10, 10);
        FrameLayout.LayoutParams frameLayoutParams = new FrameLayout.LayoutParams(  LayoutParams.FILL_PARENT, 
                                                                                    LayoutParams.WRAP_CONTENT, 
                                                                                    Gravity.BOTTOM);
        addContentView(frameLayout,frameLayoutParams);

        augmentedView = new AugmentedView(this,useCollisionDetection);
        augmentedView.setOnTouchListener(this);
        LayoutParams augLayout = new LayoutParams(  LayoutParams.WRAP_CONTENT, 
                                                    LayoutParams.WRAP_CONTENT);
        addContentView(augmentedView,augLayout);
        
        updateDataOnZoom();

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "DoNotDimScreen");
    }

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onResume() {
		super.onResume();

		wakeLock.acquire();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onPause() {
		super.onPause();

		wakeLock.release();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
    public void onSensorChanged(SensorEvent evt) {
        super.onSensorChanged(evt);

        if (    evt.sensor.getType() == Sensor.TYPE_ACCELEROMETER || 
                evt.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD
        ) {
            augmentedView.postInvalidate();
        }
    }
    
    private OnSeekBarChangeListener myZoomBarOnSeekBarChangeListener = new OnSeekBarChangeListener() {
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            updateDataOnZoom();
            camScreen.invalidate();
        }

        public void onStartTrackingTouch(SeekBar seekBar) {
            //Ignore
        }

        public void onStopTrackingTouch(SeekBar seekBar) {
            updateDataOnZoom();
            camScreen.invalidate();
        }
    };
    
    private static float calcZoomLevel(){
        int myZoomLevel = myZoomBar.getProgress();
        float myout = 5;
    
        if (myZoomLevel <= 26) {
            myout = myZoomLevel / 25f;
        } else if (25 < myZoomLevel && myZoomLevel < 50) {
            myout = (1 + (myZoomLevel - 25)) * 0.38f;
        } else if (25== myZoomLevel) {
            myout = 1;
        } else if (50== myZoomLevel) {
            myout = 10;
        } else if (50 < myZoomLevel && myZoomLevel < 75) {
            myout = (10 + (myZoomLevel - 50)) * 0.83f;
        } else {
            myout = (30 + (myZoomLevel - 75) * 2f);
        }

        return myout;
    }

    private static void updateDataOnZoom() {
        float zoomLevel = calcZoomLevel();
        ARData.setRadius(zoomLevel);
        ARData.setZoomLevel(FORMAT.format(zoomLevel));
        ARData.setZoomProgress(myZoomBar.getProgress());
    }

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean onTouch(View view, MotionEvent me) {
	    //See if the motion event is on a Marker
	    for (Marker marker : ARData.getMarkers()) {
	        if (marker.handleClick(me.getX(), me.getY())) {
	            if (me.getAction() == MotionEvent.ACTION_UP) markerTouched(marker);
	            return true;
	        }
	    }
		
		return super.onTouchEvent(me);
	};
	
	protected void markerTouched(Marker marker) {
		Log.w(TAG,"markerTouched() not implemented.");
	}
}
