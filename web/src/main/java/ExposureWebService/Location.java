package exposure.exposure;

import java.util.*;

/**
 * Location is an immutable representation of a location on the map.
 *
 * specfield id : long  // uniquely identifies this location for database interactions
 */
public class Location {

    private final long id;
    private final float lat;
    private final float lon;
    private final int totalRating;
    private final int numOfRatings;
    private final String name;
    private final String desc;
    private Set<Category> categories;
    private List<Comment> comments;

    private static final long NULL_ID = -1;

    /*
     * class invariant,
     * name != null
     * desc != null
     * categories != null
     * comments != null
     */

    /**
     * Constructs a Location with the specified parameters.
     *
     * Should not be used when inserting a new Location using DatabaseManager.
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
     * @param categories all the tags associated with this location
     * @param comments list of comments posted to this location
     */
    public Location(long id, float lat, float lon, int totalRating,
                    int numOfRatings, String name, String desc,
                    Iterable<Category> categories, Iterable<Comment> comments) {
        this.id = id;
        this.lat = lat;
        this.lon = lon;
        this.totalRating = totalRating;
        this.numOfRatings = numOfRatings;
        this.name = name;
        this.desc = desc;

        // safe deep copy in
        this.categories = new HashSet<>();
        for (Category cat: categories) {
            this.categories.add(cat);
        }
        //safe deep copy in
        this.comments = new ArrayList<>();
        for (Comment comment : comments) {
            this.comments.add(comment);
        }
    }

    /**
     * Constructs a Location with the specified parameters.
     *
     * The ID parameter is omitted. This constructor should be used when using
     * DatabaseManager to insert a new Location into the database.
     *
     * @param lat latitude of this location
     * @param lon longitude of this location
     * @param totalRating the total number of points this location has earned
     * @param numOfRatings the total number of reviews this location has received
     * @param name name of location
     * @param desc description of this location
     * @param categories all the tags associated with this location
     * @param comments list of comments posted to this location
     */
    public Location(float lat, float lon, int totalRating,
                    int numOfRatings, String name, String desc,
                    Iterable<Category> categories, List<Comment> comments) {
        this(NULL_ID, lat, lon, totalRating, numOfRatings, name, desc, categories, comments);
    }

    /**
     * Returns the unique identifier for this Location.
     *
     * The returned ID can be used to interact with DatabaseManager.
     *
     * @return the unique identifier of this user or -1 if this Location has
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
     * Returns the average rating this location has received. Returns -1 if
     * this location hasn't received any reviews yet.
     *
     * @return the average rating this location has received or -1 if there are
     * no reviews for this location yet
     */
    public double getRating() {
       return (numOfRatings == 0) ? -1 : (double)totalRating/numOfRatings;
    }

    /**
     * Returns a new Location with the updated rating.
     *
     * @param newRating the new review to add to this location
     * @return a new Location with the updated rating
     */
    public Location addRating(int newRating) {
        return new Location(id, lat, lon, totalRating + newRating,
                numOfRatings + 1, name, desc, getCategories(), getComments());
    }

    /**
     * Returns the name of this Location.
     *
     * @return the name of this Location
     */
    public String getName() {
        return name;
    }

    /**
     * Return the description of this Location
     *
     * @return the description of this Location
     */
    public String getDesc() {
        return desc;
    }

    /**
     * Returns a Set of all the categories, or tags, associated with this
     * Location.
     *
     * @return a Set of all categories, or tags, associated with this
     * Location
     */
    public Set<Category> getCategories () {
        Set<Category> retSet = new HashSet<>();
        // safe deep copy out
        for (Category cat: categories) {
            retSet.add(cat);
        }
        return retSet;
    }

    /**
     * Returns a List of all comments posted to this Location.
     *
     * @return a List of all comments posted to this Location
     */
    public List<Comment> getComments() {
        List<Comment> commentsList = new ArrayList<>();
        // safe deep copy out
        for (Comment comment : comments) {
            commentsList.add(comment);
        }
        return commentsList;
    }
}
