package org.rspeer.compiler.impl;

import org.rspeer.compiler.Compiler;

import javax.tools.JavaFileObject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public final class CompilationUnit implements Consumer<Path> {

    private final Compiler compiler;
    private final Path root;

    private final Set<JavaFileObject> sources;
    private final Set<Path> resources;

    public CompilationUnit(Compiler compiler, Path root) {
        this.compiler = compiler;
        this.root = root;
        this.sources = new HashSet<>();
        this.resources = new HashSet<>();
    }

    @Override
    public void accept(Path path) {
        Source source = Source.byExtension(path);
        if (source != Source.RESOURCE && source != Source.CLASS) {
            compiler.visit(path).forEach(sources::add);
        } else {
            resources.add(path);
        }
    }

    public Set<JavaFileObject> getSources() {
        return sources;
    }

    public Set<Path> getResources() {
        return resources;
    }

    public Path getRoot() {
        return root;
    }

    public void visit(Path target) throws IOException {
        Files.walk(target)
                .filter(Files::isRegularFile)
                .forEach(this);
    }
}
