package com.jwetherell.augmented_reality.data;

import java.util.List;

import com.jwetherell.augmented_reality.ui.Marker;


/**
 * This abstract class should be extended for new data sources.
 * 
 * @author Justin Wetherell <phishman3579@gmail.com>
 */
public abstract class DataSource {
    public abstract List<Marker> getMarkers();
}
