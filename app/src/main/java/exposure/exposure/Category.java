package exposure.exposure;

/**
 * Category is an immutable representation of a category or tag of a location.
 *
 * @specfield id : long  // uniquely identifies this Category for database interactions
 */
public class Category {

    private final long id;
    private final String content;
    private static final String[] tags = {"summer","fall","winter","spring",
            "driving","walking","hiking"};

    public static final long SUMMER_ID = 1;
    public static final long FALL_ID = 2;
    public static final long WINTER_ID = 3;
    public static final long SPRING_ID = 4;
    public static final long DRIVING_ID = 5;
    public static final long WALKING_ID = 6;
    public static final long HIKING_ID = 7;

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

