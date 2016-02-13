/*
This Fragment displays a Facebook login button which,
when selected, opens a FacebookActivity that allows the
user to input their Facebook login information to login
to Facebook through Exposure.
 */

package io.github.getExposure;

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


public class LoginFragment extends Fragment {
    private TextView message;
    private Profile profile;
    private CallbackManager mCallbackManager;
    private AccessTokenTracker mTokenTracker;
    private ProfileTracker mProfileTracker;
    private FacebookCallback<LoginResult> mFacebookCallback = new FacebookCallback<LoginResult>() {
        /*
        Actions to take when the user successfully logs in. Gets an AccessToken and a Profile
        object for accessing basic profile information.
         */
        @Override
        public void onSuccess(LoginResult loginResult) {
            AccessToken accessToken = loginResult.getAccessToken();
            if (profile != null)
                message.setText("Welcome " + profile.getFirstName() + "!");
            else
                message.setText("Login failed: please try again.");
        }

        /*
        Actions to take when the user cancels the login.
         */
        @Override
        public void onCancel() {
            profile = null;
            message.setText("Login with Facebook to post and review photos and locations!");
        }

        /*
        Actions to take when an error occurs during the login process.
         */
        @Override
        public void onError(FacebookException e) {
            profile = null;
            message.setText("Login with Facebook to post and review photos and locations!");
        }
    };

    /*
    Create the CallbackManager to track Facebook-related things
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCallbackManager = CallbackManager.Factory.create();
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
        setupLoginButton(view);
        setupMessageText(view);
    }

    /*
    When the fragment is resumed, get whatever Profile is currently logged in
     */
    @Override
    public void onResume() {
        super.onResume();
        Profile profile = Profile.getCurrentProfile();
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
                if (profile != null)
                    message.setText("Welcome " + profile.getFirstName() + "!");
                else
                    message.setText("Login with Facebook to post and review photos and locations!");
            }
        };
    }

    private void setupMessageText(View view) {
        message = (TextView) view.findViewById(R.id.message);
        if (profile != null) {
            message.setText("Welcome " + profile.getName());
        } else {
            message.setText("Login with Facebook to post and review photos and locations!");
        }
    }

    /*
    Create the login button element and associate it with the FacebookCallback
     */
    private void setupLoginButton(View view) {
        LoginButton mButtonLogin = (LoginButton) view.findViewById(R.id.login_button);
        mButtonLogin.setFragment(this);
        mButtonLogin.setReadPermissions("user_friends");
        mButtonLogin.registerCallback(mCallbackManager, mFacebookCallback);
    }
}

