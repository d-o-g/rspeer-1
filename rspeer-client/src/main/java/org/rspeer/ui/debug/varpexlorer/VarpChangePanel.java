package org.rspeer.ui.debug.varpexlorer;

import org.rspeer.runetek.adapter.Varpbit;
import org.rspeer.runetek.api.Definitions;
import org.rspeer.runetek.api.Varps;
import org.rspeer.runetek.providers.RSVarpbit;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.*;
import java.util.List;

/**
 * @author Yasper
 * <p>
 * The main panel on which varp changes are recorded and displayed.
 */
public final class VarpChangePanel extends JPanel {

    private static final int LOAD_LIMIT = 1 << 16;

    private final Map<Integer, List<Varpbit>> hierarchy;
    private final VarpChangeListModel changeModel;

    private int[] previous;

    public VarpChangePanel() {
        setPreferredSize(new Dimension(750, 300));
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));

        hierarchy = new HashMap<>();
        changeModel = new VarpChangeListModel();

        for (int i = 0; i < LOAD_LIMIT; i++) {
            RSVarpbit bit = Definitions.getVarpbit(i);
            if (bit == null) {
                continue;
            }

            List<Varpbit> container;
            if (hierarchy.containsKey(bit.getVarpIndex())) {
                container = hierarchy.get(bit.getVarpIndex());
            } else {
                container = new LinkedList<>();
            }

            container.add(new Varpbit(bit, i));
            hierarchy.put(bit.getVarpIndex(), container);
        }

        JList<VarpChange> changeList = new JList<>(changeModel);
        changeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        changeList.addListSelectionListener((e) -> {
            if (changeList.isSelectionEmpty()) {
                return;
            }

            for (Component component : getComponents()) {
                if (component instanceof VarpInfoPanel) {
                    remove(component);
                    break;
                }
            }

            VarpChange change = changeList.getSelectedValue();
            SwingUtilities.invokeLater(() -> {
                VarpInfoPanel infoPanel = new VarpInfoPanel(change);
                add(infoPanel, BorderLayout.CENTER);
                validate();
            });

        });

        JPanel leftPanel = new JPanel(new BorderLayout());

        JScrollPane listScroller = new JScrollPane(changeList);
        listScroller.setPreferredSize(new Dimension(150, 225));
        leftPanel.add(listScroller, BorderLayout.SOUTH);

        JPanel searchPanel = new JPanel(new BorderLayout());
        JTextField search = new JTextField();
        search.setHorizontalAlignment(JTextField.CENTER);
        search.setPreferredSize(new Dimension(120, 30));
        search.setBorder(new EmptyBorder(0, 0, 0, 5));
        search.setForeground(Color.GRAY);
        search.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String text = search.getText();
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    lookup(text);
                } else {
                    changeModel.setFilter(text);
                    changeList.clearSelection();
                }
            }
        });
        searchPanel.add(search, BorderLayout.WEST);

        JButton lookup = new JButton("+");
        lookup.addActionListener((e) -> lookup(search.getText()));
        searchPanel.add(lookup, BorderLayout.EAST);

        leftPanel.add(searchPanel, BorderLayout.NORTH);
        leftPanel.setBorder(new EmptyBorder(5, 0, 10, 0));

        add(leftPanel, BorderLayout.WEST);
    }

    private void lookup(String text) {
        if (text.isEmpty() || !text.chars().allMatch(Character::isDigit)) {
            return;
        }

        int index = Integer.parseInt(text);

        if (index < Varps.MAX_VARP) {
            changeModel.add(0, new VarpLookup(index, previous[index]));
        }
    }

    public void update(int index, int oldValue, int newValue) {
        changeModel.add(0, new VarpChange(index, oldValue, newValue, findChanges(index, oldValue, newValue)));
    }

    public void update(int[] varps) {
        if (previous == null) {
            previous = varps;
        } else {
            for (int i = 0; i < varps.length; i++) {
                if (varps[i] != previous[i]) {
                    changeModel.add(0, new VarpChange(i, previous[i], varps[i],
                            findChanges(i, previous[i], varps[i])));
                }
            }

            previous = Arrays.copyOfRange(varps, 0, varps.length);
        }
    }

    private VarpbitChange[] findChanges(int index, int previous, int current) {
        List<VarpbitChange> changes = new ArrayList<>();
        if (hierarchy.containsKey(index)) {
            List<Varpbit> children = hierarchy.get(index);
            for (Varpbit varpbit : children) {
                int prev = varpbit.getValue(previous);
                int curr = varpbit.getValue(current);
                if (prev != curr) {
                    changes.add(new VarpbitChange(varpbit, prev, curr));
                }
            }
        }

        return changes.toArray(new VarpbitChange[0]);
    }
}
