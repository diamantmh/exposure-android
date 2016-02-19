package exposure.exposure;

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

	public static final String WEB_SERVICE = "http://exposureweb.cloudapp.net/REST/WebService/";
    public static final long NULL_ID = -1;

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
     * @param loc the Location with the desired data
     * @return true iff the location entry matching the given ID was updated
      */
    public boolean update(Location loc) {
        final String url = WEB_SERVICE + "updateLocation";
        return restTemplate.postForObject(url, loc, Boolean.class);
    }

    /**
     * Returns true if and only if the specified user is updated. Replaces
     * the entry in the database matching the given id with the given value.
     *
     * Requires that user to be an existing user (a user returned by DatabaseManager).
     *
     * @param user the User with the desired data
     * @return true iff the user entry matching the given ID was updated
     */
    public boolean update(User user) {
        final String url = WEB_SERVICE + "updateUser";
        return restTemplate.postForObject(url, user, Boolean.class);
    }

    /**
     * Returns the ID of the new entry in the database. Makes a new entry in the
     * database for the given Location. Returns -1 if the entry was not
     * created.
     *
     * Requires that loc to be a new location (no ID specified when constructed).
     *
     * @param loc the Location with the desired data to be saved as a new
     *              entry in the database
     * @return the ID of the created location. Returns -1 if the location
     * entry was not successfully created
     */
    public long insert(Location loc) {
        final String url = WEB_SERVICE + "insertLocation";
        return restTemplate.postForObject(url, loc, Long.class);
    }

    /**
     * Returns the ID of the new entry in the database. Makes a new entry in the
     * database for the given Photo. Returns -1 if the entry was not created.
     *
     * Requires that photo to be a new photo (no ID specified when constructed).
     *
     * @param photo the Photo with the desired data to be saved as a new entry
     *              in the database
     * @return the ID of the created photo entry. Returns -1 if the photo entry
     * was not successfully created
     */
    public long insert(Photo photo) {
        final String url = WEB_SERVICE + "insertPhoto";
        return restTemplate.postForObject(url, photo, Long.class);
    }

    /**
     * Returns the ID of the new entry in the database. Makes a new entry in the
     * database for the given User. Returns -1 if the entry was not created.
     *
     * Requires that user to be a new user (no ID specified when constructed).
     *
     * @param user the User with the desired data to be saved as a new entry
     *              in the database
     * @return the ID of the created user entry. Returns -1 if the user entry
     * was not successfully created
     */
    public long insert(User user) {
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
     * Returns a User object that matches the given id. Returns null if there
     * is no user with the given id.
     *
     * Requires that id is a valid user ID provided by DatabaseManager.
     *
     * @param id the ID of the desired user
     * @return the User that matches the given id or null if no there is no
     * user with this ID
     */
    public User getUser(long id) {
        final String url = WEB_SERVICE + "getUser?id=" + id;
        return restTemplate.getForObject(url, User.class);
    }

    /**
     * Returns a Location object that matches the given id. Returns null if
     * there is no location with the given id.
     *
     * Requires that id is a valid location ID provided by DatabaseManager.
     *
     * @param id the ID of the desired location
     * @return the Location that matches the given id or null if there is no
     * location with this ID
     */
    public Location getLocation(long id) {
        final String url = WEB_SERVICE + "getLocation?id=" + id;
        return restTemplate.getForObject(url, Location.class);
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
    public Photo[] getUserPhotos(long id) {
        final String url = WEB_SERVICE + "getUserPhotos?id=" + id;
        return restTemplate.getForObject(url, Photo[].class);
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
    public Photo[] getLocationPhotos(long id) {
        final String url = WEB_SERVICE + "getLocationPhotos?id=" + id;
        return restTemplate.getForObject(url, Photo[].class);
    }
}
