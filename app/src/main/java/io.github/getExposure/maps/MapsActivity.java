package io.github.getExposure.maps;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;


import android.widget.Spinner;
import android.widget.Toast;

import com.facebook.FacebookSdk;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;

import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.VisibleRegion;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.github.getExposure.ExposureFragmentActivity;
import io.github.getExposure.database.Category;
import io.github.getExposure.database.Comment;
import io.github.getExposure.database.DatabaseManager;
import io.github.getExposure.database.ExposureLocation;
import io.github.getExposure.post.LocationView;
import io.github.getExposure.R;

/**
 *  MapsActivity is the activity class responsible for the "map view" of Exposure.
 *  It allows users to browse pins based on the location of queries and the location given
 *  by their devices' GPS, and displays them on a map.
 *
 *  @author Michael Shintaku
 *  @version 1.0
 *  @since 2016-02-03
 *
 */

public class MapsActivity extends ExposureFragmentActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, ActivityCompat.OnRequestPermissionsResultCallback, LocationListener, OnMapReadyCallback {

    //Latitude/longitude of the Paul G Allen Center
    private final static double CSE_LATITUDE = 47.6532295;
    private final static double CSE_LONGITUDE = -122.306897;

    //Latitude/longitude of Drumheller fountain
    private final static double DRUMHELLER_LATITUDE = 47.653808;
    private final static double DRUMHELLER_LONGITUDE = -122.307832;

    private static final int MAPS_LOCATION_REQUEST_CODE = 42; // unique request code to maps
    protected static final int SEARCH_RESULT_CODE = 39; // unique result code for the search functionality

    // Fields for interval of location requests, in seconds
    private static final int LOCATION_REQUEST_INTERVAL = 10;
    private static final int LOCATION_REQUEST_INTERVAL_FASTEST = 5;
    private static final int CITY_ZOOM_LEVEL = 10; // default zoom level for the maps

    private GoogleMap mMap;
    private long currentFilter; // Current filter to select which pins to display, corresponding
                                   // to the setting on the spinner (drop down menu)
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;
    private Location mCurrentLocation;
    private String mLastUpdateTime;
    private boolean canRequestLocation;
    private AddressResultReceiver mResultReceiver;
    private DatabaseManager db;
    private Map<Marker, ExposureLocation> findPin;
    private List<ExposureLocation> listOfCurrentLocations;

    /**
     * Method called when MapsActivity is active
     * initializes the database functionality, mapFragment, spinner, permissions, and Facebook sdk
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
        // Specify the layout to use
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new SpinnerSelectListener());

        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Get location permissions
            String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};
            ActivityCompat.requestPermissions(this, permissions, MAPS_LOCATION_REQUEST_CODE);
            //createLocationRequest();
        }
        createLocationRequest();
        // Initialize database
        db = new DatabaseManager();
        findPin = new HashMap<Marker, ExposureLocation>();

    }

    /**
     * Called to create a location request with settings for the GoogleApiClient
     * Factor of 1000 because the methods take in milliseconds.
     */
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(LOCATION_REQUEST_INTERVAL * 1000);
        mLocationRequest.setFastestInterval(LOCATION_REQUEST_INTERVAL_FASTEST * 1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /**
     * Called when the user clicks the center button from the Maps UI.
     * Centers the map over the user's current location, last location, or makes a new location request
     * if the prior two locations are unavailable.
     * @param view the View passed in to be manipulated
     */
    public void center(View view) {
        // Permissions for getting location
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(MapsActivity.this, "Cannot find current locations, need permission.", Toast.LENGTH_SHORT).show();
        } else { // Permission granted
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            // Set to most accurate location if available
            if (mCurrentLocation != null) {
                //Toast.makeText(MapsActivity.this, "currentLocation found", Toast.LENGTH_SHORT).show();
                LatLng curr = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLng(curr));
            }
            // Set to last location if available
            else if (mLastLocation != null) {
                //Toast.makeText(MapsActivity.this, "LastLocation found", Toast.LENGTH_SHORT).show();
                LatLng curr = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLng(curr));
            }
            // Start a new location request service, and set it to the found location
            // Or remind the user that permissions are needed for this function
            else {
                startLocationUpdates();
            }
        }
    }

    /**
     * Called when the location updates need to start (especially if there are no previous locations
     * stored, either through locations recorded through this app or any other app)
     */
    private void startLocationUpdates() {
        System.out.println("startLocationUpdates");
        Toast.makeText(MapsActivity.this, "startLocationUpdates", Toast.LENGTH_SHORT).show();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // if we have no permissions to find the user's location, do nothing
            Toast.makeText(MapsActivity.this, "Location permissions required.", Toast.LENGTH_SHORT).show();
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }


    /**
     * Callback called when the permissions have been requested and a result has been found.
     * @param requestCode the request code passed in from requestPermissions
     * @param permissions the requested permissions, cannot be null
     * @param grantResults the results for the requested permissions
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(MapsActivity.this, "Thanks for allowing permissions", Toast.LENGTH_SHORT).show();
            canRequestLocation = true;
        } else {
            Toast.makeText(MapsActivity.this, "You have not allowed permissions, we will not be able to center" +
                    "at your location.", Toast.LENGTH_SHORT).show();
            canRequestLocation = false;
        }
    }

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
        //defaults map to center at UW's Drumheller Fountain
        LatLng drum = new LatLng(DRUMHELLER_LATITUDE, DRUMHELLER_LONGITUDE);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(drum));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(CITY_ZOOM_LEVEL));

    }


    /**
     * Finds all of the pins that can be seen on the screen (at its location), and prepares to
     * add them to the map.
     * This method is a callback and is called when the "Apply Filter" button is pressed.
     * @param view the view
     */
    public void addPinsHelper(View view) {
        Toast.makeText(MapsActivity.this, "Loading pins...", Toast.LENGTH_SHORT).show();
        mMap.setOnInfoWindowClickListener(new MapsInfoWindowClickListener());
        VisibleRegion visibleRegion = mMap.getProjection().getVisibleRegion();
        LatLngBounds bounds = visibleRegion.latLngBounds;
        LatLng ne = bounds.northeast;
        LatLng sw = bounds.southwest;
        LatLng center = bounds.getCenter();
        float originLat = (float) center.latitude;
        float originLon = (float) center.longitude;
        float radiusLat = (float) (ne.latitude - sw.latitude);
        float radiusLon = (float) (ne.longitude - sw.longitude);
        new GetLocationsTask().execute(originLat, originLon, radiusLat, radiusLon);
    }

    /**
     * Called once the locations have been found, this method adds markers to the map for locations
     * that pass through the current filter.
     */
    protected void addPins() {
        if (listOfCurrentLocations == null) {
            throw new IllegalStateException("listOfCurrentLocations cannot be null, don't call this method directly");
        }
        mMap.setOnInfoWindowClickListener(new MapsInfoWindowClickListener());
        mMap.clear(); // clear markers of prior filter

        // place 1 pin per location
        for (ExposureLocation e: listOfCurrentLocations) {
            LatLng temp = new LatLng(e.getLat(), e.getLon());
            if (hasApplicableCategory(e.getCategories(), currentFilter)) {
                // if it matches the current category, we add it
                Marker currentMarker = mMap.addMarker(new MarkerOptions().position(temp).title(e.getName()).snippet("Click here for more info"));
                findPin.put(currentMarker, e);
            }
        }
        Toast.makeText(MapsActivity.this, "Pins placed on screen.", Toast.LENGTH_SHORT).show();
    }

    /**
     * Returns true if the currFilter (setting on the spinner) corresponds to a category in input
     * @param input the Categories
     * @param currFilter the current filter to compare
     * @return the int representing the categories it contains
     */
    private boolean hasApplicableCategory(Set<Category> input, long currFilter) {
        if (currFilter == 0) { // filter is "All"
            return true;
        }
        for (Category c: input) {
            System.out.println("category id: " + c.getId());
            if (c.getId() == currFilter) {
                return true;
            }
        }
        return false;
    }

    /**
     * Callback called when the user clicks the "search" button.
     * It centers map perspective around the latitude/longitude coordinates given in the
     * app's text box
     * @param view passed in for drawing/event handling
     */
    public void search(View view) {
        if (!mGoogleApiClient.isConnected()) {
            throw new IllegalStateException("google api client needs to be connected");
        }

        EditText editText = (EditText) findViewById(R.id.search_exposure);
        String searchText = editText.getText().toString();

        mResultReceiver = new AddressResultReceiver(new Handler());
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra("searchText", searchText);
        intent.putExtra("receiver", mResultReceiver);
        startService(intent);
    }


    /**
     * Callback when the gps location is changed
     * @param location the location the device is now at
     */
    @Override
    public void onLocationChanged(Location location) {
        Toast.makeText(MapsActivity.this, "onLocationChanged", Toast.LENGTH_SHORT).show();

        // first time location being updated and no other locations in device's history
        // move it to that location, else store it for the next button click
        if (mCurrentLocation == null && mLastLocation == null) {
            LatLng curr = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLng(curr));
        }
        mCurrentLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());

    }

    /**
     * Callback called asynchronously after succesfully calling connect().
     * The app can now use the GoogleAPiClient safely
     * @param connectionHint the Bundle of data provided to clients by Google Play services, can be
     *                       null
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        // No code needed, always connected by the time the user can interact with the GoogleApiClient
        // Method needed for interface
    }

    /**
     * Callback called when activity is paused.
     */
    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    /**
     * Callback called when activity is to be resumed (from state of not resumed)
     */
    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected() && !canRequestLocation) {
            startLocationUpdates();
        }
    }

    /**
     * Method to stop the service from calling location updates (and save battery!)
     */
    private void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }

    /**
     * Callback when error connecting client to google service
     * @param connectionResult the result of the attempt to connect
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // If there is no google services, it just can't retrieve current location
        // but can do all other functionality
        System.out.println("Connection failed, contents: " + connectionResult.describeContents());
        System.out.println("                   toString: " + connectionResult.toString());
    }

    /**
     * Callback when client is temporarily disconnected
     * @param i the int representing the cause of the suspension
     */
    @Override
    public void onConnectionSuspended(int i) {
        System.out.println("Connection suspended, cause: " + i);
    }

    /**
     * Callback called after this.onCreate(Bundle) is called
     */
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    /**
     * Callback called after the activity is no longer visible to user
     */
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    /**
     * Asynchronous inner class used to find the locations, given an origin and lat/long radiuses,
     * and perform it on a separate thread/in the background.
     */
    private class GetLocationsTask extends AsyncTask<Float, Void, ExposureLocation[]> {

        /**
         * Called when this.execute(params) is called, and finds the locations in the area around
         * the origin.
         * @param params the paramaters passed in
         * @return ExposureLocation[] the locations within the origin (params[0], params[1]) and it's
         * lat/long radius (params[2], params[3])
         */
        @Override
        protected ExposureLocation[] doInBackground(Float... params) {
            // remember paramaters are: originLat, originLon, radiusLat, radiusLon
            return db.getLocationsInRadius(params[0], params[1], params[2], params[3]);
        }

        /**
         * Called when doInBackground finishes, and prepares this activity to load the photos/locations
         * for the pins
         * @param result the locations within the origin and lat/long radius
         */
        protected void onPostExecute(ExposureLocation[] result) {
            listOfCurrentLocations = new ArrayList<>();
            for (ExposureLocation e: result) {
                listOfCurrentLocations.add(e);
            }
            addPins();
        }
    }


    /**
     * Callback class called when the asynchronous service to fetch the geocoded location returns
     * (FetchAddressIntentService)
     */
    private class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            LatLng query = resultData.getParcelable("address");
            if (query == null) {
                Toast.makeText(MapsActivity.this, "No result found", Toast.LENGTH_SHORT).show();
            } else {
                // just a pin for showing the searched location, not for actual clicking, so listener
                // is null
                mMap.setOnInfoWindowClickListener(null);
                mMap.addMarker(new MarkerOptions().position(query).title(resultData.getString("searchText")));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(query));
                addPinsHelper(getCurrentFocus());
            }
        }
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
            System.out.println("Pin clicked");
            Toast.makeText(MapsActivity.this, "Pin clicked", Toast.LENGTH_SHORT).show();
            // change this to post view
            ExposureLocation currentLocation = findPin.get(marker);
            Intent locationViewIntent = new Intent(getApplicationContext(), LocationView.class);
            locationViewIntent.putExtra("name", currentLocation.getName());
            locationViewIntent.putExtra("description", currentLocation.getDesc());
            String cats = "";
            for (Category c: currentLocation.getCategories()) {
                cats += c.getContent() + ", ";
            }
            if(cats.length() > 0) {
                cats = cats.substring(0, cats.length() - 2);
            }
            locationViewIntent.putExtra("categories", cats);
            locationViewIntent.putExtra("total_rating", currentLocation.getTotalRating());
            locationViewIntent.putExtra("num_rating", currentLocation.getNumOfRatings());
            locationViewIntent.putExtra("locationID", currentLocation.getID());
            String comments = "";
            for (Comment c: currentLocation.getComments()) {
                String date = new SimpleDateFormat("MM-dd-yyyy").format(c.getDate());
                comments += c.getUsername() + "," + date + "," + c.getContent() + ";";
            }
            if(comments.length() > 0) {
                comments = comments.substring(0, comments.length() - 1);
            }
            locationViewIntent.putExtra("comments", comments);
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
}