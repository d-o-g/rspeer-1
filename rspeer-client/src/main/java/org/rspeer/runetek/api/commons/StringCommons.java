package org.rspeer.runetek.api.commons;

public final class StringCommons {

    private StringCommons() {
        throw new IllegalAccessError();
    }

    public static String replaceJagspace(String text) {
        if (text == null) {
            return null;
        }
        return text.replace('\u00A0', ' ');
    }

    public static String replaceColorTag(String text) {
        return text.replaceAll("(<col=[0-9a-f]+>|</col>)", "");
    }
}
