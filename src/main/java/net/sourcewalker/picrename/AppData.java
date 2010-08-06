package net.sourcewalker.picrename;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

public class AppData implements TableModel {

    private List<TableModelListener> tableListeners;
    private List<FileEntry> files;
    private String prefix;
    private int[] selection = new int[0];
    private PropertyChangeSupport propSupport = new PropertyChangeSupport(this);

    public AppData() {
        tableListeners = new ArrayList<TableModelListener>();
        files = new ArrayList<FileEntry>();
        prefix = "";
    }

    public void addPropertyChangeListener(String property,
            PropertyChangeListener l) {
        propSupport.addPropertyChangeListener(property, l);
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String value) {
        prefix = value;
        fireDataChanged();
    }

    public int[] getSelection() {
        return selection;
    }

    public void setSelection(int[] value) {
        int[] oldValue = selection;
        selection = value;
        propSupport.firePropertyChange("selection", oldValue, value);
    }

    @Override
    public int getRowCount() {
        return files.size();
    }

    @Override
    public int getColumnCount() {
        return 4;
    }

    @Override
    public String getColumnName(int columnIndex) {
        switch (columnIndex) {
        case 0:
            return "NR";
        case 1:
            return "Description";
        case 2:
            return "Source filename";
        case 3:
            return "Target path";
        default:
            return "UNKNOWN";
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return String.class;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == 1;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        FileEntry entry = files.get(rowIndex);
        switch (columnIndex) {
        case 0:
            return String.format("%03d", entry.getId());
        case 1:
            return entry.getDescription();
        case 2:
            return entry.getSourceBasename();
        case 3:
            return entry.getTargetPath(prefix);
        default:
            return "INVALID";
        }
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        FileEntry entry = files.get(rowIndex);
        switch (columnIndex) {
        case 1:
            entry.setDescription((String) aValue);
            break;
        default:
            throw new IllegalArgumentException("Can't edit column: "
                    + columnIndex);
        }
        fireDataChanged();
    }

    @Override
    public void addTableModelListener(TableModelListener l) {
        tableListeners.add(l);
    }

    @Override
    public void removeTableModelListener(TableModelListener l) {
        tableListeners.remove(l);
    }

    public void addFileEntry(FileEntry entry) {
        if (!fileExists(entry.getSource())) {
            files.add(entry);
            fireDataChanged();
        }
    }

    private void fireDataChanged() {
        for (TableModelListener l : tableListeners) {
            l.tableChanged(new TableModelEvent(this));
        }
    }

    private boolean fileExists(File path) {
        for (FileEntry entry : files) {
            if (entry.getSource().equals(path)) {
                return true;
            }
        }
        return false;
    }

    public void clear() {
        files.clear();
        fireDataChanged();
    }

    public int nextId() {
        int maxId = 0;
        for (FileEntry entry : files) {
            maxId = Math.max(entry.getId(), maxId);
        }
        return maxId + 1;
    }

    public int getId(String searchName) {
        for (FileEntry entry : files) {
            String entryName = FileNameTools.removeExtension(entry
                    .getSourceBasename());
            if (entryName.equals(searchName)) {
                return entry.getId();
            }
        }
        return -1;
    }

    public void clearSelection() {
        selection = new int[0];
    }

    public FileEntry getEntry(int index) {
        return files.get(index);
    }

    public void sortEntries() {
        Collections.sort(files, new Comparator<FileEntry>() {

            @Override
            public int compare(FileEntry o1, FileEntry o2) {
                return o1.getId() - o2.getId();
            }
        });
        fireDataChanged();
    }

    public int getIndex(FileEntry search) {
        return files.indexOf(search);
    }

}
