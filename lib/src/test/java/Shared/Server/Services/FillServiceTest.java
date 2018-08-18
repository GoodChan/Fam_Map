package Shared.Server.Services;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import Shared.Server.Model.User;
import Shared.Server.Responses.*;
import Shared.Server.DAO.*;

import static org.junit.Assert.assertEquals;

public class FillServiceTest {
    Database db = new Database();
    FillService fillService = new FillService();
    User user = new User("Pokey_Spot", "ruffruff", "SpotAndPokey@dogs.com",
            "Pokey", "Spot", "f");
    MessageResponse messageResponse = new MessageResponse("initialized in fill service test");

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
    public void fill0() {
        try {
            db.deleteTables(db.getConn());
            new UserDAO().createUser(user, db.getConn());
            db.closeConnection(true);
            messageResponse = (MessageResponse) new FillService().fill(user.getUserName(), 0);
            assertEquals(messageResponse.getMessage(), "Successfully added 0 people and 0 Events to the database.");
            db.openConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void fillOne() {
        try {
            db.deleteTables(db.getConn());
            new UserDAO().createUser(user, db.getConn());
            db.closeConnection(true);
            messageResponse = (MessageResponse) new FillService().fill(user.getUserName(), 1);
            assertEquals(messageResponse.getMessage(), "Successfully added 2 people and 6 Events to the database.");
            db.openConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void Fill7() {
        try {
            db.deleteTables(db.getConn());
            new UserDAO().createUser(user, db.getConn());
            db.closeConnection(true);
            messageResponse = (MessageResponse) new FillService().fill(user.getUserName(), 7);
            assertEquals(messageResponse.getMessage(),
                    "Successfully added 254 people and 762 Events to the database.");
            db.openConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}