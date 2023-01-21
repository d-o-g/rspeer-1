package org.rspeer.ui.account;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class JSliderCellEditor extends AbstractCellEditor implements TableCellEditor, TableCellRenderer {

    private JSlider slider;

    JSliderCellEditor() {
        this.slider = new JSlider();
        this.slider.setMajorTickSpacing(50);
        this.slider.setPaintLabels(true);
    }

    public JSlider getSlider() {
        return slider;
    }

    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        Integer val = (Integer) value;
        slider.setValue(val);
        return slider;
    }

    public Object getCellEditorValue() {
        return slider.getValue();
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Integer val = (Integer) value;
        slider.setValue(val);
        return new JLabel(String.valueOf(slider.getValue()) + "%");
    }

}