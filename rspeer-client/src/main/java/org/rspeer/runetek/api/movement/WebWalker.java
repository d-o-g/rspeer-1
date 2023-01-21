package org.rspeer.runetek.api.movement;

import com.allatori.annotations.DoNotRename;

@DoNotRename
public enum WebWalker {
    /**
     * If the path request should be using DaxWeb.
     */
    @DoNotRename
    Dax,
    /**
     * If the path request should be using Acuity.
     */
    @DoNotRename
    Acuity,
    /**
     * If the specific users client settings should determine which web to use.
     */
    @DoNotRename
    ClientSettingsBased
}
