package org.rspeer.runetek.providers.subclass;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Subclass {
    Class<?> parent();
}
