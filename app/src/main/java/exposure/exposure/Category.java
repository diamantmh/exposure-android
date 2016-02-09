package exposure.exposure;

/**
 * Category is an immutable representation of a photo.
 *
 * @specfield id : long  // uniquely identifies this Category for database interactions
 */
public class Category {
    private final long id;
    private final String content;
    private static final String[] tags = {"OUTDOOR"};

    public static final long OUTDOORID = 1;

    /*
     * class invariant,
     * content != null
     */

    /**
     * Constructs a Category
     * @param id - the id of the content tag for this category (obtain through
     *           static constants eg Category.OUTDOORID)
     */
    public Category(long id) {
        this.id = id;
        this.content = tags[(int)id-1];
    }

    public long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }
}

