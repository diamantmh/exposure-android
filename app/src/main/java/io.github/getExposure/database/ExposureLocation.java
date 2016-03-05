package io.github.getExposure.database;

import java.util.*;

/**
 * ExposureLocation is an immutable representation of a location on the map.
 *
 * specfield id : long  // uniquely identifies this location for database interactions
 */
public class ExposureLocation {

    private final long id;
    private final float lat;
    private final float lon;
    private final int totalRating;
    private final int numOfRatings;
    private final String name;
    private final String desc;
    private Category[] categories;
    private Comment[] comments;

    private static final long NULL_ID = -1;

    /*
     * class invariant,
     * name != null
     * desc != null
     * categories != null
     * comments != null
     */

    /**
     * Constructs a ExposureLocation with the specified parameters.
     *
     * Should not be used when inserting a new ExposureLocation using DatabaseManager.
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
    public ExposureLocation(long id, float lat, float lon, int totalRating,
                            int numOfRatings, String name, String desc,
                            Set<Category> categories, List<Comment> comments) {
        this.id = id;
        this.lat = lat;
        this.lon = lon;
        this.totalRating = totalRating;
        this.numOfRatings = numOfRatings;
        this.name = name;
        this.desc = desc;

        // safe deep copy in
        this.categories = new Category[categories.size()];
        int i = 0;
        for (Category cat: categories) {
            this.categories[i] = cat;
            i++;
        }
        //safe deep copy in
        this.comments = new Comment[comments.size()];
        i = 0;
        for (Comment comment : comments) {
            this.comments[i] = comment;
            i++;
        }
    }

    /**
     * Constructs a ExposureLocation with the specified parameters.
     *
     * The ID parameter is omitted. This constructor should be used when using
     * DatabaseManager to insert a new ExposureLocation into the database.
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
    public ExposureLocation(float lat, float lon, int totalRating,
                            int numOfRatings, String name, String desc,
                            Set<Category> categories, List<Comment> comments) {
        this(NULL_ID, lat, lon, totalRating, numOfRatings, name, desc, categories, comments);
    }

    public ExposureLocation() {
        id = NULL_ID;
        lat = 0;
        lon = 0;
        totalRating = 0;
        numOfRatings = 0;
        name = "";
        desc = "";
        categories = new Category[0];
        comments = new Comment[0];
    }

    /**
     * Returns the unique identifier for this ExposureLocation.
     *
     * The returned ID can be used to interact with DatabaseManager.
     *
     * @return the unique identifier of this user or -1 if this ExposureLocation has
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
/*    public double getRating() {
       return (numOfRatings == 0) ? -1 : (double)totalRating/numOfRatings;
    }
*/
    /**
     * Returns a new ExposureLocation with the updated rating.
     *
     * @param newRating the new review to add to this location
     * @return a new ExposureLocation with the updated rating
     */
    public ExposureLocation addRating(int newRating) {
        return new ExposureLocation(id, lat, lon, totalRating + newRating,
                numOfRatings + 1, name, desc, getCategories(), getComments());
    }

    /**
     * Returns the name of this ExposureLocation.
     *
     * @return the name of this ExposureLocation
     */
    public String getName() {
        return name;
    }

    /**
     * Return the description of this ExposureLocation
     *
     * @return the description of this ExposureLocation
     */
    public String getDesc() {
        return desc;
    }

    /**
     * Returns a Set of all the categories, or tags, associated with this
     * ExposureLocation.
     *
     * @return a Set of all categories, or tags, associated with this
     * ExposureLocation
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
     * Returns a List of all comments posted to this ExposureLocation.
     *
     * @return a List of all comments posted to this ExposureLocation
     */
    public List<Comment> getComments() {
        List<Comment> commentsList = new ArrayList<>();
        // safe deep copy out
        for (Comment comment : comments) {
            commentsList.add(comment);
        }
        return commentsList;
    }

    /**
     * Returns a ExposureLocation with the given id
     *
     * This method is a more convenient way to inject an ID into the object
     * without having to construct a new one yourself. Only use this method
     * if you have been provided a valid ID from DatabaseManager.
     *
     * @return a ExposureLocation with the given id
     */
    public ExposureLocation addID(long id) {
        return new ExposureLocation(id,lat,lon,totalRating,numOfRatings,name,desc,
                getCategories(),getComments());
    }
}
