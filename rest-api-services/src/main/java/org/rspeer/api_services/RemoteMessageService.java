package org.rspeer.api_services;

import com.google.gson.reflect.TypeToken;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.rspeer.commons.Configuration;
import org.rspeer.commons.RsPeerExecutor;
import org.rspeer.entities.RemoteMessage;
import org.rspeer.runetek.event.EventDispatcher;
import org.rspeer.runetek.event.EventDispatcherProvider;
import org.rspeer.runetek.event.types.RemoteMessageEvent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class RemoteMessageService {

    private static HashSet<Integer> consumed = new HashSet<>();

    public static void poll() {
        RsPeerExecutor.scheduleAtFixedRate(() -> {
            List<RemoteMessage> messages = getMessages();
            EventDispatcher dispatcher = EventDispatcherProvider.provide();
            for (RemoteMessage message : messages) {
                if(consumed.contains(message.getId())) {
                    RsPeerExecutor.execute(() -> consumeMessage(message));
                    continue;
                }
                consumed.add(message.getId());
                RsPeerExecutor.execute(() -> consumeMessage(message));
                dispatcher.immediate(new RemoteMessageEvent(message, 0));
            }
        }, 0, 10, TimeUnit.SECONDS);
    }

    private static List<RemoteMessage> getMessages() {
        try {
            HttpResponse<String> response = Unirest.get(Configuration.NEW_API_BASE + "message/get?consumer=" + RsPeerApi.getIdentifier())
                    .header("Authorization", "Bearer " + RsPeerApi.getSession())
                    .asString();
            if(response.getStatus() != 200) {
                return new ArrayList<>();
            }
            return RsPeerApi.gson.fromJson(response.getBody(), new TypeToken<List<RemoteMessage>>(){}.getType());
        } catch (UnirestException e) {
            return new ArrayList<>();
        }
    }

    private static void consumeMessage(RemoteMessage message) {
        try {
            HttpResponse<String> response = Unirest.post(Configuration.NEW_API_BASE + "message/consume?message=" + message.getId())
                    .header("Authorization", "Bearer " + RsPeerApi.getSession())
                    .asString();
            if(response.getStatus() == 200) {
                consumed.remove(message.getId());
            }
        } catch (Exception ignored) {
        }

    }

}
