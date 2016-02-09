package ExposureWebService;

import java.util.*;

/**
 * Represents a location on the map. Since Location is mutable, be careful
 * with the set returned by getCategories().
 */
public class Location {
    private long id;
    private double lat;
    private double lon;
    private long totalRating;
    private long numOfRatings;
    private String name;
    private String desc;
    private Set<String> categories;

    public Location(long id, double lat, double lon, long totalRating,
                    long numOfRatings, String name, String desc,
                    Set<String> categories) {
        this.id = id;
        this.lat = lat;
        this.lon = lon;
        this.totalRating = totalRating;
        this.numOfRatings = numOfRatings;
        this.name = name;
        this.desc = desc;
        this.categories = categories;
    }

    public long getID() {
        return id;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }
    
    public long getTotalRating() {
    	return totalRating;
    }
    
    public long getNumOfRatings() {
    	return numOfRatings;
    }

    public double getRating() {
        return (double) (totalRating) / numOfRatings;
    }

    public void addRating(long newRating) {
        totalRating += newRating;
        numOfRatings++;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public Iterable<String> getCategories () {
        return categories;
    }
}
