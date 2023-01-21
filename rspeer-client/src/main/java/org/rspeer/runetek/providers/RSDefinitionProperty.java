package org.rspeer.runetek.providers;

public interface RSDefinitionProperty extends RSDoublyNode {
    char getType();
	int getDefaultInteger();
	String getDefaultString();
	boolean isDeleteOnUse();
}