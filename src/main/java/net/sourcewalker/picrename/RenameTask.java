package net.sourcewalker.picrename;

import java.io.File;
import java.util.List;

import javax.swing.JOptionPane;

import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.Task;

public class RenameTask extends Task<Boolean, String> {

    private boolean cancelled = false;
    private ResourceMap res;
    private final AppActions actions;
    private final AppData data;

    public RenameTask(Application application, AppActions actions, AppData data) {
        super(application);
        this.actions = actions;
        this.data = data;
        this.res = application.getContext().getResourceMap(RenameTask.class);
    }

    @Override
    protected void cancelled() {
        super.cancelled();
        cancelled = true;
    }

    @Override
    protected Boolean doInBackground() throws Exception {
        String prefix = data.getPrefix();
        List<FileEntry> files = data.getList();
        for (FileEntry entry : files) {
            if (cancelled) {
                return false;
            }
            String target = entry.getTargetPath(prefix);
            File targetFile = new File(target);
            message("renaming", entry.getSourceBasename(), targetFile.getName());

            entry.getSource().renameTo(targetFile);
            entry.setSource(targetFile);
        }
        return true;
    }

    @Override
    protected void succeeded(Boolean result) {
        if (result) {
            int clearResult = JOptionPane.showConfirmDialog(null,
                    res.getString("finishedMessage"),
                    res.getString("Application.title"),
                    JOptionPane.YES_NO_OPTION);
            if (clearResult == JOptionPane.YES_OPTION) {
                actions.clearList();
            }
        }
    }
}
