package io.github.getExposure.database;

import org.junit.Test;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.github.getExposure.database.Category;
import io.github.getExposure.database.Comment;
import io.github.getExposure.database.ExposureLocation;
import io.github.getExposure.database.Filter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * Tests functionality for Filter on different inputs to check against expected results
 */
public class FilterTest {

    @Test
    public void testEmptyCategories() throws Exception {
        Set<Category> cats = new HashSet<>();
        List<Comment> comments = new ArrayList<>();
        ExposureLocation[] locations = new ExposureLocation[1];
        locations[0] = new ExposureLocation(1,5,5,31,7,"Quad","It's great!",cats,comments);

        Set<Category> matchCriteria = new HashSet<>();

        ExposureLocation[] actual = Filter.filterLocations(locations, matchCriteria);
        ExposureLocation[] expected = null;

        assert(actual == expected);
    }

    @Test
    public void testNoResults() throws Exception {
        Set<Category> cats = new HashSet<>();
        cats.add(new Category(1, Category.FALL_ID));

        List<Comment> comments = new ArrayList<>();
        ExposureLocation[] locations = new ExposureLocation[1];
        locations[0] = new ExposureLocation(1,5,5,31,7,"Quad","It's great!",cats,comments);

        Set<Category> matchCriteria = new HashSet<>();
        matchCriteria.add(new Category(1, Category.DRIVING_ID));


        ExposureLocation[] actual = Filter.filterLocations(locations, matchCriteria);
        ExposureLocation[] expected = null;

        assert(actual == expected);
    }

    @Test
    public void testFilterRegular() throws Exception {
        Set<Category> fallCats = new HashSet<>();
        fallCats.add(new Category(1, Category.FALL_ID));
        List<Comment> comments1 = new ArrayList<>();

        Set<Category> drivingCats = new HashSet<>();
        drivingCats.add(new Category(2, Category.DRIVING_ID));
        List<Comment> comments2 = new ArrayList<>();

        ExposureLocation[] locations = new ExposureLocation[2];
        locations[0] = new ExposureLocation(1,5,5,31,7,"Quad","It's great!",fallCats,comments1);
        locations[1] = new ExposureLocation(2,5,5,31,7,"Quad","It's great!",drivingCats,comments2);

        Set<Category> matchCriteria = new HashSet<>();
        matchCriteria.add(new Category(1, Category.DRIVING_ID));


        ExposureLocation[] filtered = Filter.filterLocations(locations, matchCriteria);
        int actual = filtered.length;
        int expected = 1;

        assertEquals("Should return only the locations that match the given criteria", expected, actual);
        assert(filtered[0].getID() == 2); // this is the driving cats ID we expect
    }
}