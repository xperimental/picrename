package net.sourcewalker.picrename;

import java.io.File;
import java.util.Date;

public class FileEntry {

    private static final String DELIMITER = "_";

    private int id;
    private File source;
    private String description;
    private Date dateTaken;

    public FileEntry(final AppData data, int id, File source) {
        this.id = id;
        this.source = source;
        this.description = FileNameTools.removeExtension(source.getName());
        description = FileNameFilter.filterName(description);
        startReadDate(data);
    }

    private void startReadDate(final AppData data) {
        Thread dateReadThread = new Thread(new Runnable() {

            @Override
            public void run() {
                ExifReader exif = new ExifReader(source);
                if (exif.isSuccessful()) {
                    dateTaken = exif.getDateTaken();
                    data.fireDataChanged();
                }
            }
        });
        dateReadThread.setName("DateReader-" + getId());
        dateReadThread.start();
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

    public void setSource(File value) {
        source = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDateTaken() {
        return dateTaken;
    }

    public void setDateTaken(Date dateTaken) {
        this.dateTaken = dateTaken;
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
