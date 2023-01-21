package org.rspeer.script.provider;

import com.google.gson.JsonObject;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import org.rspeer.RSPeer;
import org.rspeer.api_services.RsPeerApi;
import org.rspeer.api_services.Logger;
import org.rspeer.commons.Configuration;
import org.rspeer.commons.HttpUtil;
import org.rspeer.script.Script;
import org.rspeer.script.ScriptCategory;
import org.rspeer.script.model.RepositoryScript;
import org.rspeer.script.model.ScriptListRequest;
import org.rspeer.script.model.ScriptQueryType;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class RemoteScriptProvider implements ScriptProvider<ScriptSource> {

    private static final ScriptCategory[] ORDER = {
            ScriptCategory.AGILITY, ScriptCategory.COMBAT, ScriptCategory.CONSTRUCTION,
            ScriptCategory.COOKING, ScriptCategory.CRAFTING, ScriptCategory.FARMING,
            ScriptCategory.FIREMAKING, ScriptCategory.FISHING, ScriptCategory.FLETCHING,
            ScriptCategory.HERBLORE, ScriptCategory.HUNTER, ScriptCategory.MAGIC,
            ScriptCategory.MINIGAME, ScriptCategory.MINING, ScriptCategory.MONEY_MAKING,
            ScriptCategory.OTHER, ScriptCategory.PRAYER, ScriptCategory.RUNECRAFTING,
            ScriptCategory.SMITHING, ScriptCategory.THIEVING, ScriptCategory.WOODCUTTING
    };

    private static ScriptCategory toCategory(int id) {
        try {
            return ORDER[id];
        } catch (Exception e) {
            return ScriptCategory.AGILITY;
        }
    }

    @Override
    public RemoteScriptSource[] load() {
        List<RemoteScriptSource> sources = new ArrayList<>();
        try {

            ScriptListRequest request = new ScriptListRequest();
            request.setType(ScriptQueryType.Mine);
            HttpResponse<String> data = Unirest.post(Configuration.NEW_API_BASE + "script/list").
                    header("Authorization",
                            "Bearer " + RsPeerApi.getSession())
                    .body(RSPeer.gson.toJson(request)).asString();

            RepositoryScript[] scripts = RSPeer.gson.fromJson(data.getBody(), RepositoryScript[].class);

            for (RepositoryScript script : scripts) {
                String name = script.getName();
                double version = script.getVersion();
                String desc = script.getDescription();
                String developer = script.getAuthor();
                ScriptCategory category = toCategory((int) script.getCategory());
                int id = (int) script.getId();
                int totalUsers = (int) script.getTotalUsers();
                String forumThread = script.getForumThread();
                RemoteScriptSource source = new RemoteScriptSource(name, version, desc, developer, category, id, totalUsers, forumThread);
                sources.add(source);
            }

        } catch (Exception e) {
            Logger.getInstance().capture(e);
            e.printStackTrace();
        }
        return sources.toArray(new RemoteScriptSource[0]);
    }

    @Override
    public RemoteScriptSource load(Path path) {
        return null;
    }

    @Override
    public void prepare(ScriptSource s) throws Exception {
        if (!(s instanceof RemoteScriptSource)) {
            return;
        }
        RemoteScriptSource source = (RemoteScriptSource) s;
        try {
            HttpResponse<InputStream> data = Unirest.get(Configuration.NEW_API_BASE + "script/content?id=" + source.getId())
                    .header("Authorization", "Bearer " + RsPeerApi.getSession())
                    .asBinary();

            if (data.getStatus() != 200) {
                String message = HttpUtil.convertStreamToString(data.getBody());
                JsonObject o = RSPeer.gson.fromJson(message, JsonObject.class);
                throw new Exception(o.has("error") ? o.get("error").getAsString() : message);
            }

            RemoteArchiveLoader<Script> loader = new RemoteArchiveLoader<>();
            Class<Script> target = loader.loadClass(data.getBody(), this);
            if (target != null) {
                source.setTarget(target);
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

}
