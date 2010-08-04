package net.sourcewalker.picrename;

import java.io.File;

public class FileEntry {

    private static final String DELIMITER = "_";

    private File source;
    private String description;

    public FileEntry(File source) {
        this.source = source;
        this.description = FileNameTools.removeExtension(source.getName());
        description = FileNameFilter.filterName(description);
    }

    public File getSource() {
        return source;
    }

    public String getSourceBasename() {
        return source.getName();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTargetPath(String prefix, int id) {
        File parent = source.getParentFile();
        StringBuilder sb = new StringBuilder();
        sb.append(parent.getAbsolutePath());
        sb.append(File.separator);
        if (prefix != null && prefix.length() > 0) {
            sb.append(prefix);
            sb.append(DELIMITER);
        }
        sb.append(String.format("%03d", id));
        if (description != null && description.length() > 0) {
            sb.append(DELIMITER);
            sb.append(description);
        }
        sb.append(FileNameTools.getExtension(source));
        return sb.toString();
    }

}
