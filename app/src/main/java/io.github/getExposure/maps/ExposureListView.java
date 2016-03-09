package io.github.getExposure.maps;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.google.android.gms.maps.model.LatLng;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import io.github.getExposure.R;
import io.github.getExposure.database.Category;
import io.github.getExposure.database.Comment;
import io.github.getExposure.database.DatabaseManager;
import io.github.getExposure.database.ExposureLocation;
import io.github.getExposure.post.LocationView;
import io.github.getExposure.post.PostActivity;
import io.github.getExposure.profile.ProfileActivity;

/**
 *  ExposureListView is the activity class responsible for the "list view" of Exposure.
 *  It allows users to find pins based on user-given search queries, and displays the related Exposure
 *  pins in a concise list.
 *
 *  @author Michael Shintaku
 *  @version 1.0
 *  @since 2016-02-03
 *
 */

public class ExposureListView extends ListActivity  {

    private static final float QUERY_LATITUDE_RADIUS = 1; // latitude radius to limit results to pins
    private static final float QUERY_LONGITUDE_RADIUS = 1;// in lat/long radius of search query

    private ListView thisListView;
    private AddressResultReceiver mResultReceiver;
    private LatLng searchResult;
    private DatabaseManager db;
    private List<ExposureLocation> listOfCurrentLocations; // locations displayed/to be displayed
    private List<String> currentLocationNames;
    private int currentFilter; // int representing the setting of the spinner/menu for filters

    /**
     * Method called when ExposureListView is active
     * initializes the database manager, spinner, and Facebook sdk
     * @param savedInstanceState passed in Bundle to create the activity off of
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);
        FacebookSdk.sdkInitialize(getApplicationContext());

        db = new DatabaseManager();

        thisListView = getListView();
        currentLocationNames = new ArrayList<String>();

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
    }

    /**
     * Callback called when the user clicks the "search" button.
     * It centers map perspective around the latitude/longitude coordinates given in the
     * app's text box
     * @param view passed in for drawing/event handling
     */
    public void searchList(View view) {
        EditText editText = (EditText) findViewById(R.id.search_exposure);
        String searchText = editText.getText().toString();

        mResultReceiver = new AddressResultReceiver(new Handler());
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra("searchText", searchText);
        intent.putExtra("receiver", mResultReceiver);
        startService(intent);
    }


    /**
     * Prepares to add all of the pins in the vicinity of the search query to the list view.
     * @param view the view
     */
    private void addPinsHelper(View view) {
        if (searchResult == null) {
            throw new IllegalStateException("Result LatLng is null, addPinsHelper should never be called" +
                    " unless search query is succesful");
        }
        //Toast.makeText(ExposureListView.this, "Loading pins...", Toast.LENGTH_SHORT).show();
        float originLat = (float) searchResult.latitude;
        float originLon = (float) searchResult.longitude;
        float radiusLat = QUERY_LATITUDE_RADIUS;
        float radiusLon = QUERY_LONGITUDE_RADIUS;
        new GetLocationsTask().execute(originLat, originLon, radiusLat, radiusLon);
    }

    /**
     * Called once the locations have been found, this method adds locations that pass through the current
     * filter to the list on screen.
     */
    private void addPins() {
        currentLocationNames.clear(); // clear list of pins from prior list/filter/search combinations
        for (Iterator<ExposureLocation> iterator = listOfCurrentLocations.iterator(); iterator.hasNext(); ) {
            ExposureLocation next = iterator.next();
            if (!hasApplicableCategory(next.getCategories(), currentFilter)) {
                // if it doesn't have an applicable category, remove it from the list.
                iterator.remove();
            } else {
                // add the names to the list of names of locations
                currentLocationNames.add(next.getName());
            }
        }

        ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, currentLocationNames);
        thisListView.setAdapter(listAdapter);
        if (currentLocationNames.isEmpty()) {
            Toast.makeText(ExposureListView.this, "No pins near this location with this filter", Toast.LENGTH_SHORT).show();
        } else {
            thisListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Toast.makeText(ExposureListView.this, "Loading location data...", Toast.LENGTH_SHORT).show();

                    // change this to post view
                    ExposureLocation currentLocation = listOfCurrentLocations.get(position);
                    ;
                    Intent locationViewIntent = new Intent(getApplicationContext(), LocationView.class);

                    startActivity(locationViewIntent);

                    locationViewIntent.putExtra("name", currentLocation.getName());
                    locationViewIntent.putExtra("description", currentLocation.getDesc());
                    String cats = "";
                    for (Category c : currentLocation.getCategories()) {
                        cats += c.getContent() + ", ";
                    }
                    if (cats.length() > 0) {
                        cats = cats.substring(0, cats.length() - 2);
                    }
                    locationViewIntent.putExtra("categories", cats);
                    locationViewIntent.putExtra("total_rating", currentLocation.getTotalRating());
                    locationViewIntent.putExtra("num_rating", currentLocation.getNumOfRatings());
                    locationViewIntent.putExtra("locationID", currentLocation.getID());
                    String comments = "";
                    for (Comment c : currentLocation.getComments()) {
                        String date = new SimpleDateFormat("MM-dd-yyyy").format(c.getDate());
                        comments += c.getUsername() + "," + date + "," + c.getContent() + ";";
                    }
                    if (comments.length() > 0) {
                        comments = comments.substring(0, comments.length() - 1);
                    }
                    locationViewIntent.putExtra("comments", comments);
                    startActivity(locationViewIntent);
                }
            });
        }
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

    /**
     * Asynchronous inner class used to find the ExposureLocations, given an origin and lat/long radiuses,
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
                Toast.makeText(ExposureListView.this, "No locations matching the search query", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ExposureListView.this, "Search completed, loading Exposure pins.", Toast.LENGTH_SHORT).show();
                // put it in the list
                searchResult = query;
                //Toast.makeText(ExposureListView.this, "Result: " + query.toString(), Toast.LENGTH_SHORT).show();
                addPinsHelper(getCurrentFocus());
            }
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
