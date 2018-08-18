package Shared.Server.Services;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import Shared.Server.Model.User;
import Shared.Server.DAO.Database;
import Shared.Server.DAO.UserDAO;
import Shared.Server.Requests.RegisterRequest;
import Shared.Server.Responses.*;

import static org.junit.Assert.*;

public class RegisterServiceTest {
    RegisterRequest registerRequest = new RegisterRequest("RegisterTestUserName", "passwordy", "MyProgram@programing", "loopy", "pants", "m");
    RegisterService registerService = new RegisterService();
    Database db = new Database();

    @Before
    public void setUp() {
        try {
            db.openConnection();
            db.deleteTables(db.getConn());
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
    public void registerPositive() {
        try {
            db.closeConnection(true);
            UserResponse registerServiceResponse = (UserResponse) registerService.register(registerRequest);
            db.openConnection();
            User registered = new UserDAO().readUser(registerRequest.getUserName(), db.getConn());
            assertEquals(registerServiceResponse.getUserName(), registerRequest.getUserName());
            assertEquals(registerServiceResponse.getPersonID(), registered.getPersonID());
            System.out.println(registerServiceResponse.getAuthToken());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void registerNegative() {
        //user already exists
        try {
            db.closeConnection(true);
            registerService.register(registerRequest);
            MessageResponse messageResponse = (MessageResponse) registerService.register(registerRequest);
            db.openConnection();
            assertEquals("Username already taken by another user.", messageResponse.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}