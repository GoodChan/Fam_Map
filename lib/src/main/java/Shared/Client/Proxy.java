package Shared.Client;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.*;
import com.google.gson.Gson;
import Shared.Server.Requests.RegisterRequest;

public class Proxy {
    private String serverHost = "";
    private String serverPort = "";

    public Proxy(String serverHost, String serverPort) {
        this.serverHost = serverHost;
        this.serverPort = serverPort;
    }

    public void register(RegisterRequest registerRequest){
       try {
           URL url = new URL("http://" + serverHost + ":" + serverPort + "/user/register");
           HttpURLConnection http = (HttpURLConnection) url.openConnection();
           http.setDoOutput(true);
           http.addRequestProperty("Accept", "application/json");
           Gson gson = new Gson();
           String request = gson.toJson(registerRequest);
           http.connect();
           OutputStream reqBody = http.getOutputStream();
           writeString(request, reqBody);
           reqBody.close();

           if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {
               System.out.println("Route successfully claimed.");
           } else {
               System.out.println("ERROR: " + http.getResponseMessage());
           }
       } catch (IOException e) {
         e.printStackTrace();
       }
    }

    /*
            The readString method shows how to read a String from an InputStream.
        */
    private static String readString(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        InputStreamReader sr = new InputStreamReader(is);
        char[] buf = new char[1024];
        int len;
        while ((len = sr.read(buf)) > 0) {
            sb.append(buf, 0, len);
        }
        return sb.toString();
    }

    /*
        The writeString method shows how to write a String to an OutputStream.
    */
    private static void writeString(String str, OutputStream os) throws IOException {
        OutputStreamWriter sw = new OutputStreamWriter(os);
        sw.write(str);
        sw.flush();
    }
}
