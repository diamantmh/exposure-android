package exposure.exposure;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.SupportMapFragment;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
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
        System.out.println("Insert bogus user,");
        User retUser = new User(50, "I'm bogus", "link", "There's nothing to say about me");
        long resID = man.insert(retUser);
        System.out.println("Returned ID is: " + resID);
        assert(resID == -1);

        // test good insert user
        System.out.println();
        System.out.println("Inserting a valid user,");
        User newUser = new User("good user", "link", "I'm a good user, I hope I make it to the database!");
        displayUser(newUser);
        long userID = man.insert(newUser);
        System.out.println("Returned ID is: " + userID);
        assert(userID > 0);

        // test bad update user
        System.out.println();
        System.out.println("Updating new user with no ID (bad operation)");
        boolean badRes = man.update(new User(newUser.getUsername(), newUser.getLink(), "This won't be saved!"));
        String resultStrBad = (badRes) ? "User updated" : "User was not updated!";
        System.out.println(resultStrBad);
        assert(badRes);

        // test good update user
        System.out.println();
        System.out.println("Updating user that was just inserted with ID = " + userID);
        boolean goodRes = man.update(new User(userID, newUser.getUsername(), newUser.getLink(), "The about me section is different now!"));
        String resultStrGood = (goodRes) ? "User updated" : "User was not updated!";
        System.out.println(resultStrGood);
        assert(goodRes);

        // test bad get user
        System.out.println();
        System.out.println("Attempting to retrieve a user that doesn't exist");
        User noResultUser = man.getUser(-1);
        assert (noResultUser == null);
        System.out.println("Returned null because no user with this ID");

        // test good get user
        System.out.println();
        System.out.println("Retrieving updated user with ID = " + userID);
        User updatedUser = man.getUser(userID);
        assert(updatedUser != null);
        System.out.println("Updated user:");
        displayUser(updatedUser);

        // test bad remove user
        System.out.println();
        System.out.println("Attempting to remove a user that doesn't exist");

        // test good remove user
        System.out.println();
        System.out.println("Removing user with id = " + userID);
        boolean remResGood = man.removeUser(userID);
        assert(remResGood);
        System.out.println("User removed");

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
