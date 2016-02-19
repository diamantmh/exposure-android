package io.github.getExposure;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.facebook.FacebookSdk;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    ImageButton toListView;
    ImageButton toProfileView;

    private GoogleMap mMap;
    public final static String EXTRA_MESSAGE = "io.github.getExposure.BLURB";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        FacebookSdk.sdkInitialize(getApplicationContext());
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // buttons for switching views
        /*
        toListView = (ImageButton) findViewById(R.id.toListView);

        toListView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent listViewIntent = new Intent(getApplicationContext(), ListActivity.class);
                startActivity(listViewIntent);
            }
        });

        toProfileView = (ImageButton) findViewById(R.id.toProfileView);

        toProfileView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent profileViewIntent = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(profileViewIntent);
            }
        });
        */
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    /** Called when the user clicks the Search button */
    //TODO: implement searching thing

    public void search(View view) {
        // DO something
        Intent intent = new Intent(this, SearchActivity.class);
        EditText editText = (EditText) findViewById(R.id.search_exposure);
        String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    // Called when the user clicks the map/list button
    public void launchListView(View view) {
        Intent listViewIntent = new Intent(getApplicationContext(), ListActivity.class);
        startActivity(listViewIntent);
    }

    // Called when the user clicks the profile button
    public void launchProfileView(View view) {
        Intent profileViewIntent = new Intent(getApplicationContext(), ProfileActivity.class);
        startActivity(profileViewIntent);
    }

    // Called when the user clicks the post button
    //TODO: currently just goes back to map view
    public void launchPostView(View view) {
        /*
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra(EXTRA_MESSAGE, message);
        EditText editText = (EditText) findViewById(R.id.);
        String message = editText.getText().toString();
        */
        Intent postViewIntent = new Intent(getApplicationContext(), PostActivity.class);
        startActivity(postViewIntent);
    }

}
