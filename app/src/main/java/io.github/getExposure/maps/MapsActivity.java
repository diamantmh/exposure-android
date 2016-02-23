package io.github.getExposure.maps;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;


import android.widget.Spinner;
import android.widget.Toast;

import com.facebook.FacebookSdk;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;

import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.VisibleRegion;

import java.io.File;
import java.util.Random;

import io.github.getExposure.ExposureFragmentActivity;
import io.github.getExposure.ListActivity;
import io.github.getExposure.database.DatabaseManager;
import io.github.getExposure.database.ExposureLocation;
import io.github.getExposure.post.LocationView;
import io.github.getExposure.post.PostActivity;
import io.github.getExposure.profile.ProfileActivity;
import io.github.getExposure.R;
/*
import android.widget.ImageButton;
import android.widget.Button;
import android.support.v4.content.ContextCompat;
import android.support.v4.app.ActivityCompat;
import android.net.Uri;
import android.location.LocationManager;
import com.google.android.gms.location.LocationListener;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.Context;
import android.Manifest;
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
import com.google.android.gms.maps.GoogleMapOptions;
import java.text.BreakIterator;
import java.text.DateFormat;
import java.util.Date;

*/
/**
 *  MapsActivity is the activity class responsible for the "map view" of Exposure.
 *  It allows users to browse pins based on the location of queries and the location given
 *  by their devices' GPS, and displays them on a map.
 *
 *  @author Michael Shintaku
 *  @version 0.5
 *  @since 2016-02-03
 *
 */

//TODO: save state of activity, changing screen orientation/language can break it
//TODO: get current phone location
public class MapsActivity extends ExposureFragmentActivity implements OnMapReadyCallback/*,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener */{

    //Latitude/longitude of the Paul G Allen Center
    private final static double CSE_LATITUDE = 47.6532295;
    private final static double CSE_LONGITUDE = -122.306897;
    //Latitude/longitude of Drumheller fountain
    private final static double DRUMHELLER_LATITUDE = 47.653808;
    private final static double DRUMHELLER_LONGITUDE = -122.307832;
    private GoogleMap mMap;
    private int currentFilter = 0; // Current filter to select which pins to display, to be implemented
                                   // in v1.0

    //private Location mLastLocation;

    private int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;// = 1;
    private int REQUEST_CHECK_SETTINGS;// = -1;
    private int MY_LOCATION_REQUEST_CODE;
    /*  Variables used in developing 1.0
    public final static String EXTRA_MESSAGE = "io.github.getExposure.BLURB";

    ImageButton toListView;
    ImageButton toProfileView;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private boolean hasLocationPermissions;
    public static final String TAG = MapsActivity.class.getSimpleName();
    private boolean mRequestingLocationUpdates = true; // permission whether to request location updates

    private BreakIterator mLatitudeText;
    private BreakIterator mLongitudeText;
    private static Location mCurrentLocation;
    private String mLastUpdateTime;
    private BreakIterator mLongitudeTextView;
    private BreakIterator mLatitudeTextView;
    private BreakIterator mLastUpdateTimeTextView;
    */

    /**
     * Method called when MapsActivity is active
     * initializes the mapFragment, spinner, and Facebook sdk
     * @param savedInstanceState passed in Bundle to create the activity off of
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        FacebookSdk.sdkInitialize(getApplicationContext());
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Add the spinner
        Spinner spinner = (Spinner) findViewById(R.id.Filters_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.maps_filter_spinner, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new SpinnerSelectListener());
    }
        /*
        Custom options, options implemented through xml
        Other way is through MapView class or MapFragment

        GoogleMapOptions options = new GoogleMapOptions();
        options.mapType(GoogleMap.MAP_TYPE_NORMAL)
                .zoomControlsEnabled(true).compassEnabled(true);
        */

        // Create an instance of GoogleAPIClient.
        /*
        System.out.println("GoogleAPICLient initialized");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        */
        /*
        checkLocationPermission();
        create location request
        createLocationRequest();
        getCurrentLocationSettingsAndRequestChangeIfNecessary();
        */

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     *
     * @param googleMap the GoogleMap to be manipulated
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        System.out.println("onMapReady() called");
        mMap = googleMap;
        /*
        get current location
        LatLng curr;
        if (mLastLocation != null) {
            System.out.println("Found a current location");
            curr = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        } else {
            LatLng seattle = new LatLng(47, -122);
            curr = seattle;
        }
        */

        checkLocationPermission();
        LatLng drum = new LatLng(DRUMHELLER_LATITUDE, DRUMHELLER_LONGITUDE);
        mMap.setOnInfoWindowClickListener(new MapsInfoWindowClickListener());
        // Add a marker near seattle and move the camera
        //mMap.addMarker(new MarkerOptions().position(drum).title("Drumheller Fountain"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(drum));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(15));

    }


    /**
     * Adds all of the pins that can be seen on the screen (at its location).
     * This method is a callback and is called when the "Apply Filter" button is pressed
     * @param view the view
     */
    public void addPins(View view) {


        /*
        //Testing methods
        Random r = new Random();
        for (int i = 0; i < 10; i++) {
            LatLng tmp = (new LatLng(r.nextInt(181)- 90, r.nextInt(361) - 180));
            String user = ""; // user who "founded" the location
            int number = -1; // number of photos at the same location
            //mMap.setInfoWindowAdapter(new MapsInfoWindow(this, "hihi"));
            mMap.addMarker(new MarkerOptions().position(tmp).title("Pin #" + i)
                    .snippet("(Lat, Long): (" + tmp.latitude + ", " + tmp.longitude + ") " +
                            "click to view location"));
        }
*/
        // temp functionality for dmeo
        LatLng drum = new LatLng(DRUMHELLER_LATITUDE, DRUMHELLER_LONGITUDE);
        mMap.setOnInfoWindowClickListener(new MapsInfoWindowClickListener());
        // Add a marker near seattle and move the camera
        mMap.addMarker(new MarkerOptions().position(drum).title("Drumheller Fountain"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(drum));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
        /*
        DatabaseManager db = new DatabaseManager();
        // need to get all ID's from db
        VisibleRegion visibleRegion = mMap.getProjection().getVisibleRegion();
        LatLngBounds bounds = visibleRegion.latLngBounds;
        LatLng ne = bounds.northeast;
        LatLng sw = bounds.southwest;
        LatLng center = bounds.getCenter();
        float originLat = (float) center.latitude;
        float originLon = (float) center.longitude;
        float radiusLat = (float) (ne.latitude - sw.latitude);
        float radiusLon = (float) (ne.longitude - sw.longitude);
        System.out.println("origin lat/lon: " + originLat + ", " + originLon + " radius lat/lon: " +
            radiusLat + ", " + radiusLon);
        ExposureLocation[] locationsInRadius = db.getLocationsInRadius(originLat, originLon, radiusLat, radiusLon);
        System.out.println("Size of locations inradius: " + locationsInRadius.length);
        for (ExposureLocation t : locationsInRadius) {
            System.out.println("In loop");
            double tLat = t.getLat();
            double tLon = t.getLon();
            LatLng temp = new LatLng(tLat, tLon);
            String name = t.getName();
            // t.toString() should be the location t's name
            mMap.addMarker(new MarkerOptions().position(temp).title(name));
        }
        */
    }

    public void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            System.out.println("thanksfor lettingmetracku");
        } else {
            System.out.println("why dafuck u not allow me to TRACK u");
            // Show rationale and request permission.
        }
    }

    /*
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == MY_LOCATION_REQUEST_CODE) {
            if (permissions.length == 1 &&
                    permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
            } else {
                // Permission was denied. Display an error message.
            }
        }

    */

    /**
     * Callback called when the user clicks the "search" button.
     * It centers map perspective around the latitude/longitude coordinates given in the
     * app's text box
     * @param view passed in for drawing/event handling
     */
    public void search(View view) {
        /*
        Intent intent = new Intent(this, SearchActivity.class);
        EditText editText = (EditText) findViewById(R.id.searchView);
        String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
        startActivity(mapIntent);
        */
        EditText editText = (EditText) findViewById(R.id.search_exposure);
        String searchText = editText.getText().toString();
        System.out.println("Searchtext: " + searchText);
        String[] LatAndLong = searchText.split(",");
        System.out.println("LatAndLong length: " + LatAndLong.length);
        for (String s : LatAndLong) {
            System.out.print("element: \n");
        }
        if (LatAndLong.length != 2) {
            System.out.println("error");
        }
        double lat = Double.parseDouble(LatAndLong[0]);
        double lng = Double.parseDouble(LatAndLong[1]);
        LatLng query = new LatLng(lat, lng);
        mMap.addMarker(new MarkerOptions().position(query).title("(" + lat + ", " + lng + ")"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(query));

    }

    /**
     * Internal listener class for MapsInfoWindowClicks
     * Class used for it's callback method that's called when a marker's info window is clicked
     */
    private class MapsInfoWindowClickListener implements GoogleMap.OnInfoWindowClickListener {
        /**
         * Callback called when the marker is pressed
         * Switches the activity from MapsActivity to PostActivity
         * @param marker the marker to be modified with the clicking functionality
         */
        //TODO: currently just goes back to list view
        @Override
        public void onInfoWindowClick(Marker marker) {
            String input = "Pin clicked";
            System.out.println("Pin clicked");
            CharSequence str =  input.subSequence(0, input.length());
            Toast.makeText(MapsActivity.this, str, Toast.LENGTH_SHORT).show();
            // change this to post view
            Intent locationViewIntent = new Intent(getApplicationContext(), LocationView.class);
            locationViewIntent.putExtra("photo", "");
            locationViewIntent.putExtra("name", "Drumheller Fountain");
            locationViewIntent.putExtra("description", "Fountain of dreams.");
            locationViewIntent.putExtra("categories", "Sunny, Summer");
            startActivity(locationViewIntent);
        }
    }

    /**
     * Internal listener class for Spinners
     * Acts when a spinner's option is selected
     */
    private class SpinnerSelectListener extends Activity implements AdapterView.OnItemSelectedListener {

        /**
         * Sets the filter, to be applied when "Apply Filter" is pressed
         * @param parent the AdapterView
         * @param view passed in for drawing/event handling
         * @param pos the pos of the item in the spinner
         * @param id the id of the selection
         */
        public void onItemSelected(AdapterView<?> parent, View view,
                                   int pos, long id) {
            // An item was selected. You can retrieve the selected item using
            // parent.getItemAtPosition(pos)
            currentFilter = pos;
            System.out.println(parent.getItemAtPosition(pos)); // for debugging
        }

        /**
         * Does nothing, since the filter's options have not been selected
         * @param parent the parent passed in
         */
        public void onNothingSelected(AdapterView<?> parent) {
            // Another interface callback
            System.out.println("Nothing selected"); // for debugging
        }
    }

    /**
     * Centers map around location
     * @param location
     */
    /*
    private void makeUseOfNewLocation(Location location) {
        LatLng curr = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.addMarker(new MarkerOptions().position(curr).title("Marker at current"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(curr));
        System.out.println("new loc: Lat: " + location.getLatitude() + " Long: " + location.getLongitude());
    }

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


    //create location request
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }
*/
    /*
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
*/

    /**
     * What to do onStart()
     */
    /*
    protected void onStart() {
        System.out.println("onStart() method called");
        mGoogleApiClient.connect();
        super.onStart();
    }

    /**
     * What to do onStop()
     */
    /*
    protected void onStop() {
        System.out.println("onStop() method called");
        mGoogleApiClient.disconnect();
        super.onStop();
    }


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


        if (mGoogleApiClient == null) {
            System.out.println("mGoogleApiClient is null");
        }
        if (mRequestingLocationUpdates) {
            startLocationUpdates();

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
    */
}


