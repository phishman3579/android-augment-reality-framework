package com.jwetherell.augmented_reality.data;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

import com.jwetherell.augmented_reality.R;
import com.jwetherell.augmented_reality.ui.IconMarker;
import com.jwetherell.augmented_reality.ui.Marker;

/**
 * This class extends DataSource to fetch data from Google Buzz.
 * 
 * @author Justin Wetherell <phishman3579@gmail.com>
 */
public class BuzzDataSource extends NetworkDataSource {

    private static final String BASE_URL = "https://www.googleapis.com/buzz/v1/activities/search?alt=json&max-results=40";

    private static Bitmap icon = null;

    public BuzzDataSource(Resources res) {
        if (res == null) throw new NullPointerException();

        createIcon(res);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String createRequestURL(double lat, double lon, double alt, float radius, String locale) {
        return BASE_URL + "&lat=" + lat + "&lon=" + lon + "&radius=" + (radius * 1000.0f);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Marker> parse(JSONObject root) {
        if (root == null) throw new NullPointerException();

        JSONObject jo = null;
        JSONArray dataArray = null;
        List<Marker> markers = new ArrayList<Marker>();

        try {
            if (root.has("data") && root.getJSONObject("data").has("items")) dataArray = root.getJSONObject("data").getJSONArray("items");
            if (dataArray == null) return markers;
            int top = Math.min(MAX, dataArray.length());
            for (int i = 0; i < top; i++) {
                jo = dataArray.getJSONObject(i);
                Marker ma = processJSONObject(jo);
                if (ma != null) markers.add(ma);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return markers;
    }

    private void createIcon(Resources res) {
        if (res == null) throw new NullPointerException();

        icon = BitmapFactory.decodeResource(res, R.drawable.buzz);
    }

    private Marker processJSONObject(JSONObject jo) {
        if (jo == null) throw new NullPointerException();

        Marker ma = null;
        if (jo.has("title") && jo.has("geocode")) {
            try {
                ma = new IconMarker(jo.getString("title"), Double.valueOf(jo.getString("geocode").split(" ")[0]), Double.valueOf(jo.getString("geocode").split(
                        " ")[1]), 0, Color.GREEN, icon);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return ma;
    }
}
