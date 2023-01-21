package org.rspeer.ui.component.log;

import net.jodah.failsafe.function.CheckedRunnable;
import org.rspeer.RSPeer;
import org.rspeer.commons.OS;
import org.rspeer.commons.RsPeerExecutor;
import org.rspeer.ui.BotView;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public final class LogTextArea extends JList<FormattedLogRecord> {

    private static final int MAX_ENTRIES = 100;

    private final LogQueue logQueue = new LogQueue();
    private final LogAreaListModel model = new LogAreaListModel();
    private final Runnable scrollToBottom = () -> scrollRectToVisible(new Rectangle(0, Integer.MAX_VALUE, 0, 0));

    public LogTextArea() {
        setModel(model);
        setCellRenderer(new Renderer());

        setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        if (OS.get() == OS.MAC) {
            setFont(new Font("Monaco", Font.PLAIN, 10));
        } else {
            setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        }

        RsPeerExecutor.scheduleAtFixedRate(logQueue, 1, 1, TimeUnit.SECONDS);
    }

    public void log(FormattedLogRecord logRecord) {
        logQueue.queue(logRecord);
    }

    public void scrollToBottom() {
        SwingUtilities.invokeLater(scrollToBottom);
    }

    private static class Renderer implements ListCellRenderer<FormattedLogRecord> {

        private static final Border EMPTY_BORDER = new EmptyBorder(1, 1, 1, 1);
        private static final Border SELECTED_BORDER = UIManager.getBorder("List.focusCellHighlightBorder");
        private static final Dimension SIZE = new Dimension(750, 100);

        public Component getListCellRendererComponent(JList list, FormattedLogRecord record, int index,
                                                      boolean selected, boolean focused) {
            JTextArea result = new JTextArea();
            result.setWrapStyleWord(true);
            result.setLineWrap(true);
            result.setDragEnabled(true);
            result.setText(record.getFormatted());
            result.setSize(SIZE);
            result.setComponentOrientation(list.getComponentOrientation());
            result.setFont(list.getFont());
            result.setBorder(focused || selected ? SELECTED_BORDER : EMPTY_BORDER);

            result.setForeground(Color.GRAY.brighter());
            if (!selected && index % 2 == 0) {
                result.setBackground(result.getBackground().darker());
            }

            if (record.getBase().getLevel() == Level.SEVERE) {
                result.setForeground(Color.RED.brighter());
            }

            if (record.getBase().getLevel() == Level.WARNING) {
                result.setForeground(Color.ORANGE);
            }

            if ((record.getBase().getLevel() == Level.FINE)
                    || (record.getBase().getLevel() == Level.FINER)
                    || (record.getBase().getLevel() == Level.FINEST)) {
                result.setForeground(Color.GREEN);
            }

            Object[] params = record.getBase().getParameters();
            if (params != null) {
                for (Object param : params) {
                    if (param != null) {
                        if (param instanceof Color) {
                            result.setForeground((Color) param);
                        } else if (param instanceof Font) {
                            result.setFont((Font) param);
                        }
                    }
                }
            }
            return result;
        }
    }

    private class LogAreaListModel extends AbstractListModel<FormattedLogRecord> {

        private List<FormattedLogRecord> records = new ArrayList<>(LogTextArea.MAX_ENTRIES);

        public void addAllElements(List<FormattedLogRecord> obj) {
            records.addAll(obj);
            if (getSize() > LogTextArea.MAX_ENTRIES) {
                records = new ArrayList<>(records.subList((getSize() - LogTextArea.MAX_ENTRIES), getSize()));
                fireContentsChanged(this, 0, getSize() - 1);
            } else {
                fireIntervalAdded(this, (getSize() - 1), (getSize() - 1));
            }
        }

        public FormattedLogRecord getElementAt(int index) {
            return records.get(index);
        }

        public int getSize() {
            return records.size();
        }
    }

    private class LogQueue implements CheckedRunnable {

        private final Object lock = new Object();

        private List<FormattedLogRecord> queue = new ArrayList<>(100);

        private void queue(final FormattedLogRecord record) {
            synchronized (lock) {
                if(queue.size() >= MAX_ENTRIES) {
                    queue.remove(0);
                }
                queue.add(record);
            }
        }

        public void run() {
            BotView view = RSPeer.getView();
            if(view == null || view.getLogPane() == null) {
                return;
            }
            boolean minified = view.getLogPane().isMinified();
            // Java isn't GCing the logs if the client is minimized, so don't release the queue
            // until they unminify the client.
            if (minified) {
                return;
            }

            List<FormattedLogRecord> toFlush = new LinkedList<>();

            synchronized (lock) {
                if (!queue.isEmpty()) {
                    toFlush.addAll(queue);
                    queue.clear();
                }
            }
            if (!toFlush.isEmpty()) {
                model.addAllElements(toFlush);
                scrollToBottom();
            }
        }
    }
}