package ExposureWebService;

import java.util.concurrent.atomic.AtomicLong;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
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
    
    static {
    	dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		dataSource.setUrl("jdbc:sqlserver://vyl5xz64ek.database.windows.net;database=Exposure");
		dataSource.setUsername("kekonat@vyl5xz64ek.database.windows.net");
		dataSource.setPassword("N0REGRETs");
    }
    
    private static final String UPDATELOCATION = "UPDATE Locations " + 
    	"SET lat = ?, lon = ?, total_rating = ?, num_of_ratings = ?, name = ?, description = ? " +
    	"WHERE id = ?";
    	
    private static final String TESTVALIDUSERID = "SELECT COUNT(*) FROM Locations WHERE ID = ?";

    @RequestMapping("/updateLocation")
    public boolean updateLocation(@RequestBody Location loc) {
    	if (DEBUG) {
    		JdbcTemplate testValidId = new JdbcTemplate(dataSource);
    		int i = testValidId.queryForObject(TESTVALIDUSERID, Integer.class, loc.getID());
    		assert(i == 1);
    	}
    	JdbcTemplate update = new JdbcTemplate(dataSource);
    	update.update(UPDATELOCATION, loc.getLat(), loc.getLon(), loc.getTotalRating(), loc.getNumOfRatings(), loc.getName(), loc.getDesc(), loc.getID());
    	return true;
    }
    
    private static final String UPDATEUSER = "UPDATE Users " + 
    	"SET username = ?, src_link = ?, about_me = ? " +
    	"WHERE id = ?";
    	 
    private static final String TESTVALIDLOCID = "SELECT COUNT(*) FROM Users WHERE ID = ?";

    @RequestMapping("/updateUser")
    public boolean updateUser(@RequestBody User user) {
    	if (DEBUG) {
    		JdbcTemplate testValidId = new JdbcTemplate(dataSource);
    		int i = testValidId.queryForObject(TESTVALIDLOCID, Integer.class, user.getID());
    		assert(i == 1);
    	}
    	JdbcTemplate update = new JdbcTemplate(dataSource);
    	update.update(UPDATEUSER, user.getUsername(), user.getLink(), user.getAboutMe(), user.getID());
    	return true;
    }
    
    private static final String INSERTLOCATION = "INSERT INTO Locations " + 
    	"VALUES (?,?,?,?,?,?)";
    private static final String TESTIFLOCEXISTS = "SELECT COUNT(*) FROM Locations WHERE ID = ?";
    
    @RequestMapping("/insertLocation")
    public int insertLocation(@RequestBody Location loc) {
    	if (DEBUG) {
    		JdbcTemplate testExists = new JdbcTemplate(dataSource);
    		int i = testExists.queryForObject(TESTIFLOCEXISTS, Integer.class, loc.getID());
    		assert(i == 0);
    	}
    	JdbcTemplate insert = new JdbcTemplate(dataSource);
    	insert.update(INSERTLOCATION, loc.getLat(), loc.getLon(), loc.getTotalRating(), loc.getNumOfRatings(), loc.getName(), loc.getDesc());
    	// This returns the newest id created by IDENTITY
    	// This part will require transactions to assure the correct value is returned
    	int id = insert.queryForObject(GETID, Integer.class);
    	return id;
    }
    
    private static final String INSERTPHOTO = "INSERT INTO Photos " + 
    	"VALUES (?,?,?,?,?)";
    private static final String TESTIFPHOTOEXISTS = "SELECT COUNT(*) FROM Photos WHERE ID = ?";

    @RequestMapping("/insertPhoto")
    public int insertPhoto(@RequestBody Photo photo) {
    	if (DEBUG) {
    		JdbcTemplate testExists = new JdbcTemplate(dataSource);
    		int i = testExists.queryForObject(TESTIFPHOTOEXISTS, Integer.class, photo.getID());
    		assert(i == 0);
    	}
    	JdbcTemplate insert = new JdbcTemplate(dataSource);
    	insert.update(INSERTPHOTO, photo.getAuthorID(), photo.getLocID(), photo.getSource(), String.format("%tF", photo.getDate()), String.format("%tT", photo.getTime()));
		// This returns the newest id created by IDENTITY
    	// This part will require transactions to assure the correct value is returned
    	int id = insert.queryForObject(GETID, Integer.class);
		return id;
    }
    
    private static final String INSERTUSER = "INSERT INTO Users " + 
    	"VALUES (?,?,?)";
    private static final String TESTIFUSEREXISTS = "SELECT COUNT(*) FROM Users WHERE ID = ?";
    
    @RequestMapping("/insertUser")
    public int insertUser(@RequestBody User user) {
    	if (DEBUG) {
    		JdbcTemplate testExists = new JdbcTemplate(dataSource);
    		int i = testExists.queryForObject(TESTIFUSEREXISTS, Integer.class, user.getID());
    		assert(i == 0);
    	}
		JdbcTemplate insert = new JdbcTemplate(dataSource);
    	insert.update(INSERTUSER, user.getUsername(), user.getLink(), user.getAboutMe());
    	// This returns the newest id created by IDENTITY
    	// This part will require transactions to assure the correct value is returned
    	int id = insert.queryForObject(GETID, Integer.class);
    	return 0;
    }
    
    private static final String REMOVEUSER = "DELETE FROM Users " + 
    	"WHERE id = ?";
    
    @RequestMapping("/removeUser")
    public boolean removeUser(@RequestBody int id) {
		if (DEBUG) {
    		JdbcTemplate testExists = new JdbcTemplate(dataSource);
    		int i = testExists.queryForObject(TESTIFUSEREXISTS, Integer.class, id);
    		assert(i == 1);
    	}
    	JdbcTemplate remove = new JdbcTemplate(dataSource);
    	remove.update(REMOVEUSER, id);
    	return true;
    }
    
    private static final String REMOVEPHOTO = "DELETE FROM Photos " + 
    	"WHERE id = ?";
    
    @RequestMapping("/removePhoto")
    public boolean removePhoto(@RequestBody int id) {
    	if (DEBUG) {
    		JdbcTemplate testExists = new JdbcTemplate(dataSource);
    		int i = testExists.queryForObject(TESTIFPHOTOEXISTS, Integer.class, id);
    		assert(i == 1);
    	}
    	JdbcTemplate remove = new JdbcTemplate(dataSource);
    	remove.update(REMOVEPHOTO, id);
    	return true;
    }
    
    private static final String GETUSER = "SELECT * FROM Users " + 
    	"WHERE id = ?";
    
    @RequestMapping("/getUser")
    public User getUser(@RequestParam(value="id") int id) {
    	if (DEBUG) {
    		JdbcTemplate testExists = new JdbcTemplate(dataSource);
    		int i = testExists.queryForObject(TESTIFUSEREXISTS, Integer.class, id);
    		assert(i == 1);
    	}
    	JdbcTemplate get = new JdbcTemplate(dataSource);
    	User user = (User)get.queryForObject(GETUSER, new Object[] {id}, new UserRowMapper());
    	return user;
    }    
    
    private static final String GETUSERPHOTOS = "SELECT * FROM Photos " + 
    	"WHERE uid = ?";
    
    // returns null if there are no results
    @RequestMapping("/getUserPhotos")
    public Photo[] getUserPhotos(@RequestParam(value="getUserPhotos") int id) {
    	if (DEBUG) {
    		JdbcTemplate testExists = new JdbcTemplate(dataSource);
    		int i = testExists.queryForObject(TESTIFUSEREXISTS, Integer.class, id);
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
			Photo photo = new Photo((int)row.get("id"), (int)row.get("uid"), (int)row.get("lid"), (String)row.get("src_link"), (Date)row.get("post_date"), (Time)row.get("post_time"));
			arr[counter] = photo;
			counter++;
		}
		return arr;
    }
    
    private static final String GETLOCPHOTOS = "SELECT * FROM Photos " + 
    	"WHERE lid = ?";
    
    // returns null if there are no results
    @RequestMapping("/getLocationPhotos")
    public Photo[] getLocationPhotos(@RequestParam(value="getLocationPhotos") int id) {
    	if (DEBUG) {
    		JdbcTemplate testExists = new JdbcTemplate(dataSource);
    		int i = testExists.queryForObject(TESTIFLOCEXISTS, Integer.class, id);
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
			Photo photo = new Photo((int)row.get("id"), (int)row.get("uid"), (int)row.get("lid"), (String)row.get("src_link"), (Date)row.get("post_date"), (Time)row.get("post_time"));
			arr[counter] = photo;
			counter++;
		}
		return arr;
    }
    
    public class UserRowMapper implements RowMapper {
		public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
			return new User(rs.getInt("id"), rs.getString("username"), rs.getString("src_link"), rs.getString("about_me"));
		}	
	}
	
	public class PhotoRowMapper implements RowMapper {
		public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
			return new Photo(rs.getInt("id"), rs.getInt("uid"), rs.getInt("lid"), rs.getString("src_link"), rs.getDate("post_date"), rs.getTime("post_time"));
		}	
	}
}


