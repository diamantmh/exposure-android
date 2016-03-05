package io.github.getExposure.profile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.widget.ProfilePictureView;

import org.json.JSONException;
import org.json.JSONObject;

import io.github.getExposure.ExposureFragmentActivity;
import io.github.getExposure.database.DatabaseManager;
import io.github.getExposure.database.ExposurePhoto;
import io.github.getExposure.database.ExposureUser;
import io.github.getExposure.post.PostActivity;
import io.github.getExposure.R;
import io.github.getExposure.maps.MapsActivity;

/**
 *  ProfileActivity is the activity class responsible for the "profile view" of Exposure.
 *  It allows users to view their basic profile information, as well as a few of their most
 *  recent photo uploads. It also facilitates Login with Facebook if the user is not logged
 *  in, and allows the user to logout if they are logged in
 *
 *  @author Calob Symonds
 *  @version 1.0
 *  @since 2016-03-02
 *
 */

public class ProfileActivity extends ExposureFragmentActivity {
    // Holds the basic Facebook Profile information of the logged in user
    private Profile profile;

    // Displays the Facebook Profile Picture of the logged in user
    private ProfilePictureView picview;

    // Displays the current city (as listed on Facebook) of the logged in user
    private TextView profilecity;

    // Displays the full name (as listed on Facebook) of the logged in user
    private TextView profilename;

    // Displays the number of photos added by the user
    private TextView picsAdded;
    private TextView count;

    // Informs the user that they need to log in
    private TextView loginMessage;

    // Indicates whether recently added photos are being loaded or not
    private TextView loadingText;

    // Label for the most recently added photos
    private TextView recentlyAddedLabel;

    // Displays up to three of the user's most recently added photos
    private ImageView imageOne;
    private ImageView imageTwo;
    private ImageView imageThree;

    // Holds the list of photos that the user has uploaded
    private ExposurePhoto[] photos;

    // Controls the behavior of the Facebook LoginButton
    private CallbackManager mCallbackManager;

    // Controls the behavior when the Facebook Profile is changed
    private ProfileTracker mProfileTracker;

    // Controls the calls to the database
    private DatabaseManager db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_view);

        // Initialize the DatabaseManager that will be used to access the database
        db = new DatabaseManager();

        // Setup the
        mCallbackManager = CallbackManager.Factory.create();
        updateActivity();

        setupProfileTracker();
    }

    /*
    When the activity is resumed, get whatever Profile is currently logged in
     */
    @Override
    public void onResume() {
        super.onResume();
        updateActivity();
    }

    /*
    When the activity is closed, stop tracking the profile information
     */
    @Override
    public void onStop() {
        super.onStop();
        mProfileTracker.stopTracking();
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

    /**
     * This method is called whenever the user reenters the activity or the current profile
     * information changes. It updates the basic UI elements of the activity
     */
    private void updateActivity() {
        // Determine if the user is logged in by whether the Facebook SDK can find a profile
        profile = Profile.getCurrentProfile();
        boolean isLoggedIn = (profile != null);

        // Setup the various UI elements
        setupProfilePicture(isLoggedIn);
        setupName(isLoggedIn);
        setupCity(isLoggedIn);
        setupAddedText(isLoggedIn);
        setupLoginMessage(isLoggedIn);
        setupImages(isLoggedIn);
    }

    /**
     * Sets up the 'recently added' images section of the ProfileActivity
     * @param isLoggedIn represent whether a user is currently logged in
     */
    private void setupImages(boolean isLoggedIn) {
        imageOne = (ImageView) findViewById(R.id.imageView1);
        imageTwo = (ImageView) findViewById(R.id.imageView2);
        imageThree = (ImageView) findViewById(R.id.imageView3);

        recentlyAddedLabel = (TextView) findViewById(R.id.textView);
        recentlyAddedLabel.setPaintFlags(recentlyAddedLabel.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        if (isLoggedIn) {
            imageOne.setVisibility(View.VISIBLE);
            imageTwo.setVisibility(View.VISIBLE);
            imageThree.setVisibility(View.VISIBLE);
            recentlyAddedLabel.setVisibility(View.VISIBLE);

            if (photos == null || photos.length == 0) {
                // DO NOTHING
            } else if (photos.length == 1) {
                setupImage(imageTwo, 0);
            } else if (photos.length == 2) {
                setupImage(imageOne, 0);
                setupImage(imageThree, 1);
            } else {
                setupImage(imageOne, 0);
                setupImage(imageTwo, 1);
                setupImage(imageThree, 2);
            }
        } else {
            imageOne.setVisibility(View.INVISIBLE);
            imageTwo.setVisibility(View.INVISIBLE);
            imageThree.setVisibility(View.INVISIBLE);
            recentlyAddedLabel.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Displays a single ExposurePhoto in an ImageView
     * @param img The ImageView to display the photo in
     * @param photo The ExposurePhoto to display
     */
    private void setupImage(ImageView img, int photo) {
        new DownloadPhotoTask().execute(photo);
        while (photos[photo].getFile() == null) {
            try { Thread.sleep(100); } catch (Exception e) {}
        }
        Bitmap bmp = BitmapFactory.decodeFile(photos[photo].getFile().getPath());
        img.setImageBitmap(bmp);
    }

    /**
     * Sets up the user's displayed name, as represented in Facebook
     * @param isLoggedIn represent whether a user is currently logged in
     */
    private void setupName(boolean isLoggedIn) {
        profilename = (TextView) findViewById(R.id.profileName);

        if (!isLoggedIn) {
            profilename.setVisibility(View.INVISIBLE);
        } else {
            profilename.setText(profile.getName());
            profilename.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Sets up the user's displayed city, as represented in Facebook
     * @param isLoggedIn represent whether a user is currently logged in
     */
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
                            try {
                                JSONObject user = response.getJSONObject();
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

    /**
     * Sets up the 'pictures added' count section of the ProfileActivity
     * @param isLoggedIn represent whether a user is currently logged in
     */
    private void setupAddedText(boolean isLoggedIn) {
        picsAdded = (TextView) findViewById(R.id.picsAdded);
        count = (TextView) findViewById(R.id.picCount);

        if (isLoggedIn) {
            loadingText = (TextView) findViewById(R.id.loadingText);
            loadingText.setVisibility(View.VISIBLE);

            picsAdded.setVisibility(View.VISIBLE);
            count.setVisibility(View.VISIBLE);
            long id = Long.parseLong(profile.getId());
            new GetPicturesTask().execute(id);
        } else {
            picsAdded.setVisibility(View.INVISIBLE);
            count.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Sets up the login message if the user is not logged in
     * @param isLoggedIn represent whether a user is currently logged in
     */
    private void setupLoginMessage(boolean isLoggedIn) {
        loginMessage = (TextView) findViewById(R.id.notLoggedInMessage);
        if (isLoggedIn)
            loginMessage.setVisibility(View.INVISIBLE);
        else
            loginMessage.setVisibility(View.VISIBLE);
    }

    /**
     * Retrieves a list of pictures the user has added to the Exposure app,
     * and stores them in the 'photos' array
     */
    private class GetPicturesTask extends AsyncTask<Long, Void, Integer> {
        @Override
        protected Integer doInBackground(Long... ids) {
            photos = db.getUserPhotos(ids[0]);
            // photos = db.getUserPhotos(3859745);
            return photos.length;
        }

        protected void onPostExecute(Integer result) {
            picsAdded = (TextView) findViewById(R.id.picsAdded);
            picsAdded.setVisibility(View.VISIBLE);

            loadingText.setVisibility(View.INVISIBLE);

            count = (TextView) findViewById(R.id.picCount);
            count.setVisibility(View.VISIBLE);
            String text = "" + result;
            count.setText(text);

            if (result == 1)
                text = "Picture Added";
            else
                text = "Pictures Added";
            picsAdded.setText(text);

            setupImages((profile != null));
        }
    }

    /**
     * Downloads an ExposurePhoto
     */
    private class DownloadPhotoTask extends AsyncTask<Integer, Void, Void> {
        @Override
        protected Void doInBackground(Integer... nums) {
            photos[nums[0]] = photos[nums[0]].downloadPhoto(getApplicationContext());
            return null;
        }
    }

    /**
     * Gets the ExposureUser associated with the current Facebook account,
     * or attempts to create one if one does not yet exist.
     */
    private void getExposureUser() {
        long id = Long.parseLong(profile.getId());
        new GetUserTask().execute(id);
    }

    /**
     * Attempts to locate the user in the database.
     */
    private class GetUserTask extends AsyncTask<Long, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Long... ids) {
            ExposureUser user = db.getUser(ids[0]);
            return (user == null);
        }

        protected void onPostExecute(Boolean result) {
            if (result)
                new InsertUserTask().execute();
        }
    }

    /**
     * Adds the user to the database.
     */
    private class InsertUserTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            return db.insert(new ExposureUser(Long.parseLong(profile.getId()), profile.getName(), "LINK", "ABOUTME"));
        }
    }

    /**
     * Setup what happens when a new profile is logged in or an old profile is logged out
     */
    private void setupProfileTracker() {
        mProfileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                updateActivity();
                if (profile != null)
                    getExposureUser();
            }
        };
        mProfileTracker.startTracking();
    }
}
