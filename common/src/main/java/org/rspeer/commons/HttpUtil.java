package org.rspeer.commons;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class HttpUtil {

    private static Gson g = new Gson();

    public static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    public static String getIpAddress() {
        try {
            URLConnection connection = new URL("https://api.ipify.org?format=json").openConnection();
            InputStream input = connection.getInputStream();
            JsonObject element = g.fromJson(new InputStreamReader(input), JsonObject.class);
            input.close();
            return element.get("ip").getAsString();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static JSONObject convertStreamToJson(java.io.InputStream is) {
        String res = convertStreamToString(is);
        try {
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new JSONObject(res);
    }
}

