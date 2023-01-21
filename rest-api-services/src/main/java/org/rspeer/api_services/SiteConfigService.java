package org.rspeer.api_services;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.rspeer.commons.Configuration;

import java.util.Optional;

public class SiteConfigService {

    public static Optional<String> getString(String key) {
        return get(key);
    }

    public static Optional<Boolean> getBoolean(String key) {
        Optional<String> result = get(key);
        return result.map(Boolean::parseBoolean);
    }

    private static Optional<String> get(String key) {
        try {
            HttpResponse<String> value = Unirest.get(Configuration.NEW_API_BASE + "config/get?key=" + key).asString();
            return Optional.of(value.getBody().replace("\"", ""));
        } catch (UnirestException e) {
            if(!e.getMessage().contains("Connection pool shut down")) {
                e.printStackTrace();
            }
        }
        return Optional.empty();
    }

}
