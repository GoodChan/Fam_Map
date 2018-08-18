package Shared.Server.DAO;

import Shared.Server.Model.AuthToken;
import Shared.Server.Model.User;

import java.sql.*;

public class AuthTokenDAO extends Database {
    /**
     * Creates an AuthToken and commits it to the database.
     * @param  authtoken auth token to commit to the database.
     * @return bool of if it was successfully committed.
     */
    public boolean createAuthToken(AuthToken authtoken, Connection conn) throws DatabaseException {
        boolean bool = false;
        PreparedStatement preparedStatement = null;
        try {
            try {
                testEdgeCases(authtoken.getUserName(), conn);
                String sql = "insert into AuthTokens(token, userName) values (?, ?);";
                preparedStatement = conn.prepareStatement(sql);
                preparedStatement.setString(1, authtoken.getAuthToken());
                preparedStatement.setString(2, authtoken.getUserName());
                preparedStatement.addBatch();
                preparedStatement.executeBatch();
                bool = true;
            }
            finally {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw new DatabaseException("SQL Exception", e);
        }
        return bool;
    }

    private void testEdgeCases(String userName, Connection conn) throws DatabaseException {
        //test if the username exists under a registered user
        User u = null;
        u = new UserDAO().readUser(userName, conn);
        if (u == null) { // if it does not exist throw an error
            throw new DatabaseException("Username does not exist.", new Exception());
        }
    }

    //used for test case above, but could be helpful in retrieving an authToken
    public String readAuthTokenFromUser(String userName, Connection conn) throws DatabaseException {
        String authtoken = "";
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            try {
                String sql = "SELECT * FROM AuthTokens WHERE userName = \'" + userName + "\';";
                preparedStatement = conn.prepareStatement(sql);
                resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    authtoken = resultSet.getString(2);
                }
                else {
                    throw new DatabaseException("AuthToken does not exist", new Exception());
                }
            }
            finally {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (resultSet != null) {
                    resultSet.close();
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw new DatabaseException("AuthToken does not exist", e);
        }
        return authtoken;
    }

    /**
     *  Reads the AuthToken from the database.
     * @param authToken the authtoken to find.
     * @return username the corresponding username found
     */
    public String readUserFromAuthToken(String authToken, Connection conn) throws DatabaseException  {
        String username = "";
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            try {
                String sql = "SELECT * FROM AuthTokens WHERE token = \'" + authToken + "\';";
                preparedStatement = conn.prepareStatement(sql);
                resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    username = resultSet.getString(2);
                }
                else {
                    throw new DatabaseException("Not logged in.", new Exception());
                }
            }
            finally {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (resultSet != null) {
                    resultSet.close();
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw new DatabaseException("Internal Server Error.", e);
        }
        return username;
    }
}
