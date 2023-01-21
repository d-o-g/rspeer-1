package org.rspeer.runetek.adapter.component;

import org.rspeer.runetek.adapter.Adapter;
import org.rspeer.runetek.adapter.Interactable;
import org.rspeer.runetek.api.Game;
import org.rspeer.runetek.api.commons.AWTUtil;
import org.rspeer.runetek.api.commons.Functions;
import org.rspeer.runetek.api.commons.Identifiable;
import org.rspeer.runetek.api.commons.StringCommons;
import org.rspeer.runetek.api.commons.predicate.Predicates;
import org.rspeer.runetek.api.component.InterfaceAddress;
import org.rspeer.runetek.api.component.Interfaces;
import org.rspeer.runetek.api.input.menu.interaction.InteractDriver;
import org.rspeer.runetek.providers.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Created by MadDev on 11/19/17.
 */
public final class InterfaceComponent extends Adapter<RSInterfaceComponent, InterfaceComponent>
        implements RSInterfaceComponent, Identifiable, Interactable {

    public static final int TYPE_PANEL = 0;
    public static final int TYPE_UNKNOWN = 1;
    public static final int TYPE_TABLE = 2;
    public static final int TYPE_BOX = 3;
    public static final int TYPE_LABEL = 4;
    public static final int TYPE_SPRITE = 5;
    public static final int TYPE_MODEL = 6;
    public static final int TYPE_MEDIA = 7;
    public static final int TYPE_TOOLTIP = 8;
    public static final int TYPE_DIVIDER = 9;

    public InterfaceComponent(RSInterfaceComponent provider) {
        super(provider);
    }

    @Override
    public long getKey() {
        return provider.getKey();
    }

    @Override
    public RSNode getNext() {
        return provider.getNext();
    }

    @Override
    public RSNode getPrevious() {
        return provider.getPrevious();
    }

    @Override
    public boolean isNoClickThrough() {
        return provider.isNoClickThrough();
    }

    @Override
    public boolean isNoScrollThrough() {
        return provider.isNoScrollThrough();
    }

    @Override
    public boolean isScrollBar() {
        return provider.isScrollBar();
    }

    @Override
    public int getDragArea() {
        return provider.getDragArea();
    }

    @Override
    public int getDragAreaThreshold() {
        return provider.getDragAreaThreshold();
    }

    @Override
    public int getAnimation() {
        return provider.getAnimation();
    }

    @Override
    public int getBorderThickness() {
        return provider.getBorderThickness();
    }

    @Override
    public int getBoundsIndex() {
        return provider.getBoundsIndex();
    }

    @Override
    public int getButtonType() {
        return provider.getButtonType();
    }

    @Override
    public int getComponentIndex() {
        return provider.getComponentIndex();
    }

    @Override
    public int getConfig() {
        RSNodeTable<RSIntegerNode> configs = Game.getClient().getInterfaceConfigs();
        if (configs == null) {
            return provider.getConfig();
        }
        RSIntegerNode node = configs.safeLookup(((long) provider.getUid() << 32) + (long) provider.getComponentIndex());
        return node != null ? node.getValue() : provider.getConfig();
    }

    @Override
    public int getContentType() {
        return provider.getContentType();
    }

    @Override
    public int getEnabledMaterialId() {
        return provider.getEnabledMaterialId();
    }

    @Override
    public int getFontId() {
        return provider.getFontId();
    }

    @Override
    public int getHeight() {
        return provider.getHeight();
    }

    @Override
    public int getHorizontalMargin() {
        return provider.getHorizontalMargin();
    }

    @Override
    public int getInsetX() {
        return provider.getInsetX();
    }

    @Override
    public int getInsetY() {
        return provider.getInsetY();
    }

    @Override
    public int getItemId() {
        return provider.getItemId();
    }

    @Override
    public int getItemStackSize() {
        return provider.getItemStackSize();
    }

    @Override
    public int getMaterialId() {
        return provider.getMaterialId();
    }

    @Override
    public int getModelId() {
        return provider.getModelId();
    }

    @Override
    public int getModelOffsetX() {
        return provider.getModelOffsetX();
    }

    @Override
    public int getModelOffsetY() {
        return provider.getModelOffsetY();
    }

    @Override
    public int getModelType() {
        return provider.getModelType();
    }

    @Override
    public int getModelZoom() {
        return provider.getModelZoom();
    }

    @Override
    public int getParentUid() {
        int raw = provider.getParentUid();
        if (raw != -1) {
            return raw;
        }
        RSNodeTable<RSInterfaceNode> nodes = Interfaces.getNodes();
        if (nodes != null) {
            RSInterfaceNode node = nodes.safeLookup(getUid());
            if (node != null) {
                return (int) node.getKey();
            }
        }
        return -1;
    }

    @Override
    public int getRelativeX() {
        return provider.getRelativeX();
    }

    @Override
    public int getRelativeY() {
        return provider.getRelativeY();
    }

    @Override
    public int getRenderCycle() {
        return provider.getRenderCycle();
    }

    @Override
    public int getShadowColor() {
        return provider.getShadowColor();
    }

    @Override
    public int getSpriteId() {
        return provider.getSpriteId();
    }

    @Override
    public int getTextColor() {
        return provider.getTextColor();
    }

    @Override
    public int getTextSpacing() {
        return provider.getTextSpacing();
    }

    @Override
    public int getType() {
        return provider.getType();
    }

    @Override
    public int getUid() {
        return provider.getUid();
    }

    @Override
    public int getVerticalMargin() {
        return provider.getVerticalMargin();
    }

    @Override
    public int getViewportHeight() {
        return provider.getViewportHeight();
    }

    @Override
    public int getViewportWidth() {
        return provider.getViewportWidth();
    }

    @Override
    public int getWidth() {
        return provider.getWidth();
    }

    @Override
    public int getXLayout() {
        return provider.getXLayout();
    }

    @Override
    public int getXMargin() {
        return provider.getXMargin();
    }

    @Override
    public int getXPadding() {
        return provider.getXPadding();
    }

    @Override
    public int getXRotation() {
        return provider.getXRotation();
    }

    @Override
    public int getYLayout() {
        return provider.getYLayout();
    }

    @Override
    public int getYMargin() {
        return provider.getYMargin();
    }

    @Override
    public int getYPadding() {
        return provider.getYPadding();
    }

    @Override
    public int getYRotation() {
        return provider.getYRotation();
    }

    @Override
    public int getZRotation() {
        return provider.getZRotation();
    }

    @Override
    public InterfaceComponent getParent() {
        return Functions.mapOrDefault(provider::getParent, RSInterfaceComponent::getWrapper, null);
    }

    @Override
    public String getSelectedAction() {
        return Functions.mapOrDefault(provider::getSelectedAction, StringCommons::replaceJagspace, "");
    }

    @Override
    public String getToolTip() {
        return Functions.mapOrDefault(provider::getToolTip, StringCommons::replaceJagspace, "");
    }

    @Override
    public String getText() {
        return Functions.mapOrDefault(provider::getText, StringCommons::replaceJagspace, "");
    }

    @Override
    public boolean isExplicitlyHidden() {
        return provider.isExplicitlyHidden();
    }

    @Override
    public boolean isFlippedHorizontally() {
        return provider.isFlippedHorizontally();
    }

    @Override
    public boolean isFlippedVertically() {
        return provider.isFlippedVertically();
    }

    @Override
    public boolean isTextShadowed() {
        return provider.isTextShadowed();
    }

    @Override
    public int[] getVarpTransmitTriggers() {
        return Functions.mapOrDefault(() -> provider, RSInterfaceComponent::getVarpTransmitTriggers, new int[0]);
    }

    @Override
    public int[] getItemIds() {
        return Functions.mapOrDefault(() -> provider, RSInterfaceComponent::getItemIds, new int[0]);
    }

    @Override
    public int[] getItemStackSizes() {
        return Functions.mapOrDefault(() -> provider, RSInterfaceComponent::getItemStackSizes, new int[0]);
    }

    @Override
    public int[] getSkillTransmitTriggers() {
        return Functions.mapOrDefault(() -> provider, RSInterfaceComponent::getSkillTransmitTriggers, new int[0]);
    }

    @Override
    public int[] getTableTransmitTriggers() {
        return Functions.mapOrDefault(() -> provider, RSInterfaceComponent::getTableTransmitTriggers, new int[0]);
    }

    @Override
    public Object[] getVarpTransmitArgs() {
        return Functions.mapOrDefault(() -> provider, RSInterfaceComponent::getVarpTransmitArgs, new Object[0]);
    }

    @Override
    public Object[] getHoverListeners() {
        Object[] raw = provider.getHoverListeners();
        if (raw == null) {
            return new Object[0];
        }

        List<Object> listeners = new ArrayList<>();
        for (Object listener : raw) {
            if (listener != null) {
                listeners.add(listener);
            }
        }
        return listeners.toArray(new Object[0]);
    }

    public boolean containsHoverListener(Predicate<Object> predicate) {
        Object[] listeners = provider.getHoverListeners();
        if (listeners == null) {
            return false;
        }

        for (Object listener : listeners) {
            if (listener != null && predicate.test(listener)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Object[] getMouseEnterListeners() {
        return Functions.mapOrDefault(() -> provider, RSInterfaceComponent::getMouseEnterListeners, new Object[0]);
    }

    @Override
    public Object[] getMouseExitListeners() {
        return Functions.mapOrDefault(() -> provider, RSInterfaceComponent::getMouseExitListeners, new Object[0]);
    }

    @Override
    public Object[] getRenderListeners() {
        return Functions.mapOrDefault(() -> provider, RSInterfaceComponent::getRenderListeners, new Object[0]);
    }

    @Override
    public Object[] getScrollListeners() {
        return Functions.mapOrDefault(() -> provider, RSInterfaceComponent::getScrollListeners, new Object[0]);
    }

    @Override
    public Object[] getSkillTransmitArgs() {
        return Functions.mapOrDefault(() -> provider, RSInterfaceComponent::getSkillTransmitArgs, new Object[0]);
    }

    @Override
    public Object[] getTableTransmitArgs() {
        return Functions.mapOrDefault(() -> provider, RSInterfaceComponent::getTableTransmitArgs, new Object[0]);
    }

    @Override
    public String[] getActions() {
        if (provider.getActions() == null) {
            return new String[0];
        }

        List<String> actions = new ArrayList<>();
        for (String action : provider.getActions()) {
            if (action != null) {
                actions.add(StringCommons.replaceColorTag(action));
            }
        }
        return actions.toArray(new String[0]);
    }

    @Override
    public String[] getRawActions() {
        if (provider.getActions() == null) {
            return new String[0];
        }

        List<String> actions = new ArrayList<>();
        for (String action : provider.getActions()) {
            if (action != null) {
                action = StringCommons.replaceColorTag(action);
            }

            actions.add(action);
        }
        return actions.toArray(new String[0]);
    }

    @Override
    public String[] getTableActions() {
        if (provider.getTableActions() == null) {
            return new String[0];
        }

        List<String> actions = new ArrayList<>();
        for (String action : provider.getTableActions()) {
            if (action != null) {
                actions.add(StringCommons.replaceColorTag(action));
            }
        }
        return actions.toArray(new String[0]);
    }

    @Override
    public int[][] getFunctionOpcodes() {
        return provider.getFunctionOpcodes();
    }

    @Override
    public InterfaceComponent getWrapper() {
        return provider.getWrapper();
    }

    @Override
    public int getRootX() {
        return provider.getRootX();
    }

    @Override
    public int getRootY() {
        return provider.getRootY();
    }

    public int getParentIndex() {
        int comp = getComponentIndex();
        if (comp != -1) { //is a grandchild
            return getUid() & 0xffff;
        }
        return getUid() >>> 16;
    }

    public int getRootIndex() {
        return getUid() >>> 16;
    }

    public int getIndex() {
        int comp = getComponentIndex();
        if (comp != -1) {
            return comp;
        }
        return getUid() & 0xffff;
    }

    public boolean isGrandchild() {
        return getComponentIndex() != -1;
    }

    public InterfaceComponent getComponent(Predicate<? super InterfaceComponent> predicate) {
        for (InterfaceComponent component : getComponents()) {
            if (predicate.test(component)) {
                return component;
            }
        }
        return null;
    }

    public InterfaceComponent[] getComponents(Predicate<? super InterfaceComponent> predicate) {
        List<InterfaceComponent> components = new ArrayList<>();
        RSInterfaceComponent[] raw = provider.getComponents();
        if (raw == null) {
            return new InterfaceComponent[0];
        }
        for (RSInterfaceComponent comp : raw) {
            if (comp != null) {
                InterfaceComponent wrapper = comp.getWrapper();
                if (predicate.test(wrapper)) {
                    components.add(wrapper);
                }
            }
        }
        return components.toArray(new InterfaceComponent[0]);
    }

    @Override
    public InterfaceComponent[] getComponents() {
        return getComponents(Predicates.always());
    }

    public InterfaceComponent[] getDynamicComponents() {
        InterfaceComponent[] all = getComponents();
        if (all.length == 0) {
            return all;
        }
        List<InterfaceComponent> components = new ArrayList<>();
        for (InterfaceComponent component : all) {
            if (component != null && component.getParentUid() == getUid()) {
                components.add(component);
            }
        }
        return components.toArray(new InterfaceComponent[0]);
    }

    public InterfaceComponent[] getStaticComponents() {
        List<InterfaceComponent> components = new ArrayList<>();
        for (InterfaceComponent component : Interfaces.get(getUid() >>> 16)) {
            if (component != null && component.getParentUid() == getUid()) {
                components.add(component);
            }
        }
        return components.toArray(new InterfaceComponent[0]);
    }

    public InterfaceComponent[] getNestedComponents() {
        RSNodeTable<RSInterfaceNode> nodes = Interfaces.getNodes();
        if (nodes == null) {
            return new InterfaceComponent[0];
        }
        RSInterfaceNode node = nodes.safeLookup(getUid());
        if (node == null) {
            return new InterfaceComponent[0];
        }
        int group = node.getId();
        if (group == -1) {
            return new InterfaceComponent[0];
        }
        List<InterfaceComponent> dynamic = new ArrayList<>();
        for (InterfaceComponent component : Interfaces.get(group)) {
            if (component != null) {
                dynamic.add(component);
            }
        }
        return dynamic.toArray(new InterfaceComponent[0]);
    }

    public InterfaceComponent getComponent(int index) {
        RSInterfaceComponent[] components = getComponents();
        if (index >= 0 && index < components.length) {
            return Functions.mapOrDefault(() -> components[index], RSInterfaceComponent::getWrapper, null);
        }
        return null;
    }

    public boolean isVisible() {
        return getRenderCycle() + 20 >= Game.getClient().getEngineCycle() && !isExplicitlyHidden();
    }

    public Rectangle getBounds() {
        return new Rectangle(getX(), getY(), getWidth(), getHeight());
    }

    public void draw(Graphics g) {
        Rectangle bounds = getBounds();
        AWTUtil.drawBorderedRectangle(g, bounds.x, bounds.y, bounds.width, bounds.height, Color.WHITE);
    }

    @Override
    public int getId() {
        return provider.getUid();
    }

    public int getComponentCount() {
        return getComponents().length;
    }

    public int getAlpha() {
        return provider.getAlpha();
    }

    @Override
    public String getName() {
        return Functions.mapOrDefault(provider::getName, StringCommons::replaceJagspace, "");
    }

    @Override
    public Object[] getMousePressListeners() {
        return provider.getMousePressListeners();
    }

    @Override
    public String getSpellName() {
        return Functions.mapOrDefault(provider::getSpellName, StringCommons::replaceJagspace, "");
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getRootIndex()).append(", ");
        if (isGrandchild()) {
            sb.append(getParentIndex()).append(", ").append(getIndex());
        } else {
            sb.append(getIndex());
        }
        return sb.toString();
    }

    public boolean interact(int opcode, int actionIndex) {
        return InteractDriver.INSTANCE.interact(this, opcode, actionIndex);
    }

    public InterfaceAddress toAddress() {
        return new InterfaceAddress(getRootIndex()).component(isGrandchild() ? getParentIndex() : getIndex()).subComponent(getComponentIndex());
    }
}
