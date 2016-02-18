package exposure.exposure;

import java.sql.Time;
import java.util.Date;

/**
 * Comment is an immutable representation of a post to a location.
 *
 * specfield id : long  // uniquely identifies this Category for database interactions
 */
public class Comment {

    private final long id;
    private final long authorID;
    private final long locID;
    private final String content;
    private final Date date;
    private final Time time;

    private static final long NULL_ID = -1;

    /*
     * class invariant,
     * String != null
     * date != null
     * time != null
     */

    /**
     * Constructs a Comment with the given parameters.
     *
     * Requires date and time to be not null
     *
     * Should not be used when adding a new comment to a Location. You should
     * omit the ID in this case, add it to Location, then update Location using
     * DatabaseManager. Only use this constructor when you have an ID provided
     * by DatabaseManager.
     *
     * @param id unique identifier supplied by DatabaseManager
     * @param authorID unique identifier of the author of photo, supplied by DatabaseManager
     * @param locID unique identifier of location where photo was taken,
     *              supplied by DatabaseManager
     * @param content the body of the message posted to the location
     */
    public Comment(long id, long authorID, long locID, String content, Date date, Time time) {
        this.id = id;
        this.authorID = authorID;
        this.locID = locID;
        this.content = (content==null) ? "" : content;
        this.date = new Date(date.getTime());
        this.time = new Time(time.getTime());
    }

    /**
     * Constructs a Comment with the given parameters.
     *
     * The ID parameter is omitted. This constructor should be used when adding
     * a new comment to a Location. Once you have a Location with new comments
     * in them, you must update that location using DatabaseManager for the
     * changes to be saved.
     *
     * @param authorID unique identifier of the author of photo, supplied by DatabaseManager
     * @param locID unique identifier of location where photo was taken,
     *              supplied by DatabaseManager
     * @param content the body of the message posted to the location
     */
    public Comment (long authorID, long locID, String content, Date date, Time time) {
        this(NULL_ID, authorID, locID, content, date, time);
    }

    /**
     * Returns the unique identifier for this Comment.
     *
     * The returned ID can be used to interact with DatabaseManager.
     *
     * @return the unique identifier of this photo or -1 if this Comment has
     * no ID (if ID omitted when constructed)
     */
    public long getId() {
        return id;
    }

    /**
     * Returns the unique identifier of the User that originally posted this
     * Comment.
     *
     * The returned ID can be used to interact with DatabaseManager.
     *
     * @return the unique identifier of the User that posted this photo
     */
    public long getAuthorID() {
        return authorID;
    }

    /**
     * Returns the unique identifier of the location to where this comment was
     * posted.
     *
     * The returned ID can be used to interact with DatabaseManager.
     *
     * @return the unique identifier of this Location
     */
    public long getLocID() {
        return locID;
    }

    /**
     * Returns the body of the message that was posted.
     *
     * @return the body of the message that was posted.
     */
    public String getContent() {
        return content;
    }

    /**
     * Returns the Date this comment was posted.
     *
     * @return the Date this comment was posted.
     */
    public Date getDate() {
        return new Date(date.getTime());
    }

    /**
     * Returns the Time this comment was posted.
     *
     * @return the Time this comment was posted.
     */
    public Time getTime() {
        return new Time(time.getTime());
    }
}

