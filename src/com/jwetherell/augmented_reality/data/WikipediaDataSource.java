package com.jwetherell.augmented_reality.data;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Color;

import com.jwetherell.augmented_reality.ui.Marker;

/**
 * This class extends DataSource to fetch data from Wikipedia.
 * 
 * @author Justin Wetherell <phishman3579@gmail.com>
 */
public class WikipediaDataSource extends DataSource {
	private static final Logger logger = Logger.getLogger(WikipediaDataSource.class.getSimpleName());
	private static final String BASE_URL = "http://ws.geonames.org/findNearbyWikipediaJSON";

	public WikipediaDataSource() {}
	
	public String createRequestURL(double lat, double lon, double alt, float radius, String locale) {
		return BASE_URL+
        "?lat=" + lat +
        "&lng=" + lon +
        "&radius="+ radius +
        "&maxRows=40" +
        "&lang=" + locale;

	}
	
	public List<Marker> parse(JSONObject root) {
		if (root==null) return null;
		
		JSONObject jo = null;
		JSONArray dataArray = null;
    	List<Marker> markers=new ArrayList<Marker>();

		try {
			if(root.has("geonames")) dataArray = root.getJSONArray("geonames");
			if (dataArray == null) return markers;
				int top = Math.min(MAX, dataArray.length());
				for (int i = 0; i < top; i++) {					
					jo = dataArray.getJSONObject(i);
					Marker ma = processJSONObject(jo);
					if(ma!=null) markers.add(ma);
				}
		} catch (JSONException e) {
			logger.info("Exception: "+e.getMessage());
		}
		return markers;
	}
	
	public Marker processJSONObject(JSONObject jo) {
		if (jo==null) return null;
		
        Marker ma = null;
        if (	jo.has("title") && 
        		jo.has("lat") && 
        		jo.has("lng") && 
        		jo.has("elevation") && 
        		jo.has("wikipediaUrl")
        ) {
        	try {
        		ma = new Marker(
        				jo.getString("title"),
        				jo.getDouble("lat"),
        				jo.getDouble("lng"),
        				jo.getDouble("elevation"),
        				Color.WHITE);
        	} catch (JSONException e) {
        		logger.info("Exception: "+e.getMessage());
        	}
        }
        return ma;
	}
}
