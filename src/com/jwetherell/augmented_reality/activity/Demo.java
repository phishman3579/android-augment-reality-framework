package com.jwetherell.augmented_reality.activity;

import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;
import com.jwetherell.augmented_reality.data.ARData;
import com.jwetherell.augmented_reality.data.BuzzDataSource;
import com.jwetherell.augmented_reality.data.DataSource;
import com.jwetherell.augmented_reality.data.TwitterDataSource;
import com.jwetherell.augmented_reality.data.WikipediaDataSource;
import com.jwetherell.augmented_reality.ui.Marker;
import android.location.Location;
import android.os.Bundle;

public class Demo extends AugmentedReality {
	private static final Logger logger = Logger.getLogger(Demo.class.getSimpleName());
	private static final String locale = Locale.getDefault().getLanguage();
	
	private static DataSource twitter = null;
	private static DataSource wikipedia = null;
	private static DataSource buzz = null;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        twitter = new TwitterDataSource(this.getResources());
        wikipedia = new WikipediaDataSource();
        buzz = new BuzzDataSource(this.getResources());
    }

    @Override
    public void onStart() {
        super.onStart();
        
        Location last = ARData.getCurrentLocation();
        if (last!=null) updateData(last.getLatitude(),last.getLongitude(),last.getAltitude());
    }
    
    @Override
    public void onLocationChanged(Location location) {
        super.onLocationChanged(location);
        
        updateData(location.getLatitude(),location.getLongitude(),location.getAltitude());
    }
    
    private void updateData(final double lat, final double lon, final double alt) {
    	Thread thread = new Thread(
    		new Runnable(){
				@Override
				public void run() {
			    	download(twitter, lat, lon, alt);
			    	download(wikipedia, lat, lon, alt);
			    	download(buzz, lat, lon, alt);
				}
			}
    	);
    	thread.start();
    }
    
    private static boolean download(DataSource source, double lat, double lon, double alt) {
		if (source==null) return false;
		
		String url = source.createRequestURL(lat, lon, alt, ARData.getRadius(), locale);    	
    	logger.info(url);
    	if (url==null) return false;
    	
    	List<Marker> markers = source.parse(url);
    	if (markers==null) return false;
    	
    	ARData.addMarkers(markers);
    	return true;
    }
}
