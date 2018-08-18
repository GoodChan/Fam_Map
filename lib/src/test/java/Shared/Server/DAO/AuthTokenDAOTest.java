package Shared.Server.DAO;

import org.junit.*;

import Shared.Server.Model.AuthToken;

import static org.junit.Assert.*;

public class AuthTokenDAOTest {
    AuthTokenDAO authTokenDAO = new AuthTokenDAO();
    AuthToken testAuthToken = new AuthToken("username");
    Database db = new Database();

    @Test
    public void createAuthTokenPositive() {
        try {
            try {
                db.openConnection();
                authTokenDAO.createAuthToken(testAuthToken, db.getConn());
                db.closeConnection(true);
            } catch (Database.DatabaseException e) {
                db.closeConnection(false);
                e.printStackTrace();
            }
        } catch (Database.DatabaseException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void createAuthTokenNegative() {
        //create auth token for a user that does not exist.
        boolean thrown = false;
        String message = "";

        try {
            try {
                db.openConnection();
                db.deleteTables(db.getConn()); // removes tables before attempting to add a user
                authTokenDAO.createAuthToken(testAuthToken, db.getConn());
            }
            finally {
                db.closeConnection(true);
            }
        } catch (Database.DatabaseException e) {
            e.printStackTrace();
            thrown = true;
            message = e.getMessage();
        }
        assert thrown;
        assertEquals(message, "No user Exists with that username.");
    }

    /*@Test
    public void createAuthTokenEdgeCase() {
        //check if the user already exists in the AuthToken table
        boolean thrown = false;
        String message = "";

        try {
            try {
                db.openConnection();
                db.deleteTables(db.getConn()); // removes tables before attempting to add a user
                User u = new User("Allen", "succulent", "AllenTheSucculent@succ.com", "Bud", "Allen", "m", "3014");
                new UserDAO().createUser(u, db.getConn());
                AuthToken tempAuthToken = new AuthToken(u.getUserName());
                authTokenDAO.createAuthToken(tempAuthToken, db.getConn());
                authTokenDAO.createAuthToken(tempAuthToken, db.getConn()); // will it let me add the user twice?
            }
            finally {
                db.closeConnection(true);
            }
        } catch (Database.DatabaseException e) {
            e.printStackTrace();
            thrown = true;
            message = e.getMessage();
        }
        assert thrown;
        assert message.contains("Username already has an authToken.");
    }*/

        @Test
    public void readUserFromAuthTokenPositive() {
        try {
            try {
                db.openConnection();
                db.deleteTables(db.getConn());
                authTokenDAO.createAuthToken(testAuthToken, db.getConn());
                String userName = authTokenDAO.readUserFromAuthToken("a130832c-e693-4cbc-8aca-84dcc9ad697d", db.getConn());
                assertEquals(userName, "username");
            }
            finally {
                db.closeConnection(false);
            }
        } catch (Database.DatabaseException e) {
            e.printStackTrace();
        }
    }
}