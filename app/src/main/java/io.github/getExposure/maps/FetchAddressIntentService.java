package io.github.getExposure.maps;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.ResultReceiver;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by Michael on 2/25/2016.
 */
public class FetchAddressIntentService extends IntentService {

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public FetchAddressIntentService(String name) {
        super(name);
    }

    // for compiler issues?
    public FetchAddressIntentService() {
        super("hi");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        if (!geocoder.isPresent()) {
            throw new IllegalArgumentException("geocoder methods not implmented");
        }
        Bundle extras = intent.getExtras();
        String searchText = extras.getString("searchText");
        System.out.println("FAIS got: " + searchText);
        try {
            List<Address> result = geocoder.getFromLocationName(searchText, 5);
            Bundle resultBundle = new Bundle();
            LatLng resultAddress = null;
            ResultReceiver receiver = (ResultReceiver) extras.get("receiver");
            if (result.size() < 1) {
                // no results found, so resultAddress is null
            } else {
                resultAddress = new LatLng(result.get(0).getLatitude(), result.get(0).getLongitude());
            }
            resultBundle.putString("searchText", searchText);
            resultBundle.putParcelable("address", resultAddress);
            receiver.send(MapsActivity.SEARCH_RESULT_CODE, resultBundle);

        } //TODO: catch exceptions then do..?
        catch (IllegalArgumentException e) {
            //if locationName is null
            e.printStackTrace();
        } catch (IOException e) {
            //if the network is unavailable or any other I/O problem occurs
            e.printStackTrace();
        }
    }
}
