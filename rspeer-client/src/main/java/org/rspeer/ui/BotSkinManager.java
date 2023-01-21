package org.rspeer.ui;

import org.rspeer.api_services.Logger;
import org.rspeer.ui.skin.BotLookAndFeel;

import javax.swing.*;

public final class BotSkinManager {

    public void initializeLookAndFeel() {
        try {
            JFrame.setDefaultLookAndFeelDecorated(true);
            JDialog.setDefaultLookAndFeelDecorated(true);
            JPopupMenu.setDefaultLightWeightPopupEnabled(false);
            ToolTipManager.sharedInstance().setLightWeightPopupEnabled(false);
            UIManager.put("IconButton", "javax.swing.plaf.basic.BasicButtonUI");
            UIManager.put("PopupMenu.consumeEventOnClose", Boolean.TRUE);
            //    UIManager.put(SubstanceLookAndFeel.BUTTON_NO_MIN_SIZE_PROPERTY, Boolean.TRUE);
            //  UIManager.put(SubstanceLookAndFeel.WINDOW_ROUNDED_CORNERS, Boolean.FALSE);
            //   UIManager.put(SubstanceLookAndFeel.USE_THEMED_DEFAULT_ICONS, Boolean.TRUE);
            UIManager.setLookAndFeel(new BotLookAndFeel());
        } catch (Exception e) {
            Logger.getInstance().capture(e);
        }
    }
}
