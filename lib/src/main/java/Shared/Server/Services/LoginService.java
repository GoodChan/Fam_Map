package Shared.Server.Services;

import Shared.Server.Requests.LoginRequest;
import Shared.Server.Responses.MessageResponse;
import Shared.Server.Responses.Response;
import Shared.Server.Responses.UserResponse;
import Shared.Server.Model.AuthToken;
import Shared.Server.Model.User;
import Shared.Server.DAO.AuthTokenDAO;
import Shared.Server.DAO.UserDAO;
import Shared.Server.DAO.Database;

public class LoginService extends SuperServices {
    /**
     * URL Path: /user/login
     Description: Logs in the user and returns an auth token.
     HTTP Method: POST
     Auth Token Required: No
     * @param request
     * @return
     */
    public Response login(LoginRequest request) {
        AuthToken authtoken = new AuthToken(request.getUserName());
        AuthTokenDAO authTokenDao = new AuthTokenDAO();
        Database db = new Database();
        User user = null;
        boolean willCommit = true;

        try {
            try {
                db.openConnection();
                authTokenDao.createAuthToken(authtoken, db.getConn());
                user = new UserDAO().readUser(request.getUserName(), request.getPassword(), db.getConn());
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
        assert user != null;
        UserResponse loginResponse = new UserResponse(authtoken.getAuthToken(), user.getUserName(), user.getPersonID());
        return loginResponse;
    }
}
