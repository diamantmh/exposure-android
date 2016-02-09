package exposure.exposure;

/**
 * Category is an immutable representation of a photo.
 *
 * @specfield id : long  // uniquely identifies this Category for database interactions
 */
public class Category {
    private final long id;
    private final String content;
    private static final String[] tags = {"Outdoor","Indoor"};

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

    /**
     * Returns the unique identifier for this Category.
     *
     * @return the unique identifier for this Category
     */
    public long getId() {
        return id;
    }

    /**
     * Returns the tag for this Category as a String.
     *
     * @return the tag for htis Category as a String
     */
    public String getContent() {
        return content;
    }
}

