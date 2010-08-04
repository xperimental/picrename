package net.sourcewalker.picrename;

import java.io.File;

public class FileEntry {

    private static final String DELIMITER = "_";
    private File source;
    private String description;

    public FileEntry(File source) {
        this.source = source;
        this.description = removeExtension(source.getName());
        description = FileNameFilter.filterName(description);
    }

    private String removeExtension(String basename) {
        String[] tokens = basename.split("\\.");
        String name;
        if (tokens.length > 1) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < tokens.length - 1; i++) {
                sb.append(tokens[i]);
            }
            name = sb.toString();
        } else {
            name = tokens[0];
        }
        return name;
    }

    public File getSource() {
        return source;
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
        sb.append(getExtension(source));
        return sb.toString();
    }

    private String getExtension(File file) {
        String[] tokens = file.getName().split("\\.");
        if (tokens.length > 1) {
            return "." + tokens[tokens.length - 1];
        } else {
            return "";
        }
    }

}
