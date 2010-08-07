package net.sourcewalker.picrename;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;

public class FileTransferHandler extends TransferHandler {

    private static final long serialVersionUID = -6895489971960523591L;
    private static final String FILE_LIST_MIMETYPE = "application/x-java-file-list; class=java.util.List";

    private final AppData data;

    public FileTransferHandler(AppData data) {
        this.data = data;
    }

    @Override
    public boolean canImport(TransferSupport support) {
        Transferable transferable = support.getTransferable();
        for (DataFlavor df : transferable.getTransferDataFlavors()) {
            if (df.getMimeType().equals(FILE_LIST_MIMETYPE)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean importData(TransferSupport support) {
        Transferable transferable = support.getTransferable();
        for (DataFlavor df : transferable.getTransferDataFlavors()) {
            if (df.getMimeType().equals(FILE_LIST_MIMETYPE)) {
                try {
                    @SuppressWarnings("unchecked")
                    final List<File> dropData = (List<File>) transferable
                            .getTransferData(df);
                    Thread importThread = new Thread(new Runnable() {

                        @Override
                        public void run() {
                            for (File file : dropData) {
                                addFile(file);
                            }
                        }
                    }, "DropImport");
                    importThread.setDaemon(true);
                    importThread.setPriority(Thread.MIN_PRIORITY);
                    importThread.start();
                } catch (UnsupportedFlavorException e) {
                } catch (IOException e) {
                    System.out.println("Error while importing dropped files: "
                            + e.getMessage());
                }
            }
        }
        return false;
    }

    private void addFile(final File file) {
        if (file.isDirectory()) {
            for (File child : file.listFiles()) {
                addFile(child);
            }
        } else if (FileNameTools.isNotIgnored(file)) {
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    data.addFile(file);
                }
            });
        }
    }

}
