/*
This Activity exists for the purpose of initializing the Facebook SDK
and initializing the Fragment that the Facebook login button will
appear on. During its creation (via onCreate()), it creates the
LoginFragment and switches to its view.
 */

package io.github.getExposure;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.facebook.FacebookSdk;

public class LoginActivity extends FragmentActivity {
    private static final String STATE_SELECTED_FRAGMENT_INDEX = "selected_fragment_index";
    public static final String FRAGMENT_TAG = "fragment_tag";
    private FragmentManager mFragmentManager;

    /*
    Initialize the FacebookSDK and bring up the fragment with the
    Facebook login button.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);

        mFragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.replace(android.R.id.content, new LoginFragment(), FRAGMENT_TAG);
        transaction.commit();
    }

    /**
     * Callback called when the user clicks the "Map" button
     * Switches the activity to MapsActivity
     * @param view
     */
    public void launchMapView(View view) {
        Intent mapViewIntent = new Intent(getApplicationContext(), MapsActivity.class);
        startActivity(mapViewIntent);
    }

    /**
     * Callback called when the user clicks the "Profile" button
     * Switches the activity to ProfileViewActivity
     * @param view
     */
    public void launchProfileView(View view) {
        Intent profileViewIntent = new Intent(getApplicationContext(), ProfileActivity.class);
        startActivity(profileViewIntent);
    }

    /**
     * Callback called when the user clicks the "Post" button
     * Switches the activity to PostActivity
     * @param view
     */
    public void launchPostView(View view) {
        Intent postViewIntent = new Intent(getApplicationContext(), PostActivity.class);
        startActivity(postViewIntent);
    }
}