package net.sourcewalker.picrename;

import java.io.File;

public class FileEntry {

    private static final String DELIMITER = "_";

    private int id;
    private File source;
    private String description;

    public FileEntry(int id, File source) {
        this.id = id;
        this.source = source;
        this.description = FileNameTools.removeExtension(source.getName());
        description = FileNameFilter.filterName(description);
    }

    public int getId() {
        return id;
    }

    public void setId(int value) {
        id = value;
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

    public String getTargetPath(String prefix) {
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
