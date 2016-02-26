package io.github.getExposure.post;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Profile;

import java.io.File;
import java.io.IOException;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

import io.github.getExposure.R;
import io.github.getExposure.database.Category;
import io.github.getExposure.database.Comment;
import io.github.getExposure.database.DatabaseManager;
import io.github.getExposure.database.ExposureLocation;
import io.github.getExposure.database.ExposurePhoto;

public class PostActivity extends AppCompatActivity {
    private ImageView photo;
    private EditText name;
    private EditText latitude;
    private EditText longitude;
    private EditText description;
    private TextView categories;
    private TextView logMessage;

    static final int REQUEST_GALLERY_PHOTO = 3;
    static final int REQUEST_IMAGE_CAPTURE = 2;
    static final int GET_CATEGORIES = 1;
    private String mCurrentPhotoPath;
    private LocationManager lm;
    private double lat;
    private double log;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_view);

        name = (EditText) findViewById(R.id.name);
        latitude = (EditText) findViewById(R.id.latitude);
        longitude = (EditText) findViewById(R.id.longitude);
        description = (EditText) findViewById(R.id.description);
        categories = (TextView) findViewById(R.id.categories);

        logMessage = (TextView) findViewById(R.id.notLoggedInMessage);

        if (Profile.getCurrentProfile() == null) {
            name.setVisibility(View.INVISIBLE);
            latitude.setVisibility(View.INVISIBLE);
            longitude.setVisibility(View.INVISIBLE);
            categories.setVisibility(View.INVISIBLE);
            description.setVisibility(View.INVISIBLE);
            findViewById(R.id.submit).setVisibility(View.INVISIBLE);
            logMessage.setVisibility(View.VISIBLE);
            return;
        }

        name.setVisibility(View.VISIBLE);
        latitude.setVisibility(View.VISIBLE);
        longitude.setVisibility(View.VISIBLE);
        categories.setVisibility(View.VISIBLE);
        description.setVisibility(View.VISIBLE);
        findViewById(R.id.submit).setVisibility(View.VISIBLE);
        logMessage.setVisibility(View.INVISIBLE);

        categories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getApplicationContext(), PopUpCategoriesView.class), GET_CATEGORIES);
            }
        });

        description = (EditText) findViewById(R.id.description);

        Button post = (Button) findViewById(R.id.submit);
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                post();

            }
        });

        photo = (ImageView) findViewById((R.id.photo));
        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 5);
        }

        lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] { android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    android.Manifest.permission.ACCESS_FINE_LOCATION}, 7);
        }

        final LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                log = location.getLongitude();
                lat = location.getLatitude();
            }
            public void onStatusChanged(String provider, int status, Bundle extras) {}
            public void onProviderEnabled(String provider) {}
            public void onProviderDisabled(String provider) {}
        };
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void post() {
        Intent postViewIntent = new Intent(getApplicationContext(), LocationView.class);
        boolean flag = false;

        if(mCurrentPhotoPath == null) {
            Toast toast = Toast.makeText(getApplicationContext(), "please upload or take a photo!", Toast.LENGTH_SHORT);
            toast.show();
            flag = true;
        } else {
            postViewIntent.putExtra("photo", mCurrentPhotoPath);
        }

        if(name.getText().toString().equals("") && !flag) {
            Toast toast = Toast.makeText(getApplicationContext(), "name your location first!", Toast.LENGTH_SHORT);
            toast.show();
            flag = true;
        } else {
            postViewIntent.putExtra("name", name.getText().toString());
        }

        if(latitude.getText().toString().equals("") && !flag) {
            Toast toast = Toast.makeText(getApplicationContext(), "need latitude first!", Toast.LENGTH_SHORT);
            toast.show();
            flag = true;
        } else {
            postViewIntent.putExtra("latitude", latitude.getText().toString());
        }

        if(longitude.getText().toString().equals("") && !flag) {
            Toast toast = Toast.makeText(getApplicationContext(), "need longitude first!", Toast.LENGTH_SHORT);
            toast.show();
            flag = true;
        } else {
            postViewIntent.putExtra("longitude", longitude.getText().toString());
        }

        if(description.getText().toString().equals("") && !flag) {
            Toast toast = Toast.makeText(getApplicationContext(), "you need to enter a description first!", Toast.LENGTH_SHORT);
            toast.show();
            flag = true;
        } else {
            postViewIntent.putExtra("description", description.getText().toString());
        }

        if(categories.getText().toString().equals("Categories") && !flag) {
            Toast toast = Toast.makeText(getApplicationContext(), "please add some categories!", Toast.LENGTH_SHORT);
            toast.show();
            flag = true;
        } else {
            postViewIntent.putExtra("categories", categories.getText().toString());
        }


        if(!flag) {
            /*
            Profile thing = Profile.getCurrentProfile();
            final Long id = Long.parseLong(thing.getId());

            final ExposurePhoto p = new ExposurePhoto(long authorID, long locID, mCurrentPath, Date date, Time time, File file);
            */
            final ExposureLocation loc = new ExposureLocation(Float.parseFloat(latitude.getText().toString()),
                    Float.parseFloat(longitude.getText().toString()), 0, 0, name.getText().toString(),
                    description.getText().toString(), new HashSet<Category>(), new ArrayList<Comment>());
            final DatabaseManager m = new DatabaseManager(getApplicationContext());
            new Thread(new Runnable() {
                public void run() {
                    long result = m.insert(loc);
                    Log.d("BUENO", "" + result);
                }
            }).start();
        startActivity(postViewIntent);
        }
    }

    private void selectImage() {
        final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(PostActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo")) {
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                        File photoFile = null;
                        try {
                            photoFile = createImageFile();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }

                        // Continue only if the File was successfully created
                        if (photoFile != null) {
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                                    Uri.fromFile(photoFile));
                            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                        }
                    }
                }
                else if (options[item].equals("Choose from Gallery")) {
                    Intent intent = new   Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_PICK);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_GALLERY_PHOTO);
                }
                else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == GET_CATEGORIES && resultCode == RESULT_OK) {
            String newValue = "";
            for(String s : data.getExtras().keySet()) {
                newValue += s + ", ";
            }
            newValue = newValue.substring(0, newValue.length() - 2);
            categories.setText(newValue);
        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bitmap myBitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
            photo.setImageBitmap(myBitmap);
            galleryAddPic();

            if(lat != 0.0) {
                latitude.setText("" + lat);
                longitude.setText("" + log);
            } else {
                Toast toast = Toast.makeText(getApplicationContext(), "can't get current location", Toast.LENGTH_SHORT);
                toast.show();
            }

        } else if (requestCode == REQUEST_GALLERY_PHOTO && resultCode == RESULT_OK) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            mCurrentPhotoPath = picturePath;
            cursor.close();
            Bitmap imageBitmap = BitmapFactory.decodeFile(picturePath);
            photo.setImageBitmap(imageBitmap);
        }
    }
}

