package Shared.Server.Handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.String;
import java.net.URI;
import Shared.Server.Responses.*;

import Shared.Server.Services.FillService;

public class FillHandler extends SuperHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        URI requestUri = exchange.getRequestURI();
        String path = requestUri.getPath();
        int generations = 4; //default generations
        String userName = "";

        //parses username and generations
        path = path.substring(6);
        if (path.contains("/")) {
            int index = path.indexOf("/");
            generations = Integer.parseInt(path.substring(index + 1));
            userName = path.substring(0, index);
        }
        else {
            userName = path;
        }

        String responseString = "intialized in FillHandler";
        Gson gson = new Gson();
        try {
            responseString = gson.toJson((MessageResponse) new FillService().fill(userName, generations));
        } catch (Exception e) {
            e.printStackTrace();
        }
        OutputStream os = exchange.getResponseBody();
        writeStringToOutputStream(responseString, exchange);
        os.close();
    }

    private int parsePath(String input, String userName) {
        int generations = 4;

        int index = input.indexOf("/");
        generations = Integer.parseInt(input.substring(index + 1));
        userName = input.substring(0, index);
        return generations;
    }
}
