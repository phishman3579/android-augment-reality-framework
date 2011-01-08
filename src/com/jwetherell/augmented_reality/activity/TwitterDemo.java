package com.jwetherell.augmented_reality.activity;

import java.util.List;
import java.util.Locale;
import com.jwetherell.augmented_reality.data.ARData;
import com.jwetherell.augmented_reality.data.DataSource;
import com.jwetherell.augmented_reality.data.TwitterDataSource;
import com.jwetherell.augmented_reality.ui.Marker;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;

public class TwitterDemo extends AugmentedReality {
	private static final String locale = Locale.getDefault().getLanguage();
	
	private static DataSource source = null;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        source = new TwitterDataSource(this.getResources());
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
    
    private void updateData(double lat, double lon, double alt) {
    	String url = source.createRequestURL(lat, lon, alt, ARData.getRadius(), locale);    	
    	new DownloadInfoTask().doInBackground(url);
    }
    
    private class DownloadInfoTask extends AsyncTask<String, Integer, Boolean> {
    	@Override
    	protected Boolean doInBackground(String... urls) {
    		if (urls==null) return false;
    		
    		String url = urls[0];
        	if (url==null) return false;
        	
        	List<Marker> markers = source.parse(url);
        	if (markers==null) return false;
        	ARData.addMarkers(markers);
        	return true;
        }
    }
}
