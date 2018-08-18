package Shared.Server.DAO;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import Shared.Server.Model.Event;
import Shared.Server.Model.SuperModel;

import java.util.ArrayList;


public class EventDAOTest {
    Database db = new Database();
    EventDAO eventDAO = new EventDAO();
    Event e = new Event("testID", "stairs", "test", "tesst", "test", "test", "test", "test", "test");
    Event event2 = new Event("309", "stairs", "Perry", "402", "4013", "xest", "zest", "planting", "2012");

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
    public void createEvent() {
        Event result = null;

        try {
            db.deleteTables(db.getConn());
            assert eventDAO.createEvent(e, db.getConn());
            result = eventDAO.readEvent(e.getEventID(), db.getConn());
        } catch (Exception e) {
            e.printStackTrace();
        }
        assert (result.getCity().equals(e.getCity()));
        assert (result.getCountry().equals(e.getCountry()));
        assert (result.getDescendant().equals(e.getDescendant()));
        assert (result.getEventID().equals(e.getEventID()));
        assert (result.getEventType().equals(e.getEventType()));
        assert (result.getLatitude().equals(e.getLatitude()));
        assert (result.getLongitude().equals(e.getLongitude()));
        assert (result.getPerson().equals(e.getPerson()));
        assert (result.getYear().equals(e.getYear()));
    }

    @Test
    public void createEventNegative() {
        //event already exists
        Boolean exceptionThrown = false;
        String message = "";
        try {
            db.deleteTables(db.getConn());
            assert eventDAO.createEvent(e, db.getConn());
            eventDAO.createEvent(e, db.getConn());
        } catch (Exception e) {
            exceptionThrown = true;
            message = e.getMessage();
        }
        assert exceptionThrown;
        assert message.equals("Event already exists.");
    }

    @Test
    public void readEventPositive() {
        Event result = null;
        try {
            db.deleteTables(db.getConn());
            eventDAO.createEvent(e, db.getConn());
            result = eventDAO.readEvent(e.getEventID(), db.getConn());
        } catch (Exception e) {
            e.printStackTrace();
        }
        assert (result.getCity().equals(e.getCity()));
        assert (result.getCountry().equals(e.getCountry()));
        assert (result.getDescendant().equals(e.getDescendant()));
        assert (result.getEventID().equals(e.getEventID()));
        assert (result.getEventType().equals(e.getEventType()));
        assert (result.getLatitude().equals(e.getLatitude()));
        assert (result.getLongitude().equals(e.getLongitude()));
        assert (result.getPerson().equals(e.getPerson()));
        assert (result.getYear().equals(e.getYear()));
    }

    @Test
    public void readEventNegative() {
        //event already exists
        Boolean exceptionThrown = false;
        String message = "";
        try {
            db.deleteTables(db.getConn());
            eventDAO.readEvent(e.getEventID(), db.getConn());
        } catch (Exception e) {
            exceptionThrown = true;
            message = e.getMessage();
        }
        assert exceptionThrown;
        assert message.equals("No event matches the eventID provided.");
    }

    @Test
    public void readEventsPositive() {
        ArrayList<SuperModel> array = null;

        try {
            db.deleteTables(db.getConn());
            eventDAO.createEvent(e, db.getConn());
            eventDAO.createEvent(event2, db.getConn());
            array = eventDAO.readEvents("stairs", db.getConn());
        } catch (Exception e) {
            e.printStackTrace();
        }
        assert (array.size() == 2);
    }

    @Test
    public void readEventsNegative() {
        //read an event that does not exist
        Boolean exceptionThrown = false;
        String message = "";

        try {
            db.deleteTables(db.getConn());
            eventDAO.readEvents("stairs", db.getConn());
        } catch (Exception e) {
            exceptionThrown = true;
            message = e.getMessage();
        }
        assert exceptionThrown;
        assert message.equals("No events to read for user.");
    }

    @Test
    public void deleteUserInfo() {
        Boolean exceptionThrown = false;
        String message = "";
        try {
            db.deleteTables(db.getConn());
            eventDAO.createEvent(e, db.getConn());
            assert eventDAO.deleteUserEvents("stairs", db.getConn());
            db.closeConnection(true);
            db.openConnection();
            int amount = eventDAO.readEvents("stairs", db.getConn()).size();
            assert (amount == 0);
        } catch (Exception e) {
            exceptionThrown = true;
            message = e.getMessage();
        }
        assert exceptionThrown;
        assert message.equals("No events to read for user.");
    }
}