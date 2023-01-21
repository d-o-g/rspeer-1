package org.rspeer.compiler.impl;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticListener;
import javax.tools.JavaFileObject;

public final class DiagnosticAdapter implements DiagnosticListener<JavaFileObject> {

    @Override
    public void report(Diagnostic<? extends JavaFileObject> diagnostic) {
        System.out.println(diagnostic);
    }
}
