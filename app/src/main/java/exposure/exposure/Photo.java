package exposure.exposure;

import java.util.Map;

/**
 * Created by michaeldiamant on 2/6/16.
 */
public class Photo {
    private int photoID;
    private int userID;
    private int locationID;
    private String source;
    private Map<String, Boolean> categories;

    public Photo(int photoID, int userID, int locationID, String source, Map<String, Boolean> categories) {
        this.photoID = photoID;
        this.userID = userID;
        this.locationID = locationID;
        this.source = source;
        this.categories = categories;
    }

    public int getPhotoID() {
        return photoID;
    }

    public int getUserID() {
        return userID;
    }

    public int getLocationID() {
        return locationID;
    }

    public String getSource() {
        return source;
    }

    public Map<String, Boolean> getCategories() {
        return categories;
    }



}
