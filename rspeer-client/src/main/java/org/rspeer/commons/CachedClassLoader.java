package org.rspeer.commons;

import java.util.HashMap;
import java.util.Map;

/**
 * A ClassLoader which defines classes on-the-fly
 */
public final class CachedClassLoader extends ClassLoader {

    private final Map<String, byte[]> classes;
    private final Map<String, Class<?>> loaded;
    private final Map<String, Class<?>> defined;

    public CachedClassLoader(Map<String, byte[]> classes) {
        super(CachedClassLoader.class.getClassLoader());
        this.classes = classes;
        this.loaded = this.defined = new HashMap<>();
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        if (loaded.containsKey(name)) {
            return loaded.get(name);
        } else if (!classes.containsKey(name)) {
            return super.loadClass(name);
        } else if (defined.containsKey(name)) {
            return defined.get(name);
        }
        byte[] def = classes.get(name);
        Class<?> clazz = super.defineClass(name, def, 0, def.length);
        loaded.put(name, clazz);
        defined.put(name, clazz);
        return clazz;
    }
}

