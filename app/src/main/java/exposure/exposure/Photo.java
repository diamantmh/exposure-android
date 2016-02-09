package exposure.exposure;
import java.util.Date;
import java.sql.Time;

/**
 * Photo is an immutable representation of a photo.
 *
 * @specfield id : long  // uniquely identifies this photo for database interactions
 */
public final class Photo {

    private final long id;
    private final long authorID;
    private final long locID;
    private final String source;
    private final Date date;
    private final Time time;

    private static final long NULL_ID = -1;

    /*
     * class invariant,
     * source != null
     * date != null
     * time != null
     */

    /**
     * Constructs a Photo with the specified parameters.
     *
     * Should not be used when inserting a new Photo using DatabaseManager. You
     * should omit the ID in this case. Only use this constructor when you have
     * an ID provided by DatabaseManager.
     *
     * @param id - unique identifier supplied by DatabaseManager
     * @param authorID - unique identifier of the author of photo, supplied by DatabaseManager
     * @param locID - unique identifier of location where photo was taken,
     *              supplied by DatabaseManager
     * @param source - source link of photo
     * @param date - date photo was taken
     * @param time - time photo was taken
     */
    public Photo(long id, long authorID, long locID, String source, Date date, Time time) {
        this.id = id;
        this.authorID = authorID;
        this.locID = locID;
        this.source = source;
        this.date = new Date(date.getTime());
        this.time = new Time(time.getTime());
    }

    /**
     * Constructs a Photo with the specified parameters.
     *
     * The ID parameter is omitted. This constructor should be used when using
     * DatabaseManager to insert a new photo into the database.
     *
     * @param authorID - unique identifier of the author of photo, supplied by DatabaseManager
     * @param locID - unique identifier of location where photo was taken,
     *              supplied by DatabaseManager
     * @param source - source link of photo
     * @param date - date photo was taken
     * @param time - time photo was taken
     */
    public Photo(long authorID, long locID, String source, Date date, Time time) {
        this(NULL_ID, authorID, locID, source, date, time);
    }

    /**
     * Returns the unique identifier for this photo.
     *
     * The returned ID can be used to interact with DatabaseManager.
     *
     * @return the unique identifier of this photo or -1 if this Photo has
     * no ID (if ID omitted when constructed)
     */
    public long getID() {
        return id;
    }

    /**
     * Returns the unique identifier of the User that originally posted this
     * photo.
     *
     * The returned ID can be used to interact with DatabaseManager.
     *
     * @return the unique identifier of the User that posted this photo
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
     * @return the unique identifier of the Location where this Photo was taken
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
}
