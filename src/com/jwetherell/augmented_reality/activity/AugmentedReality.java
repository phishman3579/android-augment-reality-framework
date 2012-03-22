package com.jwetherell.augmented_reality.activity;

import java.text.DecimalFormat;

import android.content.Context;
import android.graphics.Color;
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
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

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
    private static final int ZOOMBAR_BACKGROUND_COLOR = Color.argb((255/2),55,55,55);
    private static final String endKM = FORMAT.format(AugmentedReality.MAX_ZOOM)+"km";

    private static WakeLock wakeLock = null;
    private static CameraSurface camScreen = null;    
    private static SeekBar myZoomBar = null;
    private static TextView endLabel = null;
    private static LinearLayout zoomLayout = null;
    private static AugmentedView augmentedView = null;
    private static boolean useCollisionDetection = true;
    
    public static final boolean SHOW_RADAR = true;
    public static final float MAX_ZOOM = 100; //in KM
    public static final float ONE_PERCENT = MAX_ZOOM/100f;
    public static final float TEN_PERCENT = 10f*ONE_PERCENT;
    public static final float TWENTY_PERCENT = 2f*TEN_PERCENT;
    public static final float EIGHTY_PERCENTY = 4f*TWENTY_PERCENT;
    
    
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
        myZoomBar.setProgress(50);
        myZoomBar.setOnSeekBarChangeListener(myZoomBarOnSeekBarChangeListener);

        endLabel = new TextView(this);
        endLabel.setText(endKM);
        endLabel.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL);

        zoomLayout = new LinearLayout(this);
        zoomLayout.setOrientation(LinearLayout.HORIZONTAL);
        zoomLayout.setMinimumWidth(3000);
        zoomLayout.setPadding(10, 10, 10, 10);
        zoomLayout.setBackgroundColor(ZOOMBAR_BACKGROUND_COLOR);
        LinearLayout.LayoutParams zoomBarParams =  new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT);
        zoomBarParams.weight = 0.10f;
        zoomLayout.addView(myZoomBar, zoomBarParams);
        
        LinearLayout textLayout = new LinearLayout(this);
        LinearLayout.LayoutParams textParams =  new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
        textLayout.addView(endLabel,textParams);

        LinearLayout.LayoutParams zoomTextParams =  new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
        zoomTextParams.weight = 0.9f;
        zoomLayout.addView(textLayout, zoomTextParams);
        
        FrameLayout.LayoutParams frameLayoutParams = new FrameLayout.LayoutParams(  LayoutParams.FILL_PARENT, 
                                                                                    LayoutParams.WRAP_CONTENT, 
                                                                                    Gravity.BOTTOM);
        addContentView(zoomLayout,frameLayoutParams);

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
        float myout = 0;

        float percent = 0;
        if (myZoomLevel <= 25) {
            percent = myZoomLevel/25f;
            myout = ONE_PERCENT*percent;
        } else if (myZoomLevel > 25 && myZoomLevel <= 50) {
            percent = (myZoomLevel-25f)/25f;
            myout = ONE_PERCENT+(TEN_PERCENT*percent);
        } else if (myZoomLevel > 50 && myZoomLevel <= 75) {
            percent = (myZoomLevel-50f)/25f;
            myout = TEN_PERCENT+(TWENTY_PERCENT*percent);
        } else {
            percent = (myZoomLevel-75f)/25f;
            myout = TWENTY_PERCENT+(EIGHTY_PERCENTY*percent);
        }

        return myout;
    }

    /**
     * Called when the zoom bar has changed.
     */
    protected void updateDataOnZoom() {
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
