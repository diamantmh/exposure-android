package io.github.getExposure;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class ListActivity extends AppCompatActivity {
    private ImageButton toMapView;
    private ImageButton toProfileView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        toMapView = (ImageButton) findViewById(R.id.toMapView);

        toMapView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent mapViewIntent = new Intent(getApplicationContext(), MapsActivity.class);
                startActivity(mapViewIntent);
            }
        });

        toProfileView = (ImageButton) findViewById(R.id.toProfileView);

        toProfileView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent profileViewIntent = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(profileViewIntent);
            }
        });
    }


}
