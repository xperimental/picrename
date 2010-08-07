package net.sourcewalker.picrename;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;

import javax.swing.ActionMap;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

public class MainPanel extends JPanel implements ListSelectionListener,
        PropertyChangeListener {

    private static final long serialVersionUID = 2695052132894263047L;
    private static final int[] MIN_WIDTHS = new int[] {
            ThumbnailWorker.THUMBNAIL_SIZE, 30, 150, 150, 100, 300 };

    private JTable fileTable;
    private JToolBar toolBar;
    private final AppData data;
    private boolean settingSelection = false;

    public MainPanel(ActionMap actions, final AppData data) {
        super();
        this.data = data;
        setLayout(new BorderLayout(5, 5));

        toolBar = new JToolBar(JToolBar.HORIZONTAL);
        toolBar.add(actions.get("addFile"));
        toolBar.add(actions.get("addDirectory"));
        toolBar.add(actions.get("clearList"));
        toolBar.add(actions.get("removeSelected"));
        toolBar.addSeparator();
        toolBar.add(actions.get("replaceString"));
        toolBar.addSeparator();
        toolBar.add(actions.get("increaseId"));
        toolBar.add(actions.get("decreaseId"));
        toolBar.add(actions.get("orderByDate"));
        toolBar.addSeparator();
        toolBar.add(actions.get("renameFiles"));
        toolBar.addSeparator();
        toolBar.add(new PrefixToolbarItem(data));
        add(toolBar, BorderLayout.NORTH);

        fileTable = new JTable(data);
        fileTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        setMinWidths(fileTable.getColumnModel());
        fileTable
                .setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        fileTable.getSelectionModel().addListSelectionListener(this);
        fileTable.setRowHeight(ThumbnailWorker.THUMBNAIL_SIZE);
        fileTable.setTransferHandler(new FileTransferHandler(data));
        fileTable.setFillsViewportHeight(true);
        fileTable.addMouseListener(new OpenPictureAdapter());
        JScrollPane scroller = new JScrollPane(fileTable,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        add(scroller, BorderLayout.CENTER);

        data.addPropertyChangeListener("selection", this);
    }

    private void setMinWidths(TableColumnModel columnModel) {
        for (int i = 0; i < columnModel.getColumnCount(); i++) {
            TableColumn column = columnModel.getColumn(i);
            column.setMinWidth(MIN_WIDTHS[i]);
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting() && !settingSelection) {
            if (fileTable.getSelectedRowCount() > 0) {
                data.setSelection(fileTable.getSelectedRows());
            } else {
                data.clearSelection();
            }
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        int[] selection = (int[]) evt.getNewValue();
        settingSelection = true;
        DefaultListSelectionModel model = (DefaultListSelectionModel) fileTable
                .getSelectionModel();
        model.clearSelection();
        for (int row : selection) {
            model.addSelectionInterval(row, row);
        }
        settingSelection = false;
    }

    private class OpenPictureAdapter extends MouseAdapter {

        @Override
        public void mouseClicked(MouseEvent evt) {
            if (evt.getButton() == MouseEvent.BUTTON1
                    && evt.getClickCount() == 2) {
                int row = fileTable.getSelectedRow();
                if (row != -1) {
                    FileEntry entry = data.getEntry(row);
                    if (Desktop.isDesktopSupported()) {
                        try {
                            Desktop.getDesktop().open(entry.getSource());
                        } catch (IOException e) {
                            System.out.println("Error while launching file: "
                                    + e.getMessage());
                        }
                    }
                }
            }
        }
    }

}
