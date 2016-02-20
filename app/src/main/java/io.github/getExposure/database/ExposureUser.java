package io.github.getExposure.database;

/**
 * ExposureUser is an immutable representation of a user of Exposure.
 *
 * specfield id : long  // uniquely identifies this user for database interactions
 */
public final class ExposureUser {

    private final long id;
    private final String username;
    private final String link;
    private final String aboutMe;

    private static final long NULL_ID = -1;

    /*
     * class invariant,
     * restTemplate != null
     */

    /**
     * Constructs a ExposureUser with the specified parameters.
     *
     * Should not be used when inserting a new ExposureUser using DatabaseManager. You
     * should omit the ID in this case. Only use this constructor when you have
     * an ID provided by DatabaseManager.
     *
     * @param id unique identifier supplied by DatabaseManager
     * @param username display name of this user
     * @param link source of profile picture of this user
     * @param aboutMe the user-supplied self description of this user
     */
    public ExposureUser(long id, String username, String link, String aboutMe) {
        this.id = id;
        this.username = username;
        this.link = link;
        this.aboutMe = aboutMe;
    }

    /**
     * Constructs a ExposureUser with the specified parameters.
     *
     * The ID parameter is omitted. This constructor should be used when using
     * DatabaseManager to insert a new ExposureUser into the database.
     *
     * @param username display name of this user
     * @param link source of profile picture of this user
     * @param aboutMe the user-supplied self description of this user
     */
    public ExposureUser(String username, String link, String aboutMe) {
        this(NULL_ID,username,link,aboutMe);
    }

    public ExposureUser() {
        //empty constructor used only for JSON conversion
        this.id = NULL_ID;
        this.username = "";
        link = "";
        aboutMe = "";
    }
    /**
     * Returns the unique identifier for this user.
     *
     * The returned ID can be used to interact with DatabaseManager.
     *
     * @return the unique identifier of this user or -1 if this ExposureUser has
     * no ID (if ID omitted when constructed)
     */
    public long getID() {
        return id;
    }

    /**
     * Returns the username of this.
     *
     * @return the username of this user
     */
    public String getUsername() {
        return username;
    }

    /**
     * Returns the source link to the profile picture of this user as a String
     *
     * @return the source link to the profile picture of this user
     */
    public String getLink() {
        return link;
    }

    /**
     * Returns the "about me" section of this user.
     *
     * @return the "about me" section of this user.
     */
    public String getAboutMe() {
        return aboutMe;
    }

    /**
     * Returns a ExposureUser with the given id
     *
     * This method is a more convenient way to inject an ID into the object
     * without having to construct a new one yourself. Only use this method
     * if you have been provided a valid ID from DatabaseManager.
     *
     * @return a ExposureUser with the given id
     */
    public ExposureUser addID(long id) {
        return new ExposureUser(id,username,link,aboutMe);
    }
}
