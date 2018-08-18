package Shared.Server.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import Shared.Server.Model.Person;
import Shared.Server.Model.SuperModel;

public class PersonDAO extends Database {
    /**
     * creates a new person, returns false if creation was unsuccessful.
     * @param person
     * @return
     */
    public boolean createPerson(Person person, Connection conn) throws DatabaseException {
        boolean isCreated = false;
        PreparedStatement preparedStatement = null;

        try {
            try {
                CheckIfExists(person, conn);
                String sql = "insert into People(personID, descendant, firstName, lastName, gender, father, mother, spouse) values (?, ?, ?, ?, ?, ?, ?, ?);";
                preparedStatement = conn.prepareStatement(sql);
                preparedStatement.setString(1, person.getPersonID());
                preparedStatement.setString(2, person.getDescendant());
                preparedStatement.setString(3, person.getFirstName());
                preparedStatement.setString(4, person.getLastName());
                preparedStatement.setString(5, person.getGender());
                preparedStatement.setString(6, person.getFather());
                preparedStatement.setString(7, person.getMother());
                preparedStatement.setString(8, person.getSpouse());
                preparedStatement.addBatch();
                preparedStatement.executeBatch();
                isCreated = true;
            }
            finally {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw new DatabaseException("Internal Server Error.", e);
        }

        return isCreated;
    }

    private void CheckIfExists(Person person, Connection conn) throws DatabaseException {
        try {
            this.readPerson(person.getPersonID(), conn);
        } catch (Exception e) {
            return;
        }
        throw new DatabaseException("Person already exists.", new Exception());
    }


    public Person readPerson(String personID, Connection conn) throws DatabaseException{
        PreparedStatement stmt = null;
        ResultSet resultSet = null;
        Person person = null;

        try {
            try {
                String sql = "SELECT * FROM People WHERE personID = \'" + personID + "\';";
                stmt = conn.prepareStatement(sql);
                resultSet = stmt.executeQuery();
                if (resultSet.next()) {
                    person = new Person(resultSet.getString(1), resultSet.getString(2), resultSet.getString(3), resultSet.getString(4),
                            resultSet.getString(5), resultSet.getString(6), resultSet.getString(7), resultSet.getString(8));
                }
                else {
                    throw new DatabaseException("Person does not exist.", new Exception());
                }
            }
            finally {
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
        return person;
    }

    /**
     *  Reads the requested people associated with the authToken given in the request headers.
     * @param userName
     * @return
     */
    public ArrayList<SuperModel> readPeople(String userName, Connection conn) throws DatabaseException {
        PreparedStatement stmt = null;
        ResultSet resultSet = null;
        Person person = null;
        ArrayList<SuperModel> array = new ArrayList<SuperModel>();

        try {
            try {
                String sql = "SELECT * FROM People WHERE descendant = \'" + userName + "\';";
                stmt = conn.prepareStatement(sql);
                resultSet = stmt.executeQuery();
                if (resultSet.next()) {  //TODO check this still works with do while
                    do {
                        person = new Person(resultSet.getString(1), resultSet.getString(2), resultSet.getString(3), resultSet.getString(4),
                                resultSet.getString(5), resultSet.getString(6), resultSet.getString(7), resultSet.getString(8));
                        array.add(person);
                    } while (resultSet.next());
                }
                else {
                    throw new DatabaseException("No People related to this userName.", new Exception());
                }
            }
            finally {
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
        return array;
    }

    /**
     *  Deletes the requested person.
     *
     * @return
     */
    public boolean deleteUserInfo(String descendant, Connection conn) throws DatabaseException {
        boolean deleted = false;

        try {
            PreparedStatement stmt = null;
            try {
                String sql = "DELETE FROM People WHERE descendant = \'" + descendant + "\';";
                stmt = conn.prepareStatement(sql);
                stmt.executeUpdate();
               // int result[] = stmt.executeBatch();
                deleted = true;
            }
            finally {
                if (stmt != null) {
                    stmt.close();
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("delete users failed", e);
        }
        return deleted;
    }
}
