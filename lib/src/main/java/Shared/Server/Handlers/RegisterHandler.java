package Shared.Server.Handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import Shared.Server.Responses.*;
import Shared.Server.Requests.RegisterRequest;
import Shared.Server.Services.RegisterService;

public class RegisterHandler extends SuperHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        InputStream httpRequestBody = exchange.getRequestBody();
        String request = inputStreamToString(httpRequestBody);

        Gson gson = new Gson();
        RegisterRequest registerRequest = gson.fromJson(request, RegisterRequest.class);
        Response response = new RegisterService().register(registerRequest);
        String responseString = "";

        if (response.getClass() == UserResponse.class) {
            responseString = gson.toJson((UserResponse)response);
        }
        else {
            responseString = gson.toJson((MessageResponse)response);
        }
        OutputStream os = exchange.getResponseBody();
        writeStringToOutputStream(responseString, exchange);
    }
}
