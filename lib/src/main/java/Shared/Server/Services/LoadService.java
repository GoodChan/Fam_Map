package Shared.Server.Services;

import Shared.Server.Model.Event;
import Shared.Server.Model.Person;
import Shared.Server.Model.User;
import Shared.Server.Responses.Response;
import Shared.Server.Responses.MessageResponse;
import Shared.Server.Requests.LoadRequest;
import Shared.Server.DAO.*;

public class LoadService extends SuperServices {
    /**
     * URL Path: /load
     Description: Clears all data from the database (just like the /clear API), and then loads the
     posted user, person, and event data into the database.

     clear all data from database like /clear
     loads posted users, people, and events from the request into the database
     message returns the number of users, people, and events added.

     HTTP Method: POST
     Auth Token Required: No
     * @param request
     * @return
     */
    public Response load(LoadRequest request) {
        Database db = new Database();
        MessageResponse response = new MessageResponse("initialized response in loadService");
        boolean willCommit = true;

        try {
            try {
                db.openConnection();
                db.deleteTables(db.getConn());
                UserDAO userDAO = new UserDAO();
                int userCount = 0;
                for (User u : request.getUsers()) {
                    userDAO.createUser(u, db.getConn());
                    ++ userCount;
                }
                PersonDAO personDAO = new PersonDAO();
                int personCount = 0;
                for (Person p : request.getPersons()) {
                    personDAO.createPerson(p, db.getConn());
                    ++personCount;
                }
                EventDAO eventDAO = new EventDAO();
                int eventCount = 0;
                for (Event e : request.getEvents()) {
                    eventDAO.createEvent(e, db.getConn());
                    ++eventCount;
                }
                willCommit = true;
                response = new MessageResponse("Successfully added " + userCount + " users, "
                        + personCount + " persons, and " + eventCount + " events to the database.");
            } catch (Database.DatabaseException e) {
                MessageResponse messageResponse = new MessageResponse(e.getMessage());
                e.printStackTrace();
                willCommit = false;
                return messageResponse;
            }
            finally {
                db.closeConnection(willCommit);
            }
        } catch (Database.DatabaseException e) {
            MessageResponse messageResponse = new MessageResponse("Internal Server Error");
            e.printStackTrace();
            return messageResponse;
        }
        return response;
    }
}
