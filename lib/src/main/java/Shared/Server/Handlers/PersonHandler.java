package Shared.Server.Handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import Shared.Server.DAO.Database;
import Shared.Server.Responses.DataResponse;
import Shared.Server.Responses.MessageResponse;
import Shared.Server.Responses.PersonResponse;
import Shared.Server.Responses.Response;
import Shared.Server.Services.PersonService;

public class PersonHandler extends SuperHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException { URI requestUri = exchange.getRequestURI();
        String path = requestUri.getPath();
        PersonService personService = new PersonService();
        String responseString = "";

        if (path.equals("/person/")) {
            try {
                responseString = peopleHandler(exchange);
            } catch (Exception e) {
               e.printStackTrace();
            }
        } else {
            responseString = personHandler(exchange, path);
        }
        OutputStream os = exchange.getResponseBody();
        writeStringToOutputStream(responseString, exchange);
        os.close();
    }

    private String peopleHandler(HttpExchange exchange) throws Database.DatabaseException {
        String responseString = "itialization of eventsHandler's response string";
        Response response = null;
        Gson gson = new Gson();

        System.out.println("/person");
        Headers reqHeaders = exchange.getRequestHeaders();
        try {
            if (reqHeaders.containsKey("Authorization")) {
                String authToken = reqHeaders.getFirst("Authorization");
                MessageResponse messageResponse = new MessageResponse("");
                try {
                    response = new PersonService().people(authToken);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (response.getClass() == messageResponse.getClass()) {
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

    private String personHandler(HttpExchange exchange, String path) {
        Response response;
        path = path.substring(8); //8 removes "/person/" leaving [personID]
        PersonService personService = new PersonService();
        String responseString = "itialization of eventHandler's response string";
        Gson gson = new Gson();
        Headers reqHeaders = exchange.getRequestHeaders();

        try {
            if (reqHeaders.containsKey("Authorization")) {
                String authToken = reqHeaders.getFirst("Authorization");
                response = personService.person(path, authToken);
                MessageResponse messageResponse = new MessageResponse("not passed anywhere");
                if (response.getClass() == messageResponse.getClass()) {
                    responseString = gson.toJson((MessageResponse) response);
                } else {
                    responseString = gson.toJson((PersonResponse) response);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return responseString;
    }

}
