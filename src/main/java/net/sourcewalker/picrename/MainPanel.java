package net.sourcewalker.picrename;

import java.awt.BorderLayout;

import javax.swing.ActionMap;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

public class MainPanel extends JPanel implements ListSelectionListener {

    private static final long serialVersionUID = 2695052132894263047L;
    private static final int[] MIN_WIDTHS = new int[] { 30, 150, 150, 300 };

    private JTable fileTable;
    private JToolBar toolBar;
    private final AppData data;

    public MainPanel(ActionMap actions, AppData data) {
        super();
        this.data = data;
        setLayout(new BorderLayout(5, 5));

        toolBar = new JToolBar(JToolBar.HORIZONTAL);
        toolBar.add(actions.get("addFile"));
        toolBar.add(actions.get("addDirectory"));
        toolBar.add(actions.get("clearList"));
        toolBar.addSeparator();
        toolBar.add(actions.get("moveTop"));
        toolBar.add(actions.get("moveUp"));
        toolBar.add(actions.get("moveDown"));
        toolBar.add(actions.get("moveBottom"));
        toolBar.addSeparator();
        toolBar.add(new PrefixToolbarItem(data));
        add(toolBar, BorderLayout.NORTH);

        fileTable = new JTable(data);
        fileTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        setMinWidths(fileTable.getColumnModel());
        fileTable
                .setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        fileTable.getSelectionModel().addListSelectionListener(this);
        JScrollPane scroller = new JScrollPane(fileTable,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        add(scroller, BorderLayout.CENTER);
    }

    private void setMinWidths(TableColumnModel columnModel) {
        for (int i = 0; i < columnModel.getColumnCount(); i++) {
            TableColumn column = columnModel.getColumn(i);
            column.setMinWidth(MIN_WIDTHS[i]);
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            if (fileTable.getSelectedRowCount() > 0) {
                data.setSelection(fileTable.getSelectedRows());
            } else {
                data.clearSelection();
            }
        }
    }

}
