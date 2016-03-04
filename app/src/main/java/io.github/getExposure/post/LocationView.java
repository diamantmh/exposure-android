package io.github.getExposure.post;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

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
    private static final int SWITCH_DELAY = 5000;

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
    private int total_rating;
    private int num_rating;

    private ExposurePhoto[] photos;
    private int picCount;
    private ImageSwitcher imgs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_view);
        Bundle extras = getIntent().getExtras();
        final DatabaseManager m = new DatabaseManager(getApplicationContext());

        locationID = extras.getLong("locationID");
        total_rating = extras.getInt("total_rating");
        num_rating = extras.getInt("num_rating");
        final Profile thing = Profile.getCurrentProfile();
        final Handler h = new Handler(){
            @Override
            public void handleMessage(Message msg){
                if(msg.what == 0) {
                    rating.setIsIndicator(false);
                } else {
                    rating.setIsIndicator(true);
                    //change color
                }
            }
        };
        rating = (RatingBar) findViewById(R.id.ratingBar);
        rating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                final int cur = (int) (rating * 2);
                if (thing == null) {
                    Toast toast = Toast.makeText(getApplicationContext(), "must be logged in to rate!", Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    new Thread(new Runnable() {
                        public void run() {
                            //m.updateRating(locationID, userID, total_rating + cur, num_rating + 1);
                            h.sendEmptyMessage(1);
                        }
                    }).start();
                }
            }
        });
        rating.setRating((float) total_rating / num_rating);
        rating.setIsIndicator(true);

        if(thing != null) {
            userID = Long.parseLong(thing.getId());
            new Thread(new Runnable() {
                public void run() {
                    if(!m.userHasRatedLocation(userID, locationID)) {
                        h.sendEmptyMessage(0);
                    } else {
                        h.sendEmptyMessage(1);
                    }
                }
            }).start();
        } else {
            userID = 0;
            rating.setIsIndicator(false);
        }

        name = (TextView) findViewById(R.id.name);
        name.setText(extras.getString("name"));

        description = (TextView) findViewById(R.id.description);
        description.setText(extras.getString("description"));

        categories = (TextView) findViewById(R.id.categories);
        categories.setText(extras.getString("categories"));

        commentArea = (LinearLayout) findViewById(R.id.comments);
        String rawComments = extras.getString("comments");
        if(rawComments == null) {

        } else {
            for(String s : rawComments.split(";")) {
                String[] data = s.split(",");
                Log.d("GGGGe", s);
                addComment(data[2], data[0], data[1]);
            }
        }
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

        new GetPicturesTask().execute(locationID);
    }

    /**
     * Retrieves a list of pictures the user has added to the Exposure app,
     * and stores them in the 'photos' array
     */
    private class GetPicturesTask extends AsyncTask<Long, Void, Integer> {
        @Override
        protected Integer doInBackground(Long... ids) {
            photos = new DatabaseManager(getApplicationContext()).getLocationPhotos(ids[0]);
            return photos.length;
        }

        protected void onPostExecute(Integer result) {
            imgs = (ImageSwitcher) findViewById(R.id.photo);
            imgs.setFactory(new ViewSwitcher.ViewFactory() {

                public View makeView() {
                    // Create a new ImageView set it's properties
                    ImageView imageView = new ImageView(getApplicationContext());
                    imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    return imageView;
                }
            });

            if (result > 0) {
                imgs.setVisibility(View.VISIBLE);
                setupImageSwitcher();
            } else {
                imgs.setVisibility(View.INVISIBLE);
            }
        }
    }

    private void setupImageSwitcher() {
        picCount = 0;
        imgs.postDelayed(new Runnable() {
            public void run() {
                int picNum = picCount++ % photos.length;
                Bitmap bmp = BitmapFactory.decodeFile(photos[picNum].getFile().getPath());
                BitmapDrawable pic = new BitmapDrawable(bmp);
                imgs.setImageDrawable(pic);
                imgs.postDelayed(this, SWITCH_DELAY);
            }
        }, 1000);
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
                // TODO: Fill in username for comment c
                Comment c = new Comment(userID, locationID, "TEMPORARY USERNAME UNTIL MICHAEL D FIXES THIS",newComment.getText().toString(), new Date(), new Time(0));
                long result = m.insert(c);
                Log.d("BUENOs", "" + result);
            }
        }).start();
        addComment(newComment.getText().toString(), Profile.getCurrentProfile().getName(), new Date().toString());
        newComment.setText("");
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
}
