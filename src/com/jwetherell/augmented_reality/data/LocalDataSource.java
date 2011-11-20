package com.jwetherell.augmented_reality.data;

import java.util.ArrayList;
import java.util.List;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

import com.jwetherell.augmented_reality.R;
import com.jwetherell.augmented_reality.ui.IconMarker;
import com.jwetherell.augmented_reality.ui.Marker;


/**
 * This class should be used as a example local data source. It is an example 
 * of how to add data programatically. You can add data either programatically, 
 * SQLite or through any other source.
 * 
 * @author Justin Wetherell <phishman3579@gmail.com>
 */
public class LocalDataSource extends DataSource{
    private List<Marker> cachedMarkers = new ArrayList<Marker>();
    private static Bitmap icon = null;
    
    public LocalDataSource(Resources res) {
        if (res==null) throw new NullPointerException();
        
        createIcon(res);
    }
    
    protected void createIcon(Resources res) {
        if (res==null) throw new NullPointerException();
        
        icon=BitmapFactory.decodeResource(res, R.drawable.icon);
    }
    
    public List<Marker> getMarkers() {
        for (int i=0; i<2; i++) {
            Marker marker = null;
            if (i%2==0) marker = new IconMarker("ATL-"+i, 39.931269, -75.051261, 0, Color.YELLOW, icon);
            else  marker = new Marker("ATL-"+i, 39.931269, -75.051261, 0, Color.YELLOW);
            cachedMarkers.add(marker);
        }
        return cachedMarkers;
    }
}
