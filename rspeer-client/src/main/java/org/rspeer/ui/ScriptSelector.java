package org.rspeer.ui;

import org.rspeer.RSPeer;
import org.rspeer.commons.BotPreferences;
import org.rspeer.commons.Configuration;
import org.rspeer.runetek.api.Game;
import org.rspeer.script.GameAccount;
import org.rspeer.script.Script;
import org.rspeer.script.ScriptExecutor;
import org.rspeer.script.events.breaking.BreakProfile;
import org.rspeer.script.provider.*;
import org.rspeer.ui.account.XorSerializedAccountList;
import org.rspeer.ui.component.BotTitlePane;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.*;
import java.util.List;

public final class ScriptSelector extends JFrame {

    private static boolean onlyLocals = BotPreferences.getInstance().isLocalScriptsOnly();

    private final ScriptProvider<ScriptSource> localProvider = new LocalScriptProvider(new File(Configuration.SCRIPTS));
    private final ScriptProvider<ScriptSource> remoteProvider = new RemoteScriptProvider();
    private final DefaultComboBoxModel<String> breakModel = new DefaultComboBoxModel<>();
    private final DefaultTableModel model;
    private final JTable table;
    private final JScrollPane pane;
    private final JProgressBar progress;

    private JComboBox<GameAccount> accountSelector;
    private ScriptRow selected = null;
    private String query = "";

    public ScriptSelector() {
        super("Script Selector");
        model = createModel();
        table = createTable();
        table.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent mouseEvent) {
                JTable table = (JTable) mouseEvent.getSource();
                Point point = mouseEvent.getPoint();
                int row = table.rowAtPoint(point);
                if (mouseEvent.getClickCount() == 2 && table.getSelectedRow() == row) {
                    onScriptStarted(null);
                }
            }
        });

        progress = new JProgressBar();
        progress.setIndeterminate(true);

        pane = new JScrollPane(progress);
        pane.setPreferredSize(new Dimension(600, 425));
        pane.getVerticalScrollBar().setUnitIncrement(15);
        setLayout(new BorderLayout());

        add(pane, BorderLayout.NORTH);
        add(createBottomPanel(), BorderLayout.SOUTH);
        pack();

        setResizable(true);
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        BotTitlePane.decorate(this);
        loadScripts();
        setVisible(true);
    }

    public static boolean isLocalScriptsOnly() {
        return onlyLocals;
    }

    private DefaultTableModel createModel() {
        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        model.addColumn("Name");
        model.addColumn("Version");
        model.addColumn("Developer");
        model.addColumn("Description");

        return model;
    }

    private JPanel createBottomPanel() {
        JPanel container = new JPanel();
        container.setLayout(new BorderLayout());
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT));

        //Search
        panel.add(new JLabel("Search:"));
        JTextField query = new JTextField("Enter to search");
        query.setPreferredSize(new Dimension(180, 20));
        query.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    loadScripts();
                } else {
                    String prev = ScriptSelector.this.query;
                    if (!query.getText().equals(prev)) {
                        ScriptSelector.this.query = query.getText().toLowerCase();
                    }
                }
            }
        });
        panel.add(query);

        //Locals filter
        JCheckBox showLocals = new JCheckBox("Local scripts", onlyLocals);
        panel.add(showLocals);
        showLocals.setFocusPainted(false);
        showLocals.addActionListener(e -> {
            onlyLocals = showLocals.isSelected();
            BotPreferences.getInstance().setLocalScriptsOnly(onlyLocals);
            loadScripts();
        });

        JPanel gap = new JPanel();
        gap.setPreferredSize(new Dimension(30, 20));
        panel.add(gap);

        JLabel accountLabel = new JLabel("Account:");
        panel.add(accountLabel);

        XorSerializedAccountList list = new XorSerializedAccountList();
        list.add(0, new GameAccount("None", ""));
        accountSelector = new JComboBox<>(list.toArray(new GameAccount[0]));
        accountSelector.setPreferredSize(new Dimension(80, 20));
        panel.add(accountSelector);

        JLabel breakLabel = new JLabel("Break profile:");
        panel.add(breakLabel);

        breakModel.addElement("None");
        for (String string : BreakProfile.listProfileNames()) {
            breakModel.addElement(string);
        }
        JComboBox<String> comboBox = new JComboBox<>(breakModel);
        panel.add(comboBox);

        JButton start = new JButton("Start");
        start.setPreferredSize(new Dimension(panel.getWidth(), 30));
        start.addActionListener(this::onScriptStarted);
        container.add(start, BorderLayout.SOUTH);
        container.add(panel, BorderLayout.NORTH);
        return container;
    }

    private JTable createTable() {
        JTable table = new JTable(model);
        table.setFillsViewportHeight(true);
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBorder(noFocusBorder);
                return this;
            }
        });
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setShowVerticalLines(false);
        table.setRowSelectionAllowed(true);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setResizingAllowed(false);

        TableColumnModel model = table.getColumnModel();
        model.getColumn(0).setPreferredWidth(100);
        model.getColumn(1).setPreferredWidth(65);
        model.getColumn(2).setPreferredWidth(100);
        model.getColumn(3).setPreferredWidth(335);
        table.getSelectionModel().addListSelectionListener(this::rowSelected);
        return table;
    }

    private void rowSelected(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            int row = table.getSelectedRow();
            if (row >= 0) {
                selected = (ScriptRow) model.getDataVector().get(row);
            }
        }
    }

    private void addRow(ScriptSource source) {
        model.addRow(new ScriptRow(source));
    }

    private boolean matchesQuery(ScriptSource source) {
        if (query == null || query.isEmpty()) {
            return true;
        }

        String elem = source.getName();
        if (elem.toLowerCase().contains(query)) {
            return true;
        }

        elem = source.getDeveloper();
        if (elem.toLowerCase().contains(query)) {
            return true;
        }

        elem = source.getCategory().toString().toLowerCase();
        if (elem.toLowerCase().contains(query)) {
            return true;
        }

        elem = source.getDescription();
        if (elem.toLowerCase().contains(query)) {
            return true;
        }

        return false;
    }

    private void loadScripts() {
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                EventQueue.invokeLater(() -> pane.setViewportView(progress));
                model.getDataVector().clear();
                model.fireTableDataChanged();

                List<ScriptSource> sources = new ArrayList<>();

                if (onlyLocals) {
                    for (ScriptSource source : localProvider.load()) {
                        if (matchesQuery(source)) {
                            sources.add(source);
                        }
                    }
                } else {
                    for (ScriptSource source : remoteProvider.load()) {
                        if (matchesQuery(source)) {
                            sources.add(source);
                        }
                    }
                }
                sources.sort(Comparator.naturalOrder());
                sources.forEach(ScriptSelector.this::addRow);
                model.fireTableDataChanged();
                return null;
            }

            @Override
            protected void done() {
                EventQueue.invokeLater(() -> pane.setViewportView(table));
            }
        }.execute();
    }

    private void onScriptStarted(ActionEvent event) {
        if (selected != null) {
            try {
                if (selected.definition instanceof RemoteScriptSource) {
                    remoteProvider.prepare(selected.definition);
                } else {
                    localProvider.prepare(selected.definition);
                }
                if (selected.definition.getTarget() == null) {
                    JOptionPane.showMessageDialog(RSPeer.getView(), "Failed to start script. You may be out of instances.");
                    ScriptExecutor.stop();
                    return;
                }
                Object accObject = accountSelector.getSelectedItem();
                boolean didSet = false;
                if (accObject instanceof GameAccount) {
                    GameAccount account = (GameAccount) accObject;
                    if (!account.getUsername().equals("None")) {
                        didSet = true;
                        RSPeer.setGameAccount(account);
                    }
                }

                if (!didSet && Game.isLoggedIn()) {
                    RSPeer.setGameAccount(new GameAccount(Game.getClient().getUsername(), Game.getClient().getPassword()));
                }

                String selectedBreakProfile = (String) breakModel.getSelectedItem();
                Script instance = selected.definition.getTarget().newInstance();
                if (selectedBreakProfile != null && !selectedBreakProfile.equals("None")) {
                    instance.setBreakProfile(BreakProfile.fromName(selectedBreakProfile));
                }

                ScriptExecutor.start(selected.definition, instance);

                dispose();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(RSPeer.getView(), "Failed to start script. " + e.getMessage());
                ScriptExecutor.stop();
                e.printStackTrace();
            }
            dispose();
        }
    }

    private class ScriptRow extends Vector<String> {

        private final ScriptSource definition;

        private ScriptRow(ScriptSource definition) {
            this.definition = definition;
            Collections.addAll(this, definition.getName(), String.valueOf(definition.getVersion()), definition.getDeveloper(), definition.getDescription());
        }

        public boolean equals(Object o) {
            return o instanceof ScriptSource && o.equals(definition);
        }
    }
}
