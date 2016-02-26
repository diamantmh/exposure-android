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

import java.text.BreakIterator;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.getExposure.ExposureFragmentActivity;
import io.github.getExposure.database.Category;
import io.github.getExposure.database.DatabaseManager;
import io.github.getExposure.database.ExposureLocation;
import io.github.getExposure.database.ExposurePhoto;
import io.github.getExposure.post.LocationView;
import io.github.getExposure.R;

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
public class MapsActivity extends ExposureFragmentActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, ActivityCompat.OnRequestPermissionsResultCallback, LocationListener, OnMapReadyCallback {
    //Latitude/longitude of the Paul G Allen Center
    private final static double CSE_LATITUDE = 47.6532295;
    private final static double CSE_LONGITUDE = -122.306897;
    //Latitude/longitude of Drumheller fountain
    private final static double DRUMHELLER_LATITUDE = 47.653808;
    private final static double DRUMHELLER_LONGITUDE = -122.307832;
    private static final int MAPS_LOCATION_REQUEST_CODE = 42;
    public static final int SEARCH_RESULT_CODE = 39;
    private GoogleMap mMap;
    private int currentFilter = 0; // Current filter to select which pins to display, to be implemented
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;
    private Location mCurrentLocation;
    private String mLastUpdateTime;
    private boolean mRequestingLocationUpdates;
    private AddressResultReceiver mResultReceiver;
    private DatabaseManager db;
    private ExposureLocation[] currLocations;
    private Map<ExposureLocation, ExposurePhoto[]> locToPhotos;
    // in v1.0

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
        // DatabaseManager.setApplicationContext(getApplicationContext());
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

        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};
        ActivityCompat.requestPermissions(this, permissions, MAPS_LOCATION_REQUEST_CODE);
        createLocationRequest();

        // Initialize database
        db = new DatabaseManager(getApplicationContext());

    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    public void center(View view) {
        // Permissions for getting location
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(MapsActivity.this, "Cannot find current locations, need permission.", Toast.LENGTH_SHORT).show();
            System.out.println("dere be no permissions to do center");
        } else { // Permission granted
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            if (mCurrentLocation != null) {
                //Toast.makeText(MapsActivity.this, "currentLocation found", Toast.LENGTH_SHORT).show();
                LatLng curr = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLng(curr));
            } else if (mLastLocation != null) {
                //Toast.makeText(MapsActivity.this, "LastLocation found", Toast.LENGTH_SHORT).show();
                //Toast.makeText(MapsActivity.this, "Lat/Lon: " + mLastLocation.getLatitude() + ", " + mLastLocation.getLongitude(), Toast.LENGTH_SHORT).show();
                LatLng curr = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLng(curr));
            } else {
                startLocationUpdates();
                // todo: this message will show when location is enabled, just on first startup with no
                // current/last location
                Toast.makeText(MapsActivity.this, "Make sure that location is enabled", Toast.LENGTH_SHORT).show();
                System.out.println("LastLocation is null");
            }
        }
    }


    //TODO: maybe actually use onconnected
    @Override
    public void onConnected(Bundle connectionHint) {
        System.out.println("onConnected");
        /*
        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }
        */
    }

    private void startLocationUpdates() {
        System.out.println("startLocationUpdates");
        Toast.makeText(MapsActivity.this, "startLocationUpdates", Toast.LENGTH_SHORT).show();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // compiler check, should neveer enter here because it's checked in the calling method
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        System.out.println("permissions results length: " + grantResults.length);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            System.out.println("thanks for allowing permissions");
            Toast.makeText(MapsActivity.this, "Thanks for allowing permissions", Toast.LENGTH_SHORT).show();
            mRequestingLocationUpdates = true;
        } else {
            Toast.makeText(MapsActivity.this, "Why no permissions u allow us", Toast.LENGTH_SHORT).show();
            System.out.println("permissions not granted");
            mRequestingLocationUpdates = false;
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

        //checkLocationPermission();
        LatLng drum = new LatLng(DRUMHELLER_LATITUDE, DRUMHELLER_LONGITUDE);
        //mMap.setOnInfoWindowClickListener(new MapsInfoWindowClickListener());
        // Moves the camera near seattle
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
        Toast.makeText(MapsActivity.this, "Loading pins...", Toast.LENGTH_SHORT).show();
        mMap.setOnInfoWindowClickListener(new MapsInfoWindowClickListener());

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
        /*
        // temp functionality for dmeo
        LatLng drum = new LatLng(DRUMHELLER_LATITUDE, DRUMHELLER_LONGITUDE);
        mMap.setOnInfoWindowClickListener(new MapsInfoWindowClickListener());
        // Add a marker near seattle and move the camera
        mMap.addMarker(new MarkerOptions().position(drum).title("Drumheller Fountain"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(drum));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
        */
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

        /*
        mMap.addMarker(new MarkerOptions().position(center).title("test"));
        mMap.addMarker(new MarkerOptions().position(ne).title("test"));
        mMap.addMarker(new MarkerOptions().position(sw).title("test"));
        */

        /*
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

    private void placePins() {
        mMap.setOnInfoWindowClickListener(new MapsInfoWindowClickListener());
        //currLocations = result;
        new GetPhotosTask().execute();

/*
        //TODO: Do the photos part
        for (ExposureLocation e: result) {

            e.getLat();
            LatLng temp = new LatLng(e.getLat(), e.getLon());
            mMap.addMarker(new MarkerOptions().position(temp).title(e.getName()));
            /*
            new GetPhotosTask().execute(result);
            //ExposurePhoto[] tempPhotos = db.getLocationPhotos(e.getID());
            for (ExposurePhoto c: tempPhotos) {
                System.out.println("link: " + c.getSource());
            }


        }
        */
        Toast.makeText(MapsActivity.this, "Loading photos for pins...", Toast.LENGTH_SHORT).show();


    }

    private void actuallyPlacePins() {
        int i = 0;
        for (ExposureLocation e: locToPhotos.keySet()) {
            LatLng temp = new LatLng(e.getLat(), e.getLon());
            ExposurePhoto[] tempPhotos = locToPhotos.get(e);
            for (ExposurePhoto c: tempPhotos) {
                System.out.println("link: " + c.getSource());
                System.out.println("path: " + c.getFile().getAbsolutePath());
            }
            i++;
            String snippet;
            if (tempPhotos.length > 1) {
                System.out.println("# photos: " + tempPhotos.length);
                System.out.println("path of photo to display:" + tempPhotos[1].getFile().getAbsolutePath());
                // attach just the first photo
                // "1" because targeting the nonexposure storage files
                // and the husky picture
                //String photoPath = "http://exposurestorage.blob.core.windows.net/exposurecontainer/59";
                String photoPath = tempPhotos[1].getFile().getAbsolutePath();
                snippet = e.getName() + "," + photoPath + "," +
                        e.getDesc() + "," + e.getCategories();
                // test for how categories is stored
                for (Category c: e.getCategories()) {
                    System.out.println("categories content: " + c.getContent());
                }
                mMap.addMarker(new MarkerOptions().position(temp).title(e.getName()).snippet(snippet));
            } else { // what to do if the location exists but no photos
                snippet = e.getName() + "," + "bleh" + "," + e.getDesc() + "," + e.getCategories();
            }
            //mMap.addMarker(new MarkerOptions().position(temp).title(e.getName()).snippet(snippet));
        }
        Toast.makeText(MapsActivity.this, "pins placed on screen.", Toast.LENGTH_SHORT).show();
    }

    /**
     * Callback called when the user clicks the "search" button.
     * It centers map perspective around the latitude/longitude coordinates given in the
     * app's text box
     * @requires google api client must be connected
     * @param view passed in for drawing/event handling
     */
    public void search(View view) {
        //should never happen
        if (!mGoogleApiClient.isConnected()) {
            throw new IllegalStateException("google api client needs to be connected");
        }

        EditText editText = (EditText) findViewById(R.id.search_exposure);
        String searchText = editText.getText().toString();
        //Toast.makeText(MapsActivity.this, "search: " + searchText, Toast.LENGTH_SHORT).show();

        //todo: not sure how to instantiate this/what handler actually does
        mResultReceiver = new AddressResultReceiver(new Handler());
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra("searchText", searchText);
        intent.putExtra("receiver", mResultReceiver);
        startService(intent);

    }


    @Override
    public void onLocationChanged(Location location) {
        Toast.makeText(MapsActivity.this, "onLocationChanged", Toast.LENGTH_SHORT).show();
        if (mCurrentLocation == null && mLastLocation == null) { //first time location being updated and no other locations
            // in device's history
            LatLng curr = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLng(curr));
        }
        mCurrentLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());

    }

    //TODO: request location updates at slower/stop location updates when unnecessary
    // also why i cant do stoplocatinupdates rn
    @Override
    protected void onPause() {
        super.onPause();
        //stopLocationUpdates();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected() && !mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    private void stopLocationUpdates() {
        System.out.println("stopLocationUpdates");
        //Toast.makeText(MapsActivity.this, "stopLocationUpdates", Toast.LENGTH_SHORT).show();
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    private class GetLocationsTask extends AsyncTask<Float, Void, ExposureLocation[]> {

        @Override
        protected ExposureLocation[] doInBackground(Float... params) {
            // remember paramaters are: originLat, originLon, radiusLat, radiusLon
            return db.getLocationsInRadius(params[0], params[1], params[2], params[3]);
        }

        protected void onPostExecute(ExposureLocation[] result) {
            locToPhotos = new HashMap<ExposureLocation, ExposurePhoto[]>();
            for (ExposureLocation e: result) {
                locToPhotos.put(e, null);
            }
            placePins();
        }
    }

    private class GetPhotosTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            // remember paramaters are: originLat, originLon, radiusLat, radiusLon
            if (locToPhotos == null) { // no locations
                return false;
            }
            List<ExposurePhoto[]> result = new ArrayList<>();
            for (ExposureLocation e: locToPhotos.keySet()) {
                ExposurePhoto[] tempPhoto = db.getLocationPhotos(e.getID());
                locToPhotos.put(e, tempPhoto);
            }
            return true;
        }

        protected void onPostExecute(Boolean result) {
            if (result) {
                actuallyPlacePins();
            } else { // don't place pins, because no locations
                Toast.makeText(MapsActivity.this, "No pins found", Toast.LENGTH_SHORT).show();
            }
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
            Intent locationViewIntent = new Intent(getApplicationContext(), LocationView.class);
            String info = marker.getSnippet();
            String[] params = info.split(",");
            System.out.println("params length: " + params.length);
            locationViewIntent.putExtra("name", params[0]);
            locationViewIntent.putExtra("photo", params[1]);
            locationViewIntent.putExtra("description", params[2]);
            locationViewIntent.putExtra("categories", params[3]);
            System.out.println("phooto: " + locationViewIntent.getExtras().getString("photo"));
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