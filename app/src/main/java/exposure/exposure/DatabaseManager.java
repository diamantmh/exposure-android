package exposure.exposure;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * DatabaseManager is an abstraction that hides the model. DatabaseManager can
 * provide data from, add data to, and update existing data on the database.
 *
 * DatabaseManager makes long, synchronous calls to the database. You must use
 * an AsyncTask or a subclass of AsyncTask to make calls to DatabaseManager in
 * the background to avoid locking the UI thread.
 */
public class DatabaseManager {

    private RestTemplate restTemplate;

    public static final String WEB_SERVICE = "http://service/"; // TODO: Fill in url of web service

    public DatabaseManager() {
        restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
    }

    /**
     * Returns true if and only if the specified location is updated. Replaces
     * the entry in the database matching the given id with the given value.
     *
     * @param loc - the Location with the desired data
     * @return true iff the location entry matching the given ID was updated
      */
    public boolean update(Location loc) {
        final String url = WEB_SERVICE + "updateLocation";
        return restTemplate.postForObject(url, loc, Boolean.class);
        /* note to web service (that's you Tyler):
         * for this method server side, you can get the Location object that
         * I'm sending using:
         *
         * @RequestMapping(value="/updateLocation", method=RequestMethod.POST)
         * public boolean updateLocation(@RequestBody Location loc)
         *
         * as your method signature. This maps this url to this method
         * signature and retrieves the Location object from the body for your
         * method parameter.
         */
    }

    /**
     * Returns true if and only if the specified user is updated. Replaces
     * the entry in the database matching the given id with the given value.
     *
     * @param user - the User with the desired data
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
     * @param loc - the Location with the desired data to be saved as a new
     *              entry in the database
     * @return the ID of the created location. Returns -1 if the location
     * entry was not successfully created.
     */
    public int insert(Location loc) {
        final String url = WEB_SERVICE + "insertLocation";
        return restTemplate.postForObject(url, loc, Integer.class);
    }

    /**
     * Returns the ID of the new entry in the database. Makes a new entry in the
     * database for the given Photo. Returns -1 if the entry was not created.
     *
     * @param photo - the Photo with the desired data to be saved as a new entry
     *              in the database
     * @return the ID of the created photo entry. Returns -1 if the photo entry
     * was not successfully created.
     */
    public int insert(Photo photo) {
        final String url = WEB_SERVICE + "insertPhoto";
        return restTemplate.postForObject(url, photo, Integer.class);
    }

    /**
     * Returns the ID of the new entry in the database. Makes a new entry in the
     * database for the given User. Returns -1 if the entry was not created.
     *
     * @param user - the User with the desired data to be saved as a new entry
     *              in the database
     * @return the ID of the created user entry. Returns -1 if the user entry
     * was not successfully created.
     */
    public int insert(User user) {
        final String url = WEB_SERVICE + "insertUser";
        return restTemplate.postForObject(url, user, Integer.class);
    }

    /**
     * Returns true if and only if the entries associated with this user were
     * successfully removed from the database. Deletes the user entry that
     * matches the given id. Also deletes any photos posted by this user.
     *
     * @param id - the ID of the user entry to be deleted from the database
     * @return true iff the user entry and user photo entries were all
     * deleted from the database successfully
     */
    public boolean removeUser(int id) {
        final String url = WEB_SERVICE + "removeUser";

        // We could use restTemplate.delete() here, but then we have no return
        // A good RESTful application would use delete, but post is usable.
        return restTemplate.postForObject(url, id, Boolean.class);
        /* Note to web service:
         * you need to remove all photos where User.id = Photo.uid,
         * remove those photos from the photo storage,
         * then remove the entry for the user too.
         */
    }

    /**
     * Returns true if and only if the photo entry was successfully removed
     * from the database. Deletes the photo entry that matches the given id.
     *
     * @param id - The ID of the photo entry to be deleted from the database
     * @return true iff the entry was deleted from the database successfully
     */
    public boolean removePhoto(int id) {
        final String url = WEB_SERVICE + "removePhoto";
        return restTemplate.postForObject(url, id, Boolean.class);
    }

    /**
     * Returns a User object that matches the given id. Returns null if there
     * is no user with the given id.
     *
     * @param id - the ID of the desired user
     * @return the User that matches the given id
     */
    public User getUser(int id) {
        final String url = WEB_SERVICE + "getUser?id=" + id;
        return restTemplate.getForObject(url, User.class);
        /* note to web service:
         * for this method server side, you can get the id param using:
         *
         * @RequestMapping("/getUser")
         * public User getUser(@RequestParam(id="id") int id)
         *
         * as your method signature. This binds the id in my url to your
         * method parameter. The @RequestMapping makes sure that urls that
         * end in getUser are bound to your getUser method server side.
         */
    }

    /**
     * Returns an array of Photos posted by the user that matches the given ID.
     * The array is ordered in chronological order, newest photo first, that
     * is, by post date descending.
     *
     * @param id - the ID of the desired user
     * @return an array of Photos posted by the user matching the given id
     */
    public Photo[] getUserPhotos(int id) {
        final String url = WEB_SERVICE + "getUserPhotos?id=" + id;
        return restTemplate.getForObject(url, Photo[].class);
    }

    /**
     * Returns an array of Photos posted to the location that matches the given
     * ID. The list is returned in chronological order, newest photo first,
     * that is, by post date descending.
     *
     * @param id - the ID of the desired location
     * @return a list of Photos posted to the location matching the given id
     */
    public Photo[] getLocationPhotos(int id) {
        final String url = WEB_SERVICE + "getLocationPhotos?id=" + id;
        return restTemplate.getForObject(url, Photo[].class);
    }
}
