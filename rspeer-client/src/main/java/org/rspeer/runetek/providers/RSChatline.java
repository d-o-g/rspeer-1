package org.rspeer.runetek.providers;

public interface RSChatline extends RSDoublyNode {

    String getSource();

    String getMessage();

    int getIndex();

    int getType();
}
