package exposure.exposure;

/**
 * Created by swammer on 2/8/2016.
 */
public class DBManagerTest {
    public static void main(String[] args) {
        DatabaseManager man = new DatabaseManager();
        User user = new User("user","link","I'm a user");

        long userID = man.insert(user);
        assert(userID != 0 && userID != 1);

    }
}
