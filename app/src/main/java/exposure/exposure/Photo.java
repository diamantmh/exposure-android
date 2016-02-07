package exposure.exposure;

/**
 * Photo is an immutable representation of a photo.
 */
public class Photo {
    private int id;
    private int authorID;
    private int locID;
    private String source;

    public Photo(int id, int authorID, int locID, String source) {
        this.id = id;
        this.authorID = authorID;
        this.locID = locID;
        this.source = source;
    }

    public int getID() {
        return id;
    }

    public int getAuthorID() {
        return authorID;
    }

    public int getLocID() {
        return locID;
    }

    public String getSource() {
        return source;
    }
}
