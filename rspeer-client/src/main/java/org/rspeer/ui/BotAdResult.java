package org.rspeer.ui;

import com.allatori.annotations.DoNotRename;

import java.util.List;

@DoNotRename
public class BotAdResult {

    @DoNotRename
    private List<BotAdEntry> ads;
    @DoNotRename
    private boolean shouldShow;
    @DoNotRename
    private int hideSeconds;

    public int getHideSeconds() {
        return hideSeconds == 0 ? 120 : hideSeconds;
    }

    @DoNotRename
    public List<BotAdEntry> getAds() {
        return ads;
    }

    @DoNotRename
    public boolean isShouldShow() {
        return shouldShow;
    }
}
