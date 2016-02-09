package ExposureWebService;

/**
 * User is an immutable representation of a user of Exposure.
 */
public final class User {

    private final long id;
    private final String username;
    private final String link;
    private final String aboutMe;

    public User(long id, String username, String link, String aboutMe) {
        this.id = id;
        this.username = username;
        this.link = link;
        this.aboutMe = aboutMe;
    }

    public long getID() {
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
