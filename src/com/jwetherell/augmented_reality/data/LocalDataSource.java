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
        
        icon=BitmapFactory.decodeResource(res, R.drawable.black);
    }
    
    public List<Marker> getMarkers() {
        Marker atl = new IconMarker("ATL", 41.413287, -75.639206, 100, Color.DKGRAY, icon);
        cachedMarkers.add(atl);
        Marker home = new Marker("HOME", 41.348838, -75.548773, 1000, Color.YELLOW);
        cachedMarkers.add(home);
        
        for (int i=0; i<10; i++) {
            Marker marker = null;
            if (i%2==0) marker = new Marker("Test-"+i, 41.37093, -75.703801, 754, Color.LTGRAY);
            else marker = new IconMarker("Test-"+i, 41.37093, -75.703801, 754, Color.LTGRAY, icon);
            cachedMarkers.add(marker);
        }
        return cachedMarkers;
    }
}
