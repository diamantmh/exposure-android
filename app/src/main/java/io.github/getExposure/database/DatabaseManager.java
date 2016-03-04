package io.github.getExposure.database;

// RestTemplate Imports
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

// Image Downloader Imports
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.PrintStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.io.ByteArrayOutputStream;
import java.sql.Time;
import java.util.Date;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.content.Context;
import android.util.Log;
import android.os.Environment;
import android.util.Base64;

/**
 * DatabaseManager is an abstraction that handles interactions with the
 * database. DatabaseManager can provide data from, add data to, and update
 * existing data on the database.
 *
 * IDs are used to uniquely refer to entries in the database. It is guaranteed
 * that each ID maps to at most one entry in the database.
 *
 * DatabaseManager makes long, synchronous calls to the database. You must use
 * an AsyncTask or a subclass of AsyncTask to make calls to DatabaseManager in
 * the background to avoid locking the UI thread.
 */
public class DatabaseManager {
    private RestTemplate restTemplate;

    protected static final String WEB_SERVICE = "http://exposureweb.cloudapp.net/RESTfulProject/REST/WebService/";
    //protected static final String WEB_SERVICE = "http://kekonatvm.cloudapp.net/RESTfulProject/REST/WebService/";
    //protected static final String WEB_SERVICE = "http://10.0.2.2:8080/RESTfulProject/REST/WebService/";

    protected Context CONTEXT;
    protected static final String DEFAULT_URL = "https://exposurestorage.blob.core.windows.net/exposurecontainer/10";

    private static final long NULL_ID = -1;

    /*
     * class invariant,
     * restTemplate != null
     */

    /**
     * Constructs a DatabaseManager with the given context con.
     */
    public DatabaseManager(Context con) {
        this(new RestTemplate(), con);
    }

    /**
     * Constructs a DatabaseManager with the given RestTemplate.
     *
     * This constructor can be used to provide custom message converters,
     * or it can be used for testing purposes (providing a mocked
     * RestTemplate)
     */
    public DatabaseManager(RestTemplate rt, Context con) {
        restTemplate = rt;
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        CONTEXT = con;
    }

    /**
     * Returns true if and only if the specified location is updated. Replaces
     * the entry in the database matching the given id with the given value.
     *
     * Requires that loc to be an existing location (a location returned by DatabaseManager).
     *
     * Also inserts any unregistered categories inside loc. A category is
     * unregistered if the locID was omitted when constructed. Additional
     * categories can be registered in the database using insert(Category category)
     *
     * Comments inside loc will not be inserted into the database and must be
     * inserted separately using insert(Comment comment)
     *
     * @param loc the ExposureLocation with the desired data
     * @return true iff the location entry matching the ID of loc was updated
      */

    public boolean update(ExposureLocation loc) {
        WebLocation wLoc = new WebLocation(loc.getLat(),loc.getLon(),loc.getTotalRating(),
                loc.getNumOfRatings(),loc.getName(),loc.getDesc());

        // register location in database
        final String url = WEB_SERVICE + "updateLocation";
        boolean result = restTemplate.postForObject(url, wLoc, Boolean.class);

        registerCategoriesAndCommentsInLocation(loc);

        return result;
    }

    /**
     * Returns true if and only if the specified user is updated. Replaces
     * the entry in the database matching the given id with the given value.
     *
     * Requires that user to be an existing user (a user registered by
     * DatabaseManager).
     *
     * @param user the ExposureUser with the desired data
     * @return true iff the user entry matching the ID of user was updated
     */
    public boolean update(ExposureUser user) {
        final String url = WEB_SERVICE + "updateUser";
        return restTemplate.postForObject(url, user, Boolean.class);
    }

    /**
     * Returns true if and only if the specified location matching locID is
     * updated with the given rating by the user matching the given userID.
     *
     * @param locID the unique identifier of the location to add the rating to
     * @param userID the unique identifier of the user that made this rating
     * @param totalRating the new total rating for this location
     * @param totalNumberRatings the new total number of reviews this location
     *                           has recieved.
     * @return Returns true iff the specified location matching locID is
     * updated with the given rating by the user matching the given userID.
     */
    public boolean updateRating(long locID, long userID, int totalRating, int totalNumberRatings) {
        final String url = WEB_SERVICE + "getLocation?lid=" + locID + "&uid=" + userID
                + "&totalRating=" + totalRating + "&totalNumberRatings=" + totalNumberRatings;
        return restTemplate.getForObject(url, Boolean.class);
    }

    /**
     * Returns the ID of the new entry in the database. Makes a new entry in the
     * database for the given ExposureLocation. Returns -1 if the entry was not
     * created.
     *
     * Requires that loc to be a new location (no ID specified when constructed).
     *
     * Also inserts any unregistered categories inside loc. A category is
     * unregistered if the locID was omitted when constructed. Additional
     * categories can be registered in the database using insert(Category category)
     *
     * Comments inside loc will not be inserted into the database and must be
     * inserted separately using insert(Comment comment)
     *
     * @param loc the ExposureLocation with the desired data to be saved as a new
     *              entry in the database
     * @return the ID of the created location. Returns -1 if the location
     * entry was not successfully created
     */
    public long insert(ExposureLocation loc) {
        WebLocation wLoc = new WebLocation(loc.getLat(),loc.getLon(),loc.getTotalRating(),
                loc.getNumOfRatings(),loc.getName(),loc.getDesc());

        // register location in database
        final String url = WEB_SERVICE + "insertLocation";
        long locID = restTemplate.postForObject(url, wLoc, Long.class);

        registerCategoriesAndCommentsInLocation(loc.addID(locID));

        return locID;
    }

    /**
     * Returns the ID of the new entry in the database. Makes a new entry in the
     * database for the given ExposurePhoto. Returns a negative number if the
     * entry was not created.
     *
     * Requires that photo to be a new photo (no ID specified when constructed).
     *
     * @param photo the ExposurePhoto with the desired data to be saved as a new entry
     *              in the database
     * @return the ID of the created photo entry. Returns -1 if the photo entry
     * was not successfully created
     */
    public long insert(ExposurePhoto photo) {
        final String url = WEB_SERVICE + "insertPhoto";
        return restTemplate.postForObject(url, new WebPhoto(photo), Long.class);
    }

    /**
     * Returns true if and only if the user was registered in the database
     * successfully
     *
     * User should be constructed with the ID given by the Facebook API.
     *
     * @param user the ExposureUser with the desired data to be saved as a new entry
     *              in the database
     * @return true iff the user was registered in the database successfully
     */
    public boolean insert(ExposureUser user) {
        final String url = WEB_SERVICE + "insertUser";
        return restTemplate.postForObject(url, user, Boolean.class);
    }

    /**
     * Returns the ID of the new entry in the database. Makes a new entry in the
     * database for the given Comment. Returns -1 if the entry was not created.
     *
     * Requires that comment to be a new comment (no ID specified when constructed).
     *
     * @param comment the comment to be saved as a new entry in the database
     * @return the ID of the created comment entry. Returns -1 if the comment
     * entry was not successfully created
     */
    public long insert(Comment comment) {
        final String url = WEB_SERVICE + "insertComment";
        return restTemplate.postForObject(url, comment, Long.class);
    }

    /**
     * Returns true if and only if the new entry was successfully entered into
     * in the database. Makes a new entry in the database for the given
     * Category associated with the location id in the given category.
     *
     * @param category the category to be inserted into the database
     * @return true if and only if the category is registered into the
     * database.
     */
    public boolean insert(Category category) {
        final String url = WEB_SERVICE + "insertCategory";
        return restTemplate.postForObject(url, category, Boolean.class);
    }

    /**
     * Returns true if and only if the entries associated with this user were
     * successfully removed from the database. Deletes the user entry that
     * matches the given id. Also deletes any photos posted by this user.
     *
     * Requires that id is a valid user ID provided by DatabaseManager.
     *
     * @param id the ID of the user entry to be deleted from the database
     * @return true iff the user entry and user photo entries were all
     * deleted from the database successfully
     */
    public boolean removeUser(long id) {
        final String url = WEB_SERVICE + "removeUser";
        return restTemplate.postForObject(url, id, Boolean.class);
    }

    /**
     * Returns true if and only if the photo entry was successfully removed
     * from the database. Deletes the photo entry that matches the given id.
     *
     * Requires that id is a valid photo ID provided by DatabaseManager.
     *
     * @param id The ID of the photo entry to be deleted from the database
     * @return true iff the entry was deleted from the database successfully
     */
    public boolean removePhoto(long id) {
        final String url = WEB_SERVICE + "removePhoto";
        return restTemplate.postForObject(url, id, Boolean.class);
    }

    /**
     * Returns a ExposureUser object that matches the given id. Returns null if there
     * is no user with the given id.
     *
     * Requires that id is a valid user ID provided by DatabaseManager.
     *
     * @param id the ID of the desired user
     * @return the ExposureUser that matches the given id or null if no there is no
     * user with this ID
     */
    public ExposureUser getUser(long id) {
        final String url = WEB_SERVICE + "getUser?id=" + id;
        return restTemplate.getForObject(url, ExposureUser.class);
    }

    /**
     * Returns a ExposureLocation object that matches the given id. Returns null if
     * there is no location with the given id.
     *
     * Requires that id is a valid location ID provided by DatabaseManager.
     *
     * @param id the ID of the desired location
     * @return the ExposureLocation that matches the given id or null if there is no
     * location with this ID
     */
    public ExposureLocation getLocation(long id) {
        final String url = WEB_SERVICE + "getLocation?id=" + id;
        return restTemplate.getForObject(url, ExposureLocation.class);
    }

    /**
     * Returns an array of Photos posted by the user that matches the given ID.
     * The array is ordered in chronological order, newest photo first, that
     * is, by post date descending. Returns null if there are no results.
     *
     * Requires that id is a valid user ID provided by DatabaseManager.
     *
     * @param id the ID of the desired user
     * @return an array of Photos posted by the user matching the given id or
     * null if there are no results.
     */
    public ExposurePhoto[] getUserPhotos(long id) {
        final String url = WEB_SERVICE + "getUserPhotos?id=" + id;
        return downloadPhotos(restTemplate.getForObject(url, ExposurePhoto[].class));
    }

    /**
     * Returns an array of Photos posted to the location that matches the given
     * ID. The list is returned in chronological order, newest photo first,
     * that is, by post date descending. Returns null if there are no results.
     *
     * Requires that id is a valid location ID provided by DatabaseManager.
     *
     * @param id the ID of the desired location
     * @return an array of Photos posted to the location matching the given id
     * or null if there are no results.
     */
    public ExposurePhoto[] getLocationPhotos(long id) {
        final String url = WEB_SERVICE + "getLocationPhotos?id=" + id;
        return downloadPhotos(restTemplate.getForObject(url, ExposurePhoto[].class));
    }

    /**
     * Returns all locations within the given square radius. Returns null if
     * there are no results.
     *
     * @param originLat latitude of the center of the square
     * @param originLon longitude of the center of the square
     * @param radiusLat distance from the center to the top and bottom sides of
     *                  the square
     * @param radiusLon distance from the center to the left and right sides
     *                  of the square
     * @return an array of ExposureLocation within the given square radius or null if
     * there are no results
     */
    public ExposureLocation[] getLocationsInRadius(float originLat, float originLon, float radiusLat, float radiusLon) {
        float lat1 = originLat - radiusLat;
        float lat2 = originLat + radiusLat;
        float lon1 = originLon - radiusLon;
        float lon2 = originLon + radiusLon;

        return getLocationsInRange(lat1, lat2, lon1, lon2);
    }

    /**
     * Returns all locations within the given rectangular range. Returns null
     * if there are no results.
     *
     * requires lat1 < lat2 and lon1 < lon2
     *
     * @param lat1 latitude of the bottom side of the rectangle range
     * @param lat2 latitude of the top side of the rectangle range
     * @param lon1 longitude of the left side of the rectangle range
     * @param lon2 longitude of the right side of the rectangle range
     * @return an array of ExposureLocation within the given square radius of null if
     * there are no results
     */
    public ExposureLocation[] getLocationsInRange(float lat1, float lat2, float lon1, float lon2) {
        final String url = WEB_SERVICE + "getLocationsInRange?lat1=" + lat1 + "&lat2=" + lat2
                + "&lon1=" + lon1 + "&lon2=" + lon2;
        return restTemplate.getForObject(url, ExposureLocation[].class);
    }

    /**
     * Returns a downloaded image File from using a random url
     * @return File containing an image
     */
    protected File downLoadImage() {
        return ImageManager.DownloadFromUrl(DEFAULT_URL, "", CONTEXT);
    }

    /**
     * Returns a downloaded image File from using a random url
     * @param url The specified image we want to download
     * @return File containing an image
     */
    protected File downLoadImage(String url) {
        return ImageManager.DownloadFromUrl(url, "", CONTEXT);
    }

    /**
     * Returns a downloaded image File from using a random url
     * @return File containing an image
     */
    protected File downLoadImage(String url, String file_name) {
        return ImageManager.DownloadFromUrl(DEFAULT_URL, file_name, CONTEXT);
    }

    /**
     * Inserts any unregistered categories and comments inside loc. A category
     * or comment is unregistered if the locID was omitted when constructed.
     *
     * @param loc the location to register categories for
     */
    private void registerCategoriesAndCommentsInLocation(ExposureLocation loc) {
        // register any unregistered categories in loc
        for (Category cat : loc.getCategories()) {
            if (cat.getId() == Category.NULL_ID) { // if unregistered
                Category registeredCat = new Category(loc.getID(),cat.getId());
                insert(registeredCat);
            }
        }

        // register any unregistered comments in loc. Registers with a new ID and
        // associates it with the given location
        for (Comment com : loc.getComments()) {
            if (com.getId() == Comment.NULL_ID) { // if unregistered
                Comment registeredCom = new Comment(com.getAuthorID(),loc.getID(),
                        com.getContent(),com.getDate(),com.getTime());
                insert(com);
            }
        }
    }

    /**
     * Returns an array of downloaded photos specified by the given photo
     * array. Returns null if photo is null.
     * Downloads all the photos in the given array from the source urls in
     * each ExposurePhoto in photos.
     *
     * @param photos array of photos to be downloaded
     * @return an array of downloaded photos or null if there are no photos
     * to download.
     */
    private ExposurePhoto[] downloadPhotos(ExposurePhoto[] photos) {
        // if no results return null
        if (photos == null) {
            return null;
        }
        // download each location photo
        ExposurePhoto[] downloadedPhotos = new ExposurePhoto[photos.length];
        for (int i = 0; i < photos.length; i++) {
            ExposurePhoto photo = photos[i];
            File imgFile = ImageManager.DownloadFromUrl(photo.getSource(), String.valueOf(photos[i].getID()), CONTEXT);
            downloadedPhotos[i] = new ExposurePhoto(photo.getID(),photo.getAuthorID(),photo.getLocID(),
                    photo.getSource(),photo.getDate(),photo.getTime(),imgFile);
        }

        return downloadedPhotos;
    }

    /**
     * Checks to see if the user already rated a location before or not
     *
     * @param uid The user id to of the user that wants to add a rating
     * @param lid The location id the user wants to add a rating to
     * @return true if the user has rated this location before, and false otherwise
     */
    public boolean userHasRatedLocation(long uid, long lid) {
        final String url = WEB_SERVICE + "userHasRatedLocation?lid=" + lid + "&uid" + uid;
        return restTemplate.getForObject(url, Boolean.class);
    }

    /**
     * Remembers who rated a location
     *
     * @param uid User ID of user that rated the location
     * @param lid Location ID of the location the user rated
     */
    public boolean insertUserRating(long lid, long uid, int totalRating, int totalNumberRatings) {
        final String url = WEB_SERVICE + "updateRating?lid=" + lid + "&uid" + uid + "&totalRating"
                + totalRating + "&totalNumberRatings" + totalNumberRatings;
        return true;
    }

    /**
     * WebLocation is an immutable representation of a location on the map. This
     * class can be used for sending data to the web service and is only be used
     * internally by Databasemanager.
     *
     * specfield id : long  // uniquely identifies this location for database interactions
     */
    private static class WebLocation {

        private final long id;
        private final float lat;
        private final float lon;
        private final int totalRating;
        private final int numOfRatings;
        private final String name;
        private final String desc;

        private static final long NULL_ID = -1;

        /*
         * class invariant,
         * name != null
         * desc != null
         * categories != null
         * comments != null
         */

        /**
         * Constructs a ExposureLocation with the specified parameters.
         *
         * Should not be used when inserting a new ExposureLocation using DatabaseManager.
         * You should omit the ID in this case. Only use this constructor when you
         * have an ID provided by DatabaseManager.
         *
         * @param id unique identifier supplied by DatabaseManager
         * @param lat latitude of this location
         * @param lon longitude of this location
         * @param totalRating the total number of points this location has earned
         * @param numOfRatings the total number of reviews this location has received
         * @param name name of location
         * @param desc description of this location
         */
        public WebLocation(long id, float lat, float lon, int totalRating,
                        int numOfRatings, String name, String desc) {
            this.id = id;
            this.lat = lat;
            this.lon = lon;
            this.totalRating = totalRating;
            this.numOfRatings = numOfRatings;
            this.name = name;
            this.desc = desc;
        }

        /**
         * Constructs a ExposureLocation with the specified parameters.
         *
         * The ID parameter is omitted. This constructor should be used when using
         * DatabaseManager to insert a new ExposureLocation into the database.
         *
         * @param lat latitude of this location
         * @param lon longitude of this location
         * @param totalRating the total number of points this location has earned
         * @param numOfRatings the total number of reviews this location has received
         * @param name name of location
         * @param desc description of this location
         */
        public WebLocation(float lat, float lon, int totalRating,
                        int numOfRatings, String name, String desc) {
            this(NULL_ID, lat, lon, totalRating, numOfRatings, name, desc);
        }

        public WebLocation () {
            id = NULL_ID;
            lat = 0;
            lon = 0;
            totalRating = 0;
            numOfRatings = 0;
            name = "";
            desc = "";
        }

        /**
         * Returns the unique identifier for this ExposureLocation.
         *
         * The returned ID can be used to interact with DatabaseManager.
         *
         * @return the unique identifier of this user or -1 if this ExposureLocation has
         * no ID (if ID omitted when constructed)
         */
        public long getID() {
            return id;
        }

        /**
         * Returns the latitude of this location.
         *
         * @return the latitude of this location
         */
        public float getLat() {
            return lat;
        }

        /**
         * Returns the longitude of this location.
         *
         * @return the longitude of this location
         */
        public float getLon() {
            return lon;
        }

        /**
         * Returns the total number of points this location has earned
         *
         * @return the total number of points this location has earned
         */
        public int getTotalRating() {
            return totalRating;
        }

        /**
         * Returns the total number of reviews this location has received.
         *
         * @return the total number of reviews this location has received
         */
        public int getNumOfRatings() {
            return numOfRatings;
        }

        /**
         * Returns a new ExposureLocation with the updated rating.
         *
         * @param newRating the new review to add to this location
         * @return a new ExposureLocation with the updated rating
         */
        public WebLocation addRating(int newRating) {
            return new WebLocation(id, lat, lon, totalRating + newRating,
                    numOfRatings + 1, name, desc);
        }

        /**
         * Returns the name of this ExposureLocation.
         *
         * @return the name of this ExposureLocation
         */
        public String getName() {
            return name;
        }

        /**
         * Return the description of this ExposureLocation
         *
         * @return the description of this ExposureLocation
         */
        public String getDesc() {
            return desc;
        }

        /**
         * Returns a ExposureLocation with the given id
         *
         * This method is a more convenient way to inject an ID into the object
         * without having to construct a new one yourself. Only use this method
         * if you have been provided a valid ID from DatabaseManager.
         *
         * @return a ExposureLocation with the given id
         */
        public WebLocation addID(long id) {
            return new WebLocation(id,lat,lon,totalRating,numOfRatings,name,desc);
        }
    }



    /**
     * WebPhoto is an immutable representation of a photo. This class can be used
     * for sending data to the web service and is only be used
     * internally by Databasemanager.
     *
     * specfield id : long  // uniquely identifies this photo for database interactions
     */
    private static class WebPhoto {

        private final long id;
        private final long authorID;
        private final long locID;
        private final String source;
        private final Date date;
        private final Time time;
        private final String file;

        private static final long NULL_ID = -1;

        /**
         * Constructs a WebPhoto with the given ExposurePhoto.
         *
         * @param ep ExposurePhoto object to convert
         */
        public WebPhoto(ExposurePhoto ep) {
            this.id = ep.getID();
            this.authorID = ep.getAuthorID();
            this.locID = ep.getLocID();
            this.source = ep.getSource();
            this.date = ep.getDate();
            this.time = ep.getTime();
            this.file = convertFileToString(ep.getFile());
        }

        // Convert my file to a Base64 String
        private String convertFileToString(File file) {

            /*
            // Test
            File dest = new File("/Users/Tyler/Desktop/fromApp");

            try {
                dest.createNewFile();
                FileInputStream in = new FileInputStream(file);
                FileOutputStream out = new FileOutputStream(dest);

                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                in.close();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            // Test end
            */


            String encodedBase64 = null;
            FileInputStream fileInputStream = null;
            byte[] bytes = new byte[(int)file.length()];
            try {
                fileInputStream = new FileInputStream(file);
                fileInputStream.read(bytes);
                fileInputStream.close();

                String encodedString = Base64.encodeToString(bytes, Base64.DEFAULT);
                return encodedString;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            /*

            // Test

            try {
                File textFile = new File("/Users/Tyler/fromApp.txt");
                FileOutputStream stream = new FileOutputStream(textFile);
                PrintStream p = new PrintStream(stream);
                p.print(encodedBase64);
                p.close();
                stream.close();
            }
            catch (IOException e) {
                Log.e("Exception", "File write failed: " + e.toString());
            }
            // Test end
            */


            return encodedBase64;
        }

        /**
         * Encodes the byte array into base64 string
         *
         * @param imageByteArray - byte array
         * @return String a {@link java.lang.String}
         */
        /*
        public static String encodeImage(byte[] imageByteArray) {
            return Base64.encodeBase64URLSafeString(imageByteArray);
        }
*/
        /**
         * Default constructor required for JSON decoding.
         */
        public WebPhoto() {
            id = NULL_ID;
            authorID = NULL_ID;
            locID = NULL_ID;
            source = "";
            date = new Date(0);
            time = new Time(0);
            file = "";
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
         * Returns the File of this photo.
         *
         * @return the File of this photo.
         */
        public String getFile() {
            return file;
        }

    }


    /**
     * ImageManager is a utility that handles downloading images.
     */
    private static class ImageManager {

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
