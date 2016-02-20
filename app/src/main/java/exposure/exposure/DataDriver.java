package exposure.exposure;

import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;

import java.io.File;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Example class that demonstrates the use of DatabaseManager
 */
public class DataDriver extends FragmentActivity {

    private static DatabaseManager man;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        run();
    }

    private void run() {
        System.out.println("-----------------");
        System.out.println("Welcome to the DatabaseManager Demo");
        System.out.println("-----------------");
        System.out.println();
        System.out.println("Instantiating DatabaseManager...");
        man = new DatabaseManager();
        System.out.println("DatabaseManager initiated!");

        System.out.println("Allowing networking on main thread (this causes lock-ups so don't do this)");
        // Ignoring the NetworkOnMainThreadException.
        // Exposure shouldn't do this, but instead use an AsyncTask
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        System.out.println("Trying out some functionality...");
/*
        // test insert user
        System.out.println();
        System.out.println("Inserting a valid user,");
        User newUser = new User("HEY I'M TEMMIE", "pls work", "I'm a brand new user with id 20 maybe??");
        displayUser(newUser);
        long userID = man.insert(newUser);
        System.out.println("ID = " + userID);
        if (userID <= 0) { throw new AssertionError(); }
        System.out.println("User has been inserted");
        User retUser = newUser.addID(userID);
        displayUser(retUser);

        // test update user
        System.out.println();
        System.out.println("Updating user that was just inserted with ID = " + 47);
        boolean goodRes = man.update(new User(47, "agent47", "updated link", "The about me section is different now! updateUser works!!!!!!!"));
        if (!goodRes) { throw new AssertionError(); }
        System.out.println("User updated");

        // test get user
        System.out.println();

        System.out.println("Retrieving updated user with ID = " + 47);
        User retrievedUser = man.getUser(47);

        if (retrievedUser == null) { throw new AssertionError(); }
        System.out.println("Updated user:");
        displayUser(retrievedUser);

        // test remove user
        System.out.println();
        System.out.println("Removing user with id = " + 4);
        boolean remResGood = man.removeUser(4);
        if (!remResGood) { throw new AssertionError(); }
        System.out.println("User removed");

        // location tests
        // test insert location
        System.out.println();
        System.out.println("Inserting new location with no ID specified");
        Category[] cats = new Category[1];
        cats[0] = new Category(Category.WALKING_ID);;
        Location newLoc = new Location(10,10,20,4,"Quad", "There are nice trees here!", cats, new Comment[0]);
        displayLocation(newLoc);
        long retLocID = man.insert(newLoc);
        System.out.println("Returned ID is: " + retLocID);
        if (retLocID <= 0) { throw new AssertionError(); }
        Location retLoc = newLoc.addID(retLocID);

        */
        // test insert comment
        System.out.println();
        System.out.println("Inserting new comment with no ID specified");
        Comment newCom = new Comment(1, 1, "This is a comment!", new Date(100000), new Time(100000));
        long comID = man.insert(newCom);
        if (comID <= 0) { throw new AssertionError(); }
        Comment retCom = newCom.addID(comID);
        System.out.println("Returned ID is: " + comID);

        /*
        // test update location
        System.out.println();
        System.out.println("Updating location that was just inserted with ID = " + retLocID);
        List<Comment> comments = new ArrayList<>();
        comments.add(retCom);
        Location updatedLocation = new Location(retLoc.getID(), retLoc.getLat(), retLoc.getLon(), retLoc.getTotalRating(),
                retLoc.getNumOfRatings(), retLoc.getName(), retLoc.getDesc(), retLoc.getCategories(), comments);
        boolean goodLocRes = man.update(updatedLocation);
        if (!goodLocRes) { throw new AssertionError(); }
        System.out.println("Location updated with a comment");

        // test get location
        System.out.println();
        System.out.println("Retrieving updated location with ID = " + retLocID);
        Location retrievedLocation = man.getLocation(retLocID);
        if (retrievedLocation == null) { throw new AssertionError(); }
        System.out.println("Updated location:");
        displayLocation(retrievedLocation);
        */

        // test insert photo
        System.out.println();
        System.out.println("Inserting new photo with no ID specified");
        System.out.println("Creating a photo...");
        File newPNG = new File("/Users/Tyler/Desktop/testimage.jpg");
        Photo newPhoto = new Photo(userID,1,"",new Date(0),new Time(0), newPNG);
        long photoID = man.insert(newPhoto);
        if (photoID <= 0) { throw new AssertionError(); }
        Photo retPhoto = newPhoto.addID(photoID);
        System.out.println("Returned ID is: " + photoID);

        // test get user photos
        System.out.println();
        System.out.println("Retrieving user photos from user with id = " + userID);
        Photo[] userPhotos = man.getUserPhotos(userID);
        if (userPhotos == null) {
            System.out.println("There are no returned photos! The return array is null.");
        } else {
            System.out.println("There were " + userPhotos.length + " photos returned.");
            System.out.println("Source is: " + userPhotos[0].getSource());
        }

        // test get location photos
        System.out.println();
        System.out.println("Retrieving location photos from location with id = " + 1);
        Photo[] locPhotos = man.getLocationPhotos(1);
        if (locPhotos == null) {
            System.out.println("There are no returned photos! The return array is null.");
        } else {
            System.out.println("There were " + locPhotos.length + " photos returned.");
        }

        // test remove photo
        System.out.println();
        System.out.println("Removing photo with id = " + (photoID - 1));
        boolean removePhotoRes = man.removePhoto(photoID - 1);
        if (!removePhotoRes) { throw new AssertionError(); }
        System.out.println("Photo removed");

        // test remove user
        System.out.println();
        System.out.println("Removing user with id = " + (userID - 1);
        boolean remResGood = man.removeUser(userID);
        if (!remResGood) { throw new AssertionError(); }
        System.out.println("User removed");

        System.out.println("Thanks for using the DatabaseManager Demo!");
        System.exit(0);
    }

    private void displayLocation(Location loc) {
        System.out.println(loc.getName());
        System.out.println(loc.getDesc());
        if (loc.getCategories().length != 0) {
            System.out.println("Categories: ");
            for (Category cat : loc.getCategories()) {
                System.out.print(cat.getContent() + " ");
            }
        }
        System.out.println();
        System.out.println("\tLocation ID: " + loc.getID());
        System.out.println("\tlatitude and longitude: " + loc.getLat() + ", " + loc.getLon());
        System.out.println("\tRating: " + loc.getRating());
        if (loc.getComments().length != 0) {
            System.out.println("\tComments: ");
            for (Comment com : loc.getComments()) {
                System.out.println("\t\t" + com.getContent() + " ");
            }
        }
        System.out.println();
    }

    private void displayUser(User user) {
        System.out.println("\tUser ID: " + user.getID());
        System.out.println("\tUsername: " + user.getUsername());
        System.out.println("\tAbout me:");
        System.out.println("\t" + user.getAboutMe());
        System.out.println();
    }
}
