package io.github.getExposure.post;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import io.github.getExposure.R;
import io.github.getExposure.maps.MapsActivity;

public class LocationView extends AppCompatActivity {
    private LinearLayout commentArea;
    private ImageView photo;
    private TextView name;
    private TextView description;
    private TextView categories;
    private Button done;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_view);

        Bundle extras = getIntent().getExtras();
        String photoPath = extras.getString("photo");

        photo = (ImageView) findViewById(R.id.photo);
        Bitmap imageBitmap = BitmapFactory.decodeFile(photoPath);
        photo.setImageBitmap(imageBitmap);

        name = (TextView) findViewById(R.id.name);
        name.setText(extras.getString("name"));

        description = (TextView) findViewById(R.id.description);
        description.setText(extras.getString("description"));

        categories = (TextView) findViewById(R.id.categories);
        categories.setText(extras.getString("categories"));

        commentArea = (LinearLayout) findViewById(R.id.comments);
        String[] comments = {"dopest photo ever!", "I love this pic", "show me your ways!"};
        fillComments(comments);

        done = (Button) findViewById(R.id.done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent back = new Intent(getApplicationContext(), MapsActivity.class);
                startActivity(back);
            }
        });
    }

    private void fillComments(String[] comments) {
        View comment;
        TextView commentator;
        TextView commentDate;
        TextView commentText;
        LayoutInflater inflater = getLayoutInflater();

        for (String s : comments) {
            comment = inflater.inflate(R.layout.comment_layout, null);
            commentator = (TextView) comment.findViewById(R.id.author);
            commentDate = (TextView) comment.findViewById(R.id.date);
            commentText = (TextView) comment.findViewById(R.id.content);
            commentator.setText("Test commentator");
            commentDate.setText("12-12-2012");
            commentText.setText(s);
            commentArea.addView(comment);
        }
    }

}
