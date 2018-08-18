package Shared.Server.Responses;

public class UserResponse extends Response {
    private String authToken = "";
    private String userName = "";
    private String personID = "";

    /**
     * Constructs a user response.
     * @param authToken
     * @param userName
     * @param personID
     */
    public UserResponse(String authToken, String userName, String personID) {
        this.authToken = authToken;
        this.userName = userName;
        this.personID = personID;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPersonID() {
        return personID;
    }

    public void setPersonID(String personID) {
        this.personID = personID;
    }
}
