package com.jwetherell.augmented_reality.activity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import com.jwetherell.augmented_reality.data.ARData;
import com.jwetherell.augmented_reality.data.BuzzDataSource;
import com.jwetherell.augmented_reality.data.LocalDataSource;
import com.jwetherell.augmented_reality.data.NetworkDataSource;
import com.jwetherell.augmented_reality.data.TwitterDataSource;
import com.jwetherell.augmented_reality.data.WikipediaDataSource;
import com.jwetherell.augmented_reality.ui.Marker;

import android.location.Location;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Toast;


/**
 * This class extends the AugmentedReality and is designed to be an example on how to extends the AugmentedReality
 * class to show multiple data sources.
 * 
 * @author Justin Wetherell <phishman3579@gmail.com>
 */
public class Demo extends AugmentedReality {
    private static final String locale = Locale.getDefault().getLanguage();
	
	private static Collection<NetworkDataSource> sources = null;    
    private static Thread thread = null;

	/**
	 * {@inheritDoc}
	 */
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        if (sources==null) {
        	sources = new ArrayList<NetworkDataSource>();
            
        	LocalDataSource localData = new LocalDataSource(this.getResources());
        	ARData.addMarkers(localData.getMarkers());

            NetworkDataSource twitter = new TwitterDataSource(this.getResources());
            sources.add(twitter);
            NetworkDataSource wikipedia = new WikipediaDataSource(this.getResources());
            sources.add(wikipedia);
            NetworkDataSource buzz = new BuzzDataSource(this.getResources());
            sources.add(buzz);
        }
    }

	/**
	 * {@inheritDoc}
	 */
	@Override
    public void onStart() {
        super.onStart();
        
        Location last = ARData.getCurrentLocation();
        updateData(last.getLatitude(),last.getLongitude(),last.getAltitude());
    }

	/**
	 * {@inheritDoc}
	 */
	@Override
    public void onLocationChanged(Location location) {
        super.onLocationChanged(location);
        
        updateData(location.getLatitude(),location.getLongitude(),location.getAltitude());
    }

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void markerTouched(Marker marker) {
        Toast t = Toast.makeText(getApplicationContext(), marker.getName(), Toast.LENGTH_SHORT);
        t.setGravity(Gravity.CENTER, 0, 0);
        t.show();
	}

    private void updateData(final double lat, final double lon, final double alt) {
    	if (thread!=null && thread.isAlive()) return;
    	
    	thread = new Thread(
    		new Runnable(){
				@Override
				public void run() {
					for (NetworkDataSource source : sources) {
						download(source, lat, lon, alt);
					}
				}
			}
    	);
    	thread.start();
    }
    
    private static boolean download(NetworkDataSource source, double lat, double lon, double alt) {
		if (source==null) return false;
		
		String url = null;
		try {
			url = source.createRequestURL(lat, lon, alt, ARData.getRadius(), locale);    	
		} catch (NullPointerException e) {
			return false;
		}
    	
		List<Marker> markers = null;
		try {
			markers = source.parse(url);
		} catch (NullPointerException e) {
			return false;
		}

    	ARData.addMarkers(markers);
    	return true;
    }
}
