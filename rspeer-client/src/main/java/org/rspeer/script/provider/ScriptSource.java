package org.rspeer.script.provider;

import org.rspeer.script.Script;
import org.rspeer.script.ScriptCategory;
import org.rspeer.script.ScriptMeta;

import java.nio.file.Path;

public class ScriptSource implements Comparable<ScriptSource> {

    private final String name, description, developer;
    private final double version;
    private final ScriptCategory category;
    private Class<? extends Script> target;
    private Path filePath;
    private boolean local = true;

    public ScriptSource(Class<? extends Script> target, Path filePath) {
        this.target = target;
        this.filePath = filePath;
        ScriptMeta meta = target.getAnnotation(ScriptMeta.class);
        name = meta.name();
        description = meta.desc();
        developer = meta.developer();
        version = meta.version();
        category = meta.category();
    }

    public ScriptSource(String name, double version, String description, String developer, ScriptCategory category) {
        target = null;
        this.name = name;
        this.version = version;
        this.description = description;
        this.developer = developer;
        this.category = category;
    }

    public final Class<? extends Script> getTarget() {
        return target;
    }

    public final void setTarget(Class<? extends Script> target) {
        this.target = target;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getDeveloper() {
        return developer;
    }

    public ScriptCategory getCategory() {
        return category;
    }

    public boolean isLocal() {
        return local;
    }

    public void setLocal(boolean local) {
        this.local = local;
    }

    public Path getFilePath() {
        return filePath;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ScriptSource) || this.getClass() != o.getClass()) {
            return false;
        }
        ScriptSource other = (ScriptSource) o;
        return other.getTarget() == getTarget()
                && other.getDescription().equals(getDescription())
                && other.getDeveloper().equals(getDeveloper())
                && other.getName().equals(getName())
                && other.getCategory() == getCategory()
                && other.local == local;
    }

    @Override
    public int compareTo(ScriptSource o) {
        if (this instanceof RemoteScriptSource && o instanceof RemoteScriptSource) {
            return getName().toLowerCase().compareTo(o.getName().toLowerCase());
        }

        if (this instanceof RemoteScriptSource) {
            return 1;
        }

        if (o instanceof RemoteScriptSource) {
            return -1;
        }

        return getName().toLowerCase().compareTo(o.getName().toLowerCase());
    }

    public double getVersion() {
        return version;
    }
}
