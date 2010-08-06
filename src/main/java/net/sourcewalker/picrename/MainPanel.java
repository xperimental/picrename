package net.sourcewalker.picrename;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

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
    private static final int[] MIN_WIDTHS = new int[] { 30, 150, 150, 100, 300 };

    private JTable fileTable;
    private JToolBar toolBar;
    private final AppData data;
    private boolean settingSelection = false;

    public MainPanel(ActionMap actions, AppData data) {
        super();
        this.data = data;
        setLayout(new BorderLayout(5, 5));

        toolBar = new JToolBar(JToolBar.HORIZONTAL);
        toolBar.add(actions.get("addFile"));
        toolBar.add(actions.get("addDirectory"));
        toolBar.add(actions.get("clearList"));
        toolBar.addSeparator();
        toolBar.add(actions.get("increaseId"));
        toolBar.add(actions.get("decreaseId"));
        toolBar.addSeparator();
        toolBar.add(new PrefixToolbarItem(data));
        add(toolBar, BorderLayout.NORTH);

        fileTable = new JTable(data);
        fileTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        setMinWidths(fileTable.getColumnModel());
        fileTable
                .setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        fileTable.getSelectionModel().addListSelectionListener(this);
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

}
