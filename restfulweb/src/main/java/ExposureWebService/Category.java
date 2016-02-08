package ExposureWebService;

public class Category {

    private final long id;
    private final String content;

    public Category(long id, String content) {
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

