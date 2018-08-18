package Shared.Server.DAO;

import java.sql.*;

public class Database {
     private Connection conn;

     public Connection getConn() {
         return conn;
     }

    /**
     * Specifies a problem with the database.
      */
    public class DatabaseException extends Exception {
        DatabaseException(String error, Exception e) {
            super (error, e);
        }
    }

    /**
     * sets up part of the database connection.
     */
    static {
        try {
            final String driver = "org.sqlite.JDBC";
            Class.forName(driver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Opens a connection in the database.
     * @throws DatabaseException
     */
    public void openConnection() throws DatabaseException{
        String dbName = "database240.sqlite";
        String connectionURL = "jdbc:sqlite:" + dbName;

        conn = null;
        try {
            //Open a database connection
            conn = DriverManager.getConnection(connectionURL);
            //start a transaction
            conn.setAutoCommit(false);
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     *  Closes the database connection.
     * @param commit
     * @throws DatabaseException
     */
    public void closeConnection(boolean commit) throws DatabaseException{
        try {
            if (commit) {
                conn.commit();
            } else {
                conn.rollback();
            }
            conn.close();
            conn = null;
        } catch(SQLException e) {
            throw new DatabaseException("closeConnection failed", e);
        }
    }

    public void deleteTables(Connection conn) throws DatabaseException {
        try {
            Statement stmt = null;
            try {
                stmt = conn.createStatement();
                stmt.executeUpdate("delete from AuthTokens");
                stmt.executeUpdate("delete from Events");
                stmt.executeUpdate("delete from User");
                stmt.executeUpdate("delete from People");
            }
            finally {
                stmt.close();
            }
        } catch (SQLException e) {
            throw new DatabaseException("delete tables failed", e);
        }
    }

    public void dropTables() throws DatabaseException {
        try {
            Statement stmt = null;
            stmt = conn.createStatement();
            stmt.executeUpdate("DROP TABLE IF EXISTS User;");
            stmt.executeUpdate("DROP TABLE IF EXISTS People;");
            stmt.executeUpdate("DROP TABLE IF EXISTS Events;");
            stmt.executeUpdate("DROP TABLE IF EXISTS AuthTokens;");
        } catch (Exception e) {
            throw new DatabaseException("Error in dropTables", e);
        }
    }

    /**
     * Creates tanels for the database.
     * @throws DatabaseException
     */
    public void createTables() throws DatabaseException {
        try {
            Statement stmt = null;
            try {
                stmt = conn.createStatement();
                stmt.executeUpdate(
                        "CREATE TABLE IF NOT EXISTS User (" +
                                "username TEXT NOT NULL," +
                                "password TEXT NOT NULL," +
                                "email TEXT NOT NULL," +
                                "firstName TEXT NOT NULL," +
                                "lastName TEXT NOT NULL," +
                                "gender TEXT NOT NULL," +
                                "personID TEXT NOT NULL," +
                                "PRIMARY KEY(username)" +
                                ");");
                stmt.close();
                //can use same statement

                Statement stmt2 = conn.createStatement();
                stmt2.executeUpdate("CREATE TABLE IF NOT EXISTS People (" +
                                " personID TEXT NOT NULL," +
                                "descendant TEXT NOT NULL," +
                                "firstName TEXT NOT NULL," +
                                "lastName TEXT NOT NULL," +
                                "gender TEXT NOT NULL," +
                                "father TEXT," +
                                "mother TEXT," +
                                "spouse TEXT," +
                                "PRIMARY KEY(personID)" +
                                ");");
                stmt2.close();

                Statement stmt3 = conn.createStatement();
                stmt3.executeUpdate( "CREATE TABLE IF NOT EXISTS `Events` (" +
                                "eventID TEXT NOT NULL," +
                                "descendant TEXT," +
                                "personID TEXT," +
                                "latitude NUMERIC," +
                                "longitude NUMERIC," +
                                "country TEXT," +
                                "city TEXT," +
                                "eventType TEXT," +
                                "year TEXT," +
                                "PRIMARY KEY(eventID)" +
                                ");" +
                        "CREATE TABLE IF NOT EXISTS `AuthTokens` (" +
                                "token TEXT NOT NULL," +
                                "userName TEXT NOT NULL," +
                                "PRIMARY KEY(token)" +
                                ")");

            } finally {
                if (stmt != null) {
                    stmt.close();
                    stmt = null;
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("createTables failed", e);
        }
    }
}
