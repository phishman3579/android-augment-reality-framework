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
        Marker marker1 = new Marker("ATL-1", 39.931269, -75.051261, 0, Color.YELLOW);
        cachedMarkers.add(marker1);
        Marker marker2 = new Marker("ATL-2", 39.931269, -75.051261, 0, Color.MAGENTA);
        cachedMarkers.add(marker2);
        return cachedMarkers;
    }
}
