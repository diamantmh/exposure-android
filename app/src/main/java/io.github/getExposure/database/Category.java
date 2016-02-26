package io.github.getExposure.database;

/**
 * Category is an immutable representation of a category or tag of a location.
 *
 * specfield id : long  // uniquely identifies this Category for database interactions
 */
public class Category {

    private final long id;
    private final long locID;
    private final String content;

    protected static final long NULL_ID = -1;

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
     * Constructs a Category associated to the location that matches the given
     * location ID and representing the category matching the given category ID
     *
     * @param locID the unique identifier for the location this category is
     *              associated with
     * @param categoryID the id of the content tag for this category (obtain through
     *           static constants eg Category.WALKING_ID)
     */
    public Category(long locID, long categoryID) {
        this.id = categoryID;
        this.locID = locID;
        this.content = tags[(int)id-1];
    }

    /**
     * Constructs a Category representing the category matching the given category
     * ID.
     *
     * The location ID has been omitted. This means that this category is not
     * registered to a location in the database. You can add an unregistered
     * location to a Location, loc, and DatabaseManager will associate this
     * category to loc for you if you call insert(Location loc).
     *
     * Otherwise you can register this category to a location by calling
     * insert(Category cat) in DatabaseManager. You must insert a location first
     * to obtain a valid location ID, then use that location ID to construct a
     * category. Then you can insert the category by calling insert(Category cat)
     * on DatabaseManager where cat has the valid location ID of loc.
     *
     * @param categoryID the id of the content tag for this category (obtain through
     *           static constants eg Category.WALKING_ID)
     */
    public Category(long categoryID) {
        this(NULL_ID, categoryID);
    }

    /**
     * Returns the id matching the category type this category represents.
     *
     * @return the unique identifier for this Category
     */
    public long getId() {
        return id;
    }

    /**
     * Returns the id of the location this Category is registered to.
     *
     * @return the unique identifier for the location this is registered to
     */
    public long getLocID() {
        return locID;
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

