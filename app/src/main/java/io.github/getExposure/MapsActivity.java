package io.github.getExposure;

import android.content.Intent;
import android.content.IntentSender;
import android.location.LocationListener;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import android.location.Location;
import com.facebook.FacebookSdk;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.BreakIterator;
import java.text.DateFormat;
import java.util.Date;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        ConnectionCallbacks, OnConnectionFailedListener {
    private static final int REQUEST_CHECK_SETTINGS = -1;
    //ImageButton toListView;
    //ImageButton toProfileView;

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;

    private LocationRequest mLocationRequest;

    public final static String EXTRA_MESSAGE = "io.github.getExposure.BLURB";
    private boolean mRequestingLocationUpdates = true; // permission whether to request location updates
    private Location mLastLocation;
    private BreakIterator mLatitudeText;
    private BreakIterator mLongitudeText;
    private Location mCurrentLocation;
    private String mLastUpdateTime;
    private BreakIterator mLongitudeTextView;
    private BreakIterator mLatitudeTextView;
    private BreakIterator mLastUpdateTimeTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        FacebookSdk.sdkInitialize(getApplicationContext());
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Create an instance of GoogleAPIClient.
        //GoogleApiClient mGoogleApiClient = null;
        if (mGoogleApiClient == null) {
            System.out.println("GoogleAPICLient initialized");
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

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
                Intent profileViewIntent = new Intent(getApplicationContext(), ProfileViewActivity.class);
                startActivity(profileViewIntent);
            }
        });
        */
    }


    /**
     * What to do onStart()
     */
    protected void onStart() {
        System.out.println("onStart() method called");
        mGoogleApiClient.connect();
        super.onStart();
    }

    /**
     * What to do onStop()
     */
    protected void onStop() {
        System.out.println("onStop() method called");
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    //TODO: what happens when user doesn't give location?
    @Override
    public void onConnected(Bundle connectionHint) {
        System.out.println("onConnected() method called");
        try {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            if (mLastLocation != null) {
                System.out.println("Found last locatioN");
                mLatitudeText.setText(String.valueOf(mLastLocation.getLatitude()));
                mLongitudeText.setText(String.valueOf(mLastLocation.getLongitude()));
                /*
                LatLng curr = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                mMap.addMarker(new MarkerOptions().position(curr).title("Marker at current location, or Sydney"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(curr));
                */
                // does this work?
                // mCurrentLocation = mLastLocation;
            } else { // user doesn't want to give location
                System.out.println("current location not available");
            }
        } catch (SecurityException s) {
            // pretty sure this happens whne there's no permissions?
            // not sure if it's different than prior else statement
            // user doesn't want to give location
            System.out.println("SecurityException: " + s.toString());
        }
    }

    // TODO: something
    @Override
    public void onConnectionSuspended(int i) {
        // do something
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
        System.out.println("onMapReady() called");
        mMap = googleMap;
        LatLng curr;
        if (mLastLocation != null) {
            System.out.println("Found a current location");
            curr = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        } else {
            LatLng sydney = new LatLng(-34, 151);
            curr = sydney;
        }

        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(curr).title("Marker at current location, or Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(curr));
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
        Intent profileViewIntent = new Intent(getApplicationContext(), ProfileViewActivity.class);
        startActivity(profileViewIntent);
    }

    // Called when the user clicks the post button
    //TODO: currently just goes back to map view
    public void launchPostView(View view) {
        /*
        Intent intent = new Intent(this, ProfileViewActivity.class);
        intent.putExtra(EXTRA_MESSAGE, message);
        EditText editText = (EditText) findViewById(R.id.);
        String message = editText.getText().toString();
        */
        Intent postViewIntent = new Intent(getApplicationContext(), MapsActivity.class);
        startActivity(postViewIntent);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
