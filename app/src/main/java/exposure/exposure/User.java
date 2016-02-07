package exposure.exposure;

/**
 * User is an immutable representation of a user of Exposure.
 */
public class User {

    private int id;
    private String username;
    private String link;
    private String aboutMe;

    public User(int id, String username, String link, String aboutMe) {
        this.id = id;
        this.username = username;
        this.link = link;
        this.aboutMe = aboutMe;
    }

    public int getID() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getLink() {
        return link;
    }

    public String getAboutMe() {
        return aboutMe;
    }
}
