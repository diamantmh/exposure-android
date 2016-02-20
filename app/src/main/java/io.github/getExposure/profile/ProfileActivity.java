package io.github.getExposure.profile;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.Profile;
import com.facebook.login.widget.ProfilePictureView;

import io.github.getExposure.post.PostActivity;
import io.github.getExposure.R;
import io.github.getExposure.maps.MapsActivity;

public class ProfileActivity extends AppCompatActivity {
    private Profile profile;
    private ProfilePictureView picview;
    private TextView profilename;
    private Button loginViewSwitcher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_view);

        profile = Profile.getCurrentProfile();

        if (profile == null) {
            Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(loginIntent);
            return;
        }

        profilename = (TextView) findViewById(R.id.profilename);
        profilename.setText(profile.getName());

        picview = (ProfilePictureView) findViewById(R.id.view);
        picview.setProfileId((profile.getId()));

        loginViewSwitcher = (Button) findViewById(R.id.button);

        loginViewSwitcher.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent loginViewIntent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(loginViewIntent);
            }
        });
    }

    /**
     * Callback called when the user clicks the "Maps" button
     * Switches the activity to MapsActivity
     * @param view passed in for drawing/event handling
     */
    public void launchMapsView(View view) {
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
