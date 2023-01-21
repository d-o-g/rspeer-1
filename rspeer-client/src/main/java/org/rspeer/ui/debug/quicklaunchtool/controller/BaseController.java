package org.rspeer.ui.debug.quicklaunchtool.controller;

import org.rspeer.ui.debug.quicklaunchtool.view.BaseView;

/**
 * @author MalikDz
 */

@SuppressWarnings("rawtypes")
public abstract class BaseController<T extends BaseView> {

    protected T view;

    public abstract void initView();

    public abstract BaseView view();
} 