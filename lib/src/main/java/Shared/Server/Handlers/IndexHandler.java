package Shared.Server.Handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.net.*;
import java.io.*;
import java.nio.file.Files;
import Shared.Server.DAO.Database;
import java.io.IOException;

public class IndexHandler extends SuperHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        URI requestUrl = exchange.getRequestURI();
        String path = requestUrl.getPath();
        System.out.println(path);

        //if file path is "/"
        if (path.equals("/") || path.length() == 0) {
            System.out.println("path equals");
            try {
                Database db = new Database();
                db.openConnection();
                db.createTables();
                db.closeConnection(true);
                String indexLocation = "C:\\Users\\GoodC\\AndroidStudioProjects\\Fam_Map\\web\\index.html";
                File indexFile = new File(indexLocation);
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                OutputStream respBdy = exchange.getResponseBody();
                Files.copy(indexFile.toPath(), respBdy);
                respBdy.close();
                System.out.println("path equals");
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        //check if file exists
        path = "C:\\Users\\GoodC\\AndroidStudioProjects\\Fam_Map\\web\\" + path.substring(1);
        File file = new File(path);
        if (file.exists() && file.canRead()) {
            System.out.println("path exists");
            //send file back
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
            OutputStream respBdy = exchange.getResponseBody();
            Files.copy(file.toPath(), respBdy);
            respBdy.close();
            System.out.println("path exists");
        }
        else {
            //custom 404 error
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
            OutputStream respBdy = exchange.getResponseBody();
            //custom file location
            Files.copy(new File("C:\\Users\\GoodC\\AndroidStudioProjects\\Fam_Map\\web\\HTML\\404.html").toPath(), respBdy);
            respBdy.close();
        }
    }
}
