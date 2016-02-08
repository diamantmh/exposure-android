package ExposureWebService;

import java.util.*;

/**
 * Represents a location on the map. Since Location is mutable, be careful
 * with the set returned by getCategories().
 */
public class Location {
    private int id;
    private double lat;
    private double lon;
    private int totalRating;
    private int numOfRatings;
    private String name;
    private String desc;
    private Set<String> categories;

    public Location(int id, double lat, double lon, int totalRating,
                    int numOfRatings, String name, String desc,
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

    public int getID() {
        return id;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }
    
    public int getTotalRating() {
    	return totalRating;
    }
    
    public int getNumOfRatings() {
    	return numOfRatings;
    }

    public double getRating() {
        return (double) (totalRating) / numOfRatings;
    }

    public void addRating(int newRating) {
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
