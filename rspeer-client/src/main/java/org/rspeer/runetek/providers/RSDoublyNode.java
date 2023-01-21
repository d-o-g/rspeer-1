package org.rspeer.runetek.providers;

public interface RSDoublyNode extends RSNode {
    RSDoublyNode getNextDoubly();
	RSDoublyNode getPreviousDoubly();
}