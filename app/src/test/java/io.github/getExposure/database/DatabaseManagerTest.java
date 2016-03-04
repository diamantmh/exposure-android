package io.github.getExposure.database;

import android.test.mock.MockContext;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Before;
import org.springframework.web.client.RestTemplate;

import java.io.File;
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
    private static ExposureLocation newLoc;
    private static ExposureLocation retLoc;
    private static ExposureUser newUser;
//    private static ExposurePhoto newPhoto;
//    private static ExposurePhoto retPhoto;
    private static Category cat1;
    private static Category cat2;
    private static Comment newCom;
    private static Comment retCom;
    private static ExposurePhoto[] photoArr;

    /**
     * Sets up all the test data objects that mock RestTemplate will use
     */
    @BeforeClass
    public static void initializeDataObjects() {
        List<Comment> comments = new ArrayList<>();
        newCom = new Comment(1,1,"best user","I don't like this place at all, it stinks and is ugly.",new Date(1000000),new Time(1000000));
        retCom = new Comment(1,1,1,"best user","I don't like this place at all, it stinks and is ugly.", new Date(1000000),new Time(1000000));
        comments.add(retCom);

        long dummyLocID = 1;

        cat1 = new Category(dummyLocID, Category.DRIVING_ID);
        cat2 = new Category(dummyLocID, Category.SUMMER_ID);

        Set<Category> cats = new HashSet<>();
        cats.add(cat1);
        cats.add(cat2);

        newLoc = new ExposureLocation(5,5,5,25,"Dumpster","It's really stinky here.",cats,comments); // no ID so it's new
        retLoc = new ExposureLocation(1,5,5,5,25,"Dumpster","It's really stinky here.",cats,comments); // has ID
/*
        newPhoto = new ExposurePhoto(1,1,"link",new Date(1000000),new Time(1000000),null); // no ID so it's new
        retPhoto = new ExposurePhoto(1,1,1,"link",new Date(1000000),new Time(1000000),null); // has ID
*/
        long mockFbId = 1;
        newUser = new ExposureUser(mockFbId, "swammer","link","Hi, I like photography!");
/*
        photoArr = new ExposurePhoto[1];
        photoArr[0] = retPhoto;
*/
    }

    @Before
    public void initializeMock() {
        // set up mocked RestTemplate before each test
        mockedRest = mock(RestTemplate.class);

        // update location web service behavior
        when(mockedRest.postForObject(eq(DatabaseManager.WEB_SERVICE + "updateLocation"),anyObject(),eq(Boolean.class)))
                .thenReturn(true);

        // update user web service behavior
        when(mockedRest.postForObject(DatabaseManager.WEB_SERVICE + "updateUser",newUser,Boolean.class))
                .thenReturn(true);

        // insert location web service behavior
        when(mockedRest.postForObject(eq(DatabaseManager.WEB_SERVICE + "insertLocation"), anyObject(), eq(Long.class)))
                .thenReturn((long) 1);
/*
        // insert photo web service behavior
        when(mockedRest.postForObject(DatabaseManager.WEB_SERVICE + "insertPhoto", newPhoto, Long.class))
                .thenReturn((long) 1);
        when(mockedRest.postForObject(DatabaseManager.WEB_SERVICE + "insertPhoto", retPhoto, Long.class))
                .thenReturn((long) -1);
*/
        // insert user web service behavior
        when(mockedRest.postForObject(DatabaseManager.WEB_SERVICE + "insertUser", newUser, Boolean.class))
                .thenReturn(true);

        // insert comment web service behavior
        when(mockedRest.postForObject(DatabaseManager.WEB_SERVICE + "insertComment", newCom, Long.class))
                .thenReturn((long) 1);
        when(mockedRest.postForObject(DatabaseManager.WEB_SERVICE + "insertComment", retCom, Long.class))
                .thenReturn((long) -1);

        // insert category web service behavior
        when(mockedRest.postForObject(DatabaseManager.WEB_SERVICE + "insertCategory", cat1, Boolean.class))
                .thenReturn(true);

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
        when(mockedRest.getForObject(DatabaseManager.WEB_SERVICE + "getUser?id=" + 1, ExposureUser.class))
                .thenReturn(newUser);
        when(mockedRest.getForObject(DatabaseManager.WEB_SERVICE + "getUser?id=" + 99, ExposureUser.class))
                .thenReturn(null);

        // get location web service behavior
        when(mockedRest.getForObject(DatabaseManager.WEB_SERVICE + "getLocation?id=" + 1, ExposureLocation.class))
                .thenReturn(retLoc);
        when(mockedRest.getForObject(DatabaseManager.WEB_SERVICE + "getLocation?id=" + 99, ExposureLocation.class))
                .thenReturn(null);

        // get user photos web service behavior
        when(mockedRest.getForObject(DatabaseManager.WEB_SERVICE + "getUserPhotos?id=" + 1, ExposurePhoto[].class))
                .thenReturn(photoArr);
        when(mockedRest.getForObject(DatabaseManager.WEB_SERVICE + "getUserPhotos?id=" + 99, ExposurePhoto[].class))
                .thenReturn(null);

        // get location photos web service behavior
        when(mockedRest.getForObject(DatabaseManager.WEB_SERVICE + "getLocationPhotos?id=" + 1, ExposurePhoto[].class))
                .thenReturn(photoArr);
        when(mockedRest.getForObject(DatabaseManager.WEB_SERVICE + "getLocationPhotos?id=" + 99, ExposurePhoto[].class))
                .thenReturn(null);

        // make a new DatabaseManager that has a mocked RestTemplate inside
        man = new DatabaseManager(mockedRest, new MockContext());
    }

    @Test
    public void testUpdateReturnedLocation() throws Exception {
        boolean actual = man.update(retLoc);
        boolean expected = true;

        assertEquals("Should return true when passed a location with an ID", expected, actual);
        verify(mockedRest).postForObject(eq(DatabaseManager.WEB_SERVICE + "updateLocation"), anyObject(), eq(Boolean.class));
    }

    @Test
    public void testUpdateUser() throws Exception {
        boolean actual = man.update(newUser);
        boolean expected = true;

        assertEquals("Should return false when passed a user with an ID", expected, actual);
        verify(mockedRest).postForObject(DatabaseManager.WEB_SERVICE + "updateUser", newUser, Boolean.class);
    }

    @Test
    public void testInsertNewLocation() throws Exception {
        long actual = man.insert(newLoc);
        long expected = 1;

        assertEquals("Should return the ID of the newly created location", expected, actual);
        verify(mockedRest).postForObject(eq(DatabaseManager.WEB_SERVICE + "insertLocation"), anyObject(), eq(Long.class));
    }
/*
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
*/
    @Test
    public void testInsertNewUser() throws Exception {
        boolean actual = man.insert(newUser);
        boolean expected = true;

        assertEquals("Should return the true when the User is inserted", expected, actual);
        verify(mockedRest).postForObject(DatabaseManager.WEB_SERVICE + "insertUser", newUser, Boolean.class);
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
    public void testInsertCategory() {
        boolean actual = man.insert(cat1);
        boolean expected = true;

        assertEquals("Should return true when the Category is inserted.", expected, actual);
        verify(mockedRest).postForObject(DatabaseManager.WEB_SERVICE + "insertCategory", cat1, Boolean.class);
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
        ExposureUser actual = man.getUser(1);
        ExposureUser expected = newUser;

        assertEquals("Should return user matching the given ID", expected, actual);
        verify(mockedRest).getForObject(DatabaseManager.WEB_SERVICE + "getUser?id=" + 1, ExposureUser.class);
    }

    @Test
    public void testGetBogusUser() throws Exception {
        ExposureUser actual = man.getUser((long) 99);
        ExposureUser expected = null;

        assertEquals("Should return null when passed ID that does not match an existing user", expected, actual);
        verify(mockedRest).getForObject(DatabaseManager.WEB_SERVICE + "getUser?id=" + 99, ExposureUser.class);
    }

    @Test
    public void testGetExistingLocation() throws Exception {
        ExposureLocation actual = man.getLocation((long) 1);
        ExposureLocation expected = retLoc;

        assertEquals("Should return location matching the given ID", expected, actual);
        verify(mockedRest).getForObject(DatabaseManager.WEB_SERVICE + "getLocation?id=" + 1, ExposureLocation.class);
    }

    @Test
    public void testGetBogusLocation() throws Exception {
        ExposureLocation actual = man.getLocation((long) 99);
        ExposureLocation expected = null;

        assertEquals("Should return null when passed ID that does not match an existing location", expected, actual);
        verify(mockedRest).getForObject(DatabaseManager.WEB_SERVICE + "getLocation?id=" + 99, ExposureLocation.class);
    }
/*
    @Test
    public void testGetExistingUserPhotos() throws Exception {
        ExposurePhoto[] actual = man.getUserPhotos((long) 1);
        ExposurePhoto[] expected = photoArr;

        assertArrayEquals("Should return an array of user photos", expected, actual);
        verify(mockedRest).getForObject(DatabaseManager.WEB_SERVICE + "getUserPhotos?id=" + 1, ExposurePhoto[].class);
    }

    @Test
    public void testGetBogusUserPhotos() throws Exception {
        ExposurePhoto[] actual = man.getUserPhotos((long) 99);
        ExposurePhoto[] expected = null;

        assertArrayEquals("Should return an empty array", expected, actual);
        verify(mockedRest).getForObject(DatabaseManager.WEB_SERVICE + "getUserPhotos?id=" + 99, ExposurePhoto[].class);
    }

    @Test
    public void testGetExistingLocationPhotos() throws Exception {
        ExposurePhoto[] actual = man.getLocationPhotos((long) 1);
        ExposurePhoto[] expected = photoArr;

        assertArrayEquals("Should return an array of location photos", expected, actual);
        verify(mockedRest).getForObject(DatabaseManager.WEB_SERVICE + "getLocationPhotos?id=" + 1, ExposurePhoto[].class);
    }

    @Test
    public void testGetBogusLocationPhotos() throws Exception {
        ExposurePhoto[] actual = man.getLocationPhotos((long) 99);
        ExposurePhoto[] expected = null;

        assertArrayEquals("Should return an empty array", expected, actual);
        verify(mockedRest).getForObject(DatabaseManager.WEB_SERVICE + "getLocationPhotos?id=" + 99, ExposurePhoto[].class);
    }
*/
}