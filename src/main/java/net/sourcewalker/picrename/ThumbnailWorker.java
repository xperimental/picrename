package net.sourcewalker.picrename;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;

public class ThumbnailWorker {

    public static final int THUMBNAIL_SIZE = 96;

    private ExecutorService worker;
    private int pending = 0;
    private PropertyChangeSupport propSupport = new PropertyChangeSupport(this);

    public void addPropertyChangeListener(String propertyName,
            PropertyChangeListener l) {
        propSupport.addPropertyChangeListener(propertyName, l);
    }

    public int getPending() {
        return pending;
    }

    private void incrementPending() {
        modifyPending(1);
    }

    private void decrementPending() {
        modifyPending(-1);
    }

    private void modifyPending(final int value) {
        synchronized (this) {
            final int oldValue = pending;
            final int newValue = pending + value;
            pending = newValue;
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    propSupport.firePropertyChange("pending", oldValue,
                            newValue);
                }
            });
        }
    }

    public ThumbnailWorker() {
        worker = Executors.newFixedThreadPool(2, new ThreadFactory() {

            private int number = 0;

            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r, "ThumbnailWorker-" + (number++));
                t.setDaemon(true);
                t.setPriority(Thread.MIN_PRIORITY);
                return t;
            }
        });
    }

    public void enqueue(final FileEntry entry, final AppData data) {
        incrementPending();
        worker.submit(new Runnable() {

            @Override
            public void run() {
                try {
                    BufferedImage original = ImageIO.read(entry.getSource());
                    if (original != null) {
                        BufferedImage thumbnailImage = new BufferedImage(
                                THUMBNAIL_SIZE, THUMBNAIL_SIZE, original
                                        .getType());
                        int oWidth = original.getWidth();
                        int oHeight = original.getHeight();
                        int dX, dY, dWidth, dHeight;
                        if (oWidth > oHeight) {
                            dWidth = THUMBNAIL_SIZE;
                            dHeight = (int) (THUMBNAIL_SIZE * ((double) oHeight / oWidth));
                            dX = 0;
                            dY = THUMBNAIL_SIZE / 2 - dHeight / 2;
                        } else {
                            dWidth = (int) (THUMBNAIL_SIZE * ((double) oWidth / oHeight));
                            dHeight = THUMBNAIL_SIZE;
                            dX = THUMBNAIL_SIZE / 2 - dWidth / 2;
                            dY = 0;
                        }
                        Graphics g = thumbnailImage.getGraphics();
                        g.setColor(Color.WHITE);
                        g.fillRect(0, 0, THUMBNAIL_SIZE, THUMBNAIL_SIZE);
                        g.drawImage(original, dX, dY, dX + dWidth,
                                dY + dHeight, 0, 0, oWidth, oHeight, null);
                        entry.setThumbnail(new ImageIcon(thumbnailImage));
                        data.fireDataChanged();
                    }
                } catch (IOException e) {
                    System.err
                            .println("Error reading image file for creating thumbnail: "
                                    + e.getMessage());
                } finally {
                    decrementPending();
                }
            }
        });
    }

}
