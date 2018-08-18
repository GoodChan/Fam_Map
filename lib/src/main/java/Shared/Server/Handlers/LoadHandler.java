package Shared.Server.Handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import Shared.Server.Responses.*;
import Shared.Server.Requests.LoadRequest;
import Shared.Server.Services.LoadService;

public class LoadHandler extends SuperHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        InputStream httpRequestBody = exchange.getRequestBody();
        String request = inputStreamToString(httpRequestBody);

        Gson gson = new Gson();
        LoadRequest lr = gson.fromJson(request, LoadRequest.class);
        LoadService ls = new LoadService();
        MessageResponse loadResponseObject = (MessageResponse) ls.load(lr);
        String responseString = gson.toJson(loadResponseObject);
        OutputStream os = exchange.getResponseBody();
        writeStringToOutputStream(responseString, exchange);
    }
}
