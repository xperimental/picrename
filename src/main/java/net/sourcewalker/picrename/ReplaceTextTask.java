package net.sourcewalker.picrename;

import java.util.List;

import org.jdesktop.application.Application;
import org.jdesktop.application.Task;

public class ReplaceTextTask extends Task<Void, Void> {

    private final ReplaceDialog dlg;
    private final AppData data;
    private final String search;
    private final String replace;

    public ReplaceTextTask(Application application, ReplaceDialog dlg,
            AppData data, String search, String replace) {
        super(application);
        this.dlg = dlg;
        this.data = data;
        this.search = search;
        this.replace = replace;
    }

    @Override
    protected Void doInBackground() throws Exception {
        List<FileEntry> files = data.getList();
        for (FileEntry entry : files) {
            String description = entry.getDescription();
            String result = description.replace(search, replace);
            entry.setDescription(result);
        }
        return null;
    }

    @Override
    protected void succeeded(Void result) {
        super.succeeded(result);
        dlg.setVisible(false);
        data.fireDataChanged();
    }

}
