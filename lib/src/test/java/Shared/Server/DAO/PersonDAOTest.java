package Shared.Server.DAO;

import java.util.ArrayList;
import Shared.Server.Model.SuperModel;
import org.junit.*;
import Shared.Server.Model.Person;


public class PersonDAOTest {
    PersonDAO personDAO = new PersonDAO();
    Database db = new Database();
    Person p = new Person("rockPaint", "type", "typee", "m", "paint", "typewriter", "handwriting");

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
            db.closeConnection(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void createPersonPositive() {
        Person result = null;

        try {
            db.deleteTables(db.getConn());
            assert personDAO.createPerson(p, db.getConn());
            result = personDAO.readPerson(p.getPersonID(), db.getConn());
        } catch (Exception e) {
            e.printStackTrace();
        }
        assert (result.getPersonID().equals(p.getPersonID()));
        assert (result.getDescendant().equals(p.getDescendant()));
        assert (result.getFirstName().equals(p.getFirstName()));
        assert (result.getLastName().equals(p.getLastName()));
        assert (result.getGender().equals(p.getGender()));
        assert (result.getFather().equals(p.getFather()));
        assert (result.getMother().equals(p.getMother()));
        assert (result.getSpouse().equals(p.getSpouse()));
    }

    @Test
    public void createPersonNegative() {
        //create a person that already exists.
        Boolean exceptionThrown = false;
        String message = "";

        try {
            db.deleteTables(db.getConn());
            personDAO.createPerson(p, db.getConn());
            personDAO.createPerson(p, db.getConn()); //try creating the same person twice
        } catch (Exception e) {
            exceptionThrown = true;
            message = e.getMessage();
        }
        assert exceptionThrown;
        assert message.equals("Person already exists.");
    }


    @Test
    public void readPeoplePositive() {
        ArrayList<SuperModel> array = null;
        Person person2 = new Person("rockPaint", "typeb", "typee2", "m",
                "paint22", "typewriter", "handwriting2");

        try {
            db.deleteTables(db.getConn());
            personDAO.createPerson(p, db.getConn());
            personDAO.createPerson(person2, db.getConn());
            array = personDAO.readPeople("rockPaint", db.getConn());
        } catch (Exception e) {
            e.printStackTrace();
        }
        assert (array.size() == 2);
    }

    @Test
    public void readPeopleNegative() {
        //read someone who does not exist
        Boolean exceptionThrown = false;
        String message = "";

        try {
            db.deleteTables(db.getConn());
            personDAO.readPeople("rockPaint", db.getConn());
        } catch (Exception e) {
            exceptionThrown = true;
            message = e.getMessage();
        }
        assert exceptionThrown;
        assert message.equals("No People related to this userName.");
    }

    //TODO no readPeople or readPerson edge cases that I know of

    @Test
    public void readPersonPositive() {
        Person result = null;
        try {
            db.deleteTables(db.getConn());
            personDAO.createPerson(p, db.getConn());
            result = personDAO.readPerson(p.getPersonID(), db.getConn());
        } catch (Exception e) {
            e.printStackTrace();
        }
        assert (p.getSpouse().equals(result.getSpouse()));
        assert (p.getMother()).equals(result.getMother());
        assert (p.getFather().equals(result.getFather()));
        assert (p.getGender().equals(result.getGender()));
        assert (p.getDescendant().equals(result.getDescendant()));
        assert (p.getPersonID().equals(result.getPersonID()));
        assert (p.getLastName().equals(result.getLastName()));
        assert (p.getFirstName().equals(result.getFirstName()));
    }

    @Test
    public void readPersonNegative() {
        Boolean exceptionThrown = false;
        String message = "";
        try {
            db.deleteTables(db.getConn());
            personDAO.readPerson(p.getPersonID(), db.getConn());
        } catch (Exception e) {
            exceptionThrown = true;
            message = e.getMessage();
        }
        assert exceptionThrown;
        assert message.equals("Person does not exist.");
    }

    @Test
    public void deleteUserInfo() {
        Boolean exceptionThrown = false;
        String message = "";

        try {
            db.deleteTables(db.getConn());
            personDAO.createPerson(p, db.getConn());
            assert personDAO.deleteUserInfo("rockPaint", db.getConn());
            db.closeConnection(true);
            db.openConnection();
            int amount = personDAO.readPeople("rockPaint", db.getConn()).size();
            assert (amount == 0);
        } catch (Exception e) {
            exceptionThrown = true;
            message = e.getMessage();
        }
        assert exceptionThrown;
        assert (message.equals("No People related to this userName."));
    }
}