package dto;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Category is an immutable representation of a category or tag of a location.
 *
 * specfield id : long  // uniquely identifies this Category for database interactions
 */
@XmlRootElement
public class Category {

    private final long id;
    private final long lid;
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
     * @param id the id of the content tag for this category (obtain through
     *           static constants eg Category.WALKING_ID)
     */
    public Category(long id, long lid) {
        this.id = id;
        this.lid = lid;
        this.content = tags[(int)id-1];
    }
    
    //empty constructor used only for JSON conversion
    public Category() {
        this.id = -1;
        this.lid = -1;
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
     * Returns the location identifier for this Category.
     *
     * @return the location identifier for this Category
     */
    public long getLid() {
        return lid;
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

