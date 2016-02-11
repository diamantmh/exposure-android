package exposure.exposure;

import android.provider.ContactsContract;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Before;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.sql.Time;
import java.util.HashSet;

import static org.junit.Assert.*;

import static org.mockito.Mockito.*;

/**
 * Exercises the interaction between a mocked RestTemplate and the public
 * interface of DatabaseManager.
 */
public class DBManagerTest {

    RestTemplate mockedRest;
    DatabaseManager man;

    // data objects
    Location newLoc;
    Location retLoc;
    User newUser;
    User retUser;
    Photo newPhoto;
    Photo retPhoto;
    Category cat1;
    Category cat2;

    /**
     * Sets up all the test data objects that mock RestTemplate will use
     */
    @BeforeClass
    public void initializeDataObjects() {
        cat1 = new Category(Category.DRIVING_ID);
        cat2 = new Category(Category.SUMMER_ID);

        HashSet<Category> cats1 = new HashSet<>();
        cats1.add(cat1);
        cats1.add(cat2);

        newLoc = new Location(5,5,5,25,"Dumpster","It's really stinky here.", cats1);
        retLoc = new Location(1,5,5,5,25,"Dumpster","It's really stinky here.", cats1); // has ID

        newPhoto = new Photo(1,1,"link",new Date(1000000),new Time(1000000));
        retPhoto = new Photo(1,1,1,"link",new Date(1000000),new Time(1000000));
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
        // fill out test
    }

    @Test
    public void testUpdateReturnedUser() throws Exception {
        // fill out test
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

        assertEquals("Should return -1 because location already exists", expected, actual);
        verify(mockedRest).postForObject(DatabaseManager.WEB_SERVICE + "insertLocation", retLoc, Long.class);
    }

    @Test
    public void testInsertNewPhoto() throws Exception {
        assertEquals(false,false);
    }

    @Test
    public void testInsertReturnedPhoto() throws Exception {
        assertEquals(false, true);
    }

    @Test
    public void testInsertNewUser() throws Exception {

    }

    @Test
    public void testInsertReturnedUser() throws Exception {

    }

    @Test
    public void testInsertNewComment() throws Exception {

    }

    @Test
    public void testInsertReturnedComment() throws Exception {

    }

    @Test
    public void testRemoveExistingUser() throws Exception {

    }

    @Test
    public void testRemoveBogusUser() throws Exception {

    }

    @Test
    public void testRemoveExistingPhoto() throws Exception {

    }

    @Test
    public void testRemoveBogusPhoto() throws Exception {

    }

    @Test
    public void testGetExistingUser() throws Exception {

    }

    @Test
    public void testGetBogusUser() throws Exception {

    }

    @Test
    public void testGetExistingUserPhotos() throws Exception {

    }

    @Test
    public void testGetBogusUserPhotos() throws Exception {

    }

    @Test
    public void testGetExistingLocationPhotos() throws Exception {

    }

    @Test
    public void testGetBogusLocationPhotos() throws Exception {

    }
}
