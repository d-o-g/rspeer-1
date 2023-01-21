package org.rspeer.ui.breaksettings;

import org.rspeer.script.events.breaking.BreakProfile;
import org.rspeer.script.events.breaking.BreakTime;
import org.rspeer.ui.component.BotTitlePane;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.io.FileNotFoundException;
import java.text.ParseException;

public class BreakSettingsPanel extends JPanel {

    private final BreakProfileTableModel profileTableModel = new BreakProfileTableModel();
    private final JCheckBox repeatBreaks = new JCheckBox("Repeat breaks after last break");


    public BreakSettingsPanel() {
        setPreferredSize(new Dimension(400, 450));
        setRequestFocusEnabled(false);
        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JPanel profilePanel = new JPanel(new FlowLayout());

        JLabel profileLabel = new JLabel();

        profileLabel.setText("Profile: ");
        profilePanel.add(profileLabel);

        DefaultComboBoxModel<String> profileBoxModel = new DefaultComboBoxModel<>(BreakProfile.listProfileNames());
        JComboBox<String> profileBox = new JComboBox<>(profileBoxModel);
        profilePanel.add(profileBox);

        JButton deleteProfileButton = new JButton();
        deleteProfileButton.setText("Delete");
        deleteProfileButton.addActionListener(act -> {
            String selected = (String) profileBox.getSelectedItem();
            if (selected == null) {
                return;
            }

            BreakProfile.delete(selected);
            profileBoxModel.removeElement(selected);
        });

        profilePanel.add(deleteProfileButton);

        JButton loadProfileButton = new JButton();
        loadProfileButton.setText("Load");
        loadProfileButton.addActionListener(act -> {
            String selected = (String) profileBox.getSelectedItem();

            if (selected == null) {
                return;
            }

            loadProfile(selected);
        });

        profilePanel.add(loadProfileButton);

        JButton saveProfileButton = new JButton();
        saveProfileButton.setText("Save");
        saveProfileButton.addActionListener(act -> {
            String name = JOptionPane.showInputDialog(saveProfileButton, "Name this profile");
            if (name == null) {
                return;
            }

            BreakProfile profile = profileTableModel.toProfile(name, repeatBreaks.isSelected());
            boolean overwrite = false;
            if (profile.exists()) {
                int confirm = JOptionPane.showConfirmDialog(saveProfileButton, "A profile named " + name + " already exists! Do you want to overwrite this?",
                        "WARNING", JOptionPane.YES_NO_OPTION);
                if (confirm != JOptionPane.YES_OPTION) {
                    return;
                }

                overwrite = true;
            }

            profile.serialize();
            if (!overwrite) {
                profileBoxModel.addElement(name);
            }
        });
        profilePanel.add(saveProfileButton);

        add(profilePanel);

        JTable profileTable = new JTable(profileTableModel);
        JScrollPane tableScrollPanel = new JScrollPane(profileTable);
        add(tableScrollPanel);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        profileTable.setDefaultRenderer(Object.class, centerRenderer);

        JPanel repeatPanel = new JPanel(new BorderLayout());
        repeatPanel.add(repeatBreaks, BorderLayout.CENTER);
        add(repeatPanel);

        JSeparator tableSeparator = new JSeparator();
        add(tableSeparator);

        JPanel generatePanel = new JPanel();
        generatePanel.setBorder(new EmptyBorder(5, 0, 0, 0));
        generatePanel.setPreferredSize(new Dimension(455, 45));
        generatePanel.setOpaque(false);
        generatePanel.setLayout(new FlowLayout());

        JButton generateButton = new JButton();
        generateButton.setText("Generate random profile");
        generateButton.addActionListener(act -> {
            String answer = JOptionPane.showInputDialog(generateButton, "What standard time deviation would you like to use for generating random profile?\nUse hh:mm:ss format to enter the time",
                    "Standard Time Deviation", JOptionPane.QUESTION_MESSAGE);
            try {
                int[] time = stringToTime(answer);
                if (time[0] == 0 && time[1] <= 35) {
                    JOptionPane.showConfirmDialog(generateButton, "Please enter an amount that exceeds 35 minutes");
                    return;
                }
                profileTableModel.clear();
                BreakTime last = BreakTime.random(time[0], time[1], time[2]);
                while (last.getWaitHours() < 100) {
                    profileTableModel.addRow(last);
                    last = BreakTime.random(last, time[0], time[1], time[2]);
                }
            } catch (ParseException e) {
                JOptionPane.showMessageDialog(generateButton, "The time supplied was not in the hh:mm:ss format",
                        "Warning", JOptionPane.WARNING_MESSAGE);
            }
        });
        generatePanel.add(generateButton);

        JButton clearButton = new JButton();
        clearButton.setText("Clear profile");
        clearButton.addActionListener(act -> profileTableModel.clear());
        generatePanel.add(clearButton);

        add(generatePanel);

        JSeparator separator = new JSeparator();
        add(separator);

        JPanel modifyPanel = new JPanel();
        modifyPanel.setLayout(new GridLayout(3, 2));
        modifyPanel.setPreferredSize(new Dimension(300, 100));
        modifyPanel.setBorder(new EmptyBorder(5, 0, 5, 0));

        JLabel runtimeLabel = new JLabel("Runtime (hh:mm:ss) ");
        runtimeLabel.setBorder(new EmptyBorder(5, 5, 5, 5));
        modifyPanel.add(runtimeLabel);

        JTextField runtimeField = new JTextField();
        runtimeField.setHorizontalAlignment(JTextField.CENTER);
        modifyPanel.add(runtimeField);

        JLabel durationLabel = new JLabel("Duration (hh:mm:ss) ");
        durationLabel.setBorder(new EmptyBorder(5, 5, 5, 5));
        modifyPanel.add(durationLabel);

        JTextField durationField = new JTextField();
        durationField.setHorizontalAlignment(JTextField.CENTER);
        modifyPanel.add(durationField);

        JPanel plusWrapperPanel = new JPanel(new FlowLayout());
        JButton plusButton = new JButton("+");
        plusButton.addActionListener(act -> {
            try {
                int[] durationTime = stringToTime(durationField.getText());
                int[] waitTime = stringToTime(runtimeField.getText());
                BreakTime breakTime = new BreakTime(
                        waitTime[0],
                        waitTime[1],
                        waitTime[2],
                        durationTime[0] * BreakTime.SECONDS_PER_HOUR
                                + durationTime[1] * BreakTime.SECONDS_PER_MINUTE
                                + durationTime[2]);
                profileTableModel.addRow(breakTime);
            } catch (ParseException e) {
                JOptionPane.showMessageDialog(generateButton, "The time supplied was not in the hh:mm:ss format",
                        "Warning", JOptionPane.WARNING_MESSAGE);
            }
        });
        plusWrapperPanel.add(plusButton, BorderLayout.CENTER);
        modifyPanel.add(plusWrapperPanel);

        JPanel minusWrapperPanel = new JPanel(new FlowLayout());
        JButton minusButton = new JButton("-");
        minusButton.addActionListener(act -> {
            int selected = profileTable.getSelectedRow();
            if (selected == -1) {
                return;
            }

            profileTableModel.remove(selected);
        });

        minusWrapperPanel.add(minusButton);
        modifyPanel.add(minusWrapperPanel);

        add(modifyPanel);

        String selected = (String) profileBoxModel.getSelectedItem();
        if (selected != null) {
            loadProfile(selected);
        }

    }

    public static void createView(Component relative) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Break Settings");
            BreakSettingsPanel bsp = new BreakSettingsPanel();
            frame.setContentPane(bsp);
            frame.pack();
            frame.validate();
            BotTitlePane.decorate(frame);
            frame.setLocationRelativeTo(relative);
            frame.setVisible(true);
        });
    }

    private void loadProfile(String profile) {
        try {
            BreakProfile loaded = BreakProfile.fromName(profile);
            if (loaded == null) {
                return;
            }

            profileTableModel.clear();
            for (BreakTime time : loaded.getTimes()) {
                profileTableModel.addRow(time);
            }

            profileTableModel.clear();

            for (BreakTime time : loaded.getTimes()) {
                profileTableModel.addRow(time);
            }

            repeatBreaks.setSelected(loaded.shouldRepeat());
        } catch (FileNotFoundException ignored) {
        }
    }

    private int[] stringToTime(String value) throws ParseException {
        if (value == null) {
            throw new ParseException("Null value", 0);
        }

        int[] time = new int[3];
        String[] split = value.split(":");
        if (split.length != 3) {
            throw new ParseException("Wrong time format", 1);
        }

        time[0] = Integer.parseInt(split[0]);
        int minutes = Integer.min(Integer.parseInt(split[1]), 59);
        time[1] = minutes;
        int seconds = Integer.min(Integer.parseInt(split[2]), 59);
        time[2] = seconds;
        return time;
    }
}
