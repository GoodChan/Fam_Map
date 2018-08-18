package Shared.Server.Services;

import Shared.Server.Responses.Response;
import Shared.Server.Responses.MessageResponse;
import Shared.Server.DAO.Database;

public class ClearService extends SuperServices {
    /**
     *  URL Path: /clear
     Description: Deletes ALL data from the database, including user accounts, auth tokens, and
     generated person and event data.
     HTTP Method: POST
     Auth Token Required: No
     Request Body: None
     Errors: Internal server error
     * @return
     * @throws internalServerError
     */
    public Response clear() throws internalServerError {
        Database db = new Database();
        MessageResponse successMessage = new MessageResponse("Clear succeeded.");
        Boolean isCommited = false;

        try {
            try {
                db.openConnection();
                db.deleteTables(db.getConn());
                isCommited = true;
            } catch (Database.DatabaseException e) {
                MessageResponse mr = new MessageResponse(e.getMessage());
                e.printStackTrace();
                isCommited = false;
                return mr;
            }
            finally {
                db.closeConnection(isCommited);
            }
        } catch (Database.DatabaseException e) {
            MessageResponse mr = new MessageResponse(e.getMessage());
            e.printStackTrace();
            return mr;
        }
        return successMessage;
    }
}
