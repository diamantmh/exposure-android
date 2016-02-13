package exposure.exposure;

import android.provider.ContactsContract;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Before;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Date;
import java.sql.Time;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

import static org.mockito.Mockito.*;


/**
 * Exercises the interaction between a mocked RestTemplate and the public
 * interface of DatabaseManager.
 */
public class DatabaseManagerTest {

    private static RestTemplate mockedRest;
    private static DatabaseManager man;

    // data objects
    private static Location newLoc;
    private static Location retLoc;
    private static User newUser;
    private static User retUser;
    private static Photo newPhoto;
    private static Photo retPhoto;
    private static Category cat1;
    private static Category cat2;
    private static Comment newCom;
    private static Comment retCom;
    private static Photo[] photoArr;

    /**
     * Sets up all the test data objects that mock RestTemplate will use
     */
    @BeforeClass
    public static void initializeDataObjects() {
        cat1 = new Category(Category.DRIVING_ID);
        cat2 = new Category(Category.SUMMER_ID);

        Set<Category> cats = new HashSet<>();
        cats.add(cat1);
        cats.add(cat2);

        List<Comment> comments = new ArrayList<>();
        comments.add(retCom);

        newLoc = new Location(5,5,5,25,"Dumpster","It's really stinky here.",cats,comments); // no ID so it's new
        retLoc = new Location(1,5,5,5,25,"Dumpster","It's really stinky here.",cats,comments); // has ID

        newPhoto = new Photo(1,1,"link",new Date(1000000),new Time(1000000)); // no ID so it's new
        retPhoto = new Photo(1,1,1,"link",new Date(1000000),new Time(1000000)); // has ID

        newUser = new User("swammer","link","Hi, I like photography!");
        retUser = new User(1,"swammer","link","Hi, I like photography!");

        newCom = new Comment(1,1,"I don't like this place at all, it stinks and is ugly.");
        retCom = new Comment(1,1,1,"I don't like this place at all, it stinks and is ugly.");

        photoArr = new Photo[1];
        photoArr[0] = retPhoto;
    }

    @Before
    public void initializeMock() {
        // set up mocked RestTemplate before each test
        mockedRest = mock(RestTemplate.class);

        // update location web service behavior
        when(mockedRest.postForObject(DatabaseManager.WEB_SERVICE + "updateLocation",newLoc,Boolean.class))
                .thenReturn(false);
        when(mockedRest.postForObject(DatabaseManager.WEB_SERVICE + "updateLocation",retLoc,Boolean.class))
                .thenReturn(true);

        // update user web service behavior
        when(mockedRest.postForObject(DatabaseManager.WEB_SERVICE + "updateUser",newUser,Boolean.class))
                .thenReturn(false);
        when(mockedRest.postForObject(DatabaseManager.WEB_SERVICE + "updateUser",retUser,Boolean.class))
                .thenReturn(true);

        // insert location web service behavior
        when(mockedRest.postForObject(DatabaseManager.WEB_SERVICE + "insertLocation", newLoc, Long.class))
                .thenReturn((long) 1);
        when(mockedRest.postForObject(DatabaseManager.WEB_SERVICE + "insertLocation", retLoc, Long.class))
                .thenReturn((long) -1);

        // insert photo web service behavior
        when(mockedRest.postForObject(DatabaseManager.WEB_SERVICE + "insertPhoto", newPhoto, Long.class))
                .thenReturn((long) 1);
        when(mockedRest.postForObject(DatabaseManager.WEB_SERVICE + "insertPhoto", retPhoto, Long.class))
                .thenReturn((long) -1);

        // insert user web service behavior
        when(mockedRest.postForObject(DatabaseManager.WEB_SERVICE + "insertUser", newUser, Long.class))
                .thenReturn((long) 1);
        when(mockedRest.postForObject(DatabaseManager.WEB_SERVICE + "insertUser", retUser, Long.class))
                .thenReturn((long) -1);

        // insert comment web service behavior
        when(mockedRest.postForObject(DatabaseManager.WEB_SERVICE + "insertComment", newCom, Long.class))
                .thenReturn((long) 1);
        when(mockedRest.postForObject(DatabaseManager.WEB_SERVICE + "insertComment", retCom, Long.class))
                .thenReturn((long) -1);

        // remove user web service behavior
        when(mockedRest.postForObject(DatabaseManager.WEB_SERVICE + "removeUser", (long) 1, Boolean.class))
                .thenReturn(true);
        when(mockedRest.postForObject(DatabaseManager.WEB_SERVICE + "removeUser", (long) 99, Boolean.class))
                .thenReturn(false);

        // remove photo web service behavior
        when(mockedRest.postForObject(DatabaseManager.WEB_SERVICE + "removePhoto", (long) 1, Boolean.class))
                .thenReturn(true);
        when(mockedRest.postForObject(DatabaseManager.WEB_SERVICE + "removePhoto", (long) 99, Boolean.class))
                .thenReturn(false);

        // get user web service behavior
        when(mockedRest.getForObject(DatabaseManager.WEB_SERVICE + "getUser?id=" + 1, User.class))
                .thenReturn(retUser);
        when(mockedRest.getForObject(DatabaseManager.WEB_SERVICE + "getUser?id=" + 99, User.class))
                .thenReturn(null);

        // get user photos web service behavior
        when(mockedRest.getForObject(DatabaseManager.WEB_SERVICE + "getUserPhotos?id=" + 1, Photo[].class))
                .thenReturn(photoArr);
        when(mockedRest.getForObject(DatabaseManager.WEB_SERVICE + "getUserPhotos?id=" + 99, Photo[].class))
                .thenReturn(new Photo[0]);

        // get location photos web service behavior
        when(mockedRest.getForObject(DatabaseManager.WEB_SERVICE + "getLocationPhotos?id=" + 1, Photo[].class))
                .thenReturn(photoArr);
        when(mockedRest.getForObject(DatabaseManager.WEB_SERVICE + "getLocationPhotos?id=" + 99, Photo[].class))
                .thenReturn(new Photo[0]);

        // make a new DatabaseManager that has a mocked RestTemplate inside
        man = new DatabaseManager(mockedRest);
    }

    @Test
    public void testUpdateNewLocation() throws Exception {
        boolean actual = man.update(newLoc);
        boolean expected = false; // you can't update a location with no ID

        assertEquals("Should return false when passed a location with no ID", expected, actual);
        verify(mockedRest).postForObject(DatabaseManager.WEB_SERVICE + "updateLocation", newLoc, Boolean.class);
    }

    @Test
    public void testUpdateReturnedLocation() throws Exception {
        boolean actual = man.update(retLoc);
        boolean expected = true;

        assertEquals("Should return true when passed a location with an ID", expected, actual);
        verify(mockedRest).postForObject(DatabaseManager.WEB_SERVICE + "updateLocation", retLoc, Boolean.class);
    }

    @Test
    public void testUpdateNewUser() throws Exception {
        boolean actual = man.update(newUser);
        boolean expected = false;

        assertEquals("Should return false when passed a user with an ID", expected, actual);
        verify(mockedRest).postForObject(DatabaseManager.WEB_SERVICE + "updateUser", newUser, Boolean.class);
    }

    @Test
    public void testUpdateReturnedUser() throws Exception {
        boolean actual = man.update(retUser);
        boolean expected = true;

        assertEquals("Should return true when passed a user with an ID", expected, actual);
        verify(mockedRest).postForObject(DatabaseManager.WEB_SERVICE + "updateUser", retUser, Boolean.class);
    }

    @Test
    public void testInsertNewLocation() throws Exception {
        long actual = man.insert(newLoc);
        long expected = 1;

        assertEquals("Should return the ID of the newly created location", expected, actual);
        verify(mockedRest).postForObject(DatabaseManager.WEB_SERVICE + "insertLocation", newLoc, Long.class);
    }

    @Test
    public void testInsertReturnedLocation() throws Exception {
        long actual = man.insert(retLoc);
        long expected = -1;

        assertEquals("Should return -1 because location already exists (already has ID)", expected, actual);
        verify(mockedRest).postForObject(DatabaseManager.WEB_SERVICE + "insertLocation", retLoc, Long.class);
    }

    @Test
    public void testInsertNewPhoto() throws Exception {
        long actual = man.insert(newPhoto);
        long expected = 1;

        assertEquals("Should return the ID of the newly created photo", expected, actual);
        verify(mockedRest).postForObject(DatabaseManager.WEB_SERVICE + "insertPhoto", newPhoto, Long.class);
    }

    @Test
    public void testInsertReturnedPhoto() throws Exception {
        long actual = man.insert(retPhoto);
        long expected = -1;

        assertEquals("Should return -1 because photo already exists (already has ID)", expected, actual);
        verify(mockedRest).postForObject(DatabaseManager.WEB_SERVICE + "insertPhoto", retPhoto, Long.class);
    }

    @Test
    public void testInsertNewUser() throws Exception {
        long actual = man.insert(newUser);
        long expected = 1;

        assertEquals("Should return the ID of the newly created user", expected, actual);
        verify(mockedRest).postForObject(DatabaseManager.WEB_SERVICE + "insertUser", newUser, Long.class);
    }

    @Test
    public void testInsertReturnedUser() throws Exception {
        long actual = man.insert(retUser);
        long expected = -1;

        assertEquals("Should return -1 because user already exists (already has ID)", expected, actual);
        verify(mockedRest).postForObject(DatabaseManager.WEB_SERVICE + "insertUser", retUser, Long.class);
    }

    @Test
    public void testInsertNewComment() throws Exception {
        long actual = man.insert(newCom);
        long expected = 1;

        assertEquals("Should return the ID of the newly created comment", expected, actual);
        verify(mockedRest).postForObject(DatabaseManager.WEB_SERVICE + "insertComment", newCom, Long.class);
    }

    @Test
    public void testInsertReturnedComment() throws Exception {
        long actual = man.insert(retCom);
        long expected = -1;

        assertEquals("Should return -1 because comment already exists (already has ID)", expected, actual);
        verify(mockedRest).postForObject(DatabaseManager.WEB_SERVICE + "insertComment", retCom, Long.class);
    }

    @Test
    public void testRemoveExistingUser() throws Exception {
        boolean actual = man.removeUser((long) 1);
        boolean expected = true;

        assertEquals("Should return true when passed ID of existing user", expected, actual);
        verify(mockedRest).postForObject(DatabaseManager.WEB_SERVICE + "removeUser", (long) 1, Boolean.class);
    }

    @Test
    public void testRemoveBogusUser() throws Exception {
        boolean actual = man.removeUser((long) 99);
        boolean expected = false;

        assertEquals("Should return false when passed ID that does not match an existing user", expected, actual);
        verify(mockedRest).postForObject(DatabaseManager.WEB_SERVICE + "removeUser", (long) 99, Boolean.class);
    }

    @Test
    public void testRemoveExistingPhoto() throws Exception {
        boolean actual = man.removePhoto((long) 1);
        boolean expected = true;

        assertEquals("Should return true when passed ID of existing photo", expected, actual);
        verify(mockedRest).postForObject(DatabaseManager.WEB_SERVICE + "removePhoto", (long) 1, Boolean.class);
    }

    @Test
    public void testRemoveBogusPhoto() throws Exception {
        boolean actual = man.removePhoto((long) 99);
        boolean expected = false;

        assertEquals("Should return false when passed ID that does not match an existing photo", expected, actual);
        verify(mockedRest).postForObject(DatabaseManager.WEB_SERVICE + "removePhoto", (long) 99, Boolean.class);
    }

    @Test
    public void testGetExistingUser() throws Exception {
        User actual = man.getUser((long) 1);
        User expected = retUser;

        assertEquals("Should return user matching the given ID", expected, actual);
        verify(mockedRest).getForObject(DatabaseManager.WEB_SERVICE + "getUser?id=" + 1, User.class);
    }

    @Test
    public void testGetBogusUser() throws Exception {
        User actual = man.getUser((long) 99);
        User expected = null;

        assertEquals("Should return null when passed ID that does not match an existing user", expected, actual);
        verify(mockedRest).getForObject(DatabaseManager.WEB_SERVICE + "getUser?id=" + 99, User.class);
    }

    @Test
    public void testGetExistingUserPhotos() throws Exception {
        Photo[] actual = man.getUserPhotos((long) 1);
        Photo[] expected = photoArr;

        assertArrayEquals("Should return an array of user photos", expected, actual);
        verify(mockedRest).getForObject(DatabaseManager.WEB_SERVICE + "getUserPhotos?id=" + 1, Photo[].class);
    }

    @Test
    public void testGetBogusUserPhotos() throws Exception {
        Photo[] actual = man.getUserPhotos((long) 99);
        Photo[] expected = new Photo[0];

        assertArrayEquals("Should return an empty array", expected, actual);
        verify(mockedRest).getForObject(DatabaseManager.WEB_SERVICE + "getUserPhotos?id=" + 99, Photo[].class);
    }

    @Test
    public void testGetExistingLocationPhotos() throws Exception {
        Photo[] actual = man.getLocationPhotos((long) 1);
        Photo[] expected = photoArr;

        assertArrayEquals("Should return an array of location photos", expected, actual);
        verify(mockedRest).getForObject(DatabaseManager.WEB_SERVICE + "getLocationPhotos?id=" + 1, Photo[].class);
    }

    @Test
    public void testGetBogusLocationPhotos() throws Exception {
        Photo[] actual = man.getLocationPhotos((long) 99);
        Photo[] expected = new Photo[0];

        assertArrayEquals("Should return an empty array", expected, actual);
        verify(mockedRest).getForObject(DatabaseManager.WEB_SERVICE + "getLocationPhotos?id=" + 99, Photo[].class);
    }
}