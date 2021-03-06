package io.github.getExposure;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import io.github.getExposure.maps.*;
import io.github.getExposure.maps.ExposureListView;
import io.github.getExposure.post.PostActivity;
import io.github.getExposure.profile.ProfileActivity;

/**
 * Class to abstract button functionality for view switcher/screen taskbar
 *  author Michael Shintaku
 *  @version 1.0
 *  @since 2016-02-22
 */
public class ExposureFragmentActivity extends FragmentActivity {


    /**
     * Callback called when the user clicks the "Maps" button
     * Switches the activity to MapsActivity
     * @param view passed in for drawing/event handling
     */
    public void launchMapView(View view) {
        Intent mapViewIntent = new Intent(view.getContext(), MapsActivity.class);
        startActivity(mapViewIntent);
    }

    /**
     * Callback called when the user clicks the "ListView" button
     * Switches the activity to ListActivity
     * @param view passed in for drawing/event handling
     */
    public void launchListView(View view) {
        Intent listViewIntent = new Intent(view.getContext(), ExposureListView.class);
        startActivity(listViewIntent);
    }

    /**
     * Callback called when the user clicks the "Profile" button
     * Switches the activity to ProfileViewActivity
     * @param view passed in for drawing/event handling
     */
    public void launchProfileView(View view) {
        Intent profileViewIntent = new Intent(view.getContext(), ProfileActivity.class);
        startActivity(profileViewIntent);
    }

    /**
     * Callback called when the user clicks the "Post" button
     * Switches the activity to PostActivity
     * @param view passed in for drawing/event handling
     */
    public void launchPostView(View view) {
        Intent postViewIntent = new Intent(view.getContext(), PostActivity.class);
        postViewIntent.putExtra("add_photo", false);
        startActivity(postViewIntent);
    }

}
