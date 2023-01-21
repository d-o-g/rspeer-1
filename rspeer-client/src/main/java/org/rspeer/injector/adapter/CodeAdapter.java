package org.rspeer.injector.adapter;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.rspeer.injector.Modscript;
import org.rspeer.runetek.api.commons.predicate.Predicates;

import java.util.Map;
import java.util.function.Predicate;

public abstract class CodeAdapter implements Opcodes {

    public static final String PROVIDER_PACKAGE = "org/rspeer/runetek/providers/";
    public static final String EVENT_PACKAGE = "org/rspeer/runetek/event/";

    protected final Modscript modscript;
    protected final Map<String, ClassNode> library;
    protected final CodeAdapter delegate;

    public CodeAdapter(CodeAdapter delegate, Modscript modscript, Map<String, ClassNode> library) {
        this.delegate = delegate;
        this.modscript = modscript;
        this.library = library;
    }

    public CodeAdapter(Modscript modscript, Map<String, ClassNode> library) {
        this(null, modscript, library);
    }

    public final void visit(ClassNode target) {
        if (runnable() && classPredicate().test(target)) {
            visitClassNode(target);
            for (MethodNode mn : target.methods) {
                if (methodPredicate().test(mn)) {
                    visitMethodNode(mn);
                }
            }
        }

        if (delegate != null) {
            delegate.visit(target);
        }
    }

    public abstract void visitClassNode(ClassNode cn);

    public abstract void visitMethodNode(MethodNode mn);

    public boolean runnable() {
        return true;
    }

    //override these methods to imply predicates on the visitation
    public Predicate<ClassNode> classPredicate() {
        return Predicates.always();
    }

    public Predicate<MethodNode> methodPredicate() {
        return Predicates.always();
    }
}
