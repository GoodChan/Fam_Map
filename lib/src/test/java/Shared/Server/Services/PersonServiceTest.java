package Shared.Server.Services;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.Iterator;
import Shared.Server.DAO.AuthTokenDAO;
import Shared.Server.DAO.Database;
import Shared.Server.DAO.PersonDAO;
import Shared.Server.DAO.UserDAO;
import Shared.Server.Model.Person;
import Shared.Server.Model.SuperModel;
import Shared.Server.Model.User;
import Shared.Server.Model.AuthToken;
import Shared.Server.Responses.DataResponse;
import Shared.Server.Responses.MessageResponse;
import Shared.Server.Responses.PersonResponse;

import static org.junit.Assert.*;

public class PersonServiceTest {
    PersonService personService = new PersonService();
    Database db = new Database();
    User user = new User("ballerina", "passBaton",
            "bbb@bbb.com", "ballot", "balut", "m");
    User user2 = new User("ballet2", "passBaton",
            "bbb@bbb.com", "ballot", "balut", "m");
    Person person = new Person("ballerina", "beach",
            "beach", "f", "peachy", "peachyy", "12345");
    Person person2 = new Person("ballet2", "beach",
            "beach", "f", "peachy", "peachyy", "12345");
    PersonResponse personResponse = null;
    AuthToken authTokenUser = new AuthToken(user.getUserName());

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
        personService = new PersonService();
        personResponse = null;
        try {
            db.closeConnection(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void personPositive() {
        try {
            db.deleteTables(db.getConn());
            new UserDAO().createUser(user, db.getConn());
            new PersonDAO().createPerson(person, db.getConn());
            new AuthTokenDAO().createAuthToken(authTokenUser, db.getConn());
            db.closeConnection(true);
            db.openConnection();

            //reads a person based off of personID and authToken
            personResponse = (PersonResponse) personService.person
                    (person.getPersonID(), authTokenUser.getAuthToken());
        } catch (Exception e) {
            e.printStackTrace();
        }
        assert personResponse != null;
        assertEquals(personResponse.getDescendant(), person.getDescendant());
        assertEquals(personResponse.getFather(), person.getFather());
        assertEquals(personResponse.getFirstName(), person.getFirstName());
        assertEquals(personResponse.getLastName(), person.getLastName());
        assertEquals(personResponse.getPersonID(), person.getPersonID());
        assertEquals(personResponse.getSpouse(), person.getSpouse());
        assertEquals(personResponse.getMother(), person.getMother());
        assertEquals(personResponse.getGender(), person.getGender());
    }

    @Test
    public void personNegativeNotLoggedIn() {
        //reads a person when user exists and is not logged in
        String message = "";
        MessageResponse messageResponse = null;

        try {
            db.deleteTables(db.getConn());
            new UserDAO().createUser(user, db.getConn());
            new PersonDAO().createPerson(person, db.getConn());

            messageResponse = (MessageResponse)personService.person
                    (person.getPersonID(), authTokenUser.getAuthToken());
            message = messageResponse.getMessage();
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(message, "Not logged in.");
    }


    @Test
    public void personNegativeUserDoesNotExist() {
        //reads an event when user wants to access an event unrelated to their tree
        String message = "";
        MessageResponse messageResponse = null;

        try {
            db.deleteTables(db.getConn());
            new UserDAO().createUser(user, db.getConn());
            new UserDAO().createUser(user2, db.getConn());
            new PersonDAO().createPerson(person2, db.getConn()); //creates an person existing to user 2
            new AuthTokenDAO().createAuthToken(authTokenUser, db.getConn()); //logs in user2

            messageResponse = (MessageResponse)personService.person
                    (person.getPersonID(), authTokenUser.getAuthToken());
            message = messageResponse.getMessage();
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(message, "Not logged in.");
    }

    @Test
    public void peopleNoPeople() {
        String message = "";
        MessageResponse messageResponse = null;
        try {
            db.deleteTables(db.getConn());
            new UserDAO().createUser(user, db.getConn());
            new AuthTokenDAO().createAuthToken(authTokenUser, db.getConn());
            db.closeConnection(true);
            db.openConnection();
            messageResponse = (MessageResponse) personService.people(authTokenUser.getAuthToken());
            message = messageResponse.getMessage();
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(message, "No People related to this userName.");
    }

    @Test
    public void peopleGetOnePerson() {
        DataResponse dataResponse = null;
        try {
            db.deleteTables(db.getConn());
            new UserDAO().createUser(user, db.getConn());
            new PersonDAO().createPerson(person, db.getConn());
            new AuthTokenDAO().createAuthToken(authTokenUser, db.getConn());
            db.closeConnection(true);
            db.openConnection();
            dataResponse = (DataResponse)personService.people(authTokenUser.getAuthToken());
        } catch (Exception e) {
            e.printStackTrace();
        }
        assert dataResponse.getData().size() == 1;
        for (SuperModel sm : dataResponse.getData()) {
            Person p = (Person)sm;
            assertEquals(p.getDescendant(), person.getDescendant());
            assertEquals(p.getFather(), person.getFather());
            assertEquals(p.getFirstName(), person.getFirstName());
            assertEquals(p.getLastName(), person.getLastName());
            assertEquals(p.getPersonID(), person.getPersonID());
            assertEquals(p.getSpouse(), person.getSpouse());
            assertEquals(p.getMother(), person.getMother());
            assertEquals(p.getGender(), person.getGender());
        }
    }

    @Test
    public void peopleGetOnePersonWhenTwoExist() {
        //test that people only returns people of the user logged in
        DataResponse dataResponse = null;
        try {
            db.deleteTables(db.getConn());
            new UserDAO().createUser(user, db.getConn());
            new PersonDAO().createPerson(person, db.getConn());
            new PersonDAO().createPerson(person2, db.getConn());
            new AuthTokenDAO().createAuthToken(authTokenUser, db.getConn());
            db.closeConnection(true);
            db.openConnection();
            dataResponse = (DataResponse)personService.people(authTokenUser.getAuthToken());
        } catch (Exception e) {
            e.printStackTrace();
        }
        assert dataResponse.getData().size() == 1;
        for (SuperModel sm : dataResponse.getData()) {
            Person p = (Person)sm;
            assertEquals(p.getDescendant(), person.getDescendant());
            assertEquals(p.getFather(), person.getFather());
            assertEquals(p.getFirstName(), person.getFirstName());
            assertEquals(p.getLastName(), person.getLastName());
            assertEquals(p.getPersonID(), person.getPersonID());
            assertEquals(p.getSpouse(), person.getSpouse());
            assertEquals(p.getMother(), person.getMother());
            assertEquals(p.getGender(), person.getGender());
        }
    }

    @Test
    public void peopleGetManyPeople() {
        //test that getting events only returns events of the user logged in
        DataResponse dataResponse = null;
        Person person3 = new Person("ballet", "ballerina", "1111",
                "2222", "germany", "Jetravail", "birth", "1387000");

        Person person4 = new Person("fdballet", "ballerina", "sdf1111",
                "2222", "Russia", "Jetravail", "fdsbirth", "96");

        Person person5 = new Person("balletduh!", "ballerina", "1111",
                "2222", "China", "Jetravail", "birth", "354");

        try {
            db.deleteTables(db.getConn());
            new UserDAO().createUser(user, db.getConn());
            new PersonDAO().createPerson(person, db.getConn());
            //adds 5 people and returns 4 since person2 belongs do a different user
            new PersonDAO().createPerson(person2, db.getConn());
            new PersonDAO().createPerson(person3, db.getConn());
            new PersonDAO().createPerson(person4, db.getConn());
            new PersonDAO().createPerson(person5, db.getConn());
            new AuthTokenDAO().createAuthToken(authTokenUser, db.getConn());
            db.closeConnection(true);
            db.openConnection();
            dataResponse = (DataResponse)personService.people(authTokenUser.getAuthToken());
        } catch (Exception e) {
            e.printStackTrace();
        }
        assert dataResponse.getData().size() == 4;
        Iterator iter = dataResponse.getData().iterator();
        Person p = (Person)iter.next();
        assertEquals(p.getDescendant(), person.getDescendant());
        assertEquals(p.getFather(), person.getFather());
        assertEquals(p.getFirstName(), person.getFirstName());
        assertEquals(p.getLastName(), person.getLastName());
        assertEquals(p.getPersonID(), person.getPersonID());
        assertEquals(p.getSpouse(), person.getSpouse());
        assertEquals(p.getMother(), person.getMother());
        assertEquals(p.getGender(), person.getGender());

        //person 2 belongs to a different user (user2 instead of user)
        p = (Person)iter.next();
        assertEquals(p.getDescendant(), person3.getDescendant());
        assertEquals(p.getFather(), person3.getFather());
        assertEquals(p.getFirstName(), person3.getFirstName());
        assertEquals(p.getLastName(), person3.getLastName());
        assertEquals(p.getPersonID(), person3.getPersonID());
        assertEquals(p.getSpouse(), person3.getSpouse());
        assertEquals(p.getMother(), person3.getMother());
        assertEquals(p.getGender(), person3.getGender());

        p = (Person)iter.next();
        assertEquals(p.getDescendant(), person4.getDescendant());
        assertEquals(p.getFather(), person4.getFather());
        assertEquals(p.getFirstName(), person4.getFirstName());
        assertEquals(p.getLastName(), person4.getLastName());
        assertEquals(p.getPersonID(), person4.getPersonID());
        assertEquals(p.getSpouse(), person4.getSpouse());
        assertEquals(p.getMother(), person4.getMother());
        assertEquals(p.getGender(), person4.getGender());

        p = (Person)iter.next();
        assertEquals(p.getDescendant(), person5.getDescendant());
        assertEquals(p.getFather(), person5.getFather());
        assertEquals(p.getFirstName(), person5.getFirstName());
        assertEquals(p.getLastName(), person5.getLastName());
        assertEquals(p.getPersonID(), person5.getPersonID());
        assertEquals(p.getSpouse(), person5.getSpouse());
        assertEquals(p.getMother(), person5.getMother());
        assertEquals(p.getGender(), person5.getGender());
    }
}