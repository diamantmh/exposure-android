package exposure.exposure;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

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
        System.out.println("Trying out some functionality...");

        // test bad insert user
        System.out.println();
        System.out.println("Inserting bogus user,");
        User bogusUser = new User(50, "I'm bogus", "link", "There's nothing to say about me");
        long resID = man.insert(bogusUser);
        System.out.println("Returned ID is: " + resID);
        if (resID != -1) { throw new AssertionError(); }

        // test good insert user
        System.out.println();
        System.out.println("Inserting a valid user,");
        User newUser = new User("good user", "link", "I'm a good user, I hope I make it to the database!");
        displayUser(newUser);
        long userID = man.insert(newUser);
        if (userID <= 0) { throw new AssertionError(); }
        System.out.println("User has been inserted");
        User retUser = newUser.addID(userID);
        displayUser(retUser);

        // test bad update user
        System.out.println();
        System.out.println("Updating new user with no ID (bad operation)");
        boolean badRes = man.update(new User(newUser.getUsername(), newUser.getLink(), "This won't be saved!"));
        if (badRes) { throw new AssertionError(); }
        System.out.println("User was not updated");

        // test good update user
        System.out.println();
        System.out.println("Updating user that was just inserted with ID = " + userID);
        boolean goodRes = man.update(new User(retUser.getID(), retUser.getUsername(), retUser.getLink(), "The about me section is different now!"));
        if (!goodRes) { throw new AssertionError(); }
        System.out.println("User updated");

        // test bad get user
        System.out.println();
        System.out.println("Attempting to retrieve a user that doesn't exist");
        User noResultUser = man.getUser(-1);
        if (noResultUser != null) { throw new AssertionError(); }
        System.out.println("Returned null because no user with this ID");

        // test good get user
        System.out.println();
        System.out.println("Retrieving updated user with ID = " + userID);
        User retrievedUser = man.getUser(userID);
        if (retrievedUser == null) { throw new AssertionError(); }
        System.out.println("Updated user:");
        displayUser(retrievedUser);

        // test bad remove user
        System.out.println();
        System.out.println("Attempting to remove a user that doesn't exist (id = -1)");
        boolean remResBad = man.removeUser(-1);
        if (remResBad) { throw new AssertionError(); }
        System.out.println("User not removed because user does not exist.");

        // test good remove user
        System.out.println();
        System.out.println("Removing user with id = " + userID);
        boolean remResGood = man.removeUser(userID);
        if (!remResGood) { throw new AssertionError(); }
        System.out.println("User removed");

        // location tests
        // test good insert location
        System.out.println();
        System.out.println("Inserting new location with no ID specified");
        Category cat = new Category(Category.WALKING_ID);
        Set<Category> cats = new HashSet<>();
        cats.add(cat);
        Location newLoc = new Location(10,10,20,4,"Quad", "There are nice trees here!", cats, new ArrayList<Comment>());
        displayLocation(newLoc);
        long retLocID = man.insert(newLoc);
        if (retLocID <= 0) { throw new AssertionError(); }
        Location retLoc = newLoc.addID(retLocID);
        System.out.println("Returned ID is: " + retLocID);

        // test good insert comment
        System.out.println();
        System.out.println("Inserting new comment with no ID specified");
        Comment newCom = new Comment(retUser.getID(), retLoc.getID(), "This is a comment.", new Date(100000), new Time(100000));
        long comID = man.insert(newCom);
        if (comID <= 0) { throw new AssertionError(); }
        Comment retCom = newCom.addID(comID);
        System.out.println("Returned ID is: " + comID);

        // test good update location
        System.out.println();
        System.out.println("Updating location that was just inserted with ID = " + retLocID);
        List<Comment> comments = new ArrayList<>();
        comments.add(retCom);
        Location updatedLocation = new Location(retLoc.getID(), retLoc.getLat(), retLoc.getLon(), retLoc.getTotalRating(),
                retLoc.getNumOfRatings(), retLoc.getName(), retLoc.getDesc(), retLoc.getCategories(), comments);
        boolean goodLocRes = man.update(updatedLocation);
        if (!goodLocRes) { throw new AssertionError(); }
        System.out.println("Location updated with a comment");

        // test good get location
        System.out.println();
        System.out.println("Retrieving updated location with ID = " + retLocID);
        Location retrievedLocation = man.getLocation(retLocID);
        if (retrievedLocation == null) { throw new AssertionError(); }
        System.out.println("Updated location:");
        displayLocation(retrievedLocation);

        // test good insert photo
        System.out.println();
        System.out.println("Inserting new photo with no ID specified");
        Photo newPhoto = new Photo(retUser.getID(),updatedLocation.getID(),"sourcelink",new Date(100000),new Time(100000));
        long photoID = man.insert(newPhoto);
        if (photoID <= 0) { throw new AssertionError(); }
        Photo retPhoto = newPhoto.addID(photoID);
        System.out.println("Returned ID is: " + photoID);

        // test good remove photo
        System.out.println();
        System.out.println("Removing user with id = " + retPhoto.getID());
        boolean removePhotoRes = man.removePhoto(retPhoto.getID());
        if (removePhotoRes) { throw new AssertionError(); }
        System.out.println("Photo removed");


        System.out.println("Thanks for using the DatabaseManager Demo!");
        System.exit(0);
    }

    private void displayLocation(Location loc) {
        System.out.println(loc.getName());
        System.out.println(loc.getDesc());
        if (!loc.getCategories().isEmpty()) {
            System.out.println("Categories: ");
            for (Category cat : loc.getCategories()) {
                System.out.print(cat.getContent() + " ");
            }
        }
        System.out.println();
        System.out.println("\tLocation ID: " + loc.getID());
        System.out.println("\tlatitude and longitude: " + loc.getLat() + ", " + loc.getLon());
        System.out.println("\tRating: " + loc.getRating());
        if (!loc.getComments().isEmpty()) {
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
