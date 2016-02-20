package io.github.getExposure;

import android.content.Context;
import android.graphics.Picture;
import android.view.View;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

/**
 * Basic class to create a custom info window for pins on the map.
 * Most of the methods are callbacks
 *
 * Author: Michael Shintaku
 */

//TODO: Most of the implementation, currently using default info windows
public class MapsInfoWindow implements GoogleMap.InfoWindowAdapter {

    private Context context;
    private String text;
    private Picture pic;

    /**
     * Returns view used for entire info window
     * If view is changed after method called, it is not updated
     * @param marker
     * @return
     */
    @Override
    public View getInfoWindow(Marker marker) {
        return new MapsInfoWindowView(context, text/*, pic*/);
    }

    public MapsInfoWindow(Context context, String text/*, Picture pic*/) {
        this.context = context;
        this.text = text;
        this.pic = pic;
    }

    /**
     * Called if getInfoWindow returns null.
     * If this returns null, default info window is used
     * @param marker
     * @return
     */
    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }
}
