package org.rspeer.compiler.postprocessor;

import org.rspeer.compiler.Application;
import org.rspeer.compiler.commons.Allatori;
import org.rspeer.compiler.impl.CompilationUnit;

import java.nio.file.Paths;

public final class ObfuscateArchive implements Postprocessor {

    @Override
    public void accept(CompilationUnit unit) {
        try {
            System.out.println("Obfuscating archive...");
            Allatori allatori = new Allatori(Paths.get(Application.ALLATORI), Paths.get(Application.ALLATORI_CFG));
            Process process = allatori.execute();
            allatori.displayErrors(process.getErrorStream());
            System.out.println("Obfuscation complete with exit code " + process.waitFor());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
