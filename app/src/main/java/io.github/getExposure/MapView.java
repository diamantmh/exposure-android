package io.github.getExposure;

/**
 * Created by Michael on 2/7/2016.
 */
public class MapView {
    /**
     * TODO: get's location from LocationsController, which gets it from
     * Database Manager
     * @param latitude
     * @param longitude
     */
    Location[] getLocations(Location latitude, Location longitude) {
        return null;
    }

    /**
     * GMaps stuff?
     */
    void bleh() {

    }

    /**
     * Display pins
     */
    void displaySpots(Location[] locations) {

    }

    /**
     * Displays the pins w/photos associated with them
     * @param location
     * @param photos
     */
    void displaySpot(Location location, Photo[] photos) {

    }

    /**
     * Gets photos to display on the pins
     * @param locationID
     * @return
     */
    Photo[] getPhotos(Location locationID) {
        return null;
    }


}
