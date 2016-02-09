package ExposureWebService;
import java.util.Date;
import java.sql.Time;

/**
 * Photo is an immutable representation of a photo.
 */
public final class Photo {
    private final long id;
    private final long authorID;
    private final long locID;
    private final String source;
    private final Date date;
    private final Time time;

    public Photo(long id, long authorID, long locID, String source, Date date, Time time) {
        this.id = id;
        this.authorID = authorID;
        this.locID = locID;
        this.source = source;
        this.date = (Date) date.clone();
        this.time = (Time) time.clone();
    }

    public long getID() {
        return id;
    }

    public long getAuthorID() {
        return authorID;
    }

    public long getLocID() {
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
