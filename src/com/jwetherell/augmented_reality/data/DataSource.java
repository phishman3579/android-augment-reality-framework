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
import com.jwetherell.augmented_reality.ui.Marker;
import android.content.res.Resources;
import android.graphics.Bitmap;

public abstract class DataSource {
	private static Logger logger = Logger.getLogger(DataSource.class.getSimpleName());
	
	protected abstract void createIcon(Resources res);
	
	public abstract Bitmap getBitmap();
	
	public abstract String createRequestURL(	double lat, 
												double lon, 
												double alt, 
												float radius, 
												String locale);
    
	public abstract List<Marker> parse(String url);
	
    protected static InputStream getHttpGETInputStream(String urlStr) {
    	InputStream is = null;
    	URLConnection conn = null;

    	try {
        	if (urlStr.startsWith("file://")) return new FileInputStream(urlStr.replace("file://", ""));

    		URL url = new URL(urlStr);
    		conn =  url.openConnection();
    		conn.setReadTimeout(10000);
    		conn.setConnectTimeout(10000);

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
}
