package net.sourcewalker.picrename;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.JFileChooser;

import org.jdesktop.application.Action;

public class AppActions {

    private final AppData data;

    public AppActions(AppData data) {
        this.data = data;
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
        FileEntry entry = new FileEntry(path);
        data.addFileEntry(entry);
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

}
