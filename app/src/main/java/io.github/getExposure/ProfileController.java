package io.github.getExposure;

import android.widget.ImageView;

import com.facebook.Profile;

/**
 * Created by Othnia on 2/12/2016.
 */
public class ProfileController {
    private Profile profile;

    public ProfileController() {
        this.profile = Profile.getCurrentProfile();
    }

    public ProfileController(Profile profile) {
        this.profile = profile;
    }

    public void displayProfilePicture(ImageView view) {

    }

    public void onViewCreated() {

    }
}
