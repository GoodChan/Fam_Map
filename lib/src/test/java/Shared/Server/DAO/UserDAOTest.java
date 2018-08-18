package Shared.Server.DAO;

import org.junit.*;
import Shared.Server.Model.User;

import static org.junit.Assert.*;

public class UserDAOTest {
    User testUserGender = new User("asdf", "lksdfh", "kjhsg;", ";kjhglkah", ";kjhsksl", "kjhflk", ";kjshfl");
    User testUser = new User("userSauce", "comma", "jettison@j.com;", "upper", "Cammel", "f", "person-ID=1239084");
    UserDAO testUserDAO = new UserDAO();
    Database db = new Database();


    @Test
    public void createUserPositive() {
        User u = null;
        try {
            db.deleteTables(db.getConn());
            testUserDAO.createUser(testUser, db.getConn());
            u = testUserDAO.readUser(testUser.getUserName(), testUser.getPassword(), db.getConn());
        } catch (Exception e) {
            e.printStackTrace();
        }
        assert (testUser.getGender().equals(u.getGender()));
        assert (testUser.getPersonID().equals(u.getPersonID()));
        assert (testUser.getLastName().equals(u.getLastName()));
        assert (testUser.getFirstName().equals(u.getFirstName()));
        assert (testUser.getEmail().equals(u.getEmail()));
        assert (testUser.getPassword().equals(u.getPassword()));
        assert (testUser.getUserName().equals(u.getUserName()));
    }

    @Test
    public void createUserGenderEdgeCase() {
        //checks that create user rejects gender input that is not m or f
        Boolean exceptionThrown = false;
        String message = "";
        try {
            db.deleteTables(db.getConn());
            testUserDAO.createUser(testUserGender, db.getConn());
        } catch (Exception e) {
            exceptionThrown = true;
            message = e.getMessage();
        }
        assert exceptionThrown;
        assert message.equals("Gender must be \"M\", \"m\" or \"F\", \"f\".");
    }

    @Test
    public void createUserTwiceCreated() {
        //checks that User can't be created twice, username taken
        Boolean exceptionThrown = false;
        String message = "";
        try {
            db.deleteTables(db.getConn());
            testUserDAO.createUser(testUser, db.getConn());
            testUserDAO.createUser(testUser, db.getConn());
        } catch (Exception e) {
            exceptionThrown = true;
            message = e.getMessage();
        }
        assert exceptionThrown;
        assert message.equals("User already exists.");
    }

    @Test
    public void readUserPositive() {
        User u = null;
        try {
            db.deleteTables(db.getConn());
            testUserDAO.createUser(testUser, db.getConn());
            u = testUserDAO.readUser(testUser.getUserName(), testUser.getPassword(), db.getConn());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(testUser.getUserName(), u.getUserName());
        assertEquals(testUser.getPassword(), u.getPassword());
        assertEquals(testUser.getEmail(), u.getEmail());
        assertEquals(testUser.getFirstName(), u.getFirstName());
        assertEquals(testUser.getLastName(), u.getLastName());
        assertEquals(testUser.getGender(), u.getGender());
        assertEquals(testUser.getPersonID(), u.getPersonID());
    }

    @Test
    public void readUserNegative() {
        //np user exists
        Boolean exceptionThrown = false;
        String message = "";
        try {
            db.deleteTables(db.getConn());
            testUserDAO.readUser(testUser.getUserName(), testUser.getPassword(), db.getConn());
        } catch (Exception e) {
            exceptionThrown = true;
            message = e.getMessage();
        }
        assert exceptionThrown;
        assert message.equals("No user Exists with that username and password.");
    }

    @Before
    public void setUp() {
        try {
            db.openConnection();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @After
    public void tearDown() {
        try {
            db.closeConnection(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}