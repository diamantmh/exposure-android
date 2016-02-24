package io.github.getExposure.profile;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.ProfilePictureView;

import org.json.JSONException;
import org.json.JSONObject;

import io.github.getExposure.database.DatabaseManager;
import io.github.getExposure.database.ExposurePhoto;
import io.github.getExposure.database.ExposureUser;
import io.github.getExposure.post.PostActivity;
import io.github.getExposure.R;
import io.github.getExposure.maps.MapsActivity;

public class ProfileActivity extends AppCompatActivity {
    private Profile profile;
    private ProfilePictureView picview;
    private TextView profilecity;
    private TextView profilename;
    private TextView picsAdded;
    private TextView count;
    private ExposurePhoto[] photos;

    private CallbackManager mCallbackManager;

    private AccessTokenTracker mTokenTracker;
    private ProfileTracker mProfileTracker;

    private DatabaseManager db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_view);

        db = new DatabaseManager(getApplicationContext());
        mCallbackManager = CallbackManager.Factory.create();
        profile = Profile.getCurrentProfile();
        boolean isLoggedIn;
        if (profile == null)
            isLoggedIn = false;
        else
            isLoggedIn = true;

        setupProfilePicture(isLoggedIn);
        setupName(isLoggedIn);
        setupCity(isLoggedIn);
        setupAddedText(isLoggedIn);
        setupLoginMessage(isLoggedIn);

        setupTokenTracker();
        setupProfileTracker();

        mTokenTracker.startTracking();
        mProfileTracker.startTracking();
    }

    /*
    When the activity is resumed, get whatever Profile is currently logged in
     */
    @Override
    public void onResume() {
        super.onResume();
        profile = Profile.getCurrentProfile();
        boolean isLoggedIn;
        if (profile == null)
            isLoggedIn = false;
        else
            isLoggedIn = true;

        setupProfilePicture(isLoggedIn);
        setupName(isLoggedIn);
        setupCity(isLoggedIn);
        setupAddedText(isLoggedIn);
    }

    /*
    When the activity is closed, stop tracking the profile information
     */
    @Override
    public void onStop() {
        super.onStop();
        mProfileTracker.stopTracking();
        mTokenTracker.stopTracking();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void setupProfilePicture(boolean isLoggedIn) {
        picview = (ProfilePictureView) findViewById(R.id.profilePicture);

        if (!isLoggedIn) {
            picview.setVisibility(View.INVISIBLE);
        } else {
            picview.setVisibility(View.VISIBLE);
            picview.setProfileId(profile.getId());
        }
    }

    private void setupName(boolean isLoggedIn) {
        profilename = (TextView) findViewById(R.id.profileName);

        if (!isLoggedIn) {
            profilename.setVisibility(View.INVISIBLE);
        } else {
            profilename.setVisibility(View.VISIBLE);
            profilename.setText(profile.getName());
        }
    }

    private void setupCity(boolean isLoggedIn) {
        profilecity = (TextView) findViewById(R.id.currentCity);

        if (!isLoggedIn) {
            profilecity.setVisibility(View.INVISIBLE);
        } else {
            profilecity.setVisibility(View.VISIBLE);
            GraphRequest request = GraphRequest.newMeRequest(
                    AccessToken.getCurrentAccessToken(),
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(
                                JSONObject object,
                                GraphResponse response) {
                            JSONObject user = response.getJSONObject();
                            try {
                                JSONObject loc = user.getJSONObject("location");
                                String city = loc.getString("name");
                                profilecity.setVisibility(View.VISIBLE);
                                profilecity.setText(city);
                            } catch (JSONException e) {
                                profilecity.setVisibility(View.INVISIBLE);
                            }
                        }
                    });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "location");
            request.setParameters(parameters);
            request.executeAsync();
        }
    }

    private void setupAddedText(boolean isLoggedIn) {
        picsAdded = (TextView) findViewById(R.id.picsAdded);
        count = (TextView) findViewById(R.id.picCount);

        if (!isLoggedIn) {
            picsAdded.setVisibility(View.INVISIBLE);
            count.setVisibility(View.INVISIBLE);
        } else {
            long id = Long.parseLong(profile.getId());
            new GetPicturesTask().execute(id);
            picsAdded.setVisibility(View.VISIBLE);
            count.setVisibility(View.VISIBLE);
        }
    }

    private void setupLoginMessage(boolean isLoggedIn) {
        TextView msg = (TextView) findViewById(R.id.notLoggedInMessage);
        if (isLoggedIn) msg.setVisibility(View.INVISIBLE);
        else            msg.setVisibility(View.VISIBLE);
    }

    private class GetPicturesTask extends AsyncTask<Long, Void, Integer> {
        @Override
        protected Integer doInBackground(Long... ids) {
            photos = db.getUserPhotos(ids[0]);
            return photos.length;
        }

        protected void onPostExecute(Integer result) {
            picsAdded = (TextView) findViewById(R.id.picsAdded);
            count = (TextView) findViewById(R.id.picCount);
            String text = "" + result;
            count.setText(text);
            if (result == 1)
                picsAdded.setText("Picture Added");
            else
                picsAdded.setText("Pictures Added");
        }
    }

    private FacebookCallback<LoginResult> mFacebookCallback = new FacebookCallback<LoginResult>() {
        /*
        Actions to take when the user successfully logs in. Gets an AccessToken and a Profile
        object for accessing basic profile information. The methods here will execute before
        methods in getProfileTracker and getAccessTokenTracker
         */
        @Override
        public void onSuccess(LoginResult loginResult) {
            // The user successfully logs in
            // AccessToken accessToken = loginResult.getAccessToken();
        }

        /*
        Actions to take when the user cancels the login.
         */
        @Override
        public void onCancel() {
            // The user cancels the login at any point
            profile = null;
            // message.setText(LOGGED_OUT_TEXT);
        }

        /*
        Actions to take when an error occurs during the login process.
         */
        @Override
        public void onError(FacebookException e) {
            profile = null;
            // message.setText(ERROR_TEXT);
        }
    };

    private ExposureUser getExposureUser() {
        long id = Long.parseLong(profile.getId());
        ExposureUser user = db.getUser(id);
        if (user == null) {
            user = new ExposureUser(id, profile.getName(), profile.getProfilePictureUri(100, 100).toString(), "");

            if (!db.insert(user))
                System.out.println("Something went horribly wrong!!!!");
        }
        return user;
    }

    private void setupTokenTracker() {
        mTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {

            }
        };
    }

    /*
    Setup what happens when a new profile is logged in
     */
    private void setupProfileTracker() {
        mProfileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                profile = Profile.getCurrentProfile();
                getExposureUser();
            }
        };
    }

    /**
     * Callback called when the user clicks the "Maps" button
     * Switches the activity to MapsActivity
     * @param view passed in for drawing/event handling
     */
    public void launchMapView(View view) {
        Intent mapViewIntent = new Intent(getApplicationContext(), MapsActivity.class);
        startActivity(mapViewIntent);
    }

    /**
     * Callback called when the user clicks the "Profile" button
     * Switches the activity to ProfileViewActivity
     * @param view passed in for drawing/event handling
     */
    public void launchProfileView(View view) {
        Intent profileViewIntent = new Intent(getApplicationContext(), ProfileActivity.class);
        startActivity(profileViewIntent);
    }

    /**
     * Callback called when the user clicks the "Post" button
     * Switches the activity to PostActivity
     * @param view passed in for drawing/event handling
     */
    public void launchPostView(View view) {
        Intent postViewIntent = new Intent(getApplicationContext(), PostActivity.class);
        startActivity(postViewIntent);
    }
}
