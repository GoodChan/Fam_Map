package Shared.Server.Services;

import org.junit.Test;
import Shared.Server.DAO.*;
import Shared.Server.Model.User;

public class ClearServiceTest {
    ClearService clearService = new ClearService();
    Database db = new Database();
    User testUser = new User("userSauce", "comma", "jettison@j.com;", "upper", "Cammel", "f", "person-ID=1239084");


    @Test
    public void clear() {
        Boolean exceptionThrown = false;
        String message = "";
        try {
            try {
                db.openConnection();
                db.deleteTables(db.getConn());
                new UserDAO().createUser(testUser, db.getConn());
                db.closeConnection(true);
                clearService.clear();
                db.openConnection();
                new UserDAO().readUser(testUser.getUserName(), testUser.getPassword(), db.getConn());
            } catch (Exception e) {
                exceptionThrown = true;
                message = e.getMessage();
            } finally {
                db.closeConnection(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        assert exceptionThrown;
        assert message.equals("No user Exists with that username and password.");
    }
}