package io.github.getExposure.database;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.sql.Time;

/**
 * ExposurePhoto is an immutable representation of a photo.
 *
 * specfield id : long  // uniquely identifies this photo for database interactions
 */
public final class ExposurePhoto {

    private final long id;
    private final long authorID;
    private final long locID;
    private final String source;
    private final Date date;
    private final Time time;
    private final File file;

    private static final long NULL_ID = -1;

    /*
     * class invariant,
     * source != null
     * date != null
     * time != null
     */

    /**
     * Constructs a ExposurePhoto with the specified parameters.
     *
     * Should not be used when inserting a new ExposurePhoto using DatabaseManager.
     * Only use this constructor when you have an ID provided by DatabaseManager.
     *
     * Parameters date and time must not be null. You must fill in the date and
     * time at instantiation.
     *
     * if file is null, that means the file has not been downloaded. Download the file
     * using downloadPhoto in a new thread.
     *
     * @param id unique identifier supplied by DatabaseManager
     * @param authorID unique identifier of the author of photo, supplied by DatabaseManager
     * @param locID unique identifier of location where photo was taken,
     *              supplied by DatabaseManager
     * @param source source link of photo
     * @param date date photo was taken
     * @param time time photo was taken
     * @param file the file that stores this photo
     */
    public ExposurePhoto(long id, long authorID, long locID, String source, Date date, Time time, File file) {
        this.id = id;
        this.authorID = authorID;
        this.locID = locID;
        this.source = (source == null) ? "" : source;
        this.date = (date == null) ? new Date(0) : new Date(date.getTime());
        this.time = (time == null) ? new Time(0) : new Time(time.getTime());
        this.file = file;
    }

    /**
     * Constructs a ExposurePhoto with the specified parameters.
     *
     * The ID parameter is omitted. This constructor should be used when using
     * DatabaseManager to insert a new photo into the database.
     *
     * Parameters date and time must not be null. You must fill in the date and
     * time at instantiation.
     *
     * * if file is null, that means the file has not been downloaded. Download the file
     * using downloadPhoto in a new thread.
     *
     * @param authorID unique identifier of the author of photo, supplied by DatabaseManager
     * @param locID unique identifier of location where photo was taken,
     *              supplied by DatabaseManager
     * @param source source link of photo
     * @param date date photo was taken
     * @param time time photo was taken
     * @param file the file that stores the photo
     */
    public ExposurePhoto(long authorID, long locID, String source, Date date, Time time, File file) {
        this(NULL_ID, authorID, locID, source, date, time, file);
    }

    /**
     * Default constructor required for JSON decoding.
     */
    public ExposurePhoto() {
        this(NULL_ID, NULL_ID,NULL_ID,"",new Date(0),new Time(0), null);
    }

    /**
     * Returns the unique identifier for this photo.
     *
     * The returned ID can be used to interact with DatabaseManager.
     *
     * @return the unique identifier of this photo or -1 if this ExposurePhoto has
     * no ID (if ID omitted when constructed)
     */
    public long getID() {
        return id;
    }

    /**
     * Returns the unique identifier of the ExposureUser that originally posted this
     * photo.
     *
     * The returned ID can be used to interact with DatabaseManager.
     *
     * @return the unique identifier of the ExposureUser that posted this photo
     */
    public long getAuthorID() {
        return authorID;
    }

    /**
     * Returns the unique identifier of the location where this photo was
     * taken.
     *
     * The returned ID can be used to interact with DatabaseManager.
     *
     * @return the unique identifier of the ExposureLocation where this ExposurePhoto was taken
     */
    public long getLocID() {
        return locID;
    }

    /**
     * Returns the source link to the picture as a String
     *
     * @return the source link to the profile picture of this user
     */
    public String getSource() {
        return source;
    }

    /**
     * Returns the Date this photo was taken.
     *
     * @return the Date this photo was taken.
     */
    public Date getDate() {
        return new Date(date.getTime());
    }

    /**
     * Returns the Time this photo was taken.
     *
     * @return the Time this photo was taken.
     */
    public Time getTime() {
        return new Time(time.getTime());
    }

    /**
     * Returns the File of this photo. Or null if there is no file downloaded.
     *
     * @return the File of this photo or null if there is no file downloaded.
     */
    public File getFile() {
        return file;
    }

    /**
     * Returns a ExposurePhoto with the given id
     *
     * This method is a more convenient way to inject an ID into the object
     * without having to construct a new one yourself. Only use this method
     * if you have been provided a valid ID from DatabaseManager.
     *
     * @return a ExposurePhoto with the given id
     */
    public ExposurePhoto addID(long id) {
        return new ExposurePhoto(id,authorID,locID,source,new Date(date.getTime()),new Time(time.getTime()),file);
    }

    /**
     * Returns true if and only if this ExposurePhoto has an image stored inside.
     *
     * @return true iff this has an image stored inside
     */
    public boolean hasPhoto() {
        return file!=null;
    }

    /**
     * Returns a new ExposurePhoto with the photo at the source url downloaded inside.
     *
     * @param context the android context that Exposure is running on.
     * @return a new ExposurePhoto with the photo at the source url downloaded inside.
     */
    public ExposurePhoto downloadPhoto(Context context) {
        File imgFile = ImageManager.DownloadFromUrl(getSource(), String.valueOf(getID()), context);
        return new ExposurePhoto(getID(),getAuthorID(),getLocID(),
                getSource(),getDate(),getTime(),imgFile);
    }

    /**
     * ImageManager is a utility that handles downloading images.
     */
    public static class ImageManager {

        public static File DownloadFromUrl(String imageURL, Context context) {
            return DownloadFromUrl(imageURL, "", context);
        }

        public static File DownloadFromUrl(String imageURL, String file_name, Context context) {  //this is the downloader method
            File file = null;
            File dir;

            boolean mExternalStorageAvailable = false;
            boolean mExternalStorageWriteable = false;
            String state = Environment.getExternalStorageState();

            if (Environment.MEDIA_MOUNTED.equals(state)) {
                // We can read and write the media
                mExternalStorageAvailable = mExternalStorageWriteable = true;
            } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
                // We can only read the media
                mExternalStorageAvailable = true;
                mExternalStorageWriteable = false;
                System.out.println("We can only read");
                System.exit(1);
            } else {
                // Something else is wrong. It may be one of many other states, but all we need
                //  to know is we can neither read nor write
                System.out.println("HOLY SHIT HUSTON WE FOUND THE PROBLEM");
                System.exit(1);
            }
            System.out.println();

            try {
                URL url = new URL(imageURL); //you can write here any link

                String name = "tempImage" + file_name + ".jpg";
                file = new File(context.getCacheDir(), name);

                System.out.println();

                if (!file.createNewFile() && !file.exists()) {
                    System.out.println("\nERROR WHEN CREATING FILE\n");
                    System.exit(1);
                }

                long startTime = System.currentTimeMillis();
                    /*
                    Log.d("ImageManager", "download begining");
                    Log.d("ImageManager", "download url:" + url);
                    //Log.d("ImageManager", "downloaded file name:" + fileName);
                            Open a connection to that URL.
                    */
                URLConnection ucon = url.openConnection();

                            /*
                             * Define InputStreams to read from the URLConnection.
                             */
                InputStream is = ucon.getInputStream();
                BufferedInputStream bis = new BufferedInputStream(is);

                            /*
                             * Read bytes to the Buffer until there is nothing more to read(-1).
                             */
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();

                //We create an array of bytes
                byte[] data = new byte[50];
                int current = 0;

                while((current = bis.read(data,0,data.length)) != -1){
                    buffer.write(data, 0, current);
                }
                //System.out.println("Create File OutputStream");
                            /* Convert the Bytes read to a String. */
                FileOutputStream fos = new FileOutputStream(file);
                //System.out.println("Write to Buffer");
                fos.write(buffer.toByteArray());
                fos.close();
                //Log.d("ImageManager", "download ready in "
                //        + ((System.currentTimeMillis() - startTime) / 1000)
                //        + " sec");

            } catch (Exception e) {
                Log.d("ImageManager", "Error: " + e);
            }

            return file;

        }
    }
}
