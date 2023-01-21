package org.rspeer.script.provider;

import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

public final class RemoteArchiveLoader<T> {

    public Class<T> loadClass(InputStream stream, Predicate<Class<?>> predicate) throws IOException {
        Class<T> source = null;
        byte[] bytes = IOUtils.toByteArray(stream);
        try (JarInputStream jis = new JarInputStream(new ByteArrayInputStream(bytes))) {
            Map<String, byte[]> innards = new HashMap<>();
            JarEntry entry;
            while ((entry = jis.getNextJarEntry()) != null) {
                byte[] entryData = next(jis);
                innards.put(entry.getName().replace(".class", ""), entryData);
            }
            //TODO how do handle resources? idk if this will work for it
            ScriptClassLoader loader = new ScriptClassLoader(innards);
            for (String name : innards.keySet()) {
                if (name.endsWith("/")) {
                    continue;
                }
                Class clazz = loader.loadClass(name);
                if (clazz != null && predicate.test(clazz)) {
                    source = clazz;
                }
            }
        }
        return source;
    }

    private static byte[] next(JarInputStream jis) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[2048];
        int read;
        while (jis.available() > 0 && (read = jis.read(buffer, 0, buffer.length)) >= 0) {
            baos.write(buffer, 0, read);
        }
        return baos.toByteArray();
    }

}
