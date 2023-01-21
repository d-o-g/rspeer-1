package org.rspeer.compiler.impl;

import org.rspeer.compiler.Compiler;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import java.io.PrintWriter;
import java.util.Set;

public final class ScriptCompiler extends Compiler {

    private final PrintWriter output;

    public ScriptCompiler() {
        super(new DiagnosticAdapter());
        output = new PrintWriter(System.out);
    }

    @Override
    public JavaCompiler.CompilationTask submit(Iterable<? extends JavaFileObject> target, Set<String> options) {
        return internal.getTask(output, manager, adapter, options, null, target);
    }
}
