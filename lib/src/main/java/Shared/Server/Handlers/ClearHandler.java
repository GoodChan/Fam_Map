package Shared.Server.Handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import Shared.Server.Responses.MessageResponse;
import Shared.Server.Services.ClearService;

public class ClearHandler extends SuperHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        ClearService clearService = new ClearService();
        MessageResponse response = new MessageResponse("Error with Service.");
        Gson gson = new Gson();

        try {
            response = (MessageResponse) clearService.clear();
        } catch (Exception e) {
            writeStringToOutputStream(gson.toJson(response), exchange);
            return;
        }
        String responseString = new Gson().toJson(response);
        writeStringToOutputStream(responseString, exchange);
    }
}
