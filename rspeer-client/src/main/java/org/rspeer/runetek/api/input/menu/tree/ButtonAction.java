package org.rspeer.runetek.api.input.menu.tree;

import org.rspeer.runetek.adapter.component.InterfaceComponent;

public final class ButtonAction extends ComponentAction {

    public ButtonAction(int opcode, int subcomponentIndex, int componentUid) {
        super(opcode, 0, subcomponentIndex, componentUid);
    }

    public ButtonAction(int opcode, InterfaceComponent component) {
        this(opcode, component.getComponentIndex(), component.getUid());
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

        return builder.append("]").toString();
    }
}
