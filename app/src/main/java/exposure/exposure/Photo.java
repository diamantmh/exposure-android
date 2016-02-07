package exposure.exposure;

/**
 * Photo is an immutable representation of a photo.
 */
public final class Photo {
    private final int id;
    private final int authorID;
    private final int locID;
    private final String source;

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
