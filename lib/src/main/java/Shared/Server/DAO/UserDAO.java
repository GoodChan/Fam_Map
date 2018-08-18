package Shared.Server.DAO;

import java.sql.PreparedStatement;
import java.sql.*;
import Shared.Server.Model.User;

public class UserDAO extends Database {

    /**
     * creates a new user.
     * @param user For user
     * @return
     */
    public void createUser(User user, Connection conn) throws DatabaseException {
        PreparedStatement preparedStatement = null;
        try {
            try {
                CheckIfExists(user, conn);
                if (user.getGender().toLowerCase().equals("f") || user.getGender().toLowerCase().equals("m")) {
                    String sql = "insert into User(username, password, email, firstName, lastName, gender, personID) "
                            + "values(\'" + user.getUserName() + "\', \'" + user.getPassword() + "\', \'" + user.getEmail() +
                            "\', \'" + user.getFirstName() + "\', \'" + user.getLastName() + "\', \'" + user.getGender() +
                            "\', \'" + user.getPersonID() + "\');";
                    preparedStatement = conn.prepareStatement(sql);
                    preparedStatement.executeUpdate();
                }
                else {
                    throw new DatabaseException("Gender must be \"M\", \"m\" or \"F\", \"f\".", new Exception());
                }
            } finally {
                if (preparedStatement != null)  {
                    preparedStatement.close();
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Internal server error.", e);
        }
    }

    private void CheckIfExists(User user, Connection conn) throws DatabaseException {
        try {
            this.readUser(user.getUserName(), user.getPassword(), conn);
        } catch (Exception e) {
            return;
        }
        throw new DatabaseException("User already exists.", new Exception());
    }

    /**
     * reads the requested user.
     * @param userName
     * @return
     */
    public User readUser(String userName, String password, Connection conn) throws DatabaseException {
        PreparedStatement stmt = null;
        ResultSet resultSet = null;
        User user = null;

        try {
            try {
                String sql = "SELECT * FROM User " +
                        "WHERE username = \'" + userName + "\' AND password = \'" + password + "\';";

                stmt = conn.prepareStatement(sql);
                resultSet = stmt.executeQuery();
                if (resultSet.next()){
                    String username = resultSet.getString(1);
                    user = new User(username, resultSet.getString(2), resultSet.getString(3), resultSet.getString(4),
                            resultSet.getString(5), resultSet.getString(6), resultSet.getString(7));
                }
                else {
                    throw new DatabaseException("No user Exists with that username and password.", new Exception());
                }
            } finally {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DatabaseException("Internal Server Error.", e);
        }
        return user;
    }

    public User readUser(String userName, Connection conn) throws DatabaseException {
        PreparedStatement stmt = null;
        ResultSet resultSet = null;
        User user = null;

        try {
            try {
                String sql = "SELECT * FROM User " +
                        "WHERE username = \'" + userName + "\';";

                stmt = conn.prepareStatement(sql);
                resultSet = stmt.executeQuery();
                if (resultSet.next()){
                    String username = resultSet.getString(1);
                    user = new User(username, resultSet.getString(2), resultSet.getString(3), resultSet.getString(4),
                            resultSet.getString(5), resultSet.getString(6), resultSet.getString(7));
                }
                else {
                    throw new DatabaseException("No user Exists with that username.", new Exception());
                }
            } finally {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DatabaseException("Internal Server Error.", e);
        }
        return user;
    }
}
