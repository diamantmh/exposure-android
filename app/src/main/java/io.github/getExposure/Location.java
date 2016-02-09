package io.github.getExposure;

/**
 * Created by michaeldiamant on 2/6/16.
 */
public class Location {
    private int locationID;
    private double latitude;
    private double longitude;

    public Location(int locationID, double latitude, double longitude) {
        this.locationID = locationID;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public int getLocationID() {
        return locationID;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
