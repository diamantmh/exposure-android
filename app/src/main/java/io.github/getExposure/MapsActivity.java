package io.github.getExposure;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;

import com.google.android.gms.location.LocationListener;

import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
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
import com.google.android.gms.location.LocationListener;
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

// doesn't save state of activity, changing screen orientation/language can break it
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private static final int REQUEST_CHECK_SETTINGS = -1;

    //not quite sure what this does yet, but can't be -1, probably not negative
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    //ImageButton toListView;
    //ImageButton toProfileView;

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;

    private LocationRequest mLocationRequest;
    private boolean hasLocationPermissions;
    public static final String TAG = MapsActivity.class.getSimpleName();


    public final static String EXTRA_MESSAGE = "io.github.getExposure.BLURB";
    private boolean mRequestingLocationUpdates = true; // permission whether to request location updates
    private Location mLastLocation;
    private BreakIterator mLatitudeText;
    private BreakIterator mLongitudeText;
    private static Location mCurrentLocation;
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
        /*//
        System.out.println("GoogleAPICLient initialized");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
       // */
        //checkLocationPermission();
        // create location request
        ///createLocationRequest();
        //getCurrentLocationSettingsAndRequestChangeIfNecessary();


    }

    /**
     * Centeres map around location
     * @param location
     */
    private void makeUseOfNewLocation(Location location) {
        LatLng curr = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.addMarker(new MarkerOptions().position(curr).title("Marker at current"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(curr));
        System.out.println("new loc: Lat: " + location.getLatitude() + " Long: " + location.getLongitude());
    }

    /*
        protected void getCurrentLocationSettingsAndRequestChangeIfNecessary() {
            System.out.println("getCurrentLocationSettings() called");
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(mLocationRequest);
            PendingResult<LocationSettingsResult> result =
                    LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient,
                            builder.build());
            //location settings in the locationSettingsResult

            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    //final LocationSettingsStates =
                    result.getLocationSettingsStates();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.SUCCESS:
                            // All location settings are satisfied. The client can
                            // initialize location requests here.

                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            // Location settings are not satisfied, but this can be fixed
                            // by showing the user a dialog.
                            try {
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                status.startResolutionForResult(
                                        MapsActivity.this,
                                        REQUEST_CHECK_SETTINGS);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Location settings are not satisfied. However, we have no way
                            // to fix the settings so we won't show the dialog.

                            break;
                    }
                }
            });
        }
    */
    //create location request
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    // checks permission for accessing location, and requests if necessary
    protected void checkLocationPermission() {
        System.out.println("checkLocationPermission() called");
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                System.out.println("Permission is needed to run this app correcctly");

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        System.out.println("onRequestPermissionsResult() method called");
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    System.out.println("location permission granteddddd");

                } else {
                    System.out.println("location permission not granteddd");
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
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

    /*
    protected void myRequestLocationUpdates() {
        // Acquire a reference to the system Location Manager
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                makeUseOfNewLocation(location);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };

        // Register the listener with the Location Manager to receive location updates
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
    }
    */
    protected void startLocationUpdates() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    //TODO: what happens when user doesn't give location?
    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i(TAG, "Location services connected");
        System.out.println("onConnected() method called");
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            System.out.println("permission granted");
            if (mGoogleApiClient.isConnected()) {
                System.out.println("mGoogleAPI connected");
            }
            //PendingResult<Status> result =
            System.out.println("mLocationRequest: " + mLocationRequest.toString());
                    LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
            //System.out.println("result is: " + result.toString());
        }


/*
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            System.out.println("permission granted");
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            if (mLastLocation == null) {
                System.out.println("mLastLocation == null");
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                        mLocationRequest, this);
                //handleNewLocation(mLastLocation);
            } else {
                handleNewLocation(mLastLocation);
            }
        }
*/
        /*
        // Assume thisActivity is the current activity
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        // == PackageManager.PERMISSION_GRANTED or .PERMISSION_DENIED
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            if (mLastLocation == null) {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                        mLocationRequest, this);
            } else {
                handleNewLocation(mLastLocation);
            }
        }
        */
        /*
        if (mGoogleApiClient == null) {
            System.out.println("mGoogleApiClient is null");
        }
        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }
        // Assume thisActivity is the current activity
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        // == PackageManager.PERMISSION_GRANTED or .PERMISSION_DENIED
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            System.out.println("Permission granted");
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            if (mLastLocation != null) {
                System.out.println("Found last locatioN");
                mLatitudeText.setText(String.valueOf(mLastLocation.getLatitude()));
                mLongitudeText.setText(String.valueOf(mLastLocation.getLongitude()));
                LatLng curr = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                mMap.addMarker(new MarkerOptions().position(curr).title("Marker at current location, or Sydney"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(curr));
            } else {
                System.out.println("permission granted, but did not find last location");
            }
        } else { // permission denied
            System.out.println("Permission denied");
        }
        */
        /*
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
            /*} else { // user doesn't want to give location
                System.out.println("current location not available");
                LatLng rando = new LatLng(100, 151);

                // Add a marker in rando and move the camera
               // mMap.addMarker(new MarkerOptions().position(rando).title("Marker at rando location"));
               // mMap.moveCamera(CameraUpdateFactory.newLatLng(rando));
            }
        } catch (SecurityException s) {
            // pretty sure this happens whne there's no permissions?
            // not sure if it's different than prior else statement
            // user doesn't want to give location
            System.out.println("SecurityException: " + s.toString());
        }
        */
    }

    @Override
    public void onLocationChanged(Location location) {
        System.out.println("onlocationchanged called");
        makeUseOfNewLocation(location);
    }


    private void updateUI() {
        mLatitudeTextView.setText(String.valueOf(mCurrentLocation.getLatitude()));
        mLongitudeTextView.setText(String.valueOf(mCurrentLocation.getLongitude()));
        mLastUpdateTimeTextView.setText(mLastUpdateTime);
    }

    @Override
    protected void onPause() {
        super.onPause();
       // stopLocationUpdates();
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }

    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, (com.google.android.gms.location.LocationListener) this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    // TODO: something
    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Location services suspended");
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
        mMap.addMarker(new MarkerOptions().position(curr).title("Marker at Sydney"));
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

    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }
}
