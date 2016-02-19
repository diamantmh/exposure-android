package io.github.getExposure;

import android.content.Context;
import android.graphics.Picture;
import android.view.View;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by Michael on 2/19/2016.
 * Class for supporting the pins' info windows
 */
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
        //idk what context and picture are
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
