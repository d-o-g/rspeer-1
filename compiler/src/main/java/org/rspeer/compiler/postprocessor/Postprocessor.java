package org.rspeer.compiler.postprocessor;

import org.rspeer.compiler.impl.CompilationUnit;

public interface Postprocessor {
    void accept(CompilationUnit unit);
}
