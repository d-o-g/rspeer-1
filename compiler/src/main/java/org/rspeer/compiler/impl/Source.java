package org.rspeer.compiler.impl;

import java.nio.file.Path;

public enum Source {

    JAVA,
    RESOURCE,
    CLASS;

    public static Source byExtension(Path path) {
        if (path.toString().endsWith(".java")) {
            return JAVA;
        } else if (path.toString().endsWith(".class")) {
            return CLASS;
        }
        return RESOURCE;
    }
}
