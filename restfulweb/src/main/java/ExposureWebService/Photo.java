package ExposureWebService;
import java.util.Date;
import java.sql.Time;

/**
 * Photo is an immutable representation of a photo.
 */
public final class Photo {
    private final int id;
    private final int authorID;
    private final int locID;
    private final String source;
    private final Date date;
    private final Time time;

    public Photo(int id, int authorID, int locID, String source, Date date, Time time) {
        this.id = id;
        this.authorID = authorID;
        this.locID = locID;
        this.source = source;
        this.date = (Date) date.clone();
        this.time = (Time) time.clone();
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
    
    public Date getDate() {
    	return (Date) date.clone();
    }
    
    public Time getTime() {
    	return (Time) time.clone();
    }
}
