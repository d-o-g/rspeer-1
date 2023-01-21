package org.rspeer.ui.account;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import org.json.JSONObject;
import org.rspeer.api_services.RsPeerApi;
import org.rspeer.api_services.UserService;
import org.rspeer.script.GameAccount;
import org.rspeer.script.Script;
import org.rspeer.ui.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.util.ArrayList;

public class XorSerializedAccountList extends ArrayList<GameAccount> {

    private static final Type ACCOUNT_LIST_TYPE = new TypeToken<ArrayList<GameAccount>>() {
    }.getType();
    private static final String ACCOUNT_FILE_NAME = "abtash.dat";

    public XorSerializedAccountList() {
        try {
            deserialize();
        } catch (IOException e) {
            Log.severe("Error deserializing account data");
        }
    }

    void serialize() {
        File file = getAccountFile();
        Gson gson = new Gson();
        String json = gson.toJson(this, ACCOUNT_LIST_TYPE);
        byte[] encrypted = xor(json.getBytes(), String.valueOf(getUserId()));
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(encrypted);
        } catch (IOException exception) {
            Log.severe("Error writing account data");
        }
    }

    private void deserialize() throws IOException {
        File file = getAccountFile();
        if(!Files.exists(file.toPath())) {
            return;
        }
        try {
            byte[] data = Files.readAllBytes(file.toPath());
            data = xor(data, String.valueOf(getUserId()));
            String json = new String(data);
            Gson gson = new Gson();
            ArrayList<GameAccount> list = gson.fromJson(json, ACCOUNT_LIST_TYPE);
            for (Object element : list) {
                add((GameAccount) element);
            }

            boolean serialize = false;
            for (GameAccount account : this) {
                if (account.getXpPreference() == null) {
                    serialize = true;
                    account.fix();
                }
            }

            if (serialize) {
                serialize();
            }
        } catch (JsonSyntaxException exception) {
            //todo add this back ?
        }
    }

    private File getAccountFile() {
        return new File(Script.getDataDirectory().toString(), ACCOUNT_FILE_NAME);
    }

    private int getUserId() {
        return RsPeerApi.getUserId();
    }

    private byte[] xor(byte[] data, String key) {
        byte[] result = new byte[data.length];
        byte[] keyByte = key.getBytes();
        for (int x = 0, y = 0; x < data.length; x++, y++) {
            if (y == keyByte.length) {
                y = 0;
            }

            result[x] = (byte) (data[x] ^ keyByte[y]);
        }

        return result;
    }
}
