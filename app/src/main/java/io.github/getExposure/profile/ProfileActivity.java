package io.github.getExposure.profile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
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
    private TextView loading;
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
        updateActivity();

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
        updateActivity();
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

    private void updateActivity() {
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
        setupImages(isLoggedIn);
        if (isLoggedIn)
            findViewById(R.id.notLoggedInMessage).setVisibility(View.INVISIBLE);
        else
            findViewById(R.id.notLoggedInMessage).setVisibility(View.VISIBLE);
    }

    private void setupImages(boolean isLoggedIn) {
        ImageView one = (ImageView) findViewById(R.id.imageView1);
        ImageView two = (ImageView) findViewById(R.id.imageView2);
        ImageView three = (ImageView) findViewById(R.id.imageView3);

        TextView mTextView = (TextView) findViewById(R.id.textView);
        mTextView.setPaintFlags(mTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        if (isLoggedIn) {
            one.setVisibility(View.VISIBLE);
            two.setVisibility(View.VISIBLE);
            three.setVisibility(View.VISIBLE);
            mTextView.setVisibility(View.VISIBLE);

            if (photos == null || photos.length == 0) {

            } else if (photos.length == 1) {
                Bitmap bitmap2 = BitmapFactory.decodeFile(photos[1].getFile().getPath());
                two.setImageBitmap(bitmap2);
            } else if (photos.length == 2) {
                Bitmap bitmap1 = BitmapFactory.decodeFile(photos[0].getFile().getPath());
                one.setImageBitmap(bitmap1);
                Bitmap bitmap3 = BitmapFactory.decodeFile(photos[1].getFile().getPath());
                three.setImageBitmap(bitmap3);
            } else {
                Bitmap bitmap1 = BitmapFactory.decodeFile(photos[0].getFile().getPath());
                one.setImageBitmap(bitmap1);
                Bitmap bitmap2 = BitmapFactory.decodeFile(photos[1].getFile().getPath());
                two.setImageBitmap(bitmap2);
                Bitmap bitmap3 = BitmapFactory.decodeFile(photos[2].getFile().getPath());
                three.setImageBitmap(bitmap3);
            }
        } else {
            one.setVisibility(View.INVISIBLE);
            two.setVisibility(View.INVISIBLE);
            three.setVisibility(View.INVISIBLE);
            mTextView.setVisibility(View.INVISIBLE);
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

    private void setupAddedText(boolean isLoggedIn) {
        picsAdded = (TextView) findViewById(R.id.picsAdded);
        count = (TextView) findViewById(R.id.picCount);

        if (isLoggedIn) {
            long id = Long.parseLong(profile.getId());
            new GetPicturesTask().execute(id);
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
            // photos = db.getUserPhotos(3859745);
            return photos.length;
        }

        protected void onPostExecute(Integer result) {
            picsAdded = (TextView) findViewById(R.id.picsAdded);
            picsAdded.setVisibility(View.VISIBLE);

            loading = (TextView) findViewById(R.id.loadingText);
            loading.setVisibility(View.INVISIBLE);

            count = (TextView) findViewById(R.id.picCount);
            count.setVisibility(View.VISIBLE);
            String text = "" + result;
            count.setText(text);

            if (result == 1)
                picsAdded.setText("Picture Added");
            else
                picsAdded.setText("Pictures Added");

            setupImages((profile != null));;
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
        new GetUserTask().execute(id);
        return null;
    }

    private class GetUserTask extends AsyncTask<Long, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Long... ids) {
            ExposureUser user = db.getUser(ids[0]);
            if (user != null) {
                return false;
            } else return true;
        }

        protected void onPostExecute(Boolean result) {
            if (result) {
                new InsertUserTask().execute();
            }
        }
    }

    private class InsertUserTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            // return db.insert(new ExposureUser(Long.parseLong(profile.getId()), profile.getName(), profile.getProfilePictureUri(100, 100).toString(), ""));
            return db.insert(new ExposureUser(Long.parseLong(profile.getId()), profile.getName(), "LINK", "ABOUTME"));
        }
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
                updateActivity();
                if (profile != null)
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
