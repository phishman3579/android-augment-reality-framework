package com.jwetherell.augmented_reality.data;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Color;

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
    
    public List<Marker> getMarkers() {
        for (int i=0; i<1; i++) {
            Marker marker = new Marker("ATL-"+i, 39.931269, -75.051261, 0, Color.YELLOW);
            cachedMarkers.add(marker);
        }
        return cachedMarkers;
    }
}
