package io.github.getExposure.database;

import org.junit.Test;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;

/**
 * Tests immutability of ExposureLocation.
 */
public class LocationTest {
/*
    private static final double EPSI = .00001;

    @Test
    public void testGetRating() throws Exception {
        Set<Category> cats = new HashSet<>();
        List<Comment> comments = new ArrayList<>();
        ExposureLocation loc = new ExposureLocation(1,5,5,31,7,"Quad","It's great!",cats,comments);

//        double actual = loc.getRating();
        double actual = (double) loc.getTotalRating() / (double) loc.getNumOfRatings();
        double expected = (double)31/7;

        assertEquals("Should return average rating", expected, actual, EPSI);
    }

    @Test
    public void testGetZeroRating() throws Exception {
        Set<Category> cats = new HashSet<>();
        List<Comment> comments = new ArrayList<>();
        ExposureLocation loc = new ExposureLocation(1,5,5,0,0,"Quad","It's great!",cats,comments);

//        double actual = loc.getRating();
        double actual = loc.getNumOfRatings() == 0 ? -1 : (loc.getTotalRating() / loc.getNumOfRatings());
        double expected = -1;

        assertEquals("Should return -1 because this location has no ratings yet", expected, actual, EPSI);
    }

    @Test
    public void testAddRating() throws Exception {
        Set<Category> cats = new HashSet<>();
        List<Comment> comments = new ArrayList<>();
        ExposureLocation loc = new ExposureLocation(1,5,5,31,7,"Quad","It's great!",cats,comments);

        ExposureLocation loc2 = loc.addRating(2);

//        double actual = loc.addRating(2).getRating();
        double actual = (double) loc2.getTotalRating() / (double) loc2.getNumOfRatings();
        double expected = (double)33/8;

        assertEquals("Should return the updated average rating", expected, actual, EPSI);
    }
*/
    @Test
    public void testCategorySetImmutability() throws Exception {
        List<Comment> comments = new ArrayList<>();
        Set<Category> cats = new HashSet<>();
        cats.add(new Category(1, Category.DRIVING_ID));
        cats.add(new Category(2, Category.FALL_ID));

        ExposureLocation loc = new ExposureLocation(1,5,5,5,1,"Quad","It's great!",cats,comments);

        // try to mess with the internal set of ExposureLocation
        Set<Category> newCats = loc.getCategories();
        newCats.clear();
        // see if the set in ExposureLocation is the same as before
        int actual = loc.getCategories().size();
        int expected = 2;
        assertEquals("Internal set of categories should not be be exposed by getCategories", expected, actual);

        // check if passed in parameter was affected by the clear() statement above
        assertEquals("Passed in set should not be the same object as internal set", expected, cats.size());

        // try to delete ExposureLocation internal set through passed in parameter
        cats.clear();
        actual = loc.getCategories().size();
        assertEquals("Internal set should not be affected by parameter set", expected, actual);
    }

    @Test
    public void testCommentListImmutability() throws Exception {
        Set<Category> cats = new HashSet<>();
        List<Comment> comments = new ArrayList<>();
        comments.add(new Comment(1,1,1,"Test_Author1","This place is rad!",new Date(1000000),new Time(1000000)));
        comments.add(new Comment(2,1,1,"Test_Author2","You should check it out!",new Date(1000000),new Time(1000000)));

        ExposureLocation loc = new ExposureLocation(1,5,5,5,1,"Quad","It's great!",cats,comments);

        // try to mess up with the internal list of ExposureLocation
        List<Comment> newComments = loc.getComments();
        newComments.clear();
        // see if the list in ExposureLocation is the same as before
        int actual = loc.getComments().size();
        int expected = 2;
        assertEquals("Internal list of categories should not be exposed by getComments", expected, actual);

        // check if passed in parameter was affected by the clear() statement above
        assertEquals("Passed in set should not be the same object as internal set", expected, comments.size());

        // try to clear ExposureLocation internal list through passed in parameter
        comments.clear();
        actual = loc.getComments().size();
        assertEquals("Internal list should not be affected by parameter list", expected, actual);
    }
}