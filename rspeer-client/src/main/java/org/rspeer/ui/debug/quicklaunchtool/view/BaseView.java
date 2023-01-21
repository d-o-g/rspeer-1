package org.rspeer.ui.debug.quicklaunchtool.view;

import org.rspeer.ui.debug.quicklaunchtool.controller.BaseController;

import java.awt.*;

/**
 * @author MalikDz
 */

@SuppressWarnings("rawtypes")
public abstract class BaseView<T extends BaseController, C extends Component> {

    private T controller;

    public abstract C component();

    public void setController(T controller) {
        this.controller = controller;
    }

    public T controller() {
        return controller;
    }
} 