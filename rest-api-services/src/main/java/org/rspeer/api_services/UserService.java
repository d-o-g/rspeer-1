package org.rspeer.api_services;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import org.json.JSONObject;
import org.rspeer.commons.Configuration;
import org.rspeer.commons.Failsafe;

public class UserService {

    private static final Object lock = new Object();

    private static UserService instance;

    public static UserService getInstance() {
        synchronized (lock) {
            if(instance != null) {
                return instance;
            }
            instance = new UserService();
            return instance;
        }
    }

    public UserService() {
        Unirest.setDefaultHeader("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
        Unirest.setDefaultHeader("accept", "application/json");
        Unirest.setDefaultHeader("Content-Type", "application/json");
    }

    public boolean login(String email, String password) throws Exception {
        JSONObject body = new JSONObject();
        body.put("Email", email).put("Password", password);
        String payload = body.toString();
        HttpResponse<JsonNode> res = Unirest
                .post(Configuration.NEW_API_BASE + "user/login").body(payload).asJson();
        JSONObject response = res.getBody().getObject();
        if (response.has("token")) {
            AuthorizationService.getInstance().encryptAndWriteSession(response.getString("token"));
            return true;
        }
        if (response.has("body")) {
            throw new Exception(response.getString("body"));
        }
        throw new Exception("Failed to login.");
    }

    private JSONObject getUser(boolean full) throws Exception {
        String session = RsPeerApi.getSession();
        if (session == null) {
            return null;
        }
        HttpResponse<JsonNode> me = Unirest.post(Configuration.NEW_API_BASE + "user/me" + (full ? "?full=true" : ""))
                .header("Authorization", "Bearer " + session)
                .asJson();
        if (me.getStatus() != 200) {
            throw new Exception(me.getBody().toString());
        }
        return me.getBody().getObject();
    }

    public JSONObject getUser() throws Exception {
        return getUser(false);
    }

    public JSONObject getFullUser() throws Exception {
        return getUser(true);
    }

}
