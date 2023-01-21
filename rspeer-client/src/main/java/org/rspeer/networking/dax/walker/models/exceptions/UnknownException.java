package org.rspeer.networking.dax.walker.models.exceptions;

import com.allatori.annotations.DoNotRename;

@DoNotRename
public class UnknownException extends RuntimeException {
    public UnknownException(String message) {
        super(message);
    }
}
