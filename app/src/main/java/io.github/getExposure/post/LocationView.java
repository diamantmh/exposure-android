package io.github.getExposure.post;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
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

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.github.getExposure.R;
import io.github.getExposure.database.Comment;
import io.github.getExposure.database.DatabaseManager;
import io.github.getExposure.database.ExposurePhoto;
import io.github.getExposure.maps.MapsActivity;

public class LocationView extends AppCompatActivity {
    private static final int SWITCH_DELAY = 5000;

    private RatingBar rating;
    private TextView name;
    private TextView description;
    private TextView categories;
    private LinearLayout commentArea;
    private EditText newComment;
    private Button postComment;
    private Button done;
    private Button addPhoto;
    private long locationID;
    private long userID;
    private int total_rating;
    private int num_rating;

    private TextView loading;
    private ExposurePhoto[] photos;
    private ImageSwitcher imgs;
    private int picCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_view);
        Bundle extras = getIntent().getExtras();
        final DatabaseManager m = new DatabaseManager();

        locationID = extras.getLong("locationID");
        total_rating = extras.getInt("total_rating");
        num_rating = extras.getInt("num_rating");
        rating = (RatingBar) findViewById(R.id.ratingBar);
        rating.setRating((float) total_rating / num_rating);
        rating.setIsIndicator(true);
        final Profile thing = Profile.getCurrentProfile();
        final Handler h = new Handler(){
            @Override
            public void handleMessage(Message msg){
                if(msg.what == 0) {
                    rating.setIsIndicator(false);
                } else {
                    rating.setIsIndicator(true);
                    Drawable drawable = rating.getProgressDrawable();
                    drawable.setColorFilter(Color.parseColor("#FFD700"), PorterDuff.Mode.SRC_ATOP);
                    LayerDrawable stars = (LayerDrawable) rating.getProgressDrawable();
                    stars.getDrawable(2).setColorFilter(Color.parseColor("#FFD700"), PorterDuff.Mode.SRC_ATOP); // for filled stars
                    stars.getDrawable(1).setColorFilter(Color.parseColor("#FFD700"), PorterDuff.Mode.SRC_ATOP); // for half filled stars
                    stars.getDrawable(0).setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_ATOP); // for empty stars
                    Toast.makeText(getApplicationContext(), "thanks for rating!", Toast.LENGTH_SHORT).show();
                }
            }
        };
        if(thing != null) {
            userID = Long.parseLong(thing.getId());
            new Thread(new Runnable() {
                public void run() {
                    boolean flag = m.userHasRatedLocation(userID, locationID);
                    if(!flag) {
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


        rating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                final int cur = (int) (rating * 2);
                if (thing == null) {
                    Toast toast = Toast.makeText(getApplicationContext(), "must be logged in to rate!", Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    total_rating += cur;
                    num_rating += 1;
                    h.sendEmptyMessage(1);
                    new Thread(new Runnable() {
                        public void run() {
                            boolean result = m.updateRating(locationID, userID, total_rating, num_rating);
                        }
                    }).start();
                }
            }
        });

        name = (TextView) findViewById(R.id.name);
        name.setText(extras.getString("name"));

        description = (TextView) findViewById(R.id.description);
        description.setText(extras.getString("description"));

        categories = (TextView) findViewById(R.id.categories);
        categories.setText(extras.getString("categories"));

        commentArea = (LinearLayout) findViewById(R.id.comments);
        String rawComments = extras.getString("comments");
        if(rawComments != null) {
            for(String s : rawComments.split(";")) {
                if(s.length() > 0) {
                    String[] data = s.split(",");
                    addComment(data[2], data[0], data[1]);
                }
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
                onBackPressed();
            }
        });

        addPhoto = (Button) findViewById(R.id.add);
        addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newPhoto = new Intent(getApplicationContext(), PostActivity.class);
                newPhoto.putExtra("add_photo", true);
                newPhoto.putExtra("locationID", locationID);
                startActivity(newPhoto);
            }
        });


        imgs = (ImageSwitcher) findViewById(R.id.photo);
        imgs.setFactory(new ViewSwitcher.ViewFactory() {

            public View makeView() {
                // Create a new ImageView set it's properties
                ImageView imageView = new ImageView(getApplicationContext());
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

                return imageView;
            }
        });
        loading = (TextView) findViewById(R.id.loading);
        loading.setVisibility(View.VISIBLE);
        new GetPicturesTask().execute(locationID);
    }

    @Override
    public void onResume() {
        super.onResume();
        new GetPicturesTask().execute(locationID);
    }

    /**
     * Retrieves a list of pictures the user has added to the Exposure app,
     * and stores them in the 'photos' array
     */
    private class GetPicturesTask extends AsyncTask<Long, Void, Integer> {
        @Override
        protected Integer doInBackground(Long... ids) {
            photos = new DatabaseManager().getLocationPhotos(ids[0]);
            return photos.length;
        }

        protected void onPostExecute(Integer result) {
            loading.setVisibility(View.INVISIBLE);
            if (result > 0) {
                imgs.setVisibility(View.VISIBLE);
                setupImageSwitcher();
            } else {
                imgs.setVisibility(View.INVISIBLE);
            }
        }
    }

    /**
     * Downloads an ExposurePhoto
     */
    private class DownloadPhotoTask extends AsyncTask<Integer, Void, Void> {
        @Override
        protected Void doInBackground(Integer... nums) {
            photos[nums[0]] = photos[nums[0]].downloadPhoto(getApplicationContext());
            return null;
        }
    }

    private void setupImageSwitcher() {
        Button prev = (Button) findViewById(R.id.prev);
        Button next = (Button) findViewById(R.id.next);
        new DownloadPhotoTask().execute(picCount);
        Bitmap bmp = BitmapFactory.decodeFile(photos[picCount].getFile().getPath());
        BitmapDrawable pic = new BitmapDrawable(bmp);
        imgs.setImageDrawable(pic);

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                picCount--;
                if (picCount == -1)
                    picCount = photos.length - 1;
                new DownloadPhotoTask().execute(picCount);
                Bitmap bmp = BitmapFactory.decodeFile(photos[picCount].getFile().getPath());
                BitmapDrawable pic = new BitmapDrawable(bmp);
                imgs.setImageDrawable(pic);
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                picCount++;
                if (picCount == photos.length)
                    picCount = 0;
                new DownloadPhotoTask().execute(picCount);
                Bitmap bmp = BitmapFactory.decodeFile(photos[picCount].getFile().getPath());
                BitmapDrawable pic = new BitmapDrawable(bmp);
                imgs.setImageDrawable(pic);
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
        final DatabaseManager m = new DatabaseManager();
        new Thread(new Runnable() {
            public void run() {
                Comment c = new Comment(userID, locationID, Profile.getCurrentProfile().getName(), newComment.getText().toString(), new Date(), new Time(0));
                long result = m.insert(c);
                Log.d("BUENOs", "" + result);
            }
        }).start();
        String date = new SimpleDateFormat("MM-dd-yyyy").format(new Date());
        addComment(newComment.getText().toString(), Profile.getCurrentProfile().getName(), date);
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
