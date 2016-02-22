package io.github.getExposure.post;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
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
    static final int REQUEST_IMAGE_CAPTURE = 2;
    private String mCurrentPhotoPath;
    private String[] permissions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_view);
        name = (EditText) findViewById(R.id.name);
        latitude = (EditText) findViewById(R.id.latitude);
        longitude = (EditText) findViewById(R.id.longitude);

        categories = (TextView) findViewById(R.id.categories);

        categories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getApplicationContext(), PopUpCategoriesView.class), 1);
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
        permissions = new String[2];
        permissions[0] = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        permissions[1] = Manifest.permission.READ_EXTERNAL_STORAGE;
        ActivityCompat.requestPermissions(
                this,
                permissions,
                5
        );
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
            final DatabaseManager m = new DatabaseManager();
            new Thread(new Runnable() {
                public void run() {
                    long result = m.insert(loc);
                    //Log.d("BUENO", result);
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
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), 3);
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

        if (requestCode == 1) {
            if(resultCode == AppCompatActivity.RESULT_OK) {
                String newValue = "";
                for(String s : data.getExtras().keySet()) {
                    newValue += s + ", ";
                }
                newValue = newValue.substring(0, newValue.length() - 2);
                categories.setText(newValue);
            }
        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bitmap myBitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
            photo.setImageBitmap(myBitmap);
            galleryAddPic();
        } else if (requestCode == 3 && resultCode == RESULT_OK) {
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

    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }
}
