package Shared.Server.Handlers;

import java.io.*;
import com.sun.net.httpserver.*;
import Shared.Server.Requests.LoginRequest;
import com.google.gson.*;
import Shared.Server.Responses.MessageResponse;
import Shared.Server.Services.LoginService;
import Shared.Server.Responses.UserResponse;
import Shared.Server.Responses.Response;

public class LoginHandler extends SuperHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        InputStream httpRequestBody = exchange.getRequestBody();
        String request = inputStreamToString(httpRequestBody);

        Gson gson = new Gson();
        LoginRequest lr = gson.fromJson(request, LoginRequest.class);
        LoginService ls = new LoginService();
        Response ur = ls.login(lr);
        UserResponse userResponse = new UserResponse("", "not returnable, login Handler handle", "");
        MessageResponse mr = new MessageResponse("not returnable, login Handler handle");
        String responseString = "";

        if (ur.getClass() == userResponse.getClass()) {
            responseString = gson.toJson((UserResponse)ur);
        }
        else if (ur.getClass() == mr.getClass()) {
            responseString = gson.toJson((MessageResponse)ur);
        }

        OutputStream os = exchange.getResponseBody();
        writeStringToOutputStream(responseString, exchange);
    }
}
