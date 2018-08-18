package Shared.Server.Services;

import Shared.Server.Model.SuperModel;
import Shared.Server.DAO.AuthTokenDAO;
import Shared.Server.DAO.Database;
import Shared.Server.DAO.PersonDAO;
import Shared.Server.Responses.MessageResponse;
import Shared.Server.Responses.Response;
import Shared.Server.Responses.PersonResponse;
import Shared.Server.Responses.DataResponse;
import Shared.Server.Model.Person;
import java.util.ArrayList;

public class PersonService extends SuperServices {
    /**
     * URL Path: /person/[personID]
     Example: /person/7255e93e
     Description: Returns the single Person object with the specified ID.
     HTTP Method: GET
     Auth Token Required: Yes
     Request Body: None
     Errors: Invalid auth token, Invalid personID parameter, Requested person does not belong to
     this user, Internal server error

     throws the following errors
     throws invalidAuthToken
     throws invalidPersonID
     throws personNotWithUser
     * @param authToken
     * @return dr
     * @throws internalServerError
     */
        public Response people(String authToken) {
        Database db = new Database();
        String userName = "";
        DataResponse dataResponse = null;
        boolean willCommit = true;

        try {
            try {
                db.openConnection();
                userName = new AuthTokenDAO().readUserFromAuthToken(authToken, db.getConn());
                ArrayList<SuperModel> arrayList = new PersonDAO().readPeople(userName, db.getConn());
                dataResponse = new DataResponse(arrayList);
                willCommit = true;
            } catch (Database.DatabaseException e) {
                e.printStackTrace();
                willCommit = false;
                MessageResponse messageResponse = new MessageResponse(e.getMessage());
                return messageResponse;
            }
            finally {
                db.closeConnection(willCommit);
            }
        } catch (Database.DatabaseException ex) {
            MessageResponse messageResponse = new MessageResponse("Internal Server Error");
            return messageResponse;
        }
        return dataResponse;
    }

    /**
     * URL Path: /person
     Description: Returns ALL family members of the current user. The current user is
     determined from the provided auth token.
     HTTP Method: GET
     Auth Token Required: Yes
     Request Body: None
     Errors: Invalid auth token, Internal server error
     throws invalidAuthToken
     * @param personID
     * @return personResponse
     * @throws internalServerError
     */
    public Response person(String personID, String authToken) throws internalServerError {
        PersonResponse personResponse = null;
        Database db = new Database();
        boolean willCommit = true;

        try {
            try {
                db.openConnection();
                PersonDAO personDAO = new PersonDAO();
                String userName = new AuthTokenDAO().readUserFromAuthToken(authToken, db.getConn());
                Person tempPerson = personDAO.readPerson(personID, db.getConn());
                if (!tempPerson.getDescendant().equals(userName)) {
                    MessageResponse messageResponse = new MessageResponse("This person is not related to your auth token.");
                    return messageResponse;
                }
                personResponse = new PersonResponse(tempPerson.getPersonID(), tempPerson.getDescendant(),
                        tempPerson.getFirstName(), tempPerson.getLastName(), tempPerson.getGender(),
                        tempPerson.getFather(), tempPerson.getMother(), tempPerson.getSpouse());
                willCommit = true;
            } catch (Database.DatabaseException e) {
                e.printStackTrace();
                willCommit = false;
                MessageResponse messageResponse = new MessageResponse(e.getMessage());
                return messageResponse;
            }
            finally {
                db.closeConnection(willCommit);
            }
        } catch (Database.DatabaseException ex) {
            MessageResponse messageResponse = new MessageResponse("Internal Server Error");
            return messageResponse;
        }
        return personResponse;
    }

}
