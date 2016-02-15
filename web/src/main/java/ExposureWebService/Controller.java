package ExposureWebService; 

import java.util.concurrent.atomic.AtomicLong;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.dao.EmptyResultDataAccessException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Date;
import java.sql.Time;

@RestController
public class Controller {

	private static DriverManagerDataSource dataSource;
	private static final boolean DEBUG = true;
	private static final String GETID = "SELECT SCOPE_IDENTITY()";
    
    // Set the Microsoft SQL Server Database Access Information
    static {
    	dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		dataSource.setUrl("jdbc:sqlserver://vyl5xz64ek.database.windows.net;database=Exposure");
		dataSource.setUsername("kekonat@vyl5xz64ek.database.windows.net");
		dataSource.setPassword("N0REGRETs");
    }
    
    // Update location with given location ID
    private static final String UPDATELOCATION = "UPDATE Locations " + 
    	"SET lat = ?, lon = ?, total_rating = ?, num_of_ratings = ?, name = ?, description = ? " +
    	"WHERE id = ?";
    // Returns the number of locations that exist in the database with the specified location ID
    private static final String TESTIFLOCEXISTS = "SELECT COUNT(*) FROM Locations WHERE ID = ?";

    /**
     * Updates location with matching location ID in the database
     * @param loc Location object storing the target location ID and desired updated information
     * @return	true if successfully updated a location entry in the database
     */
    @RequestMapping("/updateLocation")
    public boolean updateLocation(@RequestBody Location loc) {
    	if (DEBUG) {
    		JdbcTemplate testValidId = new JdbcTemplate(dataSource);
    		long i = testValidId.queryForObject(TESTIFLOCEXISTS, Long.class, loc.getID());
    		assert(i == 1);
    	}
    	JdbcTemplate update = new JdbcTemplate(dataSource);
    	update.update(UPDATELOCATION, loc.getLat(), loc.getLon(), loc.getTotalRating(), loc.getNumOfRatings(), loc.getName(), loc.getDesc(), loc.getID());
    	if (DEBUG) {
    		JdbcTemplate testValidId = new JdbcTemplate(dataSource);
    		long i = testValidId.queryForObject(TESTIFLOCEXISTS, Long.class, loc.getID());
    		assert(i == 1);
    	}
    	return true;
    }
    
    // Updates user information with the given user ID
    private static final String UPDATEUSER = "UPDATE Users " + 
    	"SET username = ?, src_link = ?, about_me = ? " +
    	"WHERE id = ?";
    
    // Returns the number of users that exist in the database with the specified user ID
    private static final String TESTIFUSEREXISTS = "SELECT COUNT(*) FROM Users WHERE ID = ?";

    /**
     * Updates user with matching user ID in the database
     * @param user User object storing the target user ID and desired updated information
     * @return
     */
    @RequestMapping("/updateUser")
    public boolean updateUser(@RequestBody User user) {
    	if (DEBUG) {
    		JdbcTemplate testValidId = new JdbcTemplate(dataSource);
    		long i = testValidId.queryForObject(TESTIFUSEREXISTS, Long.class, user.getID());
    		assert(i == 1);
    	}
    	JdbcTemplate update = new JdbcTemplate(dataSource);
    	update.update(UPDATEUSER, user.getUsername(), user.getLink(), user.getAboutMe(), user.getID());
    	if (DEBUG) {
    		JdbcTemplate testValidId = new JdbcTemplate(dataSource);
    		long i = testValidId.queryForObject(TESTIFUSEREXISTS, Long.class, user.getID());
    		assert(i == 1);
    	}
    	return true;
    }
    
    // Inserts location into database
    private static final String INSERTLOCATION = "INSERT INTO Locations " + 
    	"VALUES (?,?,?,?,?,?)";
    
    /**
     * Inserts a new location with the given description
     * @param loc Location object containing the information to insert into the database
     * @return the location ID of the inserted location
     */
    @RequestMapping("/insertLocation")
    public long insertLocation(@RequestBody Location loc) {
    	JdbcTemplate insert = new JdbcTemplate(dataSource);
    	insert.update(INSERTLOCATION, loc.getLat(), loc.getLon(), loc.getTotalRating(), loc.getNumOfRatings(), loc.getName(), loc.getDesc());
    	// This returns the newest id created by IDENTITY
    	// This part will require transactions to assure the correct value is returned
    	long id = insert.queryForObject(GETID, Long.class);
    	if (DEBUG) {
    		JdbcTemplate testExists = new JdbcTemplate(dataSource);
    		long i = testExists.queryForObject(TESTIFLOCEXISTS, Long.class, id);
    		assert(i == 1);
    	}
    	return id;
    }
    
    // Insert photo into the database
    private static final String INSERTPHOTO = "INSERT INTO Photos " + 
    	"VALUES (?,?,?,?,?)";
    // Returns the number of photos that exist in the database with the specified photo ID
    private static final String TESTIFPHOTOEXISTS = "SELECT COUNT(*) FROM Photos WHERE ID = ?";

    /**
     * Insert a new photo into the database
     * @param photo Photo object containing the information to insert into the database
     * @return the photo ID of the newly inserted photo
     */
    @RequestMapping("/insertPhoto")
    public long insertPhoto(@RequestBody Photo photo) {
    	JdbcTemplate insert = new JdbcTemplate(dataSource);
    	insert.update(INSERTPHOTO, photo.getAuthorID(), photo.getLocID(), photo.getSource(), String.format("%tF", photo.getDate()), String.format("%tT", photo.getTime()));
		// This returns the newest id created by IDENTITY
    	// This part will require transactions to assure the correct value is returned
    	long id = insert.queryForObject(GETID, Long.class);
    	if (DEBUG) {
    		JdbcTemplate testExists = new JdbcTemplate(dataSource);
    		long i = testExists.queryForObject(TESTIFPHOTOEXISTS, Long.class, id);
    		assert(i == 1);
    	}
		return id;
    }
    
    // Insert a new user into the database
    private static final String INSERTUSER = "INSERT INTO Users " + 
    	"VALUES (?,?,?)";
    
    /**
     * Inserts a new user into the database
     * @param user User object containing information to insert into the database
     * @return the user ID of the newly inserted user
     */
    @RequestMapping("/insertUser")
    public long insertUser(@RequestBody User user) {
		JdbcTemplate insert = new JdbcTemplate(dataSource);
    	insert.update(INSERTUSER, user.getUsername(), user.getLink(), user.getAboutMe());
    	// This returns the newest id created by IDENTITY
    	// This part will require transactions to assure the correct value is returned
    	long id = insert.queryForObject(GETID, Long.class);
    	if (DEBUG) {
    		JdbcTemplate testExists = new JdbcTemplate(dataSource);
    		long i = testExists.queryForObject(TESTIFUSEREXISTS, Long.class, id);
    		assert(i == 1);
    	}
    	return id;
    }
    
    // Removes the specified user from the database
    private static final String REMOVEUSER = "DELETE FROM Users " + 
    	"WHERE id = ?";
    
    /**
     * Removes the specified user from the database
     * @param id the ID of the user to remove from the database
     * @return true if the user is successfully removed from the database
     */
    @RequestMapping("/removeUser")
    public boolean removeUser(@RequestBody long id) {
		if (DEBUG) {
    		JdbcTemplate testExists = new JdbcTemplate(dataSource);
    		long i = testExists.queryForObject(TESTIFUSEREXISTS, Long.class, id);
    		assert(i == 1);
    	}
    	JdbcTemplate remove = new JdbcTemplate(dataSource);
    	remove.update(REMOVEUSER, id);
    	return true;
    }
    
    // Removes the specified photo from the database
    private static final String REMOVEPHOTO = "DELETE FROM Photos " + 
    	"WHERE id = ?";
    
    /**
     * Removes the specified photo from the database
     * @param id the photo ID of the photo to remove from the database
     * @return true if the photo is successfully removed from the database
     */
    @RequestMapping("/removePhoto")
    public boolean removePhoto(@RequestBody long id) {
    	if (DEBUG) {
    		JdbcTemplate testExists = new JdbcTemplate(dataSource);
    		long i = testExists.queryForObject(TESTIFPHOTOEXISTS, Long.class, id);
    		assert(i == 1);
    	}
    	JdbcTemplate remove = new JdbcTemplate(dataSource);
    	remove.update(REMOVEPHOTO, id);
    	return true;
    }
    
    // Retrieves all users that exist in the database with the specified user ID
    private static final String GETUSER = "SELECT * FROM Users " + 
    	"WHERE id = ?";
    
    /**
     * Retrieves the user with the specified user ID
     * @param id the user ID of the desired user
     * @return the a User object or null if the user ID does not exist
     */
    @RequestMapping("/getUser")
    public User getUser(@RequestParam(value="id") long id) {
    	if (DEBUG) {
    		JdbcTemplate testExists = new JdbcTemplate(dataSource);
    		long i = testExists.queryForObject(TESTIFUSEREXISTS, Long.class, id);
    		assert(i == 1);
    	}
    	User user;
    	try {
	    	JdbcTemplate get = new JdbcTemplate(dataSource);
	    	user = (User)get.queryForObject(GETUSER, new Object[] {id}, new UserRowMapper());
    	} catch (EmptyResultDataAccessException e) {
    		user = null;
    	}
	    return user;
    }   
    
	// Retrieves all users that exist in the database with the specified user ID
    private static final String GETLOCATION = "SELECT * FROM Locations " + 
    	"WHERE id = ?";
    
    /**
     * Retrieves the user with the specified user ID
     * @param id the user ID of the desired user
     * @return the a User object or null if the user ID does not exist
     */
    @RequestMapping("/getLocation")
    public User getLocation(@RequestParam(value="id") long id) {
    	if (DEBUG) {
    		JdbcTemplate testExists = new JdbcTemplate(dataSource);
    		long i = testExists.queryForObject(TESTIFLOCATIONEXISTS, Long.class, id);
    		assert(i == 1);
    	}
    	Location loc;
    	try {
	    	JdbcTemplate get = new JdbcTemplate(dataSource);
	    	loc = (Location)get.queryForObject(GETLOCATION, new Object[] {id}, new LocationRowMapper());
    	} catch (EmptyResultDataAccessException e) {
    		loc = null;
    	}
	    return loc;
    }  
    
    // Retrieves all photos with the specified photo ID
    private static final String GETUSERPHOTOS = "SELECT * FROM Photos " + 
    	"WHERE uid = ?";
    
    /**
     * Retrieves all photos taken by the user with the specified user ID
     * @param id the target user's ID
     * @return an array of photos or null if no photos are found
     */
    @RequestMapping("/getUserPhotos")
    public Photo[] getUserPhotos(@RequestParam(value="getUserPhotos") long id) {
    	if (DEBUG) {
    		JdbcTemplate testExists = new JdbcTemplate(dataSource);
    		long i = testExists.queryForObject(TESTIFUSEREXISTS, Long.class, id);
    		assert(i == 1);
    	}
    	JdbcTemplate get = new JdbcTemplate(dataSource);
    	List rows = get.queryForList(GETUSERPHOTOS, id);
    	if (rows.isEmpty())
    		return null;
    	Photo[] arr = new Photo[rows.size()];
    	int counter = 0;
    	for (Object o : rows) {
    		Map row = (Map) o;
			Photo photo = new Photo((long)row.get("id"), (long)row.get("uid"), (long)row.get("lid"), (String)row.get("src_link"), (Date)row.get("post_date"), (Time)row.get("post_time"));
			arr[counter] = photo;
			counter++;
		}
		return arr;
    }
    
    // Retrieves all photos with the specified location ID
    private static final String GETLOCPHOTOS = "SELECT * FROM Photos " + 
    	"WHERE lid = ?";
    
    /**
     * Retrieves all photos with the given location ID
     * @param id the target locations id
     * @return an array of photos or null if no photos are found
     */
    @RequestMapping("/getLocationPhotos")
    public Photo[] getLocationPhotos(@RequestParam(value="getLocationPhotos") long id) {
    	if (DEBUG) {
    		JdbcTemplate testExists = new JdbcTemplate(dataSource);
    		long i = testExists.queryForObject(TESTIFLOCEXISTS, Long.class, id);
    		assert(i == 1);
    	}
    	JdbcTemplate get = new JdbcTemplate(dataSource);
    	List rows = get.queryForList(GETLOCPHOTOS, id);
    	if (rows.isEmpty())
    		return null;
    	Photo[] arr = new Photo[rows.size()];
    	int counter = 0;
    	for (Object o : rows) {
    		Map row = (Map) o;
			Photo photo = new Photo((long)row.get("id"), (long)row.get("uid"), (long)row.get("lid"), (String)row.get("src_link"), (Date)row.get("post_date"), (Time)row.get("post_time"));
			arr[counter] = photo;
			counter++;
		}
		return arr;
    }
    
    /**
     * 
     * @author Tyler
     *
     *	Maps SQLQuery return result row into an User object
     */
    private class UserRowMapper implements RowMapper {
		public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
			return new User(rs.getInt("id"), rs.getString("username"), rs.getString("src_link"), rs.getString("about_me"));
		}	
	}
}


