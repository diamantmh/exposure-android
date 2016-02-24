/*
This Fragment displays a Facebook login button which,
when selected, opens a FacebookActivity that allows the
user to input their Facebook login information to login
to Facebook through Exposure.
 */

package io.github.getExposure.profile;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.login.widget.ProfilePictureView;

import io.github.getExposure.R;
import io.github.getExposure.database.DatabaseManager;
import io.github.getExposure.database.ExposureUser;


public class LoginFragment extends Fragment {
    private static String LOGGED_OUT_TEXT = "Login with Facebook to post and review photos and locations!";

    @SuppressWarnings("all")
    private static String ERROR_TEXT = "Something went wrong. Please try again.";

    private DatabaseManager db;

    // The main TextView that displays text
    private TextView message;

    // The user profile object
    private Profile profile;

    // The view that displays the user's profile picture
    private ProfilePictureView profilepicture;
    private CallbackManager mCallbackManager;

    // Trackers for the user's profile and access token information
    private AccessTokenTracker mTokenTracker;
    private ProfileTracker mProfileTracker;

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
            message.setText(LOGGED_OUT_TEXT);
        }

        /*
        Actions to take when an error occurs during the login process.
         */
        @Override
        public void onError(FacebookException e) {
            profile = null;
            message.setText(ERROR_TEXT);
        }
    };

    /*
    Create the CallbackManager to track Facebook-related things
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCallbackManager = CallbackManager.Factory.create();
        profile = Profile.getCurrentProfile();

        db = new DatabaseManager();
        setupTokenTracker();
        setupProfileTracker();

        mTokenTracker.startTracking();
        mProfileTracker.startTracking();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    /*
    Create the login button when the Fragment is initialized.
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        setupProfilePicture(view);
        setupLoginButton(view);
        setupMessageText(view);
    }

    /*
    When the fragment is resumed, get whatever Profile is currently logged in
     */
    @Override
    public void onResume() {
        super.onResume();
        profile = Profile.getCurrentProfile();
    }

    /*
    When the fragment is closed, stop tracking the profile information
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

    private ExposureUser getExposureUser() {
        long id = Long.parseLong(profile.getId());
        ExposureUser user = db.getUser(id);
        if (user == null) {
            user = new ExposureUser(id, profile.getName(), profile.getProfilePictureUri(100, 100).toString(), "");
            db.insert(user);
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
                if (profile != null) {
                    String msg = "Welcome " + profile.getFirstName() + "!";
                    message.setText(msg);

                    profilepicture.setProfileId(profile.getId());
                    profilepicture.setVisibility(View.VISIBLE);
                } else {
                    message.setText(LOGGED_OUT_TEXT);
                    profilepicture.setVisibility(View.INVISIBLE);
                }
            }
        };
    }

    private void setupProfilePicture(View view) {
        profilepicture = (ProfilePictureView) view.findViewById(R.id.profilepicture);
        if (profile != null)
            profilepicture.setProfileId(profile.getId());
        else
            profilepicture.setVisibility(View.INVISIBLE);
    }

    private void setupMessageText(View view) {
        message = (TextView) view.findViewById(R.id.message);
        if (profile != null) {
            String text = "Currently logged in as " + profile.getName();
            message.setText(text);
        } else {
            message.setText(LOGGED_OUT_TEXT);
        }
    }

    /*
    Create the login button element and associate it with the FacebookCallback
     */
    private void setupLoginButton(View view) {
        LoginButton mButtonLogin = (LoginButton) view.findViewById(R.id.login_button);
        mButtonLogin.setFragment(this);

        mButtonLogin.setReadPermissions("user_location");
        mButtonLogin.registerCallback(mCallbackManager, mFacebookCallback);
    }
}

