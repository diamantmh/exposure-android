package exposure.exposure;

/**
 * DatabaseManager is an abstraction that hides the model. DatabaseManager can
 * provide data from, add data to, or update existing data on the database.
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
     * @param id - the ID of the location to be overwrite in the database
     * @param value - the Location with the desired data
     * @return true iff the location matching the given ID is updated
      */
    public boolean update(int id, Location value) {
        // TODO: consume(?) web service
        return false;
    }

    /**
     * Returns true if and only if the specified user is updated. Replaces
     * the entry in the database matching the given id with the given value.
     *
     * @param id - the ID of the user to be overwrite in the database
     * @param value - the User with the desired data
     * @return true iff the user matching the given ID is updated
     */
    public boolean update(int id, User value) {
        // TODO: consume(?) web service
        return false;
    }

    /**
     * Returns the ID of the new entryin the database. Makes a new entry in the
     * database for the given location. Returns -1 if the entry was not
     * created.
     *
     * @param value - the Location with the desired data to be written as a
     *              new entry in the database
     * @return the ID of the created location. Returns -1 if the location
     * was not successfully created.
     */
    public int insert(Location value) {
        // TODO: implement
        return -1;
    }


}
