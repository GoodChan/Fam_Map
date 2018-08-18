package Shared.Server.DAO;

import org.junit.*;

import Shared.Server.Model.Event;

public class DatabaseTest {
    Database db = new AuthTokenDAO();

    @Before
    public void setUp() {
        try {
            db.openConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @After
    public void tearDown() {
        try {
            db.closeConnection(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void deleteTables() {
        boolean deleted = false;
        String errorMessage = "";

        try {
            Event e = new Event("aniomorphs", "cactus", "86743", "010101",
                    "Amehrica", "Phenix", "marriage", "0000");
            new EventDAO().createEvent(e,db.getConn());
            db.closeConnection(true);
            db.openConnection();
            db.deleteTables(db.getConn());
            new EventDAO().readEvents("aniomorphs", db.getConn());
        } catch (Database.DatabaseException e) {
            deleted = true;
            errorMessage = e.getMessage();
            e.printStackTrace();
        }
        assert deleted;
        assert errorMessage.equals("No events to read for user.");
    }

    @Test
    public void dropTables() {
        boolean tablesDropped = false;

        try {
            db.dropTables();
            Event e = new Event("aniomorphs", "cactus", "86743", "010101",
                    "Amehrica", "Phenix", "marriage", "0000");
            new EventDAO().createEvent(e,db.getConn());
            db.createTables();
        } catch (Exception e) {
            e.printStackTrace();
            tablesDropped = true;
        }
        assert tablesDropped;
    }

    @Test
    public void createTables() {
        try {
            db.dropTables();
            db.createTables();
            Event e = new Event("aniomorphs", "cactus", "86743", "010101",
                    "Amehrica", "Phenix", "marriage", "0000");
            new EventDAO().createEvent(e,db.getConn());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}