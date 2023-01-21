package org.rspeer.ui.breaksettings;

import org.rspeer.runetek.api.commons.Time;
import org.rspeer.script.events.breaking.BreakProfile;
import org.rspeer.script.events.breaking.BreakTime;

import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class BreakProfileTableModel extends AbstractTableModel {

    private static final String[] COLUMN_NAMES = {"Break time", "Break duration"};

    private final List<BreakTime> data = new ArrayList<>();

    @Override
    public String getColumnName(int index) {
        return COLUMN_NAMES[index];
    }

    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public int getColumnCount() {
        return COLUMN_NAMES.length;
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        return false;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (rowIndex >= getRowCount() || columnIndex >= COLUMN_NAMES.length) {
            return null;
        }

        BreakTime time = data.get(rowIndex);
        if (columnIndex == 0) {
            return time.getBreakString();
        }

        return Time.format(Duration.ofSeconds(time.getBreakDurationSeconds()));
    }

    public void addRow(BreakTime time) {
        data.add(time);
        fireTableRowsInserted(getRowCount(), getRowCount());
    }

    @Override
    public void fireTableChanged(TableModelEvent event) {
        Collections.sort(data);
        super.fireTableChanged(event);
    }

    public void remove(int row) {
        data.remove(row);
        super.fireTableRowsDeleted(row, row);
    }

    public void clear() {
        int count = getRowCount();
        data.clear();
        super.fireTableRowsDeleted(0, count);
    }

    public BreakProfile toProfile(String name, boolean repeat) {
        return new BreakProfile(name, data, repeat);
    }

}
