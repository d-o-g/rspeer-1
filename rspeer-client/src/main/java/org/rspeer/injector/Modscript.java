package org.rspeer.injector;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.rspeer.api_services.RsPeerApi;
import org.rspeer.commons.Configuration;
import org.rspeer.commons.HttpUtil;
import org.rspeer.injector.hook.ClassHook;
import org.rspeer.injector.hook.FieldHook;
import org.rspeer.injector.hook.MethodHook;
import org.rspeer.injector.hook.Multiplier;
import org.rspeer.runetek.providers.RSProvider;
import org.rspeer.ui.Log;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public final class Modscript {

    public Map<String, ClassHook> classes;
    public Map<String, Multiplier> multipliers;

    private Modscript(byte[] payload) {
        initModScript(payload);
    }

    public static Modscript loadFrom(String path) {
        try {
            return new Modscript(Files.readAllBytes(Paths.get(path)));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Modscript loadFromApi() throws Exception {
        HttpResponse<InputStream> stream = Unirest.get(Configuration.NEW_API_BASE + "bot/currentModscript")
                .header("Authorization", "Bearer " + RsPeerApi.getSession())
                .queryString("botHash", RsPeerApi.getOurVersionHash())
                .asBinary();
        if (stream.getStatus() != 200) {
            JSONObject o = HttpUtil.convertStreamToJson(stream.getBody());
            throw new Exception(o.getString("body"));
        }
        return new Modscript(IOUtils.toByteArray(stream.getBody()));
    }

    private void initModScript(byte[] payload) {
        classes = new HashMap<>();
        multipliers = new HashMap<>();
        try (Buffer buffer = new Buffer(payload)) {
            buffer.assertValidMagic(Constants.HEADER);
            buffer.readUTF(); //type - oldschool/modern
            buffer.readUTF(); //hash
            int count = buffer.readInt();
            for (int i = 0; i < count; i++) {
                if (!buffer.readBoolean()) {
                    continue; //invalid/broken class
                }
                String internalName = buffer.readUTF();
                String definedName = buffer.readUTF();
                ClassHook hook = new ClassHook(definedName, internalName);
                //use separate maps for both?
                classes.put(definedName, hook);
                classes.put(internalName, hook);
                int members = buffer.readInt();
                for (int j = 0; j < members; j++) {
                    byte cst = buffer.readByte();
                    String memberName = buffer.readUTF();
                    switch (cst) {
                        case Constants.FIELDHOOK: {
                            FieldHook fh = readField(memberName, buffer);
                            hook.addMember(fh);
                            break;
                        }
                        case Constants.METHODHOOK: {
                            MethodHook mh = readMethod(memberName, buffer);
                            hook.addMember(mh);
                            break;
                        }
                    }
                }
            }
        }
        int fields = (int) classes.values().stream().mapToLong(x -> x.getFields().size()).sum();
        int methods = (int) classes.values().stream().mapToLong(x -> x.getMethods().size()).sum();
        Log.info("Modscript", "Loaded " + ((classes.size() + fields + methods) / 2) + " hooks");
        Log.info("Modscript", "Loaded " + multipliers.size() + " field decoders");
    }

    public ClassHook resolve(Class<? extends RSProvider> provider) {
        return classes.get(provider.getSimpleName().replace("RS", ""));
    }

    private FieldHook readField(String name, Buffer b) {
        return new FieldHook(name, b.readUTF(), b.readUTF(), b.readUTF(), b.readBoolean(), b.readBoolean(), b.readUTF());
    }

    private MethodHook readMethod(String name, Buffer b) {
        return new MethodHook(name, b.readUTF(), b.readUTF(), b.readUTF(), b.readUTF(), b.readInt(), b.readBoolean(), b.readBoolean());
    }

    public interface Constants {
        int HEADER = Arrays.hashCode("runelite leechers".getBytes());
        int FIELDHOOK = 0;
        int METHODHOOK = 1;
    }
}
