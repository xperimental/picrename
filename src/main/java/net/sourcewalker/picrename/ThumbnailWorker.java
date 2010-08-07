package net.sourcewalker.picrename;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class ThumbnailWorker {

    public static final int THUMBNAIL_SIZE = 96;

    private static ExecutorService worker;

    public static void enqueue(final FileEntry entry, final AppData data) {
        if (worker == null) {
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
                }
            }
        });
    }

}
