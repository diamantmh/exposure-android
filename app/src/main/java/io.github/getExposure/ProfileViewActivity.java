package io.github.getExposure;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.login.widget.ProfilePictureView;

public class ProfileViewActivity extends AppCompatActivity {
    private Profile profile;
    private ProfilePictureView picview;
    private TextView profilename;
    private Button loginViewSwitcher;
    ImageButton toMapView;
    ImageButton toListView;


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

        toMapView = (ImageButton) findViewById(R.id.toMapView);

        toMapView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent mapViewIntent = new Intent(getApplicationContext(), MapsActivity.class);
                startActivity(mapViewIntent);
            }
        });

        toListView = (ImageButton) findViewById(R.id.toListView);

        toListView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent listViewIntent = new Intent(getApplicationContext(), ListActivity.class);
                startActivity(listViewIntent);
            }
        });
    }
}
