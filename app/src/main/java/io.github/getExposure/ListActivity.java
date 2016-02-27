package io.github.getExposure;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import io.github.getExposure.maps.MapsActivity;
import io.github.getExposure.post.PostActivity;
import io.github.getExposure.profile.ProfileActivity;

/**
 *  ListActivity is the activity class responsible for the "list view" of Exposure.
 *  It allows users to browse pins based on the location of queries and the location given
 *  by their devices' GPS, but displays them in a sorted list.
 *
 *  @author Michael Shintaku
 *  @version 0.5
 *  @since 2016-02-07
 *
 */
public class ListActivity extends ExposureFragmentActivity {

    /**
     * Method called when MapsActivity is active
     * initializes the mapFragment, spinner, and Facebook sdk
     * @param savedInstanceState passed in Bundle to create the activity off of
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

    }
}
