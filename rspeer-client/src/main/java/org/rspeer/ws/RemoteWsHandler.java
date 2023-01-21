package org.rspeer.ws;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.commons.io.IOUtils;
import org.rspeer.api_services.MessageCallback;
import org.rspeer.api_services.SiteConfigService;
import org.rspeer.commons.Configuration;
import org.rspeer.script.provider.RemoteArchiveLoader;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.util.Optional;

public class RemoteWsHandler {

    public MessageCallback get() throws IOException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, UnirestException {
        Optional<String> scriptId = SiteConfigService.getString("websocket:listener:scriptId");
        if(!scriptId.isPresent()) {
            throw new IOException("Failed to register WS handler. Cannot get script id.");
        }

        HttpResponse<InputStream> data = Unirest.get(Configuration.NEW_API_BASE + "script/content?id=" + scriptId.get())
                .asBinary();

        if(data.getStatus() != 200) {
            System.out.println(IOUtils.toString(data.getBody(), Charset.defaultCharset()));
            throw new IOException("Failed to register WS handler. Received a non 200 status code.");
        }

        RemoteArchiveLoader<MessageCallback> source = new RemoteArchiveLoader<>();
        try {
            Class<MessageCallback> handler =
                    source.loadClass(data.getBody(), MessageCallback.class::isAssignableFrom);
            if (handler == null) {
                throw new IOException("Failed to register WS handler.");
            }
            Constructor<MessageCallback> constructor = handler.getConstructor();
            return constructor.newInstance();
        } catch (Throwable e) {
            e.printStackTrace();
            throw e;
        }
    }

}
