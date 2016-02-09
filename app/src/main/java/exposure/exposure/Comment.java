package exposure.exposure;

/**
 * Comment is an immutable representation of a post to a location.
 */
public class Comment {

    private final long id;
    private final String content;


    public Comment(long id, String content) {
        this.id = id;
        this.content = content;
    }

    public long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }
}

