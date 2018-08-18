package Shared.Server.Services;

import org.junit.*;

import Shared.Server.DAO.Database;
import Shared.Server.DAO.UserDAO;
import Shared.Server.Model.User;
import Shared.Server.Requests.LoginRequest;
import Shared.Server.Responses.MessageResponse;
import Shared.Server.Responses.UserResponse;

import static org.junit.Assert.*;

public class LoginServiceTest {
    LoginService loginService = new LoginService();
    Database db = new Database();
    User user = new User("matt", "satonamat", "MattyoMatt@sat.com", 
            "mat", "Grey", "m");
    LoginRequest loginRequest = new LoginRequest(user.getUserName(), user.getPassword());

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

    @Test
    public void loginPositive() {
        UserResponse userResponse = null;
        try {
            db.deleteTables(db.getConn());
            new UserDAO().createUser(user, db.getConn());
            db.closeConnection(true);

            //reads an event based off of eventID and authToken
            userResponse = (UserResponse) loginService.login(loginRequest);
            db.openConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(userResponse.getAuthToken());
        assertEquals(user.getPersonID(), userResponse.getPersonID());
        assertEquals(user.getUserName(), userResponse.getUserName());
    }

    @Test
    public void loginNegative() {
        //no users exist when trying to log in
        MessageResponse messageResponse = null;
        try {
            db.deleteTables(db.getConn());
            db.closeConnection(true);
            //reads an event based off of eventID and authToken
            messageResponse = (MessageResponse) loginService.login(loginRequest);
            db.openConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(messageResponse.getMessage(), "No user Exists with that username.");
    }
}