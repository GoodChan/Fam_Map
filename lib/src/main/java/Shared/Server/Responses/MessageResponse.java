package Shared.Server.Responses;

public class MessageResponse extends Response {
    private String message = "";

    /**
     * Constructs a response with a message. Used in errors, fillServices, load
     * /person/[personID]
     * @param message
     */
    public MessageResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
