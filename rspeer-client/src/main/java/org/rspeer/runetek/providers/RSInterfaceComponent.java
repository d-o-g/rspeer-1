package org.rspeer.runetek.providers;

import org.rspeer.runetek.adapter.component.InterfaceComponent;

public interface RSInterfaceComponent extends RSNode {

    boolean isNoClickThrough();

    boolean isNoScrollThrough();

    boolean isScrollBar();

    int getDragArea();

    int getDragAreaThreshold();

    int getAnimation();

    int getBorderThickness();

    int getBoundsIndex();

    int getButtonType();

    int getComponentIndex();

    int getConfig();

    int getContentType();

    int getEnabledMaterialId();

    int getFontId();

    int getHeight();

    int getHorizontalMargin();

    int getInsetX();

    int getInsetY();

    int getItemId();

    int getItemStackSize();

    int getMaterialId();

    int getModelId();

    int getModelOffsetX();

    int getModelOffsetY();

    int getModelType();

    int getModelZoom();

    int getParentUid();

    int getRelativeX();

    int getRelativeY();

    int getRenderCycle();

    int getShadowColor();

    int getSpriteId();

    int getTextColor();

    int getTextSpacing();

    int getType();

    int getUid();

    int getVerticalMargin();

    int getViewportHeight();

    int getViewportWidth();

    int getWidth();

    int getXLayout();

    int getXMargin();

    int getXPadding();

    int getXRotation();

    int getYLayout();

    int getYMargin();

    int getYPadding();

    int getYRotation();

    int getZRotation();

    RSInterfaceComponent getParent();

    String getSelectedAction();

    String getToolTip();

    String getText();

    boolean isExplicitlyHidden();

    boolean isFlippedHorizontally();

    boolean isFlippedVertically();

    boolean isTextShadowed();

    int[] getVarpTransmitTriggers();

    int[] getItemIds();

    int[] getItemStackSizes();

    int[] getSkillTransmitTriggers();

    int[] getTableTransmitTriggers();

    RSInterfaceComponent[] getComponents();

    Object[] getVarpTransmitArgs();

    Object[] getHoverListeners();

    Object[] getMouseEnterListeners();

    Object[] getMouseExitListeners();

    Object[] getRenderListeners();

    Object[] getScrollListeners();

    Object[] getSkillTransmitArgs();

    Object[] getTableTransmitArgs();

    Object[] getMousePressListeners();

    String[] getActions();

    String[] getTableActions();

    int[][] getFunctionOpcodes();

    InterfaceComponent getWrapper();

    int getRootX();

    int getRootY();

    default int getX() {
        return getRootX() + getRelativeX();
    }

    default int getY() {
        return getRootY() + getRelativeY();
    }

    int getAlpha();

    String getSpellName();

    String getName();
}