package org.rspeer.runetek.api.input.menu.tree;

import org.rspeer.runetek.adapter.component.InterfaceComponent;
import org.rspeer.runetek.api.input.menu.ActionOpcodes;

public final class IndexedComponentAction extends ComponentAction {

    public IndexedComponentAction(int actionIndex, int subcomponentIndex, int componentUid) {
        super(actionIndex > 5 ? ActionOpcodes.INTERFACE_ACTION_2 : ActionOpcodes.INTERFACE_ACTION,
                actionIndex, subcomponentIndex, componentUid);
    }

    public IndexedComponentAction(int actionIndex, InterfaceComponent component) {
        this(actionIndex, component.getComponentIndex(), component.getUid());
    }

    public int getActionIndex() {
        return primary;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(super.toString())
                .append("[group=")
                .append(getGroupIndex())
                .append(",component=")
                .append(getComponentIndex());

        if (getSubcomponentIndex() != -1) {
            builder.append(",subcomponent=")
                    .append(getSubcomponentIndex());
        }

        return builder.append(",actionIndex=")
                .append(getActionIndex())
                .append("]").toString();
    }
}
