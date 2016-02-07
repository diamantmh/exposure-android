package exposure.exposure;

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

    // TODO: fields?
    // TODO: constants?
    public static final String WEB_SERVICE = "http://"; // TODO: Fill in url of web service

    public DatabaseManager() {
        // TODO: initialize fields?
    }

    /**
     * Returns true if and only if the specified location is updated. Replaces
     * the entry in the database matching the given id with the given value.
     *
     * @param loc - the Location with the desired data
     * @return true iff the location entry matching the given ID was updated
      */
    public boolean update(Location loc) {
        // TODO: implement
        return false;
    }

    /**
     * Returns true if and only if the specified user is updated. Replaces
     * the entry in the database matching the given id with the given value.
     *
     * @param user - the User with the desired data
     * @return true iff the user entry matching the given ID was updated
     */
    public boolean update(User user) {
        // TODO: implement
        return false;
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
        // TODO: implement
        return -1;
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
        // TODO: implement
        return -1;
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
        // TODO: implement
        return -1;
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
        // TODO: implement
        // you need to remove all photos where User.id = Photo.uid
        // remove those photos from the photo storage
        // then remove the entry for the user too.
        return false;
    }

    /**
     * Returns true if and only if the photo entry was successfully removed
     * from the database. Deletes the photo entry that matches the given id.
     *
     * @param id - The ID of the photo entry to be deleted from the database
     * @return true iff the entry was deleted from the database successfully
     */
    public boolean removePhoto(int id) {
        // TODO: implement
        return false;
    }

    /**
     * Returns a list of Photos that have been posted by the user that matches
     * the given ID. The list is ordered in chronological order, newest photo
     * first, that is, by post date descending.
     *
     * @param id - the ID of the desired user
     * @return a list of Photos posted by the user matching the given id
     */
    public List<Photo> getUserPhotos(int id) {
        // TODO: implement
        List<Photo> photoList = new ArrayList<>();
        return photoList;
    }
}
