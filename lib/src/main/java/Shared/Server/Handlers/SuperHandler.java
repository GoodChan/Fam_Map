package Shared.Server.Handlers;

import java.io.*;
import java.net.*;
import com.sun.net.httpserver.*;


public class SuperHandler {
    protected String inputStreamToString(InputStream inputStream) {
        StringBuilder stringBuilder = new StringBuilder();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        char[] buf = new char[1024];
        int len;
        try {
            while ((len = inputStreamReader.read(buf)) > 0) {
                stringBuilder.append(buf, 0, len);
            }
        }
        catch (Exception e) {
            System.out.println("Err: SuperHandler:inputStreamToString");
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    protected void writeStringToOutputStream(String stringToWrite, HttpExchange exchange) throws IOException {
        try {
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
            OutputStream outputStream = exchange.getResponseBody();
            writeString(stringToWrite, outputStream);
            outputStream.close();
        }
        finally {
            exchange.getResponseBody().close();
        }
    }

    private void writeString(String str, OutputStream os) throws IOException {
        OutputStreamWriter streamWriter = new OutputStreamWriter(os);
        streamWriter.write(str);
        streamWriter.flush();
    }
}
