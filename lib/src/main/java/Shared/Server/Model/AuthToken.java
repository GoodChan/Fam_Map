package Shared.Server.Model;

import java.util.UUID;

public class AuthToken extends SuperModel {
    private String AuthToken = "";
    private String userName = "";

    /**
     * creates an auth token object.
     * @param userName
     */
    public AuthToken(String userName) {
        AuthToken = UUID.randomUUID().toString();
        this.userName = userName;
    }

    public String getAuthToken() {
        return AuthToken;
    }

    public void setAuthToken(String authToken) {
        AuthToken = authToken;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
