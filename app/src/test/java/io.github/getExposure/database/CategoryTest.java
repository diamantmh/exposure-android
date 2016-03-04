package io.github.getExposure.database;

import org.junit.Test;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;

/**
 * Tests the tricky constructors of Category.
 */
public class CategoryTest {

    @Test
    public void testGetContentStringConstructor() throws Exception {
        Category cat = new Category("Summer");

        String actual = cat.getContent();
        String expected = "summer";
        assertEquals("getContent should return the proper category text with string constructor", expected, actual);
    }

    @Test
    public void testGetContentIDConstructor() throws Exception {
        Category cat = new Category(Category.DRIVING_ID);

        String actual = cat.getContent();
        String expected = "driving";
        assertEquals("getContent should return the proper category text with ID constructor", expected, actual);
    }

    @Test
    public void testGetLocIDStringConstructor() throws Exception {
        Category cat = new Category(115, "out doors");

        long actual = cat.getLocID();
        long expected = 115;
        assertEquals("getContent should return the given location ID", expected, actual);
    }

    @Test
    public void testGetIdStringConstructor() throws Exception {
        Category cat = new Category("driving");

        long actual = cat.getId();
        long expected = Category.DRIVING_ID;
        assertEquals("getContent should return the proper unique identifier", expected, actual);
    }

    @Test
    public void testGetIdWithIDConstructor() throws Exception {
        Category cat = new Category(Category.DRIVING_ID);

        long actual = cat.getId();
        long expected = Category.DRIVING_ID;
        assertEquals("getContent should return the proper unique identifier", expected, actual);
    }
}