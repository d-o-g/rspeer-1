package org.rspeer.runetek.providers;

import org.rspeer.runetek.providers.subclass.GameCanvas;
import org.rspeer.runetek.providers.subclass.Subclass;

@Subclass(parent = GameCanvas.class)
public interface RSCanvas extends RSProvider {
}