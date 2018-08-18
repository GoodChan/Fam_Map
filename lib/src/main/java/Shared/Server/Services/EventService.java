package Shared.Server.Services;

import Shared.Server.Model.Event;
import Shared.Server.Model.SuperModel;
import Shared.Server.DAO.AuthTokenDAO;
import Shared.Server.DAO.Database;
import Shared.Server.DAO.EventDAO;
import Shared.Server.Responses.MessageResponse;
import Shared.Server.Responses.Response;
import Shared.Server.Responses.EventResponse;
import Shared.Server.Responses.DataResponse;

import java.util.ArrayList;


public class EventService extends SuperServices {
    /**
     * URL Path: /person/[personID]
     Example: /person/7255e93e
     Description: Returns the single Person object with the specified ID.
     HTTP Method: GET
     Auth Token Required: Yes
     Request Body: None
     Errors: Invalid auth token, Invalid personID parameter, Requested person does not belong to
     this user, Internal server error
     throws invalidPersonID
     throws invalidAuthToken
     throws personNotWithUser
     * @param eventID
     * @return
     * @throws internalServerError
     */
    public Response event(String eventID, String authToken) {
        EventResponse event = null;
        Database db = new Database();
        boolean willCommit = true;

        try {
            try {
                db.openConnection();
                EventDAO eventDAO = new EventDAO();
                String userName = new AuthTokenDAO().readUserFromAuthToken(authToken, db.getConn());
                Event tempEvent = eventDAO.readEvent(eventID, db.getConn());
                if (!tempEvent.getDescendant().equals(userName)) {
                    MessageResponse mr = new MessageResponse("This event is not related to your auth token.");
                    return mr;
                }
                event = new EventResponse(tempEvent.getEventID(), tempEvent.getDescendant(),
                        tempEvent.getPerson(), tempEvent.getLatitude(), tempEvent.getLongitude(),
                        tempEvent.getCountry(), tempEvent.getCity(), tempEvent.getEventType(),
                        tempEvent.getYear());
                willCommit = true;
            } catch (Database.DatabaseException e) {
                e.printStackTrace();
                willCommit = false;
                MessageResponse mr = new MessageResponse(e.getMessage());
                return mr;
            }
            finally {
                db.closeConnection(willCommit);
            }
        } catch (Database.DatabaseException ex) {
            MessageResponse mr = new MessageResponse("Internal Server Error");
            return mr;
        }
        return event;
    }

    /**
     * URL Path: /person
     Description: Returns ALL family members of the current user. The current user is
     determined from the provided auth token.
     HTTP Method: GET
     Auth Token Required: Yes
     Request Body: None
     Errors: Invalid auth token, Internal server error
     Success Response Body: The response body returns a JSON object with a data attribute that
     contains an array of Person objects. Each Person object has the same format as described in
     previous section on the /person/[personID] API.

     throws invalidAuthToken
     * @return
     * @throws internalServerError
     */
    public Response events(String authToken) {
        Database db = new Database();
        String userName = "";
        DataResponse dataResponse = null;

        try {
            try {
                db.openConnection();
                userName = new AuthTokenDAO().readUserFromAuthToken(authToken, db.getConn());
                ArrayList<SuperModel> arrayList = new EventDAO().readEvents(userName, db.getConn());
                dataResponse = new DataResponse(arrayList);
                db.closeConnection(true);
            } catch (Database.DatabaseException e) {
                e.printStackTrace();
                db.closeConnection(false);
                MessageResponse messageResponse = new MessageResponse(e.getMessage());
                return messageResponse;
            }
        } catch (Database.DatabaseException ex) {
            MessageResponse messageResponse = new MessageResponse("Internal Server Error");
            return messageResponse;
        }
        return dataResponse;
    }
}
