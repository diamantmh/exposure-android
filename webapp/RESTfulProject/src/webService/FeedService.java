package webService;

import java.io.File;
import java.io.FileInputStream;
import java.net.URI;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import dao.Database;
import dto.Category;
import dto.Comment;
import dto.Location;
import dto.Photo;
import dto.User;
import dto.WebLocation;

//Include the following imports to use blob APIs.
import com.microsoft.azure.storage.*;
import com.microsoft.azure.storage.blob.*;

@Path("/WebService")
public class FeedService {
	
	private static final String CONTAINER = "exposurecontainer"; // Name of Azure storage container
	private static boolean DEBUG = false;
	
	// Initialize Azure blob storage connection string
	private static final String storageConnectionString =
		    "DefaultEndpointsProtocol=http;" +
		    "AccountName=exposurestorage;" +
		    "AccountKey=6C1/yHs+M6RyuEnNAFNQYSm6FW1ygJiLwFVFpuL6Vhp7ZLYg27dzmDFWjBW++a1I0u8TMi/FI18qsWg1/c3uNA==";
	
	static {

		// Create Public Azure Blob Container
	    try
	    {
	       // Retrieve storage account from connection-string.
	       CloudStorageAccount storageAccount = CloudStorageAccount.parse(storageConnectionString);

	        // Create the blob client.
	       CloudBlobClient blobClient = storageAccount.createCloudBlobClient();

	       // Get a reference to a container.
	       // The container name must be lower case
	       CloudBlobContainer container = blobClient.getContainerReference(CONTAINER);

	       // Create the container if it does not exist.
	        container.createIfNotExists();
	        
	       // Make the container public
	        
	       // Create a permissions object.
	       BlobContainerPermissions containerPermissions = new BlobContainerPermissions();

	       // Include public access in the permissions object.
	       containerPermissions.setPublicAccess(BlobContainerPublicAccessType.CONTAINER);

	       // Set the permissions on the container.
	       container.uploadPermissions(containerPermissions);
	    }
	    catch (Exception e)
	    {
	        System.out.println("Some error was thrown when creating Azure blob container");
	        e.printStackTrace();
	    }
	    
	}

	 // Update location with given location ID
    private static final String UPDATE_LOCATION = "UPDATE Locations " + 
    	"SET lat = ?, lon = ?, total_rating = ?, num_of_ratings = ?, name = ?, description = ? " +
    	"WHERE id = ?";
    // Updates user information with the given user ID
    private static final String UPDATE_USER = "UPDATE Users " + 
    	"SET username = ?, src_link = ?, about_me = ? " +
    	"WHERE id = ?";
    // Updates photo src_link
    private static final String UPDATE_SRC_LINK = "UPDATE Photos " + 
    	"SET src_link = ? " +
    	"WHERE id = ?";
    // Inserts location into database
    private static final String INSERT_LOCATION = "INSERT INTO Locations "
    		+ "OUTPUT inserted.id "
    		+ "VALUES (?,?,?,?,?,?)";
    // Inserts a comment into database
    private static final String INSERT_COMMENT = "INSERT INTO Comments "
    		+ "OUTPUT inserted.id "
    		+ "VALUES (?,?,?,?,?)";
    // Inserts a category into database
    private static final String INSERT_CATEGORY = "INSERT INTO Categories " + 
    	"VALUES (?,?)";
	// Insert photo into the database
    private static final String INSERT_PHOTO = "INSERT INTO Photos "
    		+ "OUTPUT inserted.id "
    		+ "VALUES (?,?,?,?,?)";
    // Insert a new user into the database
    private static final String INSERT_USER = "INSERT INTO Users "
    		+ "OUTPUT inserted.id "
    		+ "VALUES (?,?,?) ";
    // Removes the specified user from the database
    private static final String REMOVE_USER = "DELETE FROM Users " + 
    	"WHERE id = ?";
    // Removes the specified photo from the database
    private static final String REMOVE_PHOTO = "DELETE FROM Photos " + 
    	"WHERE id = ?";
    // Removes the specified comment from the database
    private static final String REMOVE_COMMENT = "DELETE FROM Comments " + 
    	"WHERE id = ?";
	// Retrieves all users that exist in the database with the specified user ID
	private static final String GET_USER = "SELECT * FROM Users " + 
	    "WHERE id = ?";
	// Retrieves all users that exist in the database with the specified user ID
    private static final String GET_LOCATION = "SELECT * FROM Locations " + 
    	"WHERE id = ?";
    // Retrieves all Categories with the specified location ID
    private static final String GET_CATEGORIES = "SELECT * FROM Categories " + 
    	"WHERE lid = ?";
    // Retrieves all Comments with the specified location ID
    private static final String GET_COMMENTS = "SELECT * FROM Comments " + 
    	"WHERE lid = ?";
    // Retrieves all photos with the specified photo ID
    private static final String GET_USER_PHOTOS = "SELECT * FROM Photos " + 
    	"WHERE uid = ?";
    // Retrieves all photos with the specified location ID
    private static final String GET_LOCATION_PHOTOS = "SELECT * FROM Photos " + 
    	"WHERE lid = ?";
    // Retrieves all locations within a range of coordinates
    private static final String GET_LOCATIONS_IN_RANGE = "SELECT * FROM Locations " + 
    	"WHERE lat > ? "
    	+ "and lat < ? "
    	+ "and lon > ? "
    	+ "and lon < ?";
	
    /**
     * Updates location with matching location ID in the database
     * @param loc Location object storing the target location ID and desired updated information
     * @return	true if successfully updated the location, and false otherwise
     */
    @POST
    @Path("/updateLocation")
	@Produces("application/json")
    public boolean updateLocation(WebLocation location) {
    	boolean updated;
    	
    	// Create new database object
		Database database = new Database();
		Connection connection = null;	    
		// Create database connection
		try {
			connection = database.Get_Connection();
		} catch (Exception e) {
			System.out.println("Error making database connection");
			return false;
		}
		
    	try {
			PreparedStatement updateLocation = connection.prepareStatement(UPDATE_LOCATION);
			updateLocation.setFloat(1, location.getLat());
			updateLocation.setFloat(2, location.getLon());
			updateLocation.setLong(3, location.getTotalRating());
			updateLocation.setLong(4, location.getNumOfRatings());
			updateLocation.setString(5, location.getName());
			updateLocation.setString(6, location.getDesc());
			updateLocation.setLong(7, location.getID());
			updateLocation.execute();
			updated = true;
		} catch (SQLException e) {
			System.out.println("There was some sort of error in updateLocation");
			return false;
		}
    	return updated;
    }
    
    /**
     * Updates user with matching user ID in the database
     * @param user User object storing the target user ID and desired updated information
     * @return a Json object true if the user was updated, and false otherwise
     */
    @POST
    @Path("/updateUser")
	@Produces("application/json")
    public boolean updateUser(User user) {
    	boolean updated;
    	
    	// Create new database object
		Database database = new Database();
		Connection connection = null;	    
		// Create database connection
		try {
			connection = database.Get_Connection();
		} catch (Exception e) {
			System.out.println("Error making database connection");
			return false;
		}
		
    	try {
			PreparedStatement updateUser = connection.prepareStatement(UPDATE_USER);
			updateUser.setString(1, user.getUsername());
			updateUser.setString(2, user.getLink());
			updateUser.setString(3, user.getAboutMe());
			updateUser.setLong(4, user.getID());
			updateUser.execute();
			updated = true;
		} catch (SQLException e) {
			System.out.println("There was some sort of error in updateUser");
			return false;
		}
    	return updated;
    }
    
    /**
     * Inserts a new location with the given description
     * @param loc Location object containing the information to insert into the database
     * @return the location ID of the inserted location
     */
    @POST
    @Path("/insertLocation")
	@Produces("application/json")
    public long insertLocation(WebLocation location) {
    	long id = -1;
    	
    	// Create new database object
		Database database = new Database();
		Connection connection = null;	    
		// Create database connection
		try {
			connection = database.Get_Connection();
		} catch (Exception e) {
			System.out.println("Error making database connection");
			return -1;
		}
		
    	try {
			PreparedStatement insertLocation = connection.prepareStatement(INSERT_LOCATION);
			insertLocation.setFloat(1, location.getLat());
			insertLocation.setFloat(2, location.getLon());
			insertLocation.setLong(3, location.getTotalRating());
			insertLocation.setLong(4, location.getNumOfRatings());
			insertLocation.setString(5, location.getName());
			insertLocation.setString(6, location.getDesc());
			ResultSet rs = insertLocation.executeQuery();
			
			if (rs.next()) {
				id = rs.getLong("id");
			}
		} catch (SQLException e) {
			System.out.println("There was some sort of error in insertPhoto");
			return -2;
		}
		return id;
    }
    
    /**
     * Inserts a new comment into the database
     * @param lid the location id of the comment
     * @param comment the comment to be added to the database
     * @return inserted comment id
     */
    private long insertComment(long lid, Comment comment, Connection connection) {
    	long id = -1;
		
    	try {
			PreparedStatement insertComment = connection.prepareStatement(INSERT_COMMENT);
			insertComment.setLong(1, comment.getAuthorID());
			insertComment.setLong(2, lid);
			insertComment.setString(3, comment.getContent());
			insertComment.setDate(4, new java.sql.Date(comment.getDate().getTime()));
			insertComment.setTime(5, comment.getTime());
			ResultSet rs = insertComment.executeQuery();
			
			if (rs.next()) {
				id = rs.getLong("id");
			}
		} catch (SQLException e) {
			System.out.println("There was some sort of error in insertComment");
			return -1;
		}
    	return id;
    }
    
    /**
     * Insert a new comment into the database
     * @param comment object containing the information to insert into the database
     * @return the ID of the newly inserted comment, or -1 if comment was not inserted
     */
    @POST
    @Path("/insertCategory")
	@Produces("application/json")
    public boolean insertCategory(Category category) {
    	// Create new database object
		Database database = new Database();
		Connection connection = null;	 

		// Create database connection
		try {
			connection = database.Get_Connection();
		} catch (Exception e) {
			System.out.println("Error making database connection");
			return false;
		}

		try {
			PreparedStatement insertCategory = connection.prepareStatement(INSERT_CATEGORY);
			insertCategory.setLong(1, category.getLid());
			insertCategory.setLong(2, category.getId());
			insertCategory.execute();
		} catch (SQLException e) {
			System.out.println("There was some sort of error in insertCategory");
			return false;
		}
    	return true;
    }
    
    /**
     * Insert a new comment into the database
     * @param comment object containing the information to insert into the database
     * @return the ID of the newly inserted comment, or -1 if comment was not inserted
     */
    @POST
    @Path("/insertComment")
	@Produces("application/json")
    public long insertComment(Comment comment) {
    	// Create new database object
		Database database = new Database();
		Connection connection = null;	 

		// Create database connection
		try {
			connection = database.Get_Connection();
		} catch (Exception e) {
			System.out.println("Error making database connection");
			return -1;
		}
    	return insertComment(comment.getLocID(), comment, connection);
    }

    /**
     * Insert a new photo into the database
     * @param photo Photo object containing the information to insert into the database
     * @return the photo ID of the newly inserted photo, or -1 if photo was not inserted
     */
    @POST
    @Path("/insertPhoto")
	@Produces("application/json")
    public long insertPhoto(Photo photo) {
    	long id = -1;
    	
    	// Create new database object
		Database database = new Database();
		Connection connection = null;	    
		// Create database connection
		try {
			connection = database.Get_Connection();
		} catch (Exception e) {
			System.out.println("Error making database connection");
			return -1;
		}
    	
		try {
			PreparedStatement insertPhoto = connection.prepareStatement(INSERT_PHOTO);
			insertPhoto.setLong(1, photo.getAuthorID());
			insertPhoto.setLong(2, photo.getLocID());
			insertPhoto.setString(3, photo.getSource());
			insertPhoto.setDate(4, new java.sql.Date(photo.getDate().getTime()));
			insertPhoto.setTime(5, photo.getTime());
			ResultSet rs = insertPhoto.executeQuery();

			if (rs.next()) {
				id = rs.getLong("id");
			}
		} catch (SQLException e) {
			System.out.println("There was some sort of error in insertPhoto");
			return -2;
		}
		
    	File image = photo.getFile();
    	URI imageUri = null;
    	
    	// Insert image file into Azure Blob Storage
    	try
    	{
    	    // Retrieve storage account from connection-string.
    	    CloudStorageAccount storageAccount = CloudStorageAccount.parse(storageConnectionString);

    	    // Create the blob client.
    	    CloudBlobClient blobClient = storageAccount.createCloudBlobClient();

    	    // Retrieve reference to a previously created container.
    	    CloudBlobContainer container = blobClient.getContainerReference(CONTAINER);

    	    // Create or overwrite the "myimage.jpg" blob with contents from a local file.
    	    CloudBlockBlob blob = container.getBlockBlobReference(String.valueOf(id));
    	    blob.upload(new FileInputStream(image), image.length());
    	    
    	    imageUri = blob.getUri();
    	}
    	catch (Exception e)
    	{
    		System.out.println("There was some sort of error in insertPhoto when inserting blob");
    		return -3;
    	}
    	
    	// Update recently added Photo src_link to be URI of inserted blob
    	try {
			PreparedStatement updatePhotoSrcLink = connection.prepareStatement(UPDATE_SRC_LINK);
			updatePhotoSrcLink.setString(1, imageUri.toString());
			updatePhotoSrcLink.setLong(2, id);
			updatePhotoSrcLink.execute();
		} catch (SQLException e) {
			System.out.println("There was some sort of error in insertPhoto");
			return -4;
		}
    	
		return id;
    }
    
    /**
     * Inserts a new user into the database
     * @param user User object containing information to insert into the database
     * @return the user ID of the newly inserted user
     */
    @POST
    @Path("/insertUser")
	@Produces("application/json")
    public long insertUser(User user) {
		long id = -1;
		
		// Create new database object
		Database database = new Database();
		Connection connection = null;	    
		// Create database connection
		try {
			connection = database.Get_Connection();
		} catch (Exception e) {
			System.out.println("Error making database connection");
			return -1;
		}
		
		try {
			PreparedStatement insertUser = connection.prepareStatement(INSERT_USER);
			insertUser.setString(1, user.getUsername());
			insertUser.setString(2, user.getLink());
			insertUser.setString(3, user.getAboutMe());
			ResultSet rs = insertUser.executeQuery();
			
			if (rs.next()) {
				id = rs.getLong("id");
			}
		} catch (SQLException e) {
			System.out.println("There was some sort of error in insertUser");
			return -1;
		}
		return id;
    }
    
    /**
     * Removes the specified user from the database
     * @param id the user ID of the photo to remove from the database
     * @return true if the user is successfully removed from the database
     */
    @POST 
    @Path("/removeUser")
	@Produces("application/json")
    public boolean removeUser(long id) {
		boolean removed;
		
		// Create new database object
		Database database = new Database();
		Connection connection = null;	    
		// Create database connection
		try {
			connection = database.Get_Connection();
		} catch (Exception e) {
			System.out.println("Error making database connection");
			return false;
		}
		
		try {
			PreparedStatement removeUser = connection.prepareStatement(REMOVE_USER);
			removeUser.setLong(1, id);
			removeUser.execute();
			removed = true;
		} catch (SQLException e) {
			System.out.println("There was some sort of error in removeUser");
    		return false;
		}
		return removed;
    }
    
    /**
     * Removes the specified photo from the database
     * @param id the photo ID of the photo to remove from the database
     * @return true if the photo is successfully removed from the database
     */
    @POST 
    @Path("/removePhoto")
	@Produces("application/json")
    public boolean removePhoto(long id) {
    	boolean removed;
    	
    	// Create new database object
		Database database = new Database();
		Connection connection = null;	    
		// Create database connection
		try {
			connection = database.Get_Connection();
		} catch (Exception e) {
			System.out.println("Error making database connection");
			return false;
		}
		
		try {
			PreparedStatement removePhoto = connection.prepareStatement(REMOVE_PHOTO);
	    	removePhoto.setLong(1, id);
			removePhoto.execute();
			removed = true;
		} catch (SQLException e) {
			System.out.println("There was some sort of error in removePhoto");
    		return false;
		}
		
		// Delete blob from Azure Blob Storage
		try
		{
		   // Retrieve storage account from connection-string.
		   CloudStorageAccount storageAccount = CloudStorageAccount.parse(storageConnectionString);

		   // Create the blob client.
		   CloudBlobClient blobClient = storageAccount.createCloudBlobClient();

		   // Retrieve reference to a previously created container.
		   CloudBlobContainer container = blobClient.getContainerReference(CONTAINER);

		   // Retrieve reference to a blob.
		   CloudBlockBlob blob = container.getBlockBlobReference(String.valueOf(id));

		   // Delete the blob.
		   blob.deleteIfExists();
		}
		catch (Exception e)
		{
			System.out.println("There was some sort of error in removePhoto when deleting blob");
		    return false;
		}
		return removed;
    }
    
    /**
     * Removes the specified comment from the database
     * @param id the comment ID of the comment to remove from the database
     * @return Json object true if the comment is removed, and false otherwise
     */
    @POST 
    @Path("/removeComment")
	@Produces("application/json")
    public boolean removeComment(long id) {
		boolean removed;
		
		// Create new database object
		Database database = new Database();
		Connection connection = null;	    
		// Create database connection
		try {
			connection = database.Get_Connection();
		} catch (Exception e) {
			System.out.println("Error making database connection");
			return false;
		}
		
		try {
			PreparedStatement removeComment = connection.prepareStatement(REMOVE_COMMENT);
			removeComment.setLong(1, id);
			removeComment.execute();
			removed = true;
		} catch (SQLException e) {
			System.out.println("There was some sort of error in removePhoto");
    		return false;
		}
		return removed;
    }
    
    /**
     * Retrieves the user with the specified user ID
     * @param id the user ID of the desired user
     * @return the a Json User object or null if the user ID does not exist
     */
    @GET
	@Path("/getUser")
	@Produces("application/json")
    public User getUser(@QueryParam("id") long id) {
    	User user;
    	
    	// Create new database object
		Database database = new Database();
		Connection connection = null;	    
		// Create database connection
		try {
			connection = database.Get_Connection();
		} catch (Exception e) {
			System.out.println("Error making database connection");
			return null;
		}
		
    	try {
    		PreparedStatement getUser = connection.prepareStatement(GET_USER);
    		getUser.setLong(1, id);
			ResultSet rs = getUser.executeQuery();
			if (rs.next()) {
				long uid = rs.getLong("id");
				String username = rs.getString("username");
				String link = rs.getString("src_link");
				String aboutMe = rs.getString("about_me");
				user = new User(uid, username, link, aboutMe);
			} else {
				System.out.println("No users with id: " + id + " in database");
				return null;
			}
			
			if (DEBUG && rs.next()) {
				System.out.println("Error: getUser for id: " + id + " Contained multiple Users");
				assert(false);
			}
    	} catch (Exception e) {
    		System.out.println("There was some sort of error in getUser");
    		return null;
    	}
	    return user;
    }   
    
    /**
     * Retrieves the location with the specified location ID
     * @param id the location ID of the desired user
     * @return the Json Location object or null if the location ID does not exist
     */
    @GET
	@Path("/getLocation")
	@Produces("application/json")
    public Location getLocation(@QueryParam("id") long id) {
    	Location location;
    	
    	// Create new database object
		Database database = new Database();
		Connection connection = null;	    
		// Create database connection
		try {
			connection = database.Get_Connection();
		} catch (Exception e) {
			System.out.println("Error making database connection");
			return null;
		}
		
    	try {
    		
    		Set<Category> categories = new HashSet<Category>();
    		List<Comment> comments = new ArrayList<Comment>();
    		
    		setCommentsAndCategories(id, comments, categories, connection);

    		PreparedStatement getLocation = connection.prepareStatement(GET_LOCATION);
    		getLocation.setLong(1, id);
			ResultSet rs = getLocation.executeQuery();
			if (rs.next()) {
				long lid = rs.getLong("id");
				float lat = rs.getFloat("lat");
				float lon = rs.getFloat("lon");
				int total_rating = rs.getInt("total_rating");
				int num_of_ratings = rs.getInt("num_of_ratings");
				String name = rs.getString("name");
				String description = rs.getString("description");
				location = new Location(lid,lat,lon,total_rating,num_of_ratings,
						name,description,categories,comments);
			} else {
				System.out.println("No users with id: " + id + " in database");
				return null;
			}
			if (DEBUG && rs.next()) {
				System.out.println("Error: getLocation for id: " + id + " Contained multiple locations");
				assert(false);
			}
    	} catch (Exception e) {
    		System.out.println("There was some sort of error in getLocation");
    		return null;
    	}
	    return location;
    }  
    
    private void setCommentsAndCategories(long id, List<Comment> comments, Set<Category> categories, Connection connection) {
    	try {
	    	PreparedStatement getCategories = connection.prepareStatement(GET_CATEGORIES);
			getCategories.setLong(1, id);
			ResultSet rs = getCategories.executeQuery();
			while (rs.next()) {
				int cat_type_id = rs.getInt("cat_type_id");
				Category category = new Category(cat_type_id, id);
				categories.add(category);
			} 
			
			PreparedStatement getComments = connection.prepareStatement(GET_COMMENTS);
			getComments.setLong(1, id);
			rs = getComments.executeQuery();
			while (rs.next()) {
				long cid = rs.getLong("id");
				long uid = rs.getLong("uid");
				long lid = rs.getLong("lid");
				String content = rs.getString("comment");
				Date date = rs.getDate("post_date");
				Time time = rs.getTime("post_time");
				Comment comment = new Comment(cid,uid,lid,content,date,time);
				comments.add(comment);
			} 
    	} catch (Exception e) {
    		System.out.println("There was some sort of error in setCommentsAndCategories");
    	}
    }
    
    /**
     * Retrieves the photos with the specified user ID
     * @param id the user that we want to photos from
     * @return the Json List of Photos object or null if the user ID does not exist
     */
    @GET
	@Path("/getUserPhotos")
	@Produces("application/json")
    public List<Photo> getUserPhotos(@QueryParam("id") long id) {
    	List<Photo> photos;
    	
    	// Create new database object
		Database database = new Database();
		Connection connection = null;	    
		// Create database connection
		try {
			connection = database.Get_Connection();
		} catch (Exception e) {
			System.out.println("Error making database connection");
			return null;
		}
		
    	try {
    		PreparedStatement getUserPhotos = connection.prepareStatement(GET_USER_PHOTOS);
    		getUserPhotos.setLong(1, id);
    		photos = new ArrayList<Photo>();
			ResultSet rs = getUserPhotos.executeQuery();
			while (rs.next()) {
				long pid = rs.getLong("id");
				long uid = rs.getLong("uid");
				long lid = rs.getLong("lid");
				String src_link = rs.getString("src_link");
				Date date = rs.getDate("post_date");
				Time time = rs.getTime("post_time");

				Photo photo = new Photo(pid,uid,lid,src_link,date,time,null);
				photos.add(photo);
			}
			
		} catch (Exception e) {
    		System.out.println("There was some sort of error in getUserPhotos");
    		e.printStackTrace();
    		return null;
    	} 
		return photos;
    }
    
    /**
     * Retrieves the photos with a specified location ID
     * @param id the location ID of the desired photos
     * @return the Json List of Photos object or null if the location ID does not exist
     */
    @GET
	@Path("/getLocationPhotos")
	@Produces("application/json")
    public List<Photo> getLocationPhotos(@QueryParam("id") long id) {
    	List<Photo> photos;
    	
    	// Create new database object
		Database database = new Database();
		Connection connection = null;	    
		// Create database connection
		try {
			connection = database.Get_Connection();
		} catch (Exception e) {
			System.out.println("Error making database connection");
			return null;
		}
		
    	try {
    		PreparedStatement getLocationPhotos = connection.prepareStatement(GET_LOCATION_PHOTOS);
    		getLocationPhotos.setLong(1, id);
    		photos = new ArrayList<Photo>();
			ResultSet rs = getLocationPhotos.executeQuery();
			while (rs.next()) {
				long pid = rs.getLong("id");
				long uid = rs.getLong("uid");
				long lid = rs.getLong("lid");
				String src_link = rs.getString("src_link");
				Date date = rs.getDate("post_date");
				Time time = rs.getTime("post_time");

				Photo photo = new Photo(pid,uid,lid,src_link,date,time,null);
				photos.add(photo);
			}
		} catch (Exception e) {
    		System.out.println("There was some sort of error in getLocationPhotos");
    		e.printStackTrace();
    		return null;
		} 
		return photos;
	}
    
    /**
     * Retrieves the locations in a range of coordinates
     * @param lat1 lower latitude bound
     * @param lat2 upper latitude bound
     * @param lotn lower longitude bound
     * @param lon2 upper longitude bound
     * @return list of Locations within the given coordinates
     */
    @GET
	@Path("/getLocationsInRange")
	@Produces("application/json")
    public List<Location> getLocationsInRange(	@QueryParam("lat1") float lat1, 
									            @QueryParam("lat2") float lat2, 
									            @QueryParam("lon1") float lon1, 
									            @QueryParam("lon2") float lon2) {
    	List<Location> locations;
    	
    	// Create new database object
		Database database = new Database();
		Connection connection = null;	    
		// Create database connection
		try {
			connection = database.Get_Connection();
		} catch (Exception e) {
			System.out.println("Error making database connection");
			return null;
		}
		
    	try {
    		locations = new ArrayList<Location>();
    		
    		PreparedStatement getLocations = connection.prepareStatement(GET_LOCATIONS_IN_RANGE);
    		getLocations.setFloat(1, lat1);
    		getLocations.setFloat(2, lat2);
    		getLocations.setFloat(3, lon1);
    		getLocations.setFloat(4, lon2);
    		
			ResultSet rs = getLocations.executeQuery();
			while (rs.next()) {
				long id = rs.getLong("id");
				float lat = rs.getFloat("lat");
				float lon = rs.getFloat("lon");
				int totalRating = rs.getInt("total_rating");
				int numOfRatings = rs.getInt("num_of_ratings");
				String name = rs.getString("name");
				String desc = rs.getString("description");
				
				List<Comment> comments = new ArrayList<Comment>();
				Set<Category> categories = new HashSet<Category>();
				setCommentsAndCategories(id,comments,categories,connection);

				Location location = new Location(id,lat,lon,totalRating,numOfRatings,name,desc,categories,comments);
				locations.add(location);
			}
		} catch (Exception e) {
    		System.out.println("There was some sort of error in getLocationsInRange");
    		e.printStackTrace();
    		return null;
		} 
		return locations;
    }
} 
