package net.sourcewalker.picrename;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
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
    private String lastDir = null;
    protected int pendingThumbs;

    public AppActions(ApplicationContext context, AppData data) {
        this.data = data;
        res = context.getResourceMap(AppActions.class);

        this.data.addPropertyChangeListener("selection",
                new PropertyChangeListener() {

                    @Override
                    public void propertyChange(PropertyChangeEvent evt) {
                        boolean oldValue = ((int[]) evt.getOldValue()).length > 0;
                        boolean newValue = ((int[]) evt.getNewValue()).length > 0;
                        propSupport.firePropertyChange("selectionNotEmpty",
                                oldValue, newValue);
                    }
                });
        this.data.addPropertyChangeListener("files",
                new PropertyChangeListener() {

                    @Override
                    public void propertyChange(PropertyChangeEvent evt) {
                        boolean value = isListNotEmpty();
                        propSupport.firePropertyChange("listNotEmpty", !value,
                                value);
                        updateRenameReady();
                    }
                });
        this.data.getThumbnailWorker().addPropertyChangeListener("pending",
                new PropertyChangeListener() {

                    @Override
                    public void propertyChange(PropertyChangeEvent evt) {
                        pendingThumbs = (Integer) evt.getNewValue();
                        updateRenameReady();
                    }
                });
    }

    public boolean isRenameReady() {
        return isListNotEmpty() && (pendingThumbs == 0);
    }

    protected void updateRenameReady() {
        boolean newValue = isRenameReady();
        propSupport.firePropertyChange("renameReady", !newValue, newValue);
    }

    public void addPropertyChangeListener(PropertyChangeListener l) {
        propSupport.addPropertyChangeListener(l);
    }

    @Action
    public void addDirectory(ActionEvent evt) {
        Component parent = (Component) evt.getSource();
        JFileChooser chooser = getFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int result = chooser.showOpenDialog(parent);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedDir = chooser.getSelectedFile();
            for (File child : selectedDir.listFiles()) {
                if (FileNameTools.isNotIgnored(child)) {
                    addFile(child);
                }
            }
            lastDir = chooser.getSelectedFile().getAbsolutePath();
        }
    }

    private JFileChooser getFileChooser() {
        JFileChooser chooser;
        if (lastDir != null) {
            chooser = new JFileChooser(lastDir);
        } else {
            chooser = new JFileChooser();
        }
        return chooser;
    }

    protected void addFile(File path) {
        data.addFile(path);
    }

    @Action
    public void addFile(ActionEvent evt) {
        Component parent = (Component) evt.getSource();
        JFileChooser chooser = getFileChooser();
        chooser.setMultiSelectionEnabled(true);
        int result = chooser.showOpenDialog(parent);
        if (result == JFileChooser.APPROVE_OPTION) {
            for (File selected : chooser.getSelectedFiles()) {
                addFile(selected);
            }
            lastDir = chooser.getSelectedFiles()[0].getAbsolutePath();
        }
    }

    @Action(enabledProperty = "listNotEmpty")
    public void clearList() {
        data.clear();
    }

    private List<FileEntry> getSelectedEntries() {
        int[] selection = data.getSelection();
        List<FileEntry> result = new ArrayList<FileEntry>();
        for (int idx : selection) {
            result.add(data.getEntry(idx));
        }
        return result;
    }

    @Action(enabledProperty = "selectionNotEmpty")
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

    @Action(enabledProperty = "selectionNotEmpty")
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

    public boolean isSelectionNotEmpty() {
        return data.getSelection().length > 0;
    }

    @Action(enabledProperty = "renameReady", block = BlockingScope.APPLICATION)
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

    @Action(enabledProperty = "selectionNotEmpty")
    public void removeSelected() {
        List<FileEntry> selected = getSelectedEntries();
        data.clearSelection();
        for (FileEntry entry : selected) {
            data.removeFileEntry(entry);
        }
    }

    @Action(enabledProperty = "listNotEmpty")
    public void replaceString() {
        App app = (App) Application.getInstance();
        ReplaceDialog dlg = new ReplaceDialog(app, data);
        dlg.setVisible(true);
    }

}
