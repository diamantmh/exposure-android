package io.github.getExposure.profile;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.widget.ProfilePictureView;

import org.json.JSONException;
import org.json.JSONObject;

import io.github.getExposure.database.DatabaseManager;
import io.github.getExposure.database.ExposurePhoto;
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
    private Button loginViewSwitcher;

    private DatabaseManager db;
// Use ID 49-54
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_view);

        db = new DatabaseManager();
        profile = Profile.getCurrentProfile();

        if (profile == null) {
            Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(loginIntent);
            return;
        }

        profilename = (TextView) findViewById(R.id.profilename);
        profilename.setText(profile.getName());

        setPicsTaken();
        setCity();

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
    private void setCity() {
        profilecity = (TextView) findViewById(R.id.profilecity);
        /* make the API call */
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
    private void setPicsTaken() {
        picsAdded = (TextView) findViewById(R.id.picsAdded);
        count = (TextView) findViewById(R.id.count);
        ExposurePhoto[] photos = null;
        // photos = db.getUserPhotos(49);
        int pics = 0;
        if (photos != null)
            pics = photos.length;
        String text = "" + pics;
        count.setText(text);
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
