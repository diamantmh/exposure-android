package exposure.exposure;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

/**
 * Example class that demonstrates the use of DatabaseManager
 */
public class DataDriver {

    private static DatabaseManager man;

    public static void main(String[] args ) {
        System.out.println("Instantiating DatabaseManager...");
        man = new DatabaseManager();

        System.out.println("-----------------");
        System.out.println("Welcome to Exposure Lite");
        System.out.println("-----------------");
        menu();
        interact();
        System.out.println("Thanks for using Exposure Lite!");
        System.out.println("Exiting...");
        System.exit(0);
    }

    private static void interact() {
        Scanner in = new Scanner(System.in);
        boolean quit = false;
        while(!quit) {
            System.out.println("Enter a menu option");
            String input = in.nextLine();
            input = input.toLowerCase().trim();
            if (input.equals("m")) {
                menu();
            } else if (input.equals("insert u")) {
                insertUser(in);
            } else if (input.equals("insert l")) {
                insertLocation(in);
            } else if (input.equals("get u")) {
                getUser(in);
            } else if (input.equals("get l")) {
                getLocation(in);
            } else if (input.equals("q")) {
                quit = true;
            } else {
                System.out.println("Unrecognized command. Try 'm' to see valid commands.");
            }
        }
    }

    private static void menu() {
        System.out.println("m - display menu");
        System.out.println("insert u - add a new user");
        System.out.println("insert l - add a new location");
        System.out.println("get u - retrieve a user");
        System.out.println("get l - retrieve a location");
        System.out.println("q - quit");
        System.out.println();
    }

    private static void insertUser(Scanner in) {
        System.out.println("Enter the username of this new user");
        String username = in.nextLine();

        System.out.println("Enter a link to the profile picture of this user");
        String link = in.nextLine();

        System.out.println("Enter a description for the \"About Me\" section of this user");
        String aboutMe = in.nextLine();

        User user = new User(username,link,aboutMe);
        long id = man.insert(user);
        System.out.println("New user has been entered into the database with ID, " + id);
    }

    private static void insertLocation(Scanner in) {
        System.out.println("Enter the latitude: ");
        float lat = Float.parseFloat(in.nextLine());

        System.out.println("Enter the longitude: ");
        float lon = Float.parseFloat(in.nextLine());

        System.out.println("Enter the total rating: ");
        int totalRating = Integer.getInteger(in.nextLine());

        System.out.println("Enter the total number of reviews: ");
        int numOfRatings = Integer.getInteger(in.nextLine());

        System.out.println("Enter the name: ");
        String name = in.nextLine();

        System.out.println("Enter the description: ");
        String desc = in.nextLine();

        Set<Category> cats = new HashSet<>();
        System.out.println("Is this location a walking location? (type y if so?)");
        if (in.nextLine().toLowerCase().trim().equals("y")) {
            cats.add(new Category(Category.WALKING_ID));
        }
        System.out.println("Is this location a driving location?");
        if (in.nextLine().toLowerCase().trim().equals("y")) {
            cats.add(new Category(Category.DRIVING_ID));
        }
        System.out.println("Is this location a summer location?");
        if (in.nextLine().toLowerCase().trim().equals("y")) {
            cats.add(new Category(Category.SUMMER_ID));
        }
        System.out.println("Is this location a winter location?");
        if (in.nextLine().toLowerCase().trim().equals("y")) {
            cats.add(new Category(Category.WINTER_ID));
        }

        List<Comment> comments = new ArrayList<>();
        System.out.println("Enter a first comment, or press return to create this location.");
        String comment = in.nextLine();
        while (!comment.equals("")) {
            comments.add(new Comment(-1,-1,comment)); // here we would enter the ID of the author and location
            System.out.println("Enter another comment, or press return to create this location.");
        }

        System.out.println();
        Location loc = new Location(lat, lon, totalRating, numOfRatings, name, desc, cats, comments);
        long id = man.insert(loc);
        System.out.println("New location has been entered into the database with ID, " + id);
    }

    private static void getUser(Scanner in) {
        System.out.println("What is the ID of the user you want to look up?");
        long id = Long.parseLong(in.nextLine());
        User user = man.getUser(id);
        if (user == null) {
            System.out.println("That is not a valid user ID. Try \"insert u\" to add a user to the database.");
        } else {
            displayUser(user);
        }
    }

    private static void getLocation(Scanner in) {
        System.out.println("What is the ID of the location you want to look up?");
        long id = Long.parseLong(in.nextLine());
        Location loc = man.getLocation(id);
        if (loc == null) {
            System.out.println("That is not a valid location ID. Try \"insert l\" to add a location to the database.");
        } else {
            displayLocation(loc);
        }

    }

    private static void displayLocation(Location loc) {
        System.out.println(loc.getName());
        System.out.println(loc.getDesc());
        if (!loc.getCategories().isEmpty()) {
            System.out.println("Categories: ");
            for (Category cat : loc.getCategories()) {
                System.out.print(cat.getContent() + " ");
            }
        }
        System.out.println();
        System.out.println("Location ID: " + loc.getID());
        System.out.println("latitude and longitude: " + loc.getLat() + ", " + loc.getLon());
        System.out.println("Rating: " + loc.getRating());
        if (!loc.getComments().isEmpty()) {
            System.out.println("Comments: ");
            for (Comment com : loc.getComments()) {
                System.out.println(com.getContent() + " ");
            }
        }
        System.out.println();
    }

    private static void displayUser(User user) {
        System.out.println("User ID: " + user.getID());
        System.out.println("Username: " + user.getUsername());
        System.out.println("About me:");
        System.out.println(user.getAboutMe());
        System.out.println();
    }
}
