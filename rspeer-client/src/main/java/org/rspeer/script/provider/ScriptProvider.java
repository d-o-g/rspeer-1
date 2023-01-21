package org.rspeer.script.provider;

import org.rspeer.script.Script;
import org.rspeer.script.ScriptMeta;

import java.lang.reflect.Modifier;
import java.nio.file.Path;
import java.util.function.Predicate;

public interface ScriptProvider<S> extends Predicate<Class<?>> {

    S[] load();

    S load(Path path);

    void prepare(S source) throws Exception;

    @Override
    default boolean test(Class<?> c) {
        return c != null && !Modifier.isAbstract(c.getModifiers())
                && Script.class.isAssignableFrom(c)
                && c.isAnnotationPresent(ScriptMeta.class);
    }
}

