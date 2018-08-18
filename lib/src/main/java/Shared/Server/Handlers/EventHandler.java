package Shared.Server.Handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.OutputStream;
import java.net.*;
import java.io.IOException;
import Shared.Server.Responses.*;
import Shared.Server.Services.EventService;
import com.sun.net.httpserver.*;
import Shared.Server.DAO.*;


public class EventHandler extends SuperHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        URI requestUri = exchange.getRequestURI();
        String path = requestUri.getPath();
        String responseString = "";

        if (path.equals("/event/")) {
            try {
                responseString = eventsHandler(exchange);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            responseString = eventHandler(exchange, path);
        }
        OutputStream os = exchange.getResponseBody();
        writeStringToOutputStream(responseString, exchange);
        os.close();
    }

    //handler for multiple events
    private String eventsHandler(HttpExchange exchange) throws Database.DatabaseException {
        String responseString = "itialization of eventsHandler's response string";
        Response response = null;
        Gson gson = new Gson();

        System.out.println("/event");
        Headers reqHeaders = exchange.getRequestHeaders();
        try {
            if (reqHeaders.containsKey("Authorization")) {
                String authToken = reqHeaders.getFirst("Authorization");
                MessageResponse mr = new MessageResponse("");
                try {
                    response = new EventService().events(authToken);
                } catch (Exception e) {
                        e.printStackTrace();
                }
                if (response.getClass() == mr.getClass()) {
                    responseString = gson.toJson((MessageResponse) response);
                } else {
                    responseString = gson.toJson((DataResponse) response);
                }
                return responseString;
            } else {
                responseString = gson.toJson(new MessageResponse("Invalid auth token."));
                return responseString;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return responseString;
    }

    //handler for event specified with an event ID
    private String eventHandler(HttpExchange exchange, String path) {
        final int EVENT_CONTEXT_LENGTH = 7;
        Response response;
        path = path.substring(EVENT_CONTEXT_LENGTH); //7 removes "/event/" leaving [eventID]
        EventService eventService = new EventService();
        String responseString = "itialization of eventHandler's response string";
        Gson gson = new Gson();
        Headers reqHeaders = exchange.getRequestHeaders();

        try {
            if (reqHeaders.containsKey("Authorization")) {
                String authToken = reqHeaders.getFirst("Authorization");
                response = eventService.event(path, authToken);
                MessageResponse messageResponse = new MessageResponse("not passed anywhere");
                if (response.getClass() == messageResponse.getClass()) {
                    responseString = gson.toJson((MessageResponse) response);
                } else {
                    responseString = gson.toJson((EventResponse) response);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return responseString;
    }
}
