package io.github.getExposure.post;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import io.github.getExposure.R;
import io.github.getExposure.maps.MapsActivity;
import io.github.getExposure.profile.ProfileActivity;

public class ExposureListView extends ListActivity {

    private ListView thisListView;
    private String[] lisst = {"test1", "test2", "test3"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);

        thisListView = getListView();
        ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, lisst);
        thisListView.setAdapter(listAdapter);
        thisListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(ExposureListView.this, "Test", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onListItemClick(android.widget.ListView l, View v, int position, long id) {
        // Do something when a list item is clicked
    }


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
