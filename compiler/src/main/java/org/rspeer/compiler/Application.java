package org.rspeer.compiler;

import org.rspeer.compiler.impl.*;
import org.rspeer.compiler.postprocessor.*;

import javax.tools.*;
import java.nio.file.*;
import java.util.*;

public final class Application {

    //TODO change these paths
    private static final String BASE_DIR = "C:\\Users\\root\\Documents\\RSPeer\\";
    private static final String CLASSPATH = BASE_DIR + "cache\\rspeer.jar";

    public static final String SCRIPT_DIR = BASE_DIR + "source\\";
    public static final String OUTPUT_ARCH = BASE_DIR + "test.jar";
    public static final String OUTPUT_OBF_ARCH = BASE_DIR + "test.jar";

    public static final String ALLATORI = BASE_DIR + "obf\\allatori.jar";
    public static final String ALLATORI_CFG = BASE_DIR + "obf\\config.xml";

    private final Compiler compiler;

    private Application() {
        compiler = new ScriptCompiler();
    }

    public static void main(String[] args) throws Exception {
        Application application = new Application();
        CompilationUnit unit = application.cook(Paths.get(SCRIPT_DIR));
        Set<String> options = application.buildCompilerOptions();
        application.createPostprocessors();

        System.out.println("Compiling...");
        application.compile(unit, options);
    }

    private void createPostprocessors() {
        List<Postprocessor> processors = compiler.getPostprocessors();
        processors.add(new GenerateArchive());
        processors.add(new ObfuscateArchive());
        processors.add(new Cleanup());
    }

    private Set<String> buildCompilerOptions() {
        Set<String> ops = new HashSet<>();
        ops.add("-classpath");
        ops.add(CLASSPATH);
        return ops;
    }

    private CompilationUnit cook(Path target) throws Exception {
        CompilationUnit unit = new CompilationUnit(compiler, target);
        unit.visit(target);
        return unit;
    }

    private void compile(CompilationUnit unit, Set<String> options) {
        JavaCompiler.CompilationTask task = compiler.submit(unit.getSources(), options);
        if (task.call()) {
            compiler.getPostprocessors().forEach(x -> x.accept(unit));
        }
    }
}
