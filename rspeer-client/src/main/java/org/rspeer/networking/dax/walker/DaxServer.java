package org.rspeer.networking.dax.walker;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.request.body.RequestBodyEntity;
import org.rspeer.api_services.RsPeerApi;
import org.rspeer.commons.Configuration;
import org.rspeer.networking.dax.walker.models.BulkBankPathRequest;
import org.rspeer.networking.dax.walker.models.BulkPathRequest;
import org.rspeer.networking.dax.walker.models.PathResult;
import org.rspeer.networking.dax.walker.models.exceptions.AuthorizationException;
import org.rspeer.networking.dax.walker.models.exceptions.RateLimitException;
import org.rspeer.networking.dax.walker.models.exceptions.UnknownException;
import org.rspeer.runetek.api.movement.WebWalker;
import org.rspeer.ui.Log;

import java.util.List;
import java.util.logging.Level;

public class DaxServer {

    private static final String BASE_URL = Configuration.NEW_API_BASE + "walker";

    private Gson gson;
    private long rateLimit;

    public DaxServer() {
        this.gson = new Gson();
        this.rateLimit = 0L;
    }

    public List<PathResult> getPaths(BulkPathRequest bulkPathRequest) {
        return makePathRequest(BASE_URL + "/generatePaths", gson.toJson(bulkPathRequest));
    }

    public List<PathResult> getBankPaths(BulkBankPathRequest bulkBankPathRequest) {
        return makePathRequest(BASE_URL + "/generateBankPaths", gson.toJson(bulkBankPathRequest));
    }

    private List<PathResult> makePathRequest(String url, String jsonPayload) {
        if (System.currentTimeMillis() - rateLimit < 5000L) throw new RateLimitException("Throttling requests because key rate limit.");
        RequestBodyEntity request = Unirest.post(url)
                .header("Authorization", "Bearer " + RsPeerApi.getSession())
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36")
                .header("Content-Type", "application/json")
                .queryString("walker", WebWalker.Dax)
                .body(jsonPayload);
        try {

            HttpResponse<String> response = request.asString();

            switch (response.getStatus()) {

                case 429:
                    Log.log(Level.WARNING, "Server", "Rate limit hit");
                    this.rateLimit = System.currentTimeMillis();
                    throw new RateLimitException(response.getStatusText());

                case 401:
                    throw new AuthorizationException(String.format("Invalid API Key [%s]", response.getStatusText()));

                case 200:
                    String responseBody = response.getBody();
                    if (responseBody == null) throw new IllegalStateException("Illegal response returned from server.");
                    return gson.fromJson(responseBody, new TypeToken<List<PathResult>>() {}.getType());

            }
        } catch (Exception e) {
            Log.log(Level.SEVERE, "Server", e.toString());
        }
        throw new UnknownException("Error connecting to server.");
    }
}
