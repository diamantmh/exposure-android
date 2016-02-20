package io.github.getExposure;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import io.github.getExposure.post.PostActivity;
import io.github.getExposure.profile.ProfileActivity;

/**
 * Created by Michael on 2/19/2016.
 */
public class ViewLauncher extends AppCompatActivity {

    // Called when the user clicks the map/list button

    /**
     * Callback called when the user clicks the "Maps" button
     * Switches the activity to MapsActivity
     * @param view passed in for drawing/event handling
     */
    public void launchMapView(View view) {
        Intent listViewIntent = new Intent(view.getContext(), ListActivity.class);

        startActivity(listViewIntent);
    }

    /**
     * Callback called when the user clicks the "ListView" button
     * Switches the activity to ListActivity
     * @param view passed in for drawing/event handling
     */
    public void launchListView(View view) {
        Intent listViewIntent = new Intent(view.getContext(), ListActivity.class);
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
        startActivity(postViewIntent);
    }


}
