package io.github.getExposure.database;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

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

    protected static final String WEB_SERVICE = "http://kekonatvm.cloudapp.net/RESTfulProject/REST/WebService/";

    private static final long NULL_ID = -1;

    /*
     * class invariant,
     * restTemplate != null
     */

    /**
     * Constructs a DatabaseManager.
     */
    public DatabaseManager() {
        this(new RestTemplate());
    }

    /**
     * Constructs a DatabaseManager with the given RestTemplate.
     *
     * This constructor can be used to provide custom message converters,
     * or it can be used for testing purposes (providing a mocked
     * RestTemplate)
     */
    public DatabaseManager(RestTemplate rt) {
        restTemplate = rt;
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
    }

    /**
     * Returns true if and only if the specified location is updated. Replaces
     * the entry in the database matching the given id with the given value.
     *
     * Requires that loc to be an existing location (a location returned by DatabaseManager).
     *
     * @param loc the ExposureLocation with the desired data
     * @return true iff the location entry matching the given ID was updated
      */

    public boolean update(ExposureLocation loc) {
        WebLocation wLoc = new WebLocation(loc.getLat(),loc.getLon(),loc.getTotalRating(),
                loc.getNumOfRatings(),loc.getName(),loc.getDesc());
        final String url = WEB_SERVICE + "updateLocation";
        return restTemplate.postForObject(url, wLoc, Boolean.class);
    }

    /**
     * Returns true if and only if the specified user is updated. Replaces
     * the entry in the database matching the given id with the given value.
     *
     * Requires that user to be an existing user (a user returned by DatabaseManager).
     *
     * @param user the ExposureUser with the desired data
     * @return true iff the user entry matching the given ID was updated
     */
    public boolean update(ExposureUser user) {
        final String url = WEB_SERVICE + "updateUser";
        return restTemplate.postForObject(url, user, Boolean.class);
    }

    /**
     * Returns the ID of the new entry in the database. Makes a new entry in the
     * database for the given ExposureLocation. Returns -1 if the entry was not
     * created.
     *
     * Requires that loc to be a new location (no ID specified when constructed).
     *
     * @param loc the ExposureLocation with the desired data to be saved as a new
     *              entry in the database
     * @return the ID of the created location. Returns -1 if the location
     * entry was not successfully created
     */
    public long insert(ExposureLocation loc) {
        WebLocation wLoc = new WebLocation(loc.getLat(),loc.getLon(),loc.getTotalRating(),
                loc.getNumOfRatings(),loc.getName(),loc.getDesc());
        final String url = WEB_SERVICE + "insertLocation";
        return restTemplate.postForObject(url, wLoc, Long.class);
    }

    /**
     * Returns the ID of the new entry in the database. Makes a new entry in the
     * database for the given ExposurePhoto. Returns -1 if the entry was not created.
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
        return restTemplate.postForObject(url, photo, Long.class);
    }

    /**
     * Returns the ID of the new entry in the database. Makes a new entry in the
     * database for the given ExposureUser. Returns -1 if the entry was not created.
     *
     * Requires that user to be a new user (no ID specified when constructed).
     *
     * @param user the ExposureUser with the desired data to be saved as a new entry
     *              in the database
     * @return the ID of the created user entry. Returns -1 if the user entry
     * was not successfully created
     */
    public long insert(ExposureUser user) {
        final String url = WEB_SERVICE + "insertUser";
        return restTemplate.postForObject(url, user, Long.class);
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
        return restTemplate.getForObject(url, ExposurePhoto[].class);
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
        return restTemplate.getForObject(url, ExposurePhoto[].class);
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
}
