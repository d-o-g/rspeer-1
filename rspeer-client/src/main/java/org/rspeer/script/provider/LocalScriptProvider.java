package org.rspeer.script.provider;

import org.rspeer.Application;
import org.rspeer.script.Script;
import org.rspeer.ui.Log;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public final class LocalScriptProvider implements ScriptProvider<ScriptSource> {

    private final File[] roots;

    public LocalScriptProvider(File... roots) {
        this.roots = roots;
    }

    @Override
    public ScriptSource load(Path path) {
        try (JarFile jar = new JarFile(path.toFile());
             URLClassLoader ucl = URLClassLoader.newInstance(new URL[]{path.toUri().toURL()}, Application.class.getClassLoader())) {
            Enumeration<JarEntry> elems = jar.entries();
            while (elems.hasMoreElements()) {
                JarEntry entry = elems.nextElement();
                if (entry.getName().endsWith(".class")) {
                    String name = entry.getName();
                    name = name.substring(0, name.length() - ".class".length());
                    name = name.replace('/', '.');
                    Class<?> clazz = ucl.loadClass(name);
                    if (test(clazz)) {
                        return new ScriptSource((Class<? extends Script>) clazz, path);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void prepare(ScriptSource source) {

    }

    @Override
    public ScriptSource[] load() {
        Set<ScriptSource> entries = new HashSet<>();
        Deque<File> files = new ArrayDeque<>();
        Deque<File> visited = new ArrayDeque<>();
        for (File root : roots) {
            try (URLClassLoader loader = URLClassLoader.newInstance(new URL[]{root.toURI().toURL()}, Application.class.getClassLoader())) {
                files.push(root);
                if (root.isDirectory()) {
                    for (File file : root.listFiles()) {
                        files.push(file);
                    }
                }
                while (!files.isEmpty()) {
                    File file = files.pop();
                    visited.add(file);
                    if (file.isDirectory()) {
                        File[] subFiles = file.listFiles();
                        if (subFiles != null) {
                            for (File sub : subFiles) {
                                if (!visited.contains(sub)) {
                                    files.add(sub);
                                }
                            }
                        }
                    } else if (file.getName().endsWith(".class")) {
                        String raw = file.getPath();
                        raw = raw.substring(root.getPath().length() + 1);
                        raw = raw.substring(0, raw.length() - ".class".length());
                        raw = raw.replace(File.separatorChar, '.');
                        Class<?> clazz = loader.loadClass(raw);
                        if (test(clazz)) {
                            entries.add(new ScriptSource((Class<? extends Script>) clazz, file.toPath()));
                        }
                    } else if (file.getName().endsWith(".jar")) {
                        try (JarFile jar = new JarFile(file);
                             URLClassLoader ucl = URLClassLoader.newInstance(new URL[]{file.toURI().toURL()}, Application.class.getClassLoader())) {
                            Enumeration<JarEntry> elems = jar.entries();
                            while (elems.hasMoreElements()) {
                                try {
                                    JarEntry entry = elems.nextElement();
                                    if (entry.getName().endsWith(".class")) {
                                        String name = entry.getName();
                                        name = name.substring(0, name.length() - ".class".length());
                                        name = name.replace('/', '.');
                                        Class<?> clazz = ucl.loadClass(name);
                                        if (test(clazz)) {
                                            entries.add(new ScriptSource((Class<? extends Script>) clazz, file.toPath()));
                                        }
                                    }
                                } catch (Throwable e) {
                                    Log.severe(e);
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return entries.toArray(new ScriptSource[0]);
    }
}