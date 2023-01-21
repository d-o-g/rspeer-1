package org.rspeer.verifyer.asm;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

public class ClassLibrary {

    private final Map<String, ClassNode> library = new HashMap<>();
    private final Map<String, JarEntry> nonClassResources = new HashMap<>();

    public void loadClassesFromJar(JarInputStream jar) throws IOException {
        JarEntry entry;
        while ((entry = jar.getNextJarEntry()) != null) {
            if (!entry.getName().endsWith(".class")) {
                nonClassResources.put(entry.getName(), entry);
                continue;
            }

            if (entry.getName().equals("module-info.class")) {
                continue;
            }

            ClassNode cn = new ClassNode();
            ClassReader reader = new ClassReader(jar);
            reader.accept(cn, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
            library.put(cn.name, cn);
        }
    }

    public Collection<ClassNode> getClassNodes() {
        return library.values();
    }

    public ClassNode getClassNode(String name) {
        if (!library.containsKey(name) && !isSecureClass(name)) {
            return loadInMemory(name);
        } else {
            return library.get(name);
        }
    }

    private boolean isSecureClass(String name) {
        return name.contains("java") && !name.contains("io");
    }

    public MethodNode findMethodRecursive(MethodInsnNode cast, String start) {
        ClassNode node = getClassNode(start);
        if (node == null) {
            return null;
        }

        MethodNode call = node.getMethod(cast.desc);
        if (call == null) {
            return findMethodRecursive(cast, node.superName);
        }

        return call;
    }

    private ClassNode loadInMemory(String name) {
        try {
            ClassReader reader = new ClassReader(ClassLoader.getSystemClassLoader().getResourceAsStream(name + ".class"));
            ClassNode cn = new ClassNode();
            reader.accept(cn, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
            return cn;
        } catch (IOException e) {
            return null;
        }
    }

}
