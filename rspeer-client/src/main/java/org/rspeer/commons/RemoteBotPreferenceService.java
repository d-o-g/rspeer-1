package org.rspeer.commons;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.rspeer.RSPeer;
import org.rspeer.api_services.RsPeerApi;

public class RemoteBotPreferenceService {

    public static BotPreferences get() {
        try {
            HttpResponse<String> value = Unirest
                    .get(Configuration.NEW_API_BASE + "botPreference/get")
                    .header("Authorization", "Bearer " + RsPeerApi.getSession())
                    .asString();
            if(value.getStatus() != 200) {
                return BotPreferences.getInstance();
            }
            return RSPeer.gson.fromJson(value.getBody(), BotPreferences.class);
        } catch (UnirestException e) {
            if(!e.getMessage().contains("Connection pool shut down")) {
                e.printStackTrace();
            }
        }
        return BotPreferences.getInstance();
    }

    public static void save(BotPreferences preferences) {
        try {
            Unirest
                    .post(Configuration.NEW_API_BASE + "botPreference/overwrite")
                    .header("Authorization", "Bearer " + RsPeerApi.getSession())
                    .body(RsPeerApi.gson.toJson(preferences))
                    .asString();
        } catch (UnirestException e) {
            if(!e.getMessage().contains("Connection pool shut down")) {
                e.printStackTrace();
            }
        }
    }
}
