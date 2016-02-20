package exposure.exposure;

import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;

import java.io.File;
import java.sql.Time;
import java.util.Date;

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



        File newPNG = new File("/Users/Tyler/Desktop/testimage.jpg"); // MUST SPECIFY A VALID PHOTO




        System.out.println("Allowing networking on main thread (this causes lock-ups so don't do this)");
        // Ignoring the NetworkOnMainThreadException.
        // Exposure shouldn't do this, but instead use an AsyncTask
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        System.out.println("Trying out some functionality...");

        // test insert user
        System.out.println();
        System.out.println("Inserting a valid user,");
        ExposureUser newUser = new ExposureUser("HEY I'M GOKU", "GOKU LINK", "MY POWER LEVEL IS OVER 9000!!!!!!!!");
        displayUser(newUser);
        long userID = man.insert(newUser);
        System.out.println("ID = " + userID);
        if (userID <= 0) { throw new AssertionError(); }
        System.out.println("User has been inserted");
        ExposureUser retUser = newUser.addID(userID);
        displayUser(retUser);

        // test update user
        System.out.println();
        System.out.println("Updating user that was just inserted with ID = " + userID);
        boolean goodRes = man.update(new ExposureUser(userID, "GOGITA", "SUPERSAYAN LINK", "HUUUUUUGE MUSCLES"));
        if (!goodRes) { throw new AssertionError(); }
        System.out.println("User updated");

        // test get user
        System.out.println();

        System.out.println("Retrieving updated user with ID = " + userID);
        ExposureUser retrievedUser = man.getUser(userID);

        if (retrievedUser == null) { throw new AssertionError(); }
        System.out.println("Got User:");
        displayUser(retrievedUser);

        // location tests
        // test insert location
        System.out.println();
        System.out.println("Inserting new location with no ID specified");
        Category cat = new Category(userID, Category.WALKING_ID);
        DatabaseManager.WebLocation newLoc = new DatabaseManager.WebLocation(10,10,20,4,"Quad", "There are nice trees here!");
        displayLocation(newLoc);
        long retLocID = man.insert(newLoc);
        System.out.println("Returned Location ID is: " + retLocID);
        if (retLocID <= 0) { throw new AssertionError(); }
        DatabaseManager.WebLocation retLoc = newLoc.addID(retLocID);

        // test insert comment
        System.out.println();
        System.out.println("Inserting new comment with no ID specified");
        Comment newCom = new Comment(userID, retLocID, "This is a comment!", new Date(100000), new Time(100000));
        long comID = man.insert(newCom);
        if (comID <= 0) { throw new AssertionError(); }
        Comment retCom = newCom.addID(comID);
        System.out.println("Returned Comment ID is: " + comID);

        // test update location
        System.out.println();
        System.out.println("Updating location that was just inserted with ID = " + retLocID);
        DatabaseManager.WebLocation updatedLocation = new DatabaseManager.WebLocation(retLocID, retLoc.getLat(), retLoc.getLon(), retLoc.getTotalRating() + 10,
                retLoc.getNumOfRatings(), retLoc.getName(), retLoc.getDesc());
        boolean goodLocRes = man.update(updatedLocation);
        if (!goodLocRes) { throw new AssertionError(); }
        System.out.println("Location updated with rating + 10");
        // test get location
        System.out.println();
        System.out.println("Retrieving updated location with ID = " + retLocID);
        ExposureLocation retrievedLocation = man.getLocation(retLocID);
        if (retrievedLocation == null) { throw new AssertionError(); }
        System.out.println("Got location:");
        displayLocation(retrievedLocation);

        // test insert photo
        System.out.println();
        System.out.println("Inserting new photo with no ID specified");
        System.out.println("Creating a photo...");
        ExposurePhoto newPhoto = new ExposurePhoto(userID,retLocID,"",new Date(999),new Time(888), newPNG);
        long photoID = man.insert(newPhoto);
        System.out.println("PhotoID: " + photoID);
        if (photoID <= 0) { throw new AssertionError(); }
        ExposurePhoto retPhoto = newPhoto.addID(photoID);
        System.out.println("Returned ID is: " + photoID);

        // test get user photos
        System.out.println();
        System.out.println("Retrieving user photos from user with id = " + userID);
        ExposurePhoto[] userPhotos = man.getUserPhotos(userID);
        if (userPhotos == null) {
            System.out.println("There are no returned photos! The return array is null.");
        } else {
            System.out.println("There were " + userPhotos.length + " photos returned.");
            System.out.println("Source is: " + userPhotos[0].getSource());
        }

        // test get location photos
        System.out.println();
        System.out.println("Retrieving location photos from location with id = " + retLocID);
        ExposurePhoto[] locPhotos = man.getLocationPhotos(retLocID);
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
        System.out.println("Removing user with id = " + 35);
        boolean remResGood = man.removeUser(35);
        if (!remResGood) { throw new AssertionError(); }
        System.out.println("User removed");

        System.out.println("Thanks for using the DatabaseManager Demo!");
        System.exit(0);
    }

    private void displayLocation(DatabaseManager.WebLocation loc) {
        System.out.println(loc.getName());
        System.out.println(loc.getDesc());
        System.out.println();
        System.out.println("\tLocation ID: " + loc.getID());
        System.out.println("\tlatitude and longitude: " + loc.getLat() + ", " + loc.getLon());
        System.out.println("\tRating: " + (loc.getTotalRating() / loc.getNumOfRatings()));
        System.out.println();
    }

    private void displayLocation(ExposureLocation loc) {
        System.out.println(loc.getName());
        System.out.println(loc.getDesc());
        System.out.println();
        System.out.println("\tLocation ID: " + loc.getID());
        System.out.println("\tlatitude and longitude: " + loc.getLat() + ", " + loc.getLon());
        System.out.println("\tRating: " + (loc.getTotalRating() / loc.getNumOfRatings()));
        System.out.println("\tComments: ");
        for (Comment comment: loc.getComments()) {
            System.out.println("\t\tAuthor: " + comment.getAuthorID());
            System.out.println("\t\tContent: " + comment.getContent());
            System.out.println("\t\tID: " + comment.getId());
        }
        System.out.println("\tCategories: ");
        for (Category category: loc.getCategories()) {
            System.out.println("\t\tID: " + category.getId());
            System.out.println("\t\tContent: " + category.getContent());
        }
        System.out.println();
    }

    private void displayUser(ExposureUser user) {
        System.out.println("\tUser ID: " + user.getID());
        System.out.println("\tUsername: " + user.getUsername());
        System.out.println("\tAbout me:");
        System.out.println("\t" + user.getAboutMe());
        System.out.println();
    }
}
