package org.rspeer.runetek.providers;

public interface RSHealthBar extends RSNode {
    RSLinkedList getHitsplats();
	RSHealthBarDefinition getDefinition();
}