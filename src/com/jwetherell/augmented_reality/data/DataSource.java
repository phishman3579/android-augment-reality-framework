package com.jwetherell.augmented_reality.data;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.logging.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import com.jwetherell.augmented_reality.ui.Marker;

/**
 * This abstract class should be extended for new data sources. It has many methods to get and parse data from numerous
 * web sources.
 * 
 * @author Justin Wetherell <phishman3579@gmail.com>
 */
public abstract class DataSource {
	private static final Logger logger = Logger.getLogger(DataSource.class.getSimpleName());

	protected static final int MAX = 5;
	
	public abstract String createRequestURL(	double lat, 
												double lon, 
												double alt, 
												float radius, 
												String locale);

	public abstract List<Marker> parse(JSONObject root);
	
    protected static InputStream getHttpGETInputStream(String urlStr) {
    	if (urlStr==null) return null;
    	
    	InputStream is = null;
    	URLConnection conn = null;

    	try {
        	if (urlStr.startsWith("file://")) return new FileInputStream(urlStr.replace("file://", ""));

    		URL url = new URL(urlStr);
    		conn =  url.openConnection();
    		conn.setReadTimeout(2000);
    		conn.setConnectTimeout(2000);

    		is = conn.getInputStream();

    		return is;
    	} catch (Exception ex) {
    		try {
    			is.close();
    		} catch (Exception e) {
    			// Ignore
    		}
    		try {
    			if(conn instanceof HttpURLConnection) ((HttpURLConnection)conn).disconnect();
    		} catch (Exception e) {
    			// Ignore
    		}
    		logger.info("Exception: "+ex.getMessage());
    	}
    	
    	return null;
    }
    
    protected String getHttpInputString(InputStream is) {
    	if (is==null) return null;
    	
    	BufferedReader reader = new BufferedReader(new InputStreamReader(is), 8 * 1024);
    	StringBuilder sb = new StringBuilder();

    	try {
    		String line;
    		while ((line = reader.readLine()) != null) {
    			sb.append(line + "\n");
    		}
    	} catch (IOException e) {
    		e.printStackTrace();
    	} finally {
    		try {
    			is.close();
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
    	}
    	return sb.toString();
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
}
