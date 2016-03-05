package io.github.getExposure.database;

import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;

import java.io.File;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

/**
 * Example class that demonstrates the use of DatabaseManager
 */
public class DataDriverTest2 extends FragmentActivity {

    private static DatabaseManager man;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        run();
    }

    private void run() {

        ///////////////////////////////////// INITIALIZE VARIABLES NEEDED //////////////////////////////////////
        // Specify how many locations we want to create
        int numberOfLocations = 3;
        // Specify the user id we will use
        long userID = 777777777;
        // Specify which photos to use
        String[] photoUrls = new String[numberOfLocations];
        photoUrls[0] = "https://static.pexels.com/photos/11434/Life-of-Pix-free-stock-photos-sunset-sea-light-mikewilson.jpeg";
        photoUrls[1] = "https://static.pexels.com/photos/6550/nature-sky-sunset-man.jpeg";
        photoUrls[2] = "https://helpx.adobe.com/content/dam/help/en/photoshop/how-to/combine-photos-psmix_1408x792.jpg";

        boolean insertSucceeded = false;
        long[] locationIDs = new long[numberOfLocations];
        long[] commentIDs = new long[numberOfLocations];
        long[] photoIDs = new long[numberOfLocations];

        ExposureUser newUser = null;
        Comment[] comments = new Comment[numberOfLocations];
        Category[] categories = new Category[numberOfLocations];
        ExposureLocation[] locations = new ExposureLocation[numberOfLocations];
        ExposurePhoto[] photos = new ExposurePhoto[numberOfLocations];



        System.out.println("-----------------");
        System.out.println("Welcome to the DatabaseManager");
        System.out.println("This DataDriver tests all Insert Functions");
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

        System.out.println();
        System.out.println("----------------- Initializing User Objects -----------------");
        System.out.println();

        System.out.print("Creating User Object... ");
        newUser = new ExposureUser(userID, "DataDriverInsertTestUserName", "DataDriverInsertTestSrcLink", "DataDriverInsertTestAbout");
        System.out.println("\t\tCOMPLETE");
        displayUser(newUser);

        System.out.println();
        System.out.println("----------------- Initializing Location Objects -----------------");
        System.out.println();

        System.out.print("Creating Location Objects... ");

        // Initialize Location Objects
        for (int i = 0; i < numberOfLocations; i++) {
            locations[i] = new ExposureLocation(i, i, i, i, "DataDriverTestInsertLocationName", "DataDriverTestInsertDescription", new HashSet<Category>(), new ArrayList<Comment>());
        }
        System.out.println("\t\tComplete");
        System.out.println("## Note Location ID is set to -1 ##");
        for (int i = 0; i < locations.length; i++) {
            System.out.print("Location_" + i + ": ");
            displayLocation(locations[i]);
        }

        System.out.println();
        System.out.println("----------------- Initializing Comment Objects -----------------");
        System.out.println();

        System.out.print("Creating Comment Objects... ");

        for (int i = 0; i < numberOfLocations; i++) {
            comments[i] = new Comment(userID, -1, "TestInsertCommentAuthor" + i, "DataDriverTestInsertComment" + i, new Date(i), new Time(i));
        }
        System.out.println("\t\tComplete");
        System.out.println("## Note Comment ID and Location ID are set to -1 ##");
        for (int i = 0; i < comments.length; i++) {
            System.out.print("Comment_" + i + ": ");
            displayComment(comments[i]);
        }

        System.out.println();
        System.out.println("----------------- Initializing Category Objects -----------------");
        System.out.println();

        System.out.print("Creating Category Objects... ");

        for (int i = 0; i < categories.length; i++) {
            categories[i] = new Category(i + 1);
        }
        System.out.println("\t\tComplete");
        System.out.println("## Note Category ID is set to -1 ##");
        for (int i = 0; i < comments.length; i++) {
            System.out.print("Category_" + i + ": ");
            displayCategory(categories[i]);
        }

        System.out.println();
        System.out.println("----------------- Initializing Photo Objects -----------------");
        System.out.println();

        System.out.println("CANNOT BE DONE UNTIL WE OBTAIN LOCATION ID INFORMATION");
        System.out.println("############### THIS WILL BE DONE LATER ##############");

        System.out.println();
        System.out.println("#######################################################################");
        System.out.println("----------------- Testing DatabaseManager Insert Methods -----------------");
        System.out.println("#######################################################################");
        System.out.println();

        System.out.println();
        System.out.println("----------------- Testing Insert User -----------------");
        System.out.println();

        insertSucceeded = man.insert(newUser);
        if (!insertSucceeded) {
            System.out.println("###################### Error: Insert User Returned False ###################### ");
            System.out.println("Please Confirm that the User with ID: " + userID + " does not already exist in the Database");
            System.out.println("###############################################################################");
            System.exit(1);
        }
        System.out.println("----------------- Done Test Insert User -----------------");


        System.out.println();
        System.out.println("----------------- Testing Insert Location -----------------");
        System.out.println();

        for (int i = 0; i < numberOfLocations; i++) {
            locationIDs[i] = man.insert(locations[i]);
            if (locationIDs[i] == -1) {
                System.out.println("###################### Error: Insert Location Returned -1 ###################### ");
                System.out.println("Location has failed to be inserted to database");
                System.out.println("###############################################################################");
                System.exit(1);
            }
        }
        System.out.println();
        System.out.println("Updating Location Objects with Location IDs returned by Database");
        for (int i = 0; i < numberOfLocations; i++) {
            locations[i] = locations[i].addID(locationIDs[i]);
        }
        System.out.println("Update Location ID's COMPLETE");

        System.out.println("----------------- Done Testing Insert Locations -----------------");

        System.out.println();
        System.out.println("----------------- Testing Insert Comments -----------------");
        System.out.println();

        System.out.println("First, Updating Comment Location ID's with Location IDs returned by Database");
        for (int i = 0; i < numberOfLocations; i++) {
            comments[i] = new Comment(comments[i].getId(),comments[i].getAuthorID(),
                    locationIDs[i],comments[i].getUsername(),comments[i].getContent(),comments[i].getDate(),comments[i].getTime());
        }
        System.out.println("Update Comment Location ID's COMPLETE");

        for (int i = 0; i < numberOfLocations; i++) {
            commentIDs[i] = man.insert(comments[i]);
            if (commentIDs[i] == -1) {
                System.out.println("###################### Error: Insert Comment Returned -1 ###################### ");
                System.out.println("Comment has failed to be inserted to database");
                System.out.println("###############################################################################");
                System.exit(1);
            }
        }
        System.out.println();
        System.out.println("Updating Comment Objects with Comment IDs returned by Database");
        for (int i = 0; i < numberOfLocations; i++) {
            comments[i] = comments[i].addID(commentIDs[i]);
        }
        System.out.println("Update Comment ID's COMPLETE");
        System.out.println("----------------- Done Testing Insert Comments -----------------");

        System.out.println();
        System.out.println("----------------- Testing Insert Categories -----------------");
        System.out.println();

        System.out.println("First, Updating Category Location ID's with Location IDs returned by Database");
        for (int i = 0; i < numberOfLocations; i++) {
            categories[i] = new Category(locationIDs[i], categories[i].getId());
        }
        System.out.println("Update Category ID's COMPLETE");

        for (int i = 0; i < numberOfLocations; i++) {
            insertSucceeded = man.insert(categories[i]);
            if (!insertSucceeded) {
                System.out.println("###################### Error: Insert Category Returned False ###################### ");
                System.out.println("Category has failed to be inserted to database");
                System.out.println("###############################################################################");
                System.exit(1);
            }
        }
        System.out.println("----------------- Done Testing Insert Categories -----------------");

        System.out.println();
        System.out.println("Creating Photo Objects... ");
        System.out.println();

        System.out.print("Downloading Images...");
        File[] files = new File[numberOfLocations];
        for (int i = 0; i < 3; i++) {
            ExposurePhoto dummy = new ExposurePhoto(i,i,i,photoUrls[i],null,null,null);
            dummy = dummy.downloadPhoto(this);
            files[i] = dummy.getFile();
        }
        System.out.println("\t\tComplete");
        System.out.println();

        for (int i = 0; i < numberOfLocations; i++) {
            photos[i] = new ExposurePhoto(userID,locationIDs[i],"THIS_SRC_SHOULD_BE_UPDATED_BY_WEBAPP",new Date(i),new Time(i), files[i]);
        }
        System.out.println("\t\tComplete");
        System.out.println("## Note Src_Link is initially a Default String, but will be updated when inserted to DB ##");
        for (int i = 0; i < photos.length; i++) {
            System.out.print("Photo_" + i + ": ");
            displayPhoto(photos[i]);
        }
        System.out.println("Creating Photo Objects... " + "\t\tComplete");

        System.out.println();
        System.out.println("----------------- Testing Insert Photos -----------------");
        System.out.println();

        for (int i = 0; i < numberOfLocations; i++) {
            photoIDs[i] = man.insert(photos[i]);
            if (photoIDs[i] == -1) {
                System.out.println("###################### Error: Insert Photo Returned -1 ###################### ");
                System.out.println("Photo has failed to be inserted to database");
                System.out.println("###############################################################################");
                System.exit(1);
            }
        }
        System.out.println();
        System.out.println("Updating Photo Objects with Photo IDs returned by Database");
        for (int i = 0; i < numberOfLocations; i++) {
            photos[i] = photos[i].addID(photoIDs[i]);
        }
        System.out.println("Update Photo ID's COMPLETE");
        System.out.println("----------------- Done Testing Insert Photo -----------------");

        System.out.println();
        System.out.println("----------------- Outputting Final State of all Objects -----------------");
        System.out.println();

        displayUser(newUser);
        System.out.println();

        for (int i = 0; i < numberOfLocations; i++) {
            System.out.print("Location_" + i + ": ");
            displayLocation(locations[i]);
        }
        System.out.println();

        for (int i = 0; i < numberOfLocations; i++) {
            System.out.print("Comment_" + i + ": ");
            displayComment(comments[i]);
        }
        System.out.println();

        for (int i = 0; i < numberOfLocations; i++) {
            System.out.print("Category_" + i + ": ");
            displayCategory(categories[i]);
        }
        System.out.println();

        for (int i = 0; i < numberOfLocations; i++) {
            System.out.print("Photo_" + i + ": ");
            displayPhoto(photos[i]);
        }
        System.out.println();

        System.out.println();
        System.out.println("----------------- Outputting Final State of all Objects Complete -----------------");
        System.out.println();



        System.out.println();
        System.out.println("#######################################################################");
        System.out.println("----------------- Testing DatabaseManager Get Methods -----------------");
        System.out.println("#######################################################################");
        System.out.println();

        System.out.println();
        System.out.println("----------------- Testing Get User -----------------");
        System.out.println();

        System.out.println("User ID to get: \t" + userID);
        ExposureUser getUser = man.getUser(userID);

        System.out.print("Getting User.... \t");
        if (getUser == null) {
            System.out.println();
            System.out.println("###################################################");
            System.out.println("ERROR: USER RETURNED NULL WHEN GETTING USER WITH ID: " + userID);
            System.out.println("PLEASE CHECK THAT USER EXISTS");
            System.out.println("###################################################");
            System.exit(1);
        } else if (!equals(newUser, getUser)) {
            System.out.println();
            System.out.println("###################################################");
            System.out.println("ERROR: USER RETRIEVED DOES NOT MATCH ORIGINAL USER");
            System.out.println("###################################################");
            System.exit(1);
        } else {
            System.out.println("SUCCESS!");
            displayUser(getUser);
        }
        System.out.println();
        System.out.println("----------------- Testing Get User Complete -----------------");
        System.out.println();

        System.out.println();
        System.out.println("----------------- Testing Get Location -----------------");
        System.out.println();

        ExposureLocation[] getLocations = new ExposureLocation[numberOfLocations];

        for (int i = 0; i < numberOfLocations; i++) {
            getLocations[i] = man.getLocation(locationIDs[i]);

            if (getLocations[i] == null) {
                System.out.println();
                System.out.println("###################################################");
                System.out.println("ERROR: LOCATION RETURNED NULL WHEN GETTING LOCATION WITH ID: " + locationIDs[i]);
                System.out.println("PLEASE CHECK THAT LOCATION EXISTS");
                System.out.println("###################################################");
                System.exit(1);
            }
            /*
            } else if (!equals(getLocations[i],locations[i])) {
                System.out.println();
                System.out.println("###################################################");
                System.out.println("ERROR: LOCATION RETRIEVED DOES NOT MATCH ORIGINAL LOCATION");
                System.out.println("###################################################");
                System.exit(1);
            }

            */
        }
        System.out.println("SUCCESS!");
        for (int i = 0; i < numberOfLocations; i++) {
            displayLocation(getLocations[i]);
        }

        System.out.println();
        System.out.println("----------------- Testing Get Location Complete -----------------");
        System.out.println();



        System.out.println();
        System.out.println("----------------- Testing Get User Photos -----------------");
        System.out.println();

        ExposurePhoto[] getUserPhotos = man.getUserPhotos(userID);

        if (getUserPhotos == null) {
            System.out.println();
            System.out.println("###################################################");
            System.out.println("ERROR: GET USER PHOTOS RETURNED NULL WHEN GETTING USER ID: " + userID);
            System.out.println("PLEASE CHECK THAT USER EXISTS");
            System.out.println("###################################################");
            System.exit(1);
        }
        if (getUserPhotos.length != 3) {
            System.out.println();
            System.out.println("###################################################");
            System.out.println("ERROR: EXPECTED 3 PHOTOS BUT ONLY RECEIVED: " + getUserPhotos.length);
            System.out.println("###################################################");
            System.exit(1);
        }
        /*
        } else if (!equals(getComments[i],comments[i])) {
            System.out.println();
            System.out.println("###################################################");
            System.out.println("ERROR: COMMENT RETRIEVED DOES NOT MATCH ORIGINAL COMMENT");
            System.out.println("###################################################");
            System.exit(1);
        }

            */
        System.out.println("SUCCESS!");
        for (int i = 0; i < getUserPhotos.length; i++) {
            displayPhoto(getUserPhotos[i]);
        }

        System.out.println();
        System.out.println("----------------- Testing Get User Photos Complete -----------------");
        System.out.println();



        System.out.println();
        System.out.println("----------------- Testing Get Location Photos -----------------");
        System.out.println();

        ExposurePhoto[][] getLocationPhotos = new ExposurePhoto[numberOfLocations][];

        for (int i = 0; i < numberOfLocations; i++) {
            getLocationPhotos[i] = man.getLocationPhotos(locationIDs[i]);

            if (getLocationPhotos[i] == null) {
                System.out.println();
                System.out.println("###################################################");
                System.out.println("ERROR: GET LOCATION PHOTOS RETURNED NULL WHEN GETTING LOCATION ID: " + locationIDs[i]);
                System.out.println("PLEASE CHECK THAT LOCATION EXISTS");
                System.out.println("###################################################");
                System.exit(1);
            }
            if (getLocationPhotos[i].length != 1) {
                System.out.println();
                System.out.println("###################################################");
                System.out.println("ERROR: EXPECTED 1 PHOTO BUT ONLY RECEIVED: " + getLocationPhotos[i].length);
                System.out.println("###################################################");
                System.exit(1);
            }
            /*
            } else if (!equals(getComments[i],comments[i])) {
                System.out.println();
                System.out.println("###################################################");
                System.out.println("ERROR: COMMENT RETRIEVED DOES NOT MATCH ORIGINAL COMMENT");
                System.out.println("###################################################");
                System.exit(1);
            }
            */
        }
        System.out.println("SUCCESS!");
        for (int i = 0; i < getLocationPhotos.length; i++) {
            displayPhoto(getLocationPhotos[i][0]);
        }

        System.out.println();
        System.out.println("----------------- Testing Get Location Photos Complete -----------------");
        System.out.println();


        System.out.println();
        System.out.println("----------------- Testing Get Locationn In Range -----------------");
        System.out.println();

        ExposureLocation[][] getLocationInRange = new ExposureLocation[numberOfLocations][];

        for (int i = 0; i < numberOfLocations; i++) {
            getLocationInRange[i] = man.getLocationsInRange((float)-0.5,(float)(i+0.5),(float)-0.5,(float)(i+0.5));

            if (getLocationInRange[i] == null) {
                System.out.println();
                System.out.println("###################################################");
                System.out.println("ERROR: GET LOCATIONS IN RANGE RETURNED NULL WHEN GETTING COORDIANATES: "
                        + "-0.5, -0.5, " + (i+0.5) + ", " + (i+0.5));
                System.out.println("PLEASE CHECK THAT LOCATION WITH COORDINATES EXISTS");
                System.out.println("###################################################");
                System.exit(1);
            }
            if (getLocationInRange[i].length != i + 1) {
                System.out.println();
                System.out.println("###################################################");
                System.out.println("ERROR: EXPECTED " + (i+1) + " PHOTOS BUT ONLY RECEIVED: " + getLocationInRange[i].length);
                System.out.println("###################################################");
                System.exit(1);
            }
            /*
            } else if (!equals(getComments[i],comments[i])) {
                System.out.println();
                System.out.println("###################################################");
                System.out.println("ERROR: COMMENT RETRIEVED DOES NOT MATCH ORIGINAL COMMENT");
                System.out.println("###################################################");
                System.exit(1);
            }
            */
        }
        System.out.println("SUCCESS!");
        for (int i = 0; i < getLocationPhotos.length; i++) {
            for (int j = 0; j <= i; j++)
                displayLocation(getLocationInRange[i][j]);
        }

        System.out.println();
        System.out.println("----------------- Testing Get Locations In Range Complete -----------------");
        System.out.println();





        System.out.println();
        System.out.println("##########################################################################");
        System.out.println("----------------- Testing DatabaseManager Remove Methods -----------------");
        System.out.println("##########################################################################");
        System.out.println();

        System.out.println();
        System.out.println("----------------- Testing Remove Photo -----------------");
        System.out.println();

        boolean removed = false;

        for (int i = 0; i < photoIDs.length; i++) {
            System.out.print("Removing Photo ID: " + photoIDs[i] + "... \t");
            removed = man.removePhoto(photoIDs[i]);
            if (!removed) {
                System.out.println();
                System.out.println("###################################################");
                System.out.println("ERROR: REMOVE PHOTO RETURNED FALSE WHEN REMOVING PHOTO WITH ID: " + photoIDs[i]);
                System.out.println("PLEASE CHECK THAT PHOTO EXISTS");
                System.out.println("###################################################");
                System.exit(1);
            }
            System.out.println("SUCCESS!");
        }

        System.out.println();
        System.out.println("----------------- Testing Remove Photo Complete -----------------");
        System.out.println();


        System.out.println();
        System.out.println("----------------- Testing Remove User -----------------");
        System.out.println();

        removed = false;

        System.out.print("Removing User with ID: " + userID + "... \t");
        removed = man.removeUser(userID);
        if (!removed) {
            System.out.println();
            System.out.println("###################################################");
            System.out.println("ERROR: REMOVE USER RETURNED FALSE WHEN REMOVING USER WITH ID: " + userID);
            System.out.println("PLEASE CHECK THAT USER EXISTS");
            System.out.println("###################################################");
            System.exit(1);
        }
        System.out.print("SUCCESS!");

        System.out.println();
        System.out.println("----------------- Testing Remove User Complete -----------------");
        System.out.println();




        System.out.println("----------------- Thank you for running DataDriverTestInsert -----------------");
        System.out.println("Goodbye!");
        System.exit(0);

    }

    private void displayLocation(ExposureLocation loc) {
        System.out.println("Displaying Location Contents:");

        System.out.println("\tLocation ID: \t" + loc.getID());
        System.out.println("\tLatitude: \t" + loc.getLat());
        System.out.println("\tLongitude: \t" + loc.getLon());
        System.out.println("\tTotal Rating: \t" + loc.getTotalRating());
        System.out.println("\tNumber of Ratings: \t" + loc.getNumOfRatings());
        System.out.println("\tName: \t" + loc.getName());
        System.out.println("\tDescription: \t" + loc.getDesc());
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
    }

    private void displayUser(ExposureUser user) {
        System.out.println("Displaying User Contents:");
        System.out.println("\tUser ID: \t" + user.getID());
        System.out.println("\tUsername: \t" + user.getUsername());
        System.out.println("\tSrc_Link: \t" + user.getLink());
        System.out.println("\tAbout me: \t" + user.getAboutMe());
    }

    private void displayComment(Comment comment) {
        System.out.println("Displaying Comment Contents:");
        System.out.println("\tComment ID: \t" + comment.getId());
        System.out.println("\tAuthor ID: \t" + comment.getAuthorID());
        System.out.println("\tLocation ID: \t" + comment.getLocID());
        System.out.println("\tContent: \t" + comment.getContent());
        System.out.println("\tDate: \t" + comment.getDate());
        System.out.println("\tTime: \t" + comment.getTime());
    }

    private void displayCategory(Category category) {
        System.out.println("Displaying Category Contents:");
        System.out.println("\tCategory ID: \t" + category.getId());
        System.out.println("\tLocation ID: \t" + category.getLocID());
        System.out.println("\tContent: \t" + category.getContent());
    }

    private void displayPhoto(ExposurePhoto photo) {
        System.out.println("Displaying Photo Contents:");
        System.out.println("\tPhoto ID: \t" + photo.getID());
        System.out.println("\tAutho ID: \t" + photo.getAuthorID());
        System.out.println("\tLocation ID: \t" + photo.getLocID());
        System.out.println("\tSrc_Link: \t" + photo.getSource());
        System.out.println("\tDate: \t" + photo.getDate());
        System.out.println("\tTime: \t" + photo.getTime());
    }

    private boolean equals(ExposureUser first, ExposureUser second) {

        if (first.getID() != second.getID()) {
            return false;
        } else if (!first.getAboutMe().equals(second.getAboutMe())) {
            return false;
        } else if (!first.getLink().equals(second.getLink())) {
            return false;
        } else if (!first.getUsername().equals(second.getUsername())) {
            return false;
        }
        return true;
    }

    private boolean equals(ExposureLocation first, ExposureLocation second) {

        if (first.getID() != second.getID()) {
            return false;
        } else if (first.getLat() != second.getLat()) {
            return false;
        } else if (first.getLon() != second.getLon()) {
            return false;
        } else if (first.getTotalRating() != second.getTotalRating()) {
            return false;
        } else if (first.getNumOfRatings() != second.getNumOfRatings()) {
            return false;
        } else if (!first.getName().equals(second.getName())) {
            return false;
        } else if (!first.getDesc().equals(second.getDesc())) {
            return false;
        } else if (first.getComments().size() != second.getComments().size()){
            return false;
        } else if (first.getCategories().size() != second.getCategories().size()) {
            return false;
        }
        return true;
        /*
        private final long id;
        private final float lat;
        private final float lon;
        private final int totalRating;
        private final int numOfRatings;
        private final String name;
        private final String desc;
        private Category[] categories;
        private Comment[] comments;
         */
    }
    private boolean equals(Comment first, Comment second) {

        if (first.getId() != second.getId()) {
            return false;
        } else if (first.getAuthorID() != second.getAuthorID()) {
            return false;
        } else if (first.getLocID() != second.getLocID()) {
            return false;
        } else if (!first.getContent().equals(second.getContent())) {
            return false;
        } else if (!first.getDate().equals(second.getDate())) {
            return false;
        } else if (!first.getTime().equals(second.getTime())) {
            return false;
        }
        return true;
        /*
        private final long id;
        private final long authorID;
        private final long locID;
        private final String content;
        private final Date date;
        private final Time time;
         */
    }
    private boolean equals(Category first, Category second) {

        if (first.getId() != second.getId()) {
            return false;
        } else if (first.getLocID() != second.getLocID()) {
            return false;
        } else if (!first.getContent().equals(second.getContent())) {
            return false;
        }
        return true;
        /*
        private final long id;
        private final long locID;
        private final String content;
         */
    }
    private boolean equals(ExposurePhoto first, ExposurePhoto second) {

        if (first.getID() != second.getID()) {
            return false;
        } else if (first.getAuthorID() != second.getAuthorID()) {
            return false;
        } else if (first.getLocID() != second.getLocID()) {
            return false;
        } else if (!first.getSource().equals(second.getSource())) {
            return false;
        } else if (!first.getDate().equals(second.getDate())) {
            return false;
        } else if (!first.getTime().equals(second.getDate())) {
            return false;
        }
        return true;
        /*
        private final long id;
        private final long authorID;
        private final long locID;
        private final String source;
        private final Date date;
        private final Time time;
         */
    }
}
