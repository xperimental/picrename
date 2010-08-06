package net.sourcewalker.picrename;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.Task;
import org.jdesktop.application.Task.BlockingScope;

public class AppActions {

    private final AppData data;
    private ResourceMap res;
    private PropertyChangeSupport propSupport = new PropertyChangeSupport(this);

    public AppActions(ApplicationContext context, AppData data) {
        this.data = data;
        res = context.getResourceMap(AppActions.class);
    }

    public void addPropertyChangeListener(PropertyChangeListener l) {
        propSupport.addPropertyChangeListener(l);
    }

    @Action
    public void addDirectory(ActionEvent evt) {
        Component parent = (Component) evt.getSource();
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int result = chooser.showOpenDialog(parent);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedDir = chooser.getSelectedFile();
            for (File child : selectedDir.listFiles()) {
                if (child.isFile()) {
                    addFile(child);
                }
            }
        }
    }

    private void addFile(File path) {
        int id = data.getId(FileNameTools.removeExtension(path.getName()));
        if (id == -1) {
            id = data.nextId();
        }
        FileEntry entry = new FileEntry(data, id, path);
        data.addFileEntry(entry);
        propSupport.firePropertyChange("listNotEmpty", false, true);
    }

    @Action
    public void addFile(ActionEvent evt) {
        Component parent = (Component) evt.getSource();
        JFileChooser chooser = new JFileChooser();
        chooser.setMultiSelectionEnabled(true);
        int result = chooser.showOpenDialog(parent);
        if (result == JFileChooser.APPROVE_OPTION) {
            for (File selected : chooser.getSelectedFiles()) {
                addFile(selected);
            }
        }
    }

    @Action(enabledProperty = "listNotEmpty")
    public void clearList() {
        data.clear();
        propSupport.firePropertyChange("listNotEmpty", true, false);
    }

    private List<FileEntry> getSelectedEntries() {
        int[] selection = data.getSelection();
        List<FileEntry> result = new ArrayList<FileEntry>();
        for (int idx : selection) {
            result.add(data.getEntry(idx));
        }
        return result;
    }

    @Action(enabledProperty = "listNotEmpty")
    public void decreaseId() {
        modifyId(-1);
    }

    private void modifyId(int byValue) {
        List<FileEntry> entries = getSelectedEntries();
        for (FileEntry entry : entries) {
            if (entry.getId() + byValue > 0) {
                entry.setId(entry.getId() + byValue);
            }
        }
        data.sortEntries();
        int[] indexes = new int[entries.size()];
        for (int i = 0; i < entries.size(); i++) {
            indexes[i] = data.getIndex(entries.get(i));
        }
        data.setSelection(indexes);
    }

    @Action(enabledProperty = "listNotEmpty")
    public void increaseId() {
        modifyId(1);
    }

    @Action(enabledProperty = "listNotEmpty")
    public void orderByDate() {
        data.clearSelection();
        data.orderByDate();
    }

    public boolean isListNotEmpty() {
        return data.getRowCount() > 0;
    }

    @Action(enabledProperty = "listNotEmpty", block = BlockingScope.APPLICATION)
    public Task<Boolean, String> renameFiles() {
        int result = JOptionPane.showConfirmDialog(null,
                res.getString("renameConfirmMessage"),
                res.getString("Application.title"), JOptionPane.YES_NO_OPTION);
        if (result != JOptionPane.YES_OPTION) {
            return null;
        } else {
            return new RenameTask(Application.getInstance(), this, data);
        }
    }

}
