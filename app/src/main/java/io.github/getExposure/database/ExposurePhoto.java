package io.github.getExposure.database;
import java.io.File;
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
    private static final String DEFAULT_PICTURE = "https://avatars2.githubusercontent.com/u/16708552?v=3&s=200";

    /*
     * class invariant,
     * source != null
     * date != null
     * time != null
     */

    /**
     * Constructs a ExposurePhoto with the specified parameters.
     *
     * Should not be used when inserting a new ExposurePhoto using DatabaseManager. You
     * should omit the ID in this case. Only use this constructor when you have
     * an ID provided by DatabaseManager.
     *
     * Parameters date and time must not be null. You must fill in the date and
     * time at instantiation. Behavior not specified if file is null.
     *
     * @param id unique identifier supplied by DatabaseManager
     * @param authorID unique identifier of the author of photo, supplied by DatabaseManager
     * @param locID unique identifier of location where photo was taken,
     *              supplied by DatabaseManager
     * @param source source link of photo
     * @param date date photo was taken
     * @param time time photo was taken
     */
    public ExposurePhoto(long id, long authorID, long locID, String source, Date date, Time time, File file) {
        this.id = id;
        this.authorID = authorID;
        this.locID = locID;
        this.source = (source == null) ? "" : source;
        this.date = (date == null) ? new Date(0) : new Date(date.getTime());
        this.time = (time == null) ? new Time(0) : new Time(time.getTime());
        this.file = (file == null) ? new File(DEFAULT_PICTURE) : file;
    }

    /**
     * Constructs a ExposurePhoto with the specified parameters.
     *
     * The ID parameter is omitted. This constructor should be used when using
     * DatabaseManager to insert a new photo into the database.
     *
     * Parameters date and time must not be null. You must fill in the date and
     * time at instantiation. Behavior not specified if file is null.
     *
     * @param authorID unique identifier of the author of photo, supplied by DatabaseManager
     * @param locID unique identifier of location where photo was taken,
     *              supplied by DatabaseManager
     * @param source source link of photo
     * @param date date photo was taken
     * @param time time photo was taken
     */
    public ExposurePhoto(long authorID, long locID, String source, Date date, Time time, File file) {
        this(NULL_ID, authorID, locID, source, date, time, file);
    }
    
    public ExposurePhoto() {
        id = NULL_ID;
        authorID = NULL_ID;
        locID = NULL_ID;
        source = "";
        date = new Date(0);
        time = new Time(0);
        file = new File(DEFAULT_PICTURE);
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
        return new ExposurePhoto(id,locID,source,new Date(date.getTime()),new Time(time.getTime()), file);
    }
}
