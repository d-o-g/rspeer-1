package org.rspeer.compiler;

import org.rspeer.compiler.postprocessor.Postprocessor;

import javax.tools.*;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.*;

public abstract class Compiler {

    protected final DiagnosticListener<? super JavaFileObject> adapter;
    protected final JavaCompiler internal;
    protected final StandardJavaFileManager manager;

    private final List<Postprocessor> postprocessors;

    protected Compiler(DiagnosticListener<? super JavaFileObject> adapter) {
        this.adapter = adapter;
        this.internal = ToolProvider.getSystemJavaCompiler();
        this.manager = internal.getStandardFileManager(adapter, Locale.getDefault(), Charset.defaultCharset());
        this.postprocessors = new ArrayList<>();
    }

    public Iterable<? extends JavaFileObject> visit(Path path) {
        return manager.getJavaFileObjectsFromFiles(Collections.singletonList(path.toFile()));
    }

    public List<Postprocessor> getPostprocessors() {
        return postprocessors;
    }

    public abstract JavaCompiler.CompilationTask submit(Iterable<? extends JavaFileObject> target, Set<String> options);
}
