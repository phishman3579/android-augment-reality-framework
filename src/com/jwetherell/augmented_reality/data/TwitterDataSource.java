package com.jwetherell.augmented_reality.data;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.jwetherell.augmented_reality.R;
import com.jwetherell.augmented_reality.ui.Marker;
import com.jwetherell.augmented_reality.ui.IconMarker;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

/**
 * This class extends DataSource to fetch data from Twitter.
 * 
 * @author Justin Wetherell <phishman3579@gmail.com>
 */
public class TwitterDataSource extends DataSource {
	private static final Logger logger = Logger.getLogger(TwitterDataSource.class.getSimpleName());
	private static final String URL = "http://search.twitter.com/search.json";

	private static Bitmap icon = null;

	public TwitterDataSource(Resources res) {
		if (res==null) return;
		
		createIcon(res);
	}
	
	protected void createIcon(Resources res) {
		if (res==null) return;
		
		icon=BitmapFactory.decodeResource(res, R.drawable.twitter);
	}
	
	public Bitmap getBitmap() {
		return icon;
	}
	
	public String createRequestURL(double lat, double lon, double alt, float radius, String locale) {
		return URL+"?geocode=" + lat + "%2C" + lon + "%2C" + Math.max(radius, 1.0) + "km";
	}
	
	public List<Marker> parse(String url) {
		if (url==null) return null;
		
		InputStream stream = null;
    	stream = getHttpGETInputStream(url);
    	if (stream==null) return null;
    	
    	String string = null;
    	string = getHttpInputString(stream);
    	if (string==null) return null;
    	
    	JSONObject json = null;
    	try {
    		json = new JSONObject(string);
    	} catch (JSONException e) {
    		logger.info("Exception: "+e.getMessage());
    	}
    	if (json==null) return null;
    	
    	return parse(json);
	}
	
	public List<Marker> parse(JSONObject root) {
		if (root==null) return null;
		
		JSONObject jo = null;
		JSONArray dataArray = null;
    	List<Marker> markers=new ArrayList<Marker>();

		try {
			if(root.has("results")) dataArray = root.getJSONArray("results");
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
		
		if (!jo.has("geo")) return null;
		
		Marker ma = null;
		try {
			Double lat=null, lon=null;
			
			if(!jo.isNull("geo")) {
				JSONObject geo = jo.getJSONObject("geo");
				JSONArray coordinates = geo.getJSONArray("coordinates");
				lat=Double.parseDouble(coordinates.getString(0));
				lon=Double.parseDouble(coordinates.getString(1));
			} else if(jo.has("location")) {
				Pattern pattern = Pattern.compile("\\D*([0-9.]+),\\s?([0-9.]+)");
				Matcher matcher = pattern.matcher(jo.getString("location"));

				if(matcher.find()){
					lat=Double.parseDouble(matcher.group(1));
					lon=Double.parseDouble(matcher.group(2));
				}					
			}
			if(lat!=null) {
				String user=jo.getString("from_user");

				ma = new IconMarker(
						user+": "+jo.getString("text"), 
						lat, 
						lon, 
						0,
						Color.RED,
						icon);
			}
		} catch (Exception e) {
			logger.info("Exception: "+e.getMessage());
		}
		return ma;
	}
}
