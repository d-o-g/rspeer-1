package org.rspeer.runetek.providers;

/**
 * Created by Spencer on 01/04/2018.
 */
public interface RSMenuItem extends RSProvider {

    int getOpcode();

    int getPrimaryArg();

    int getSecondaryArg();

    int getTertiaryArg();

    String getAction();
}
