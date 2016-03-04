package io.github.getExposure.database;

import java.io.Serializable;
import java.sql.Time;
import java.util.Date;

/**
 * Comment is an immutable representation of a post to a location.
 *
 * specfield id : long  // uniquely identifies this Category for database interactions
 */
public class Comment implements Serializable {

    private final long id;
    private final long authorID;
    private final long locID;
    private final String username;
    private final String content;
    private final Date date;
    private final Time time;

    protected static final long NULL_ID = -1;

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
     * Should not be used when adding a new comment to a ExposureLocation. You should
     * omit the ID in this case, add it to ExposureLocation, then update ExposureLocation using
     * DatabaseManager. Only use this constructor when you have an ID provided
     * by DatabaseManager.
     *
     * @param id unique identifier supplied by DatabaseManager
     * @param authorID unique identifier of the author of photo, supplied by DatabaseManager
     * @param locID unique identifier of location where photo was taken,
     *              supplied by DatabaseManager
     * @param username the username of the user who authored this comment
     * @param content the body of the message posted to the location
     * @param date the date this comment was posted
     * @param time the time this comment was posted
     */
    public Comment(long id, long authorID, long locID, String username, String content, Date date, Time time) {
        this.id = id;
        this.authorID = authorID;
        this.locID = locID;
        this.username = (username==null) ? "" : username;
        this.content = (content==null) ? "" : content;
        this.date = new Date(date.getTime());
        this.time = new Time(time.getTime());
    }

    /**
     * Constructs a Comment with the given parameters.
     *
     * The ID parameter is omitted. This constructor should be used when adding
     * a new comment to a ExposureLocation. Once you have a ExposureLocation with new comments
     * in them, you must update that location using DatabaseManager for the
     * changes to be saved.
     *
     * @param authorID unique identifier of the author of photo, supplied by DatabaseManager
     * @param locID unique identifier of location where photo was taken,
     *              supplied by DatabaseManager
     * @param username the username of the user who authored this comment
     * @param content the body of the message posted to the location
     * @param date the date this comment was posted
     * @param time the time this comment was posted
     */
    public Comment (long authorID, long locID, String username, String content, Date date, Time time) {
        this(NULL_ID, authorID, locID, username, content, date, time);
    }

    public Comment () {
        id = NULL_ID;
        authorID = NULL_ID;
        locID = NULL_ID;
        username = "";
        content = "";
        date = new Date(0);
        time = new Time(0);
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
     * Returns the unique identifier of the ExposureUser that originally posted this
     * Comment.
     *
     * The returned ID can be used to interact with DatabaseManager.
     *
     * @return the unique identifier of the ExposureUser that posted this photo
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
     * @return the unique identifier of this ExposureLocation
     */
    public long getLocID() {
        return locID;
    }

    /**
     * Returns the username of the author of this comment
     *
     * @return the username of the author of this comment
     */
    public String getUsername() {
        return username;
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

    /**
     * Returns a Comment with the given id
     *
     * This method is a more convenient way to inject an ID into the object
     * without having to construct a new one yourself. Only use this method
     * if you have been provided a valid ID from DatabaseManager.
     *
     * @return a Comment with the given id
     */
    public Comment addID(long id) {
        return new Comment(id,authorID,locID,username,content,
                new Date(date.getTime()),new Time(time.getTime()));
    }
}

