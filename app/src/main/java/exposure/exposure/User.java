package exposure.exposure;

/**
 * Created by michaeldiamant on 2/6/16.
 */
public class User {
    private int userID;
    private String firstName;
    private String lastName;
    private String profilePicLink;

    public User(int userID, String firstName, String lastName, String profilePicLink) {
        this.userID = userID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.profilePicLink = profilePicLink;
    }

    public int getID() {
        return userID;
    }

    public String getName() {
        return firstName + " " + lastName;
    }

    public Photo[] getPhotos() {
        return new Photo[0];
    }



}
