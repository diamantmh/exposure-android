package io.github.getExposure.post;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Profile;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Time;
import java.util.Date;

import io.github.getExposure.R;
import io.github.getExposure.database.Comment;
import io.github.getExposure.database.DatabaseManager;
import io.github.getExposure.database.ExposurePhoto;
import io.github.getExposure.maps.MapsActivity;

public class LocationView extends AppCompatActivity {
    private RatingBar rating;
    private ImageView photo;
    private TextView name;
    private TextView description;
    private TextView categories;
    private LinearLayout commentArea;
    private EditText newComment;
    private Button postComment;
    private Button done;
    private long locationID;
    private long userID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_view);
        Bundle extras = getIntent().getExtras();
        String photoPath = extras.getString("photo");
        final DatabaseManager m = new DatabaseManager(getApplicationContext());

        locationID = extras.getLong("locationID");
        final Profile thing = Profile.getCurrentProfile();
        rating = (RatingBar) findViewById(R.id.ratingBar);
        rating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if(thing == null) {
                    Toast toast = Toast.makeText(getApplicationContext(), "must be logged in to rate!", Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    new Thread(new Runnable() {
                        public void run() {
                            //add rating
                        }
                    }).start();
                }
            }
        });
        rating.setRating((float) 5.0);
        rating.setIsIndicator(true);
        if(thing != null) {
            userID = Long.parseLong(thing.getId());
            new Thread(new Runnable() {
                public void run() {
                    //checkRating(m.userHasRatedLocation(userID, locationID));
                }
            }).start();
        } else {
            userID = 0;
            rating.setIsIndicator(false);
        }

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
        String[] comments = {"dopest photo ever!", "I love this pic"};
        fillComments(comments);

        newComment = (EditText) findViewById(R.id.new_comment);
        postComment = (Button) findViewById(R.id.post_comment);
        postComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postNewComment();
            }
        });

        done = (Button) findViewById(R.id.done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent back = new Intent(getApplicationContext(), MapsActivity.class);
                startActivity(back);
            }
        });
    }

    public void postNewComment() {
        if(userID == 0) {
            Toast toast = Toast.makeText(getApplicationContext(), "please login before commenting!", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

        if(newComment.getText().toString().equals("")) {
            Toast toast = Toast.makeText(getApplicationContext(), "please enter some comment!", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        final DatabaseManager m = new DatabaseManager(getApplicationContext());
        new Thread(new Runnable() {
            public void run() {
                Comment c = new Comment(userID, locationID, newComment.getText().toString(), new Date(), new Time(0));
                long result = m.insert(c);
                Log.d("BUENOs", "" + result);
            }
        }).start();
        addComment(newComment.getText().toString(), Profile.getCurrentProfile().getName(), new Date().toString());
        newComment.setText("");
    }

    public void checkRating(boolean flag) {
        if(!flag) {
            rating.setIsIndicator(false);
        } else {
            // change star color
        }
    }

    public void addComment(String content, String author, String time) {
        LayoutInflater inflater = getLayoutInflater();
        View comment = inflater.inflate(R.layout.comment_layout, null);
        TextView commentator = (TextView) comment.findViewById(R.id.author);
        TextView commentDate = (TextView) comment.findViewById(R.id.date);
        TextView commentText = (TextView) comment.findViewById(R.id.content);
        commentator.setText(author);
        commentDate.setText(time);
        commentText.setText(content);
        commentArea.addView(comment);
    }

    private void fillComments(String[] comments) {
        for (String s : comments) {
            addComment(s, "Test commentator", "12-12-2012");
        }
    }

}
