package Shared.Server.DAO;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.*;

import Shared.Server.Model.SuperModel;

import java.util.ArrayList;

import Shared.Server.Model.Event;

public class EventDAO extends Database {
    /**
     * creates a new event.
     * @param event
     * @return
     */
    public boolean createEvent(Event event, Connection conn) throws DatabaseException {
        boolean isCreated = false;
        PreparedStatement preparedStatement = null;

        try {
            try {
                CheckIfExists(event, conn);
                String sql = "insert into Events(eventID, descendant, personID, Latitude, Longitude, country, city, eventType, year) values (?, ?, ?, ?, ?, ?, ?, ?, ?);";
                preparedStatement = conn.prepareStatement(sql);
                preparedStatement.setString(1, event.getEventID());
                preparedStatement.setString(2, event.getDescendant());
                preparedStatement.setString(3, event.getPerson());
                preparedStatement.setString(4, event.getLatitude());
                preparedStatement.setString(5, event.getLongitude());
                preparedStatement.setString(6, event.getCountry());
                preparedStatement.setString(7, event.getCity());
                preparedStatement.setString(8, event.getEventType());
                preparedStatement.setString(9, event.getYear());
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

    private void CheckIfExists(Event event, Connection conn) throws DatabaseException {
        try {
            this.readEvent(event.getEventID(), conn);
        } catch (Exception e) {
            return;
        }
        throw new DatabaseException("Event already exists.", new Exception());
    }

    /**
     * reads the requested person from the database.
     * @param eventID
     * @return
     */
    public Event readEvent(String eventID, Connection conn) throws DatabaseException {
        PreparedStatement stmt = null;
        ResultSet resultSet = null;
        Event event = null;

        try {
            try {
                String sql = "SELECT * FROM Events WHERE eventID = \'" + eventID + "\';";
                stmt = conn.prepareStatement(sql);
                resultSet = stmt.executeQuery();
                if (resultSet.next()) {
                    event = new Event(resultSet.getString(1), resultSet.getString(2), resultSet.getString(3), resultSet.getString(4),
                            resultSet.getString(5), resultSet.getString(6), resultSet.getString(7), resultSet.getString(8), resultSet.getString(9));
                }
                else {
                    throw new DatabaseException("No event matches the eventID provided.", new Exception());
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
        return event;
    }

    public ArrayList<SuperModel> readEvents(String userName, Connection conn) throws DatabaseException {
        PreparedStatement stmt = null;
        ResultSet resultSet = null;
        Event event = null;
        ArrayList<SuperModel> array = new ArrayList<SuperModel>();

        try {
            try {
                String sql = "SELECT * FROM Events WHERE descendant = \'" + userName + "\';";
                stmt = conn.prepareStatement(sql);
                resultSet = stmt.executeQuery();
                if (resultSet.next()) { //TODO check this still works
                    do {
                        event = new Event(resultSet.getString(1), resultSet.getString(2), resultSet.getString(3), resultSet.getString(4),
                                resultSet.getString(5), resultSet.getString(6), resultSet.getString(7), resultSet.getString(8), resultSet.getString(9));
                        array.add(event);
                    } while (resultSet.next());
                }
                else {
                    throw new DatabaseException("No events to read for user.", new Exception());
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
     *d eletes an event.
     * @param userName
     * @return
     */
    public boolean deleteUserEvents(String userName, Connection conn) throws DatabaseException {
        boolean deleted = false;

        try {
            PreparedStatement stmt = null;
            try {
                String sql = "DELETE FROM Events WHERE descendant = \'" + userName + "\';";
                stmt = conn.prepareStatement(sql);
                stmt.executeUpdate();
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