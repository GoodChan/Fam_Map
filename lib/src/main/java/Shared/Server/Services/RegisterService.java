package Shared.Server.Services;

import Shared.Server.DAO.*;
import Shared.Server.Model.User;
import Shared.Server.Requests.LoginRequest;
import Shared.Server.Requests.RegisterRequest;
import Shared.Server.Responses.*;

public class RegisterService extends SuperServices {
    /**
     * URL Path: /user/register
     Description: Creates a new user account, generates 4 generations of ancestor data for the new
     user, logs the user in, and returns an auth token.
     HTTP Method: POST
     Auth Token Required: No

     create new user account
     generate 4 generations of ancestor data for this user
     logs user in
     return auth token

     throws requestMissingOrInvalid
     throws userNameTaken
     * @param request
     * @return
     * @throws internalServerError
     */
    public Response register(RegisterRequest request) {
        Database db = new Database();
        UserResponse userResponse = new UserResponse("intialized in in register service", "", "");
        Boolean isCommit = false;
        User user = new User(request.getUserName(), request.getPassword(), request.getEmail(), request.getFirstName(), request.getLastName(), request.getGender());

        try {
            try {
                db.openConnection();
                Boolean userExists = false;
                try {
                    new UserDAO().readUser(user.getUserName(), db.getConn());
                } catch (Exception e) {
                    userExists = true;
                }
                if (!userExists) {
                    return new MessageResponse("Username already taken by another user.");
                }
                new UserDAO().createUser(user, db.getConn());
                isCommit = true;
            } catch (Database.DatabaseException e) {
                e.printStackTrace();
                MessageResponse messageResponse = new MessageResponse(e.getMessage());
                isCommit = false;
                return messageResponse;
            }
            finally {
                db.closeConnection(isCommit);
            }
            new FillService().fill(user.getUserName(),4);
            LoginRequest loginRequest = new LoginRequest(user.getUserName(), user.getPassword());
            Response response = new LoginService().login(loginRequest);
            MessageResponse messageResponse = new MessageResponse("test");
            if (response.getClass() == messageResponse.getClass()) {
                return new MessageResponse("Invalid input.");
            }
            else {
                userResponse = (UserResponse) response;
            }
        } catch (Database.DatabaseException ex) {
            MessageResponse messageResponse = new MessageResponse("Internal Server Error");
            return messageResponse;
        }
        return userResponse;
    }
}
