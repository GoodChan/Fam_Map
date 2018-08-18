package Shared.Server.Services;

import org.junit.*;

import Shared.Server.Model.Event;
import Shared.Server.Model.Person;
import Shared.Server.Model.User;
import Shared.Server.Requests.LoadRequest;
import Shared.Server.Responses.MessageResponse;
import Shared.Server.DAO.Database;
import static org.junit.Assert.*;
import com.google.gson.*;

public class LoadServiceTest {
    LoadRequest loadRequest = null;
    LoadService loadService = new LoadService();
    Database db = new Database();

    @Before
    public void setup() {
        //setting up the load request.
        User user1 = new User("user1", "password1", "email@1", "user1", "lastuser1", "m", "1234");
        User user2 = new User("user2", "password2", "email@2", "user2", "lastuser2", "f", "12345");
        User userArray[] = new User[2];
        userArray[0] = user1;
        userArray[1] = user2;

        Person person = new Person("descendant", "firstName", "lastName", "f", "father", "mother", "spouse");
        Person person2 = new Person("descendant", "firstName2", "lastName2", "f", "father", "mother2", "spouse2");
        Person personArray[] = new Person[2];
        personArray[0] = person;
        personArray[1] = person2;

        Event event = new Event("descendant", "person", "lat", "long", "country", "city", "marriage", "year");
        Event event2 = new Event("descendant", "person2", "lat", "long", "country", "city2", "marriage", "year");
        Event eventArray[] = new Event[2];
        eventArray[0] = event;
        eventArray[1] = event2;

        try {
            db.openConnection();
            db.deleteTables(db.getConn());
            db.createTables();
            db.closeConnection(true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        loadRequest = new LoadRequest(userArray, personArray, eventArray);
    }

    @Test
    public void load() {
        MessageResponse messageResponse = (MessageResponse) loadService.load(loadRequest);
        assertEquals("Successfully added 2 users, 2 persons, and 2 events to the database.", messageResponse.getMessage());
    }

    @Test
    public void generateArray() {
        Gson gson = new Gson();
        String jsonInput = gson.toJson(loadRequest, LoadRequest.class);
        System.out.println(jsonInput);
    }

    @Test
    public void negativeLoad() {
        //loads nothing
        Gson gson = new Gson();
        LoadRequest nullLoadRequest= new LoadRequest(null, null,null);
        String jsonInput = gson.toJson(nullLoadRequest, LoadRequest.class);
        assertEquals(jsonInput, "{}");
    }

}