package Shared.Server.Services;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import Shared.Server.Model.AuthToken;
import Shared.Server.Model.Event;
import Shared.Server.Model.SuperModel;
import Shared.Server.Model.User;
import Shared.Server.Responses.*;
import Shared.Server.DAO.*;
import static org.junit.Assert.*;
import java.util.*;

public class EventServiceTest {
    //test information
    User user = new User("ballet", "passBaton", "bbb@bbb.com", "ballot", "balut", "m");
    User user2 = new User("ballet2", "passBaton", "bbb@bbb.com", "ballot", "balut", "m");
    Event event = new Event("ballet", "ballerina", "1111",
            "2222", "France", "Jetravail", "birth", "1300");
    Event event2 = new Event("ballet2", "ballerina2", "1111",
            "2222", "France", "Jetravail", "birth", "1300");
    AuthToken authToken = new AuthToken(user.getUserName());
    Database db = new Database();
    EventService eventService = new EventService();
    EventResponse eventResponse = null;
    Response response = null;
    MessageResponse messageResponse = null;

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
        eventService = new EventService();
        eventResponse = null;
        try {
            db.closeConnection(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void eventPositive() {
        try {
            db.deleteTables(db.getConn());
            new UserDAO().createUser(user, db.getConn());
            new EventDAO().createEvent(event, db.getConn());
            new AuthTokenDAO().createAuthToken(authToken, db.getConn());
            db.closeConnection(true);

            //reads an event based off of eventID and authToken
            eventResponse = (EventResponse)eventService.event(event.getEventID(), authToken.getAuthToken());
            db.openConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
        assert eventResponse != null;
        assertEquals(eventResponse.getCity(), event.getCity());
        assertEquals(eventResponse.getCountry(), event.getCountry());
        assertEquals(eventResponse.getDescendant(), event.getDescendant());
        assertEquals(eventResponse.getPersonID(), event.getPerson());
        assertEquals(eventResponse.getLatitude(), event.getLatitude());
        assertEquals(eventResponse.getLongitude(), event.getLongitude());
        assertEquals(eventResponse.getYear(), event.getYear());
        assertEquals(eventResponse.getEventID(), event.getEventID());
        assertEquals(eventResponse.getEventType(), event.getEventType());
    }

    @Test
    public void eventNegativeNotLoggedIn() {
        //reads an event when user exists and is not logged in
        String message = "";

        try {
            db.deleteTables(db.getConn());
            new UserDAO().createUser(user, db.getConn());
            new EventDAO().createEvent(event, db.getConn());
            db.closeConnection(true);
            messageResponse = (MessageResponse)eventService.event(event2.getEventID(), authToken.getAuthToken());
            message = messageResponse.getMessage();
            db.openConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(message, "Not logged in.");
    }


    @Test
    public void eventNegativeUserDoesNotExist() {
        //reads an event when user wants to access an event unrelated to their tree
        String message = "";

        try {
            db.deleteTables(db.getConn());
            new UserDAO().createUser(user, db.getConn());
            new UserDAO().createUser(user2, db.getConn());
            new EventDAO().createEvent(event2, db.getConn()); //creates an event existing to user 2
            new AuthTokenDAO().createAuthToken(authToken, db.getConn()); //logs in user2
            db.closeConnection(true);
            messageResponse = (MessageResponse)eventService.event(event2.getEventID(), authToken.getAuthToken());
            message = messageResponse.getMessage();
            db.openConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(message, "This event is not related to your auth token.");
    }

    @Test
    public void eventsNoEvents() {
        String message = "";
        try {
            db.deleteTables(db.getConn());
            new UserDAO().createUser(user, db.getConn());
            new AuthTokenDAO().createAuthToken(authToken, db.getConn());
            db.closeConnection(true);
            messageResponse = (MessageResponse) eventService.events(authToken.getAuthToken());
            db.openConnection();
            message = messageResponse.getMessage();
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(message, "No events to read for user.");
    }

    @Test
    public void eventsGetOneEvent() {
        DataResponse dataResponse = null;
        try {
            db.deleteTables(db.getConn());
            new UserDAO().createUser(user, db.getConn());
            new EventDAO().createEvent(event, db.getConn());
            new AuthTokenDAO().createAuthToken(authToken, db.getConn());
            db.closeConnection(true);
            dataResponse = (DataResponse)eventService.events(authToken.getAuthToken());
            db.openConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
        assert dataResponse.getData().size() == 1;
        for (SuperModel sm : dataResponse.getData()) {
            Event e = (Event)sm;
            assertEquals(e.getCity(), event.getCity());
            assertEquals(e.getCountry(), event.getCountry());
            assertEquals(e.getDescendant(), event.getDescendant());
            assertEquals(e.getPerson(), event.getPerson());
            assertEquals(e.getLatitude(), event.getLatitude());
            assertEquals(e.getLongitude(), event.getLongitude());
            assertEquals(e.getYear(), event.getYear());
            assertEquals(e.getEventID(), event.getEventID());
            assertEquals(e.getEventType(), event.getEventType());
        }
    }

    @Test
    public void eventsGetOneEventWhenTwoExist() {
        //test that getting events only returns events of the user logged in
        DataResponse dataResponse = null;
        try {
            db.deleteTables(db.getConn());
            new UserDAO().createUser(user, db.getConn());
            new EventDAO().createEvent(event, db.getConn());
            new EventDAO().createEvent(event2, db.getConn());
            new AuthTokenDAO().createAuthToken(authToken, db.getConn());
            db.closeConnection(true);
            dataResponse = (DataResponse)eventService.events(authToken.getAuthToken());
            db.openConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
        assert dataResponse.getData().size() == 1;
        for (SuperModel sm : dataResponse.getData()) {
            Event e = (Event)sm;
            assertEquals(e.getCity(), event.getCity());
            assertEquals(e.getCountry(), event.getCountry());
            assertEquals(e.getDescendant(), event.getDescendant());
            assertEquals(e.getPerson(), event.getPerson());
            assertEquals(e.getLatitude(), event.getLatitude());
            assertEquals(e.getLongitude(), event.getLongitude());
            assertEquals(e.getYear(), event.getYear());
            assertEquals(e.getEventID(), event.getEventID());
            assertEquals(e.getEventType(), event.getEventType());
        }
    }

    @Test
    public void eventsGetManyEvents() {
        //test that getting events only returns events of the user logged in
        DataResponse dataResponse = null;
        Event event3 = new Event("ballet", "ballerina", "1111",
                "2222", "germany", "Jetravail", "birth", "1387000");

        Event event4 = new Event("ballet", "ballerina", "1111",
                "2222", "Russia", "Jetravail", "birth", "96");

        Event event5 = new Event("ballet", "ballerina", "1111",
                "2222", "China", "Jetravail", "birth", "354");

        try {
            db.deleteTables(db.getConn());
            new UserDAO().createUser(user, db.getConn());
            new EventDAO().createEvent(event, db.getConn());
            //adds 5 events and returns 4 since event2 belongs do a different user
            new EventDAO().createEvent(event2, db.getConn());
            new EventDAO().createEvent(event3, db.getConn());
            new EventDAO().createEvent(event4, db.getConn());
            new EventDAO().createEvent(event5, db.getConn());
            new AuthTokenDAO().createAuthToken(authToken, db.getConn());
            db.closeConnection(true);
            dataResponse = (DataResponse)eventService.events(authToken.getAuthToken());
            db.openConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
        assert dataResponse.getData().size() == 4;
        Iterator iter = dataResponse.getData().iterator();
        Event e = (Event)iter.next();
        assertEquals(e.getCity(), event.getCity());
        assertEquals(e.getCountry(), event.getCountry());
        assertEquals(e.getDescendant(), event.getDescendant());
        assertEquals(e.getPerson(), event.getPerson());
        assertEquals(e.getLatitude(), event.getLatitude());
        assertEquals(e.getLongitude(), event.getLongitude());
        assertEquals(e.getYear(), event.getYear());
        assertEquals(e.getEventID(), event.getEventID());
        assertEquals(e.getEventType(), event.getEventType());

        //event 2 belongs to a different user (user2 instead of user)
        e = (Event)iter.next();
        assertEquals(e.getCity(), event3.getCity());
        assertEquals(e.getCountry(), event3.getCountry());
        assertEquals(e.getDescendant(), event3.getDescendant());
        assertEquals(e.getPerson(), event3.getPerson());
        assertEquals(e.getLatitude(), event3.getLatitude());
        assertEquals(e.getLongitude(), event3.getLongitude());
        assertEquals(e.getYear(), event3.getYear());
        assertEquals(e.getEventID(), event3.getEventID());
        assertEquals(e.getEventType(), event3.getEventType());

        e = (Event)iter.next();
        assertEquals(e.getCity(), event4.getCity());
        assertEquals(e.getCountry(), event4.getCountry());
        assertEquals(e.getDescendant(), event4.getDescendant());
        assertEquals(e.getPerson(), event4.getPerson());
        assertEquals(e.getLatitude(), event4.getLatitude());
        assertEquals(e.getLongitude(), event4.getLongitude());
        assertEquals(e.getYear(), event4.getYear());
        assertEquals(e.getEventID(), event4.getEventID());
        assertEquals(e.getEventType(), event4.getEventType());

        e = (Event)iter.next();
        assertEquals(e.getCity(), event5.getCity());
        assertEquals(e.getCountry(), event5.getCountry());
        assertEquals(e.getDescendant(), event5.getDescendant());
        assertEquals(e.getPerson(), event5.getPerson());
        assertEquals(e.getLatitude(), event5.getLatitude());
        assertEquals(e.getLongitude(), event5.getLongitude());
        assertEquals(e.getYear(), event5.getYear());
        assertEquals(e.getEventID(), event5.getEventID());
        assertEquals(e.getEventType(), event5.getEventType());
    }
}